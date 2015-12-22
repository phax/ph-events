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
package com.helger.event.dispatch.async;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.event.IEvent;
import com.helger.event.dispatch.AbstractEventDispatcher;
import com.helger.event.dispatch.EffectiveEventObserverList;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Event dispatcher that spawns a thread for each triggered event and notifies
 * all observers in a serial way.
 *
 * @author Philip Helger
 */
public class AsynchronousSerialEventDispatcher extends AbstractEventDispatcher implements IAsynchronousEventDispatcher
{
  public AsynchronousSerialEventDispatcher (@Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    super (aExceptionHandler);
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
      // At least one handler was found
      AsynchronousEventResultCollector aLocalResultCallback = null;
      if (nHandlingObserverCountWithReturnValue > 0)
      {
        // If we have handling observers, we need an overall result callback!
        if (aOverallResultCallback == null)
          throw new IllegalStateException ("Are you possibly using a unicast event manager and sending an event that has a return value?");

        // Create collector and start thread only if we expect a result
        aLocalResultCallback = new AsynchronousEventResultCollector (nHandlingObserverCountWithReturnValue,
                                                                     aEvent.getResultAggregator (),
                                                                     aOverallResultCallback);
        aLocalResultCallback.start ();
      }

      // Spawn a separate thread for each event that is triggered
      new AsyncSerialDispatcherThread (aEvent,
                                       aHandlingObservers,
                                       aLocalResultCallback,
                                       getExceptionCallback ()).start ();
    }
  }
}