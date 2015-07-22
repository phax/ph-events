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
package com.helger.event.scopes;

import com.helger.commons.exception.InitializationException;
import com.helger.event.impl.EventObservingExceptionCallback;

/**
 * Specialized exception handler
 *
 * @author Philip Helger
 */
final class ScopedEventObservingExceptionCallback extends EventObservingExceptionCallback
{
  @Override
  public void handleObservingException (final Throwable aThrowable)
  {
    // don't catch these exceptions:
    if (aThrowable instanceof InitializationException)
      throw (InitializationException) aThrowable;

    // Pass through!
    super.handleObservingException (aThrowable);
  }
}