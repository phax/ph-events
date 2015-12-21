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
package com.helger.event.crud;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.EventTypeRegistry;
import com.helger.event.IEventType;

/**
 * Default implementation if the {@link ICRUDEventType} interface.
 *
 * @author Philip Helger
 */
@Immutable
public final class CRUDEventType implements ICRUDEventType
{
  private final IEventType m_aEventType;
  private final EEventPointInTime m_ePointInTime;
  private final EEventCRUD m_eCRUD;

  public CRUDEventType (@Nonnull @Nonempty final String sBaseName,
                        @Nonnull final EEventPointInTime ePointInTime,
                        @Nonnull final EEventCRUD eCRUD)
  {
    ValueEnforcer.notNull (ePointInTime, "PointInTime");
    ValueEnforcer.notNull (eCRUD, "CRUD");
    // Ensure that the name is unique!
    final String sEventName = sBaseName + '.' + ePointInTime.getID () + '.' + eCRUD.getID ();
    m_aEventType = EventTypeRegistry.createEventType (sEventName);
    m_ePointInTime = ePointInTime;
    m_eCRUD = eCRUD;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_aEventType.getName ();
  }

  @Nonnull
  public EEventPointInTime getPointInTime ()
  {
    return m_ePointInTime;
  }

  @Nonnull
  public EEventCRUD getCRUD ()
  {
    return m_eCRUD;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final CRUDEventType rhs = (CRUDEventType) o;
    return m_aEventType.equals (rhs.m_aEventType) &&
           m_ePointInTime.equals (rhs.m_ePointInTime) &&
           m_eCRUD.equals (rhs.m_eCRUD);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aEventType).append (m_ePointInTime).append (m_eCRUD).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("name", getName ())
                                       .append ("pointInTime", m_ePointInTime)
                                       .append ("CRUD", m_eCRUD)
                                       .toString ();
  }
}
