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
package com.helger.event.async.mgr.impl;

import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.event.IEvent;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcherFactory;
import com.helger.event.mgr.IUnidirectionalEventManager;

public class UnidirectionalAsynchronousUnicastEventManager extends AbstractAsynchronousUnicastEventManager implements
                                                                                                          IUnidirectionalEventManager
{
  public UnidirectionalAsynchronousUnicastEventManager (final IAsynchronousEventDispatcherFactory aEventDispatcherFactory)
  {
    super (aEventDispatcherFactory);
  }

  public void trigger (final IEvent aEvent)
  {
    if (!m_aObserver.isEmpty ())
    {
      m_aObserver.beforeDispatch ();
      m_aEventDispatcher.dispatch (aEvent, m_aObserver, null);
      m_aObserver.afterDispatch ();
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof UnidirectionalAsynchronousUnicastEventManager))
      return false;
    final UnidirectionalAsynchronousUnicastEventManager rhs = (UnidirectionalAsynchronousUnicastEventManager) o;
    return m_aObserver.equals (rhs.m_aObserver) && m_aEventDispatcher.equals (rhs.m_aEventDispatcher);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aObserver).append (m_aEventDispatcher).getHashCode ();
  }
}
