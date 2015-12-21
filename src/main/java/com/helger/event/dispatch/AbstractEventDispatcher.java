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
package com.helger.event.dispatch;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.factory.IFactory;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

@Immutable
public abstract class AbstractEventDispatcher implements IEventDispatcher
{
  private final IAggregator <Object, ?> m_aResultAggregator;

  public AbstractEventDispatcher (@Nonnull final IFactory <IAggregator <Object, ?>> aResultAggregatorFactory)
  {
    ValueEnforcer.notNull (aResultAggregatorFactory, "ResultAggregatorFactory");

    m_aResultAggregator = aResultAggregatorFactory.get ();
    if (m_aResultAggregator == null)
      throw new IllegalArgumentException ("No dispatch result aggregator was created");
  }

  @Nonnull
  protected final IAggregator <Object, ?> getResultAggregator ()
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
    final AbstractEventDispatcher rhs = (AbstractEventDispatcher) o;
    return m_aResultAggregator.equals (rhs.m_aResultAggregator);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aResultAggregator).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ResultAggregator", m_aResultAggregator).toString ();
  }
}
