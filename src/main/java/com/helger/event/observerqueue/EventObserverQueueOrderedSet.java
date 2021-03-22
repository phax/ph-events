/**
 * Copyright (C) 2015-2020 Philip Helger (www.helger.com)
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
package com.helger.event.observerqueue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.observer.IEventObserver;

/**
 * Implementation of {@link IEventObserverQueue} based on a
 * {@link CommonsLinkedHashSet}. Order of observers is maintained!
 *
 * @author Philip Helger
 */
@ThreadSafe
public class EventObserverQueueOrderedSet implements IEventObserverQueue
{
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final ICommonsOrderedSet <IEventObserver> m_aSet = new CommonsLinkedHashSet <> ();

  public EventObserverQueueOrderedSet ()
  {}

  @Nonnull
  public EChange addObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    return m_aRWLock.writeLockedGet ( () -> m_aSet.addObject (aObserver));
  }

  @Nonnull
  public EChange removeObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    return m_aRWLock.writeLockedGet ( () -> m_aSet.removeObject (aObserver));
  }

  public boolean isEmpty ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aSet.isEmpty ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IEventObserver> getAllObservers ()
  {
    return m_aRWLock.readLockedGet ( () -> m_aSet.getCopyAsList ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final EventObserverQueueOrderedSet rhs = (EventObserverQueueOrderedSet) o;
    return m_aSet.equals (rhs.m_aSet);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aSet).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("set", m_aSet).getToString ();
  }
}
