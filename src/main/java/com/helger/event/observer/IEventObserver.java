/**
 * Copyright (C) 2015-2019 Philip Helger (www.helger.com)
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
package com.helger.event.observer;

import java.io.Serializable;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.event.IEvent;

/**
 * Base interface of event observers.
 *
 * @author Philip Helger
 */
public interface IEventObserver extends Serializable
{
  /**
   * Identify observers that are interested only in a single observation.
   *
   * @return <code>true</code> if this observer is to be invoked only once,
   *         <code>false</code> otherwise.
   */
  default boolean isOnlyOnce ()
  {
    return false;
  }

  /**
   * Determine whether we can handle this event or not. This method is always
   * called synchronously.
   *
   * @param aEvent
   *        The event to be checked. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  EEventObserverHandlerType canHandleEvent (@Nonnull IEvent aEvent);

  /**
   * Main event handling routine. This routine is only called if the call to
   * {@link #canHandleEvent(IEvent)} did not return
   * {@link EEventObserverHandlerType#CANNOT_HANDLE}.
   *
   * @param aEvent
   *        The event to be handled. May not be <code>null</code>.
   * @param aResultConsumer
   *        The callback to which the result needs to be delivered. May be
   *        <code>null</code> if the dispatcher is unidirectional.
   * @throws Exception
   *         on error
   */
  void onEvent (@Nonnull IEvent aEvent, @Nullable Consumer <Object> aResultConsumer) throws Exception;
}
