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
import javax.annotation.Nullable;

import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.factory.IFactory;
import com.helger.event.IEvent;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcher;
import com.helger.event.mgr.IBidirectionalAsynchronousEventManager;
import com.helger.event.observerqueue.IEventObserverQueue;

public class BidirectionalAsynchronousMulticastEventManager extends AbstractAsynchronousMulticastEventManager
                                                            implements IBidirectionalAsynchronousEventManager
{
  public BidirectionalAsynchronousMulticastEventManager (final IFactory <? extends IEventObserverQueue> aObserverQueueFactory,
                                                         final IFactory <? extends IAsynchronousEventDispatcher> aEventDispatcherFactory)
  {
    super (aObserverQueueFactory, aEventDispatcherFactory);
  }

  public void trigger (@Nonnull final IEvent aEvent,
                       @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
  {
    if (!m_aObserverQueue.isEmpty ())
    {
      m_aObserverQueue.beforeDispatch ();
      m_aEventDispatcher.dispatch (aEvent, m_aObserverQueue, aResultCallback);
      m_aObserverQueue.afterDispatch ();
    }
  }
}
