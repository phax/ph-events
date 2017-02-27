/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.event.dispatch.async;

import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.concurrent.SimpleLock;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEvent;
import com.helger.event.dispatch.AbstractEventDispatcher;
import com.helger.event.dispatch.EffectiveEventObserverList;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Dispatch events based on a {@link java.util.concurrent.BlockingQueue}.
 *
 * @author Philip Helger
 */
public class AsynchronousEventDispatcher extends AbstractEventDispatcher implements IAsynchronousEventDispatcher
{
  private final SimpleLock m_aLock = new SimpleLock ();
  private final AsyncQueueDispatcherThread m_aQueueThread;

  public AsynchronousEventDispatcher (@Nullable final IEventObservingExceptionCallback aExceptionCallback)
  {
    super (aExceptionCallback);
    m_aQueueThread = new AsyncQueueDispatcherThread (getExceptionCallback ());
    m_aQueueThread.start ();
  }

  public void dispatch (@Nonnull final IEvent aEvent,
                        @Nonnull final IEventObserverQueue aObservers,
                        @Nullable final Consumer <Object> aOverallResultConsumer)
  {
    ValueEnforcer.notNull (aEvent, "Event");
    ValueEnforcer.notNull (aObservers, "Observers");
    ValueEnforcer.notNull (aOverallResultConsumer, "OverallResultConsumer");

    // find all observers that can handle the passed event
    final EffectiveEventObserverList aHandlingInfo = EffectiveEventObserverList.getListOfObserversThatCanHandleTheEvent (aEvent,
                                                                                                                         aObservers);
    if (aHandlingInfo.hasNoObservers ())
      return;

    m_aLock.locked ( () -> {
      // At least one handler was found
      AsynchronousEventResultCollectorThread aLocalResultCollector = null;

      final int nHandlingObserverCountWithReturnValue = aHandlingInfo.getHandlingObserverCountWithReturnValue ();
      if (nHandlingObserverCountWithReturnValue > 0)
      {
        // Create collector and start thread only if we expect a result
        aLocalResultCollector = new AsynchronousEventResultCollectorThread (nHandlingObserverCountWithReturnValue,
                                                                            aEvent.getResultAggregator (),
                                                                            aOverallResultConsumer);
        aLocalResultCollector.start ();
      }

      // Iterate all handling observers
      for (final Map.Entry <IEventObserver, EEventObserverHandlerType> aEntry : aHandlingInfo.getObservers ()
                                                                                             .entrySet ())
      {
        m_aQueueThread.addEventToQueue (aEvent,
                                        aEntry.getKey (),
                                        aEntry.getValue ().hasReturnValue () ? aLocalResultCollector : null);
      }
    });
  }

  @Override
  @Nonnull
  public EChange stop ()
  {
    return m_aLock.locked ( () -> {
      // Interrupt the dispatcher thread
      if (m_aQueueThread.isInterrupted ())
        return EChange.UNCHANGED;
      m_aQueueThread.interrupt ();
      return EChange.CHANGED;
    });
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("QueueThread", m_aQueueThread).getToString ();
  }
}
