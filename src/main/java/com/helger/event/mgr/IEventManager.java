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
package com.helger.event.mgr;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;
import com.helger.commons.state.IStoppable;
import com.helger.event.IEvent;
import com.helger.event.observer.IEventObserver;

/**
 * Base interface for a all event managers.
 *
 * @author Philip Helger
 */
public interface IEventManager extends IStoppable
{
  /**
   * Register an additional observer.
   *
   * @param aObserver
   *        The observer to be registered. May not be <code>null</code>.
   * @return {@link EChange}
   * @see com.helger.event.observerqueue.IEventObserverQueue
   */
  @Nonnull
  EChange registerObserver (@Nonnull IEventObserver aObserver);

  /**
   * Unregister an existing observer.
   *
   * @param aObserver
   *        The observer to be unregistered. May not be <code>null</code>.
   * @return {@link EChange}
   * @see com.helger.event.observerqueue.IEventObserverQueue
   */
  @Nonnull
  EChange unregisterObserver (@Nonnull IEventObserver aObserver);

  @Nullable
  Object triggerSynchronous (@Nonnull IEvent aEvent);

  void triggerAsynchronous (@Nonnull IEvent aEvent, @Nonnull Consumer <Object> aResultConsumer);
}
