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
package com.helger.event.dispatch.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.exception.mock.IMockException;
import com.helger.commons.factory.IFactory;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEvent;
import com.helger.event.dispatch.AbstractEventDispatcher;
import com.helger.event.dispatch.EffectiveEventObserverList;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.EventObservingExceptionCallback;
import com.helger.event.observer.exception.EventObservingExceptionWrapper;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.IEventObserverQueue;

public class SynchronousEventDispatcher extends AbstractEventDispatcher implements ISynchronousEventDispatcher
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SynchronousEventDispatcher.class);

  private final IEventObservingExceptionCallback m_aExceptionHandler;

  public SynchronousEventDispatcher (@Nonnull final IFactory <IAggregator <Object, ?>> aResultAggregatorFactory,
                                     @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    super (aResultAggregatorFactory);
    m_aExceptionHandler = aExceptionHandler != null ? aExceptionHandler
                                                    : EventObservingExceptionCallback.getInstance ();
  }

  @Nullable
  public Object dispatch (@Nonnull final IEvent aEvent, @Nonnull final IEventObserverQueue aObservers)
  {
    ValueEnforcer.notNull (aEvent, "Event");
    ValueEnforcer.notNull (aObservers, "Observers");

    // find all observers that can handle the passed event
    final EffectiveEventObserverList aHandlingInfo = EffectiveEventObserverList.getListOfObserversThatCanHandleTheEvent (aEvent,
                                                                                                                         aObservers);
    final Map <IEventObserver, EEventObserverHandlerType> aHandlingObservers = aHandlingInfo.getObservers ();

    Object aDispatchResult;
    if (aHandlingObservers.isEmpty ())
    {
      // No observer -> no result
      aDispatchResult = null;
    }
    else
    {
      // At least one handler was found

      // The list of all callback return values
      final List <Object> aCallbackReturnValues = new ArrayList <Object> ();

      // The local result callback that puts the different values into the list
      final INonThrowingRunnableWithParameter <Object> aResultCallback = aCurrentObject -> aCallbackReturnValues.add (aCurrentObject);

      // Iterate all handling observers
      for (final Map.Entry <IEventObserver, EEventObserverHandlerType> aEntry : aHandlingObservers.entrySet ())
      {
        final boolean bHasReturnValue = aEntry.getValue ().hasReturnValue ();
        final IEventObserver aObserver = aEntry.getKey ();
        final int nOldSize = aCallbackReturnValues.size ();

        try
        {
          // main event call
          aObserver.onEvent (aEvent, bHasReturnValue ? aResultCallback : null);
        }
        catch (final Throwable t)
        {
          m_aExceptionHandler.handleObservingException (t);
          s_aLogger.error ("Failed to notify " + aObserver + " on " + aEvent, t instanceof IMockException ? null : t);

          // Handle eventual exception gracefully
          if (bHasReturnValue)
          {
            // Add the exception wrapper even if the observer already added a
            // result -> this leads to an IllegalStateException below!
            aCallbackReturnValues.add (new EventObservingExceptionWrapper (aObserver, aEvent, t));
          }
        }

        // Check whether a value was really added (no matter whether it was an
        // exception or a real value)
        if (bHasReturnValue)
        {
          if (aCallbackReturnValues.size () == nOldSize)
            throw new IllegalStateException ("The observer " +
                                             aObserver +
                                             " did not add any return value even though he claimed to have one!");
          if (aCallbackReturnValues.size () > nOldSize + 1)
            throw new IllegalStateException ("The observer " +
                                             aObserver +
                                             " added more than one return value which is generally not allowed!");
        }
      }

      // aggregate all result values
      aDispatchResult = getResultAggregator ().aggregate (aCallbackReturnValues);
    }

    // Return the main dispatch result
    return aDispatchResult;
  }

  @Nonnull
  public EChange stop ()
  {
    // Nothing to do in here
    return EChange.UNCHANGED;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final SynchronousEventDispatcher rhs = (SynchronousEventDispatcher) o;
    return m_aExceptionHandler.equals (rhs.m_aExceptionHandler);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aExceptionHandler).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("ExceptionHandler", m_aExceptionHandler)
                            .toString ();
  }
}
