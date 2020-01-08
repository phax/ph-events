/**
 * Copyright (C) 2015-2020 Philip Helger (www.helger.com)
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
package com.helger.event;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;

/**
 * Event type registry. Keeps all event types, and ensures, that no event type
 * name is used more than once.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class EventTypeRegistry
{
  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static final ICommonsMap <String, EventType> s_aMap = new CommonsHashMap <> ();

  private EventTypeRegistry ()
  {}

  @Nonnull
  public static IEventType createEventType (@Nonnull @Nonempty final String sName)
  {
    return s_aRWLock.writeLocked ( () -> {
      if (s_aMap.containsKey (sName))
        throw new IllegalArgumentException ("An event type with the name '" + sName + "' already exists!");

      final EventType aEventType = new EventType (sName);
      s_aMap.put (sName, aEventType);
      return aEventType;
    });
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsMap <String, ? extends IEventType> getAllEventTypes ()
  {
    return s_aRWLock.readLocked ( () -> s_aMap.getClone ());
  }
}
