/**
 * Copyright (C) 2015-2018 Philip Helger (www.helger.com)
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
package com.helger.event.dispatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.ToStringGenerator;
import com.helger.event.observer.exception.EventObservingExceptionCallback;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;

public abstract class AbstractEventDispatcher implements IEventDispatcher
{
  private final IEventObservingExceptionCallback m_aExceptionCallback;

  public AbstractEventDispatcher (@Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    m_aExceptionCallback = aExceptionHandler != null ? aExceptionHandler : new EventObservingExceptionCallback ();
  }

  @Nonnull
  protected final IEventObservingExceptionCallback getExceptionCallback ()
  {
    return m_aExceptionCallback;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ExceptionCallback", m_aExceptionCallback).getToString ();
  }
}
