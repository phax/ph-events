/**
 * Copyright (C) 2015-2016 Philip Helger (www.helger.com)
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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for events that have knowledge of their sender
 *
 * @author Philip Helger
 * @param <T>
 *        The type of the sender
 */
public class BaseSenderAwareEvent <T> extends BaseEvent implements ISenderAwareEvent <T>
{
  private final T m_aSender;

  public BaseSenderAwareEvent (@Nonnull final IEventType aEventType, @Nonnull final T aSender)
  {
    super (aEventType);

    m_aSender = ValueEnforcer.notNull (aSender, "Sender");
  }

  @Nonnull
  public final T getSender ()
  {
    return m_aSender;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final BaseSenderAwareEvent <?> rhs = (BaseSenderAwareEvent <?>) o;
    return m_aSender.equals (rhs.m_aSender);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aSender).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("sender", m_aSender).toString ();
  }
}
