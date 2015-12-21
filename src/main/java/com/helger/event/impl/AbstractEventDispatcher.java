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
package com.helger.event.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.factory.IFactory;
import com.helger.event.IEvent;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observerqueue.IEventObserverQueue;

public abstract class AbstractEventDispatcher
{
  public static final class ObserverList
  {
    private final int m_nHandlingObserverCountWithReturnValue;
    private final Map <IEventObserver, EEventObserverHandlerType> m_aHandlers;

    private ObserverList (@Nonnegative final int nHandlingObserverCountWithReturnValue,
                          @Nonnull final Map <IEventObserver, EEventObserverHandlerType> aHandlers)
    {
      m_nHandlingObserverCountWithReturnValue = nHandlingObserverCountWithReturnValue;
      m_aHandlers = aHandlers;
    }

    public int getHandlingObserverCountWithReturnValue ()
    {
      return m_nHandlingObserverCountWithReturnValue;
    }

    @Nonnull
    @ReturnsMutableObject ("design")
    public Map <IEventObserver, EEventObserverHandlerType> getObservers ()
    {
      return m_aHandlers;
    }
  }

  protected final IAggregator <Object, ?> m_aResultAggregator;

  public AbstractEventDispatcher (@Nonnull final IFactory <IAggregator <Object, ?>> aResultAggregatorFactory)
  {
    ValueEnforcer.notNull (aResultAggregatorFactory, "ResultAggregatorFactory");

    m_aResultAggregator = aResultAggregatorFactory.get ();
    if (m_aResultAggregator == null)
      throw new IllegalArgumentException ("No dispatch result aggregator was created");
  }

  @Nonnull
  protected static final ObserverList getListOfObserversThatCanHandleTheEvent (@Nonnull final IEvent aEvent,
                                                                               @Nonnull final IEventObserverQueue aObservers)
  {
    // find all handling observers
    final Map <IEventObserver, EEventObserverHandlerType> aHandler = new LinkedHashMap <> ();
    final List <IEventObserver> aObserversToRemove = new ArrayList <> ();
    int nHandlingObserverCountWithReturnValue = 0;
    for (final IEventObserver aObserver : aObservers.getAllObservers ())
    {
      final EEventObserverHandlerType eHandleType = aObserver.canHandleEvent (aEvent);
      if (eHandleType.isHandling ())
      {
        aHandler.put (aObserver, eHandleType);
        if (eHandleType.hasReturnValue ())
          nHandlingObserverCountWithReturnValue++;

        // "Only once" observer?
        if (aObserver.isOnlyOnce ())
          aObserversToRemove.add (aObserver);
      }
    }

    // remove all "only once" observers
    for (final IEventObserver aObserver : aObserversToRemove)
      if (aObservers.removeObserver (aObserver).isUnchanged ())
        throw new IllegalStateException ("Failed to remove only-omce observer " + aObserver + " from " + aObservers);

    // return number of handling + handling observer map
    return new ObserverList (nHandlingObserverCountWithReturnValue, aHandler);
  }
}
