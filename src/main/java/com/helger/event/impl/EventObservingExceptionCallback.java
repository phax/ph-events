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
package com.helger.event.impl;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.event.IEventObserverExceptionAware;
import com.helger.event.IEventObservingExceptionCallback;

/**
 * Utility class that knows what to with an exception that is thrown while
 * performing an event.
 *
 * @author Philip Helger
 */
@Immutable
public class EventObservingExceptionCallback implements IEventObservingExceptionCallback
{
  private static final IEventObservingExceptionCallback s_aInstance = new EventObservingExceptionCallback ();

  protected EventObservingExceptionCallback ()
  {}

  @Nonnull
  public static IEventObservingExceptionCallback getInstance ()
  {
    return s_aInstance;
  }

  @OverridingMethodsMustInvokeSuper
  public void handleObservingException (@Nonnull final Throwable aThrowable)
  {
    ValueEnforcer.notNull (aThrowable, "Throwable");

    // is it a "pass through" exception?
    if (aThrowable instanceof IEventObserverExceptionAware)
    {
      // encapsulate exception
      if (aThrowable instanceof RuntimeException)
        throw (RuntimeException) aThrowable;
      throw new RuntimeException ("Wrapped exception from event", aThrowable);
    }
  }
}