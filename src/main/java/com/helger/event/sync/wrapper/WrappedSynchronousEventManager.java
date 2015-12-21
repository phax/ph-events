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
package com.helger.event.sync.wrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.event.IEvent;
import com.helger.event.mgr.IEventManager;
import com.helger.event.observer.IEventObserver;
import com.helger.event.sync.SynchronousEventManager;

public class WrappedSynchronousEventManager implements IEventManager
{
  private final SynchronousEventManager m_aEvtMgr;

  public WrappedSynchronousEventManager (@Nonnull final SynchronousEventManager aEvtMgr)
  {
    ValueEnforcer.notNull (aEvtMgr, "EvtMgr");
    m_aEvtMgr = aEvtMgr;
  }

  @Nonnull
  public EChange registerObserver (@Nonnull final IEventObserver aObserver)
  {
    return m_aEvtMgr.registerObserver (aObserver);
  }

  @Nonnull
  public EChange unregisterObserver (@Nonnull final IEventObserver aObserver)
  {
    return m_aEvtMgr.unregisterObserver (aObserver);
  }

  public void trigger (@Nonnull final IEvent aEvent,
                       @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
  {
    m_aEvtMgr.trigger (aEvent, aResultCallback);
  }

  @Nonnull
  public EChange stop ()
  {
    return m_aEvtMgr.stop ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final WrappedSynchronousEventManager rhs = (WrappedSynchronousEventManager) o;
    return m_aEvtMgr.equals (rhs.m_aEvtMgr);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aEvtMgr).getHashCode ();
  }
}
