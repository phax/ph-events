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

import com.helger.commons.factory.IFactory;
import com.helger.event.IEvent;
import com.helger.event.dispatch.async.IAsynchronousEventDispatcher;
import com.helger.event.mgr.IUnidirectionalEventManager;
import com.helger.event.observerqueue.IEventObserverQueue;

public class UnidirectionalAsynchronousMulticastEventManager extends AbstractAsynchronousMulticastEventManager
                                                             implements IUnidirectionalEventManager
{
  public UnidirectionalAsynchronousMulticastEventManager (final IFactory <? extends IEventObserverQueue> aObserverQueueFactory,
                                                          final IFactory <? extends IAsynchronousEventDispatcher> aEventDispatcherFactory)
  {
    super (aObserverQueueFactory, aEventDispatcherFactory);
  }

  public void trigger (@Nonnull final IEvent aEvent)
  {
    final IEventObserverQueue aObserverQueue = getObserverQueue ();
    if (!aObserverQueue.isEmpty ())
    {
      aObserverQueue.beforeDispatch ();
      getEventDispatcher ().dispatch (aEvent, aObserverQueue, null);
      aObserverQueue.afterDispatch ();
    }
  }
}
