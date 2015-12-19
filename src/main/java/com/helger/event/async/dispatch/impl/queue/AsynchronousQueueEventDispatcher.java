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
package com.helger.event.async.dispatch.impl.queue;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.collection.pair.IPair;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.event.EEventObserverHandlerType;
import com.helger.event.IAggregatorFactory;
import com.helger.event.IEvent;
import com.helger.event.IEventObserver;
import com.helger.event.IEventObservingExceptionCallback;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcher;
import com.helger.event.async.dispatch.impl.AsynchronousEventResultCollector;
import com.helger.event.impl.AbstractEventDispatcher;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Dispatch events based on a {@link java.util.concurrent.BlockingQueue}.
 *
 * @author Philip Helger
 */
public final class AsynchronousQueueEventDispatcher extends AbstractEventDispatcher
                                                    implements IAsynchronousEventDispatcher
{
  private final Lock m_aLock = new ReentrantLock ();
  private final AsyncQueueDispatcherThread m_aQueueThread;

  public AsynchronousQueueEventDispatcher (@Nonnull final IAggregatorFactory <Object, Object> aResultAggregatorFactory,
                                           @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    super (aResultAggregatorFactory);

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
    final IPair <Integer, Map <IEventObserver, EEventObserverHandlerType>> aHandlingInfo = getListOfObserversThatCanHandleTheEvent (aEvent,
                                                                                                                                    aObservers);

    final int nHandlingObserverCountWithReturnValue = aHandlingInfo.getFirst ().intValue ();
    final Map <IEventObserver, EEventObserverHandlerType> aHandlingObservers = aHandlingInfo.getSecond ();

    if (!aHandlingObservers.isEmpty ())
    {
      m_aLock.lock ();
      try
      {
        // At least one handler was found
        AsynchronousEventResultCollector aLocalResultCallback = null;
        if (nHandlingObserverCountWithReturnValue > 0)
        {
          // If we have handling observers, we need an overall result callback!
          if (aOverallResultCallback == null)
            throw new IllegalStateException ("Are you possibly using a unicast event manager and sending an event that has a return value?");

          // Create collector and start thread only if we expect a result
          aLocalResultCallback = new AsynchronousEventResultCollector (nHandlingObserverCountWithReturnValue,
                                                                       m_aResultAggregator,
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
      }
      finally
      {
        m_aLock.unlock ();
      }
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
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AsynchronousQueueEventDispatcher rhs = (AsynchronousQueueEventDispatcher) o;
    return m_aResultAggregator.equals (rhs.m_aResultAggregator);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aResultAggregator).getHashCode ();
  }
}
