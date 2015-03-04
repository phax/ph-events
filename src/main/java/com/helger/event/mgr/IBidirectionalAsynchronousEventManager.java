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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.event.IEvent;

/**
 * Interface for a bidirectional, asynchronous event manager. Bidirectional
 * means that the event observer can provide a result of the event handling.
 * Asynchronous means, that the results cannot be determined synchronously.
 *
 * @author Philip Helger
 */
public interface IBidirectionalAsynchronousEventManager
{
  void trigger (@Nonnull IEvent aEvent, @Nullable INonThrowingRunnableWithParameter <Object> aResultCallback);
}
