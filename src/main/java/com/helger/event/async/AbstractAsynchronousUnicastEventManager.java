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
package com.helger.event.async;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EChange;
import com.helger.event.dispatch.async.IAsynchronousEventDispatcher;
import com.helger.event.mgr.IUnicastEventManager;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observerqueue.EventObserverQueueSingleElement;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Abstract base class for asynchronous unicast event managers.
 *
 * @author Philip Helger
 */
public abstract class AbstractAsynchronousUnicastEventManager implements IUnicastEventManager
{
  private final IEventObserverQueue m_aObserverQueue = new EventObserverQueueSingleElement ();
  private final IAsynchronousEventDispatcher m_aEventDispatcher;

  public AbstractAsynchronousUnicastEventManager (@Nonnull final IAsynchronousEventDispatcher aEventDispatcher)
  {
    ValueEnforcer.notNull (aEventDispatcher, "EventDispatcher");

    m_aEventDispatcher = aEventDispatcher;
  }

  @Nonnull
  protected final IEventObserverQueue getObserverQueue ()
  {
    return m_aObserverQueue;
  }

  @Nonnull
  protected final IAsynchronousEventDispatcher getEventDispatcher ()
  {
    return m_aEventDispatcher;
  }

  public final void setObserver (@Nonnull final IEventObserver aObserver)
  {
    m_aObserverQueue.addObserver (aObserver);
  }

  @Nonnull
  public final EChange stop ()
  {
    return m_aEventDispatcher.stop ();
  }
}
