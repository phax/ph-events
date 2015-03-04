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
package com.helger.event.observerqueue.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEventObserver;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Implementation of {@link IEventObserverQueue} based on a {@link WeakHashMap}
 * used as a {@link Set}. Order of observers is not maintained!
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class EventObserverQueueWeakSet extends AbstractEventObserverQueue
{
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <IEventObserver, Boolean> m_aWeakMap = new WeakHashMap <IEventObserver, Boolean> ();

  public EventObserverQueueWeakSet ()
  {}

  @Nonnull
  public EChange addObserver (@Nonnull final IEventObserver aObserver)
  {
    if (aObserver == null)
      throw new NullPointerException ("observer");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (m_aWeakMap.put (aObserver, Boolean.TRUE) == null);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange removeObserver (@Nonnull final IEventObserver aObserver)
  {
    if (aObserver == null)
      throw new NullPointerException ("observer");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (m_aWeakMap.remove (aObserver) != null);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  public boolean isEmpty ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aWeakMap.isEmpty ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IEventObserver> getAllObservers ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (m_aWeakMap.keySet ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof EventObserverQueueWeakSet))
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
    return new ToStringGenerator (this).append ("set", m_aWeakMap).toString ();
  }
}
