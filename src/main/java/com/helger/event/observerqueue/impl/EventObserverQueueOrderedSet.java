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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEventObserver;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Implementation of {@link IEventObserverQueue} based on a
 * {@link LinkedHashSet}. Order of observers is maintained!
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class EventObserverQueueOrderedSet extends AbstractEventObserverQueue
{
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Set <IEventObserver> m_aSet = new LinkedHashSet <IEventObserver> ();

  public EventObserverQueueOrderedSet ()
  {}

  @Nonnull
  public EChange addObserver (@Nonnull final IEventObserver aObserver)
  {
    if (aObserver == null)
      throw new NullPointerException ("observer");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (m_aSet.add (aObserver));
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
      return EChange.valueOf (m_aSet.remove (aObserver));
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
      return m_aSet.isEmpty ();
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
      return CollectionHelper.newList (m_aSet);
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
    if (!(o instanceof EventObserverQueueOrderedSet))
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
