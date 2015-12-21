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
package com.helger.event.observer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.event.IEvent;
import com.helger.event.IEventType;

public enum EEventObserverHandlerType
{
  /** We cannot handle this event. */
  CANNOT_HANDLE (false, false),

  /** We handle this event but have no return value. */
  HANDLE_NO_RETURN (true, false),

  /** We handle this event and return something. */
  HANDLE_RETURN_VALUE (true, true);

  private final boolean m_bIsHandling;
  private final boolean m_bHasReturnValue;

  private EEventObserverHandlerType (final boolean bIsHandling, final boolean bHasReturnValue)
  {
    m_bIsHandling = bIsHandling;
    m_bHasReturnValue = bHasReturnValue;
  }

  public boolean isHandling ()
  {
    return m_bIsHandling;
  }

  public boolean hasReturnValue ()
  {
    return m_bHasReturnValue;
  }

  /**
   * This method is just a shortcut to make the code more readable.
   *
   * @param aEvent
   *        Source event.
   * @param aEventType
   *        Expected event type.
   * @return {@link #HANDLE_NO_RETURN} or {@link #CANNOT_HANDLE}
   */
  @Nonnull
  public static EEventObserverHandlerType handleNoReturnIfMatches (@Nonnull final IEvent aEvent,
                                                                   @Nullable final IEventType aEventType)
  {
    return aEvent.getEventType ().equals (aEventType) ? HANDLE_NO_RETURN : CANNOT_HANDLE;
  }
}
