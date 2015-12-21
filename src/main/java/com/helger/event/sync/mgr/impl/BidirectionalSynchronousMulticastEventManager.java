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
package com.helger.event.sync.mgr.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.factory.IFactory;
import com.helger.event.IEvent;
import com.helger.event.mgr.IBidirectionalSynchronousEventManager;
import com.helger.event.observerqueue.IEventObserverQueue;
import com.helger.event.sync.dispatch.ISynchronousEventDispatcher;

public class BidirectionalSynchronousMulticastEventManager extends AbstractSynchronousMulticastEventManager
                                                           implements IBidirectionalSynchronousEventManager
{
  public BidirectionalSynchronousMulticastEventManager (@Nonnull final IFactory <IEventObserverQueue> aObserverQueueFactory,
                                                        @Nonnull final IFactory <ISynchronousEventDispatcher> aEventDispatcherFactory)
  {
    super (aObserverQueueFactory, aEventDispatcherFactory);
  }

  @Nullable
  public Object trigger (@Nonnull final IEvent aEvent)
  {
    if (m_aObserverQueue.isEmpty ())
      return null;

    m_aObserverQueue.beforeDispatch ();
    final Object ret = m_aEventDispatcher.dispatch (aEvent, m_aObserverQueue);
    m_aObserverQueue.afterDispatch ();

    return ret;
  }
}
