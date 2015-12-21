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
import javax.annotation.Nullable;

import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.event.IEvent;
import com.helger.event.dispatch.sync.ISynchronousEventDispatcher;
import com.helger.event.observerqueue.IEventObserverQueue;

public class SynchronousEventManager extends AbstractSynchronousEventManager
{
  public SynchronousEventManager (@Nonnull final IEventObserverQueue aObserverQueue,
                                                        @Nonnull final ISynchronousEventDispatcher aEventDispatcher)
  {
    super (aObserverQueue, aEventDispatcher);
  }

  @Nullable
  public void trigger (@Nonnull final IEvent aEvent,
                       @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
  {
    Object ret = null;
    final IEventObserverQueue aObserverQueue = getObserverQueue ();
    if (!aObserverQueue.isEmpty ())
    {
      aObserverQueue.beforeDispatch ();
      ret = getEventDispatcher ().dispatch (aEvent, aObserverQueue);
      aObserverQueue.afterDispatch ();
    }

    if (aResultCallback != null)
      aResultCallback.run (ret);
  }
}
