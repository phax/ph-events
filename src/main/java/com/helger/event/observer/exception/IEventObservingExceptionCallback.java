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
package com.helger.event.observer.exception;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.callback.ICallback;

/**
 * Implement this interface to instruct an event dispatcher what to do with
 * exceptions thrown by event observers.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IEventObservingExceptionCallback extends ICallback
{
  /**
   * Handle the thrown exception. Logging is performed automatically!
   *
   * @param aThrowable
   *        The thrown exception. May not be <code>null</code>.
   */
  void handleObservingException (@Nonnull Throwable aThrowable);
}
