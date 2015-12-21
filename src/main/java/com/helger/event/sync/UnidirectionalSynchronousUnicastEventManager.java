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
package com.helger.event.sync;

import javax.annotation.Nonnull;

import com.helger.event.IEvent;
import com.helger.event.dispatch.sync.ISynchronousEventDispatcher;
import com.helger.event.mgr.IUnidirectionalEventManager;
import com.helger.event.observerqueue.IEventObserverQueue;

public class UnidirectionalSynchronousUnicastEventManager extends AbstractSynchronousUnicastEventManager
                                                          implements IUnidirectionalEventManager
{
  public UnidirectionalSynchronousUnicastEventManager (final ISynchronousEventDispatcher aEventDispatcher)
  {
    super (aEventDispatcher);
  }

  public void trigger (@Nonnull final IEvent aEvent)
  {
    final IEventObserverQueue aObserverQueue = getObserverQueue ();
    if (!aObserverQueue.isEmpty ())
    {
      aObserverQueue.beforeDispatch ();
      getEventDispatcher ().dispatch (aEvent, aObserverQueue);
      aObserverQueue.afterDispatch ();
    }
  }
}
