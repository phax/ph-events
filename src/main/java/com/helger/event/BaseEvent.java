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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IEvent} interface that only takes an
 * event type and has no additional parameters.
 *
 * @author Philip Helger
 */
@Immutable
public class BaseEvent implements IEvent
{
  private final IEventType m_aEventType;
  private final IAggregator <Object, ?> m_aResultAggregator;

  /**
   * Constructor using only the first provided result.
   *
   * @param aEventType
   *        Event type to use.
   */
  public BaseEvent (@Nonnull final IEventType aEventType)
  {
    this (aEventType, x -> CollectionHelper.getFirstElement (x));
  }

  /**
   * Constructor
   *
   * @param aEventType
   *        Event type to use. May not be <code>null</code>.
   * @param aResultAggregator
   *        Result aggregator. Aggregates the results of all observers. May not
   *        be <code>null</code>.
   */
  public BaseEvent (@Nonnull final IEventType aEventType, @Nonnull final IAggregator <Object, ?> aResultAggregator)
  {
    m_aEventType = ValueEnforcer.notNull (aEventType, "EventType");
    m_aResultAggregator = ValueEnforcer.notNull (aResultAggregator, "ResultAggregator");
  }

  @Nonnull
  public final IEventType getEventType ()
  {
    return m_aEventType;
  }

  @Nonnull
  public final IAggregator <Object, ?> getResultAggregator ()
  {
    return m_aResultAggregator;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BaseEvent rhs = (BaseEvent) o;
    return m_aEventType.equals (rhs.getEventType ());
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aEventType).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("EventType", m_aEventType).getToString ();
  }
}
