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
package com.helger.event.observerqueue;

import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.CommonsWeakHashMap;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.observer.IEventObserver;

/**
 * Implementation of {@link IEventObserverQueue} based on a {@link WeakHashMap}
 * used as a {@link Set}. Order of observers is not maintained!
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class EventObserverQueueWeakSet implements IEventObserverQueue
{
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final ICommonsMap <IEventObserver, Boolean> m_aWeakMap = new CommonsWeakHashMap <> ();

  public EventObserverQueueWeakSet ()
  {}

  @Nonnull
  public EChange addObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    return EChange.valueOf (m_aRWLock.writeLocked ( () -> m_aWeakMap.put (aObserver, Boolean.TRUE) == null));
  }

  @Nonnull
  public EChange removeObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    return EChange.valueOf (m_aRWLock.writeLocked ( () -> m_aWeakMap.remove (aObserver) != null));
  }

  public boolean isEmpty ()
  {
    return m_aRWLock.readLocked ( () -> m_aWeakMap.isEmpty ());

  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IEventObserver> getAllObservers ()
  {
    return m_aRWLock.readLocked ( () -> new CommonsArrayList <> (m_aWeakMap.keySet ()));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final EventObserverQueueWeakSet rhs = (EventObserverQueueWeakSet) o;
    return m_aWeakMap.equals (rhs.m_aWeakMap);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aWeakMap).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("WeakMap", m_aWeakMap).toString ();
  }
}
