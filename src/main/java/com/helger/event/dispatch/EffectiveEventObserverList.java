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
package com.helger.event.dispatch;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.event.IEvent;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observerqueue.IEventObserverQueue;

public final class EffectiveEventObserverList
{
  private final ICommonsOrderedMap <IEventObserver, EEventObserverHandlerType> m_aObservers;
  private final int m_nHandlingObserverCountWithReturnValue;

  EffectiveEventObserverList (@Nonnull final ICommonsOrderedMap <IEventObserver, EEventObserverHandlerType> aObservers,
                              @Nonnegative final int nHandlingObserverCountWithReturnValue)
  {
    ValueEnforcer.notNull (aObservers, "Observers");
    ValueEnforcer.isGE0 (nHandlingObserverCountWithReturnValue, "HandlingObserverCountWithReturnValue");
    ValueEnforcer.isTrue (aObservers.size () >= nHandlingObserverCountWithReturnValue, "Internal inconsistency");

    m_aObservers = aObservers;
    m_nHandlingObserverCountWithReturnValue = nHandlingObserverCountWithReturnValue;
  }

  public boolean hasNoObservers ()
  {
    return m_aObservers.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public ICommonsOrderedMap <IEventObserver, EEventObserverHandlerType> getObservers ()
  {
    return m_aObservers;
  }

  @Nonnegative
  public int getHandlingObserverCountWithReturnValue ()
  {
    return m_nHandlingObserverCountWithReturnValue;
  }

  @Nonnull
  public static EffectiveEventObserverList getListOfObserversThatCanHandleTheEvent (@Nonnull final IEvent aEvent,
                                                                                    @Nonnull final IEventObserverQueue aObserverQueue)
  {
    // find all handling observers
    final ICommonsOrderedMap <IEventObserver, EEventObserverHandlerType> aObservers = new CommonsLinkedHashMap <> ();
    final ICommonsList <IEventObserver> aObserversToRemove = new CommonsArrayList <> ();
    int nHandlingObserverCountWithReturnValue = 0;

    // For all
    for (final IEventObserver aObserver : aObserverQueue.getAllObservers ())
    {
      // Find the one matching our event type
      final EEventObserverHandlerType eHandleType = aObserver.canHandleEvent (aEvent);
      if (eHandleType.isHandling ())
      {
        aObservers.put (aObserver, eHandleType);
        if (eHandleType.hasReturnValue ())
          nHandlingObserverCountWithReturnValue++;

        // "Only once" observer?
        if (aObserver.isOnlyOnce ())
          aObserversToRemove.add (aObserver);
      }
    }

    // remove all "only once" observers
    for (final IEventObserver aObserver : aObserversToRemove)
      if (aObserverQueue.removeObserver (aObserver).isUnchanged ())
        throw new IllegalStateException ("Failed to remove only-omce observer " +
                                         aObserver +
                                         " from " +
                                         aObserverQueue);

    // return number of handling + handling observer map
    return new EffectiveEventObserverList (aObservers, nHandlingObserverCountWithReturnValue);
  }
}
