/**
 * Copyright (C) 2015-2016 Philip Helger (www.helger.com)
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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.observer.IEventObserver;

/**
 * Implementation of {@link IEventObserverQueue} based on a
 * {@link LinkedHashSet}. Order of observers is maintained!
 *
 * @author Philip Helger
 */
@ThreadSafe
public class EventObserverQueueOrderedSet implements IEventObserverQueue
{
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final Set <IEventObserver> m_aSet = new LinkedHashSet <> ();

  public EventObserverQueueOrderedSet ()
  {}

  @Nonnull
  public EChange addObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    return EChange.valueOf (m_aRWLock.writeLocked ( () -> m_aSet.add (aObserver)));
  }

  @Nonnull
  public EChange removeObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    return EChange.valueOf (m_aRWLock.writeLocked ( () -> m_aSet.remove (aObserver)));
  }

  public boolean isEmpty ()
  {
    return m_aRWLock.readLocked ( () -> m_aSet.isEmpty ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IEventObserver> getAllObservers ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aSet));
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
    return new ToStringGenerator (this).append ("set", m_aSet).toString ();
  }
}
