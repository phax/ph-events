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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEventType;

/**
 * Default implementation if the {@link IEventType} interface.
 *
 * @author Philip Helger
 */
@Immutable
final class EventType implements IEventType
{
  private final String m_sName;

  public EventType (@Nonnull @Nonempty final String sName)
  {
    if (StringHelper.hasNoText (sName))
      throw new IllegalArgumentException ("Passed event type name is empty");
    m_sName = sName;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof EventType))
      return false;
    final EventType rhs = (EventType) o;
    return m_sName.equals (rhs.m_sName);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sName).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("name", m_sName).toString ();
  }
}
