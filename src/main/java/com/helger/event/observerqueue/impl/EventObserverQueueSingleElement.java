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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.observer.IEventObserver;

@ThreadSafe
public final class EventObserverQueueSingleElement extends AbstractEventObserverQueue
{
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private IEventObserver m_aObserver;

  public EventObserverQueueSingleElement ()
  {}

  @Nonnull
  public EChange addObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aObserver = aObserver;
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange removeObserver (@Nonnull final IEventObserver aObserver)
  {
    ValueEnforcer.notNull (aObserver, "Observer");

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (!EqualsHelper.equals (m_aObserver, aObserver))
        return EChange.UNCHANGED;
      m_aObserver = null;
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IEventObserver> getAllObservers ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.newList (m_aObserver);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean isEmpty ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aObserver == null;
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
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final EventObserverQueueSingleElement rhs = (EventObserverQueueSingleElement) o;
    return EqualsHelper.equals (m_aObserver, rhs.m_aObserver);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aObserver).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("observer", m_aObserver).toString ();
  }
}
