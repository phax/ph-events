/**
 * Copyright (C) 2015 Philip Helger (www.helger.com)
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
package com.helger.event.dispatch.async.queue;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.concurrent.SimpleLock;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEvent;
import com.helger.event.dispatch.AbstractEventDispatcher;
import com.helger.event.dispatch.EffectiveEventObserverList;
import com.helger.event.dispatch.async.AsynchronousEventResultCollector;
import com.helger.event.dispatch.async.IAsynchronousEventDispatcher;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Dispatch events based on a {@link java.util.concurrent.BlockingQueue}.
 *
 * @author Philip Helger
 */
public final class AsynchronousQueueEventDispatcher extends AbstractEventDispatcher
                                                    implements IAsynchronousEventDispatcher
{
  private final SimpleLock m_aLock = new SimpleLock ();
  private final AsyncQueueDispatcherThread m_aQueueThread;

  public AsynchronousQueueEventDispatcher (@Nonnull final IAggregator <Object, ?> aResultAggregator,
                                           @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    super (aResultAggregator);

    m_aQueueThread = new AsyncQueueDispatcherThread (aExceptionHandler);
    m_aQueueThread.start ();
  }

  public void dispatch (@Nonnull final IEvent aEvent,
                        @Nonnull final IEventObserverQueue aObservers,
                        final INonThrowingRunnableWithParameter <Object> aOverallResultCallback)
  {
    ValueEnforcer.notNull (aEvent, "Event");
    ValueEnforcer.notNull (aObservers, "Observers");

    // find all observers that can handle the passed event
    final EffectiveEventObserverList aHandlingInfo = EffectiveEventObserverList.getListOfObserversThatCanHandleTheEvent (aEvent,
                                                                                                                         aObservers);

    final int nHandlingObserverCountWithReturnValue = aHandlingInfo.getHandlingObserverCountWithReturnValue ();
    final Map <IEventObserver, EEventObserverHandlerType> aHandlingObservers = aHandlingInfo.getObservers ();

    if (!aHandlingObservers.isEmpty ())
    {
      m_aLock.locked ( () -> {
        // At least one handler was found
        AsynchronousEventResultCollector aLocalResultCallback = null;
        if (nHandlingObserverCountWithReturnValue > 0)
        {
          // If we have handling observers, we need an overall result callback!
          if (aOverallResultCallback == null)
            throw new IllegalStateException ("Are you possibly using a unicast event manager and sending an event that has a return value?");

          // Create collector and start thread only if we expect a result
          aLocalResultCallback = new AsynchronousEventResultCollector (nHandlingObserverCountWithReturnValue,
                                                                       getResultAggregator (),
                                                                       aOverallResultCallback);
          aLocalResultCallback.start ();
        }

        // Iterate all handling observers
        for (final Map.Entry <IEventObserver, EEventObserverHandlerType> aEntry : aHandlingObservers.entrySet ())
        {
          m_aQueueThread.addToQueue (aEvent,
                                     aEntry.getKey (),
                                     aEntry.getValue ().hasReturnValue () ? aLocalResultCallback : null);
        }
      });
    }
  }

  @Nonnull
  public EChange stop ()
  {
    // Interrupt the dispatcher thread
    if (m_aQueueThread.isInterrupted ())
      return EChange.UNCHANGED;
    m_aQueueThread.interrupt ();
    return EChange.CHANGED;
  }

  @Override
  public boolean equals (final Object o)
  {
    return o == this;
  }

  @Override
  public int hashCode ()
  {
    return System.identityHashCode (this);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("QueueThread", m_aQueueThread).toString ();
  }
}
