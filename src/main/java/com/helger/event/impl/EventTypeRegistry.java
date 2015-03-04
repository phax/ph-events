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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.event.IEventType;

/**
 * Event type registry. Keeps all event types, and ensures, that no event type
 * name is used more than once.
 *
 * @author Philip Helger
 */
public final class EventTypeRegistry
{
  private static final Map <String, IEventType> s_aMap = new HashMap <String, IEventType> ();

  private EventTypeRegistry ()
  {}

  @Nonnull
  public static IEventType createEventType (@Nonnull @Nonempty final String sName)
  {
    if (s_aMap.containsKey (sName))
      throw new IllegalArgumentException ("An event type with the name '" + sName + "' already exists!");
    final IEventType aEventType = new EventType (sName);
    s_aMap.put (sName, aEventType);
    return aEventType;
  }
}
