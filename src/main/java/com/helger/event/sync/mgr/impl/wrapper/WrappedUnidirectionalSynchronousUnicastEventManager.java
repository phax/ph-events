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
package com.helger.event.sync.mgr.impl.wrapper;

import javax.annotation.Nonnull;

import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.state.IStoppable;
import com.helger.event.IEvent;
import com.helger.event.IEventObserver;
import com.helger.event.mgr.IUnicastEventManager;
import com.helger.event.mgr.IUnidirectionalEventManager;
import com.helger.event.sync.mgr.impl.UnidirectionalSynchronousUnicastEventManager;

public class WrappedUnidirectionalSynchronousUnicastEventManager implements IUnidirectionalEventManager, IUnicastEventManager, IStoppable
{
  private final UnidirectionalSynchronousUnicastEventManager m_aEvtMgr;

  public WrappedUnidirectionalSynchronousUnicastEventManager (@Nonnull final UnidirectionalSynchronousUnicastEventManager aEvtMgr)
  {
    if (aEvtMgr == null)
      throw new NullPointerException ("eventManager");
    m_aEvtMgr = aEvtMgr;
  }

  public void setObserver (@Nonnull final IEventObserver aObserver)
  {
    m_aEvtMgr.setObserver (aObserver);
  }

  public void trigger (@Nonnull final IEvent aEvent)
  {
    m_aEvtMgr.trigger (aEvent);
  }

  @Nonnull
  public final EChange stop ()
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
    final WrappedUnidirectionalSynchronousUnicastEventManager rhs = (WrappedUnidirectionalSynchronousUnicastEventManager) o;
    return m_aEvtMgr.equals (rhs.m_aEvtMgr);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aEvtMgr).getHashCode ();
  }
}
