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
package com.helger.event.observer.exception;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEvent;

public final class EventObservingExceptionWrapper
{
  private final String m_sMessage;
  private final IEvent m_aEvent;
  private final Throwable m_aThrowable;

  public EventObservingExceptionWrapper (@Nonnull final Object aObserver,
                                         @Nonnull final IEvent aEvent,
                                         @Nonnull final Throwable aThrowable)
  {
    ValueEnforcer.notNull (aObserver, "Observer");
    ValueEnforcer.notNull (aEvent, "Event");
    ValueEnforcer.notNull (aThrowable, "Throwable");

    m_sMessage = "Failed to notify " + aObserver;
    m_aEvent = aEvent;
    m_aThrowable = aThrowable;
  }

  @Nonnull
  @Nonempty
  public String getMessage ()
  {
    return m_sMessage;
  }

  @Nonnull
  public IEvent getEvent ()
  {
    return m_aEvent;
  }

  @Nonnull
  public Throwable getThrowable ()
  {
    return m_aThrowable;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Message", m_sMessage)
                                       .append ("Event", m_aEvent)
                                       .append ("Throwable", m_aThrowable)
                                       .getToString ();
  }
}
