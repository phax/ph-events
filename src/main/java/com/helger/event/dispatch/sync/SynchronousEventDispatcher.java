/**
 * Copyright (C) 2015-2018 Philip Helger (www.helger.com)
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
package com.helger.event.dispatch.sync;

import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.exception.mock.IMockException;
import com.helger.event.IEvent;
import com.helger.event.dispatch.AbstractEventDispatcher;
import com.helger.event.dispatch.EffectiveEventObserverList;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.EventObservingExceptionWrapper;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.IEventObserverQueue;

public class SynchronousEventDispatcher extends AbstractEventDispatcher implements ISynchronousEventDispatcher
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SynchronousEventDispatcher.class);

  public SynchronousEventDispatcher (@Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    super (aExceptionHandler);
  }

  @Nullable
  public Object dispatch (@Nonnull final IEvent aEvent, @Nonnull final IEventObserverQueue aObservers)
  {
    ValueEnforcer.notNull (aEvent, "Event");
    ValueEnforcer.notNull (aObservers, "Observers");

    // find all observers that can handle the passed event
    final EffectiveEventObserverList aHandlingInfo = EffectiveEventObserverList.getListOfObserversThatCanHandleTheEvent (aEvent,
                                                                                                                         aObservers);
    final ICommonsOrderedMap <IEventObserver, EEventObserverHandlerType> aHandlingObservers = aHandlingInfo.getObservers ();

    Object aAggregatedDispatchResult;
    if (aHandlingObservers.isEmpty ())
    {
      // No observer -> no result
      aAggregatedDispatchResult = null;
    }
    else
    {
      // At least one handler was found

      // The list of all callback return values
      final ICommonsList <Object> aEventReturnValues = new CommonsArrayList <> ();

      // The local result callback that puts the different values into the list
      final Consumer <Object> aResultCollector = o -> aEventReturnValues.add (o);

      // Iterate all handling observers
      for (final Map.Entry <IEventObserver, EEventObserverHandlerType> aEntry : aHandlingObservers.entrySet ())
      {
        final IEventObserver aObserver = aEntry.getKey ();
        final boolean bHasReturnValue = aEntry.getValue ().hasReturnValue ();
        final int nOldReturnValueCount = aEventReturnValues.size ();

        try
        {
          // main event call
          aObserver.onEvent (aEvent, bHasReturnValue ? aResultCollector : null);
        }
        catch (final Throwable t)
        {
          getExceptionCallback ().handleObservingException (t);
          LOGGER.error ("Failed to notify " +
                           aObserver +
                           " on " +
                           aEvent +
                           " because of " +
                           t.getClass ().getName (),
                           t instanceof IMockException ? null : t);

          // Handle eventual exception gracefully
          if (bHasReturnValue)
          {
            // Add the exception wrapper even if the observer already added a
            // result -> this leads to an IllegalStateException below!
            aEventReturnValues.add (new EventObservingExceptionWrapper (aObserver, aEvent, t));
          }
        }

        /*
         * Consistency cCheck whether a value was really added (no matter
         * whether it was an exception or a real value)
         */
        if (bHasReturnValue)
        {
          if (aEventReturnValues.size () == nOldReturnValueCount)
            throw new IllegalStateException ("The observer " +
                                             aObserver +
                                             " did not add any return value on event " +
                                             aEvent +
                                             " even though he claimed to have one!");
          if (aEventReturnValues.size () > nOldReturnValueCount + 1)
            throw new IllegalStateException ("The observer " +
                                             aObserver +
                                             " added more than one return value on " +
                                             aEvent +
                                             " which is generally not allowed!");
        }
      }

      // finally aggregate all event return values
      aAggregatedDispatchResult = aEvent.getResultAggregator ().apply (aEventReturnValues);
    }

    // Return the main dispatch result
    return aAggregatedDispatchResult;
  }
}
