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

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.factory.IFactory;
import com.helger.commons.state.EChange;
import com.helger.commons.state.IStoppable;
import com.helger.event.IEventObserver;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcher;
import com.helger.event.mgr.IUnicastEventManager;
import com.helger.event.observerqueue.impl.EventObserverQueueSingleElement;

/**
 * Abstract base class for asynchronous unicast event managers.
 *
 * @author Philip Helger
 */
public abstract class AbstractAsynchronousUnicastEventManager implements IUnicastEventManager, IStoppable
{
  protected final IAsynchronousEventDispatcher m_aEventDispatcher;
  protected EventObserverQueueSingleElement m_aObserver;

  public AbstractAsynchronousUnicastEventManager (@Nonnull final IFactory <IAsynchronousEventDispatcher> aEventDispatcherFactory)
  {
    ValueEnforcer.notNull (aEventDispatcherFactory, "EventDispatcherFactory");

    m_aEventDispatcher = aEventDispatcherFactory.get ();
    if (m_aEventDispatcher == null)
      throw new IllegalStateException ("An illegal event dispatcher was created");

    m_aObserver = new EventObserverQueueSingleElement ();
  }

  public final void setObserver (@Nonnull final IEventObserver aObserver)
  {
    m_aObserver.addObserver (aObserver);
  }

  @Nonnull
  public final EChange stop ()
  {
    return m_aEventDispatcher.stop ();
  }
}
