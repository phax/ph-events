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
package com.helger.event.resultaggregator.impl;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.AbstractAggregator;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.event.impl.EventObservingExceptionWrapper;

/**
 * Aggregate a list of Boolean result values by combining them with a logical
 * OR.
 *
 * @author Philip Helger
 */
public final class DispatchResultAggregatorBooleanOr extends AbstractAggregator <Object, Object>
{
  public DispatchResultAggregatorBooleanOr ()
  {}

  @Nonnull
  public Boolean aggregate (final Collection <Object> aResults)
  {
    ValueEnforcer.notNull (aResults, "Results");

    boolean bResult = false;
    for (final Object aResult : aResults)
      if (!(aResult instanceof EventObservingExceptionWrapper))
      {
        bResult = bResult || ((Boolean) aResult).booleanValue ();

        // No need to continue calculation :)
        if (bResult)
          break;
      }
    return Boolean.valueOf (bResult);
  }

  @Override
  public boolean equals (final Object o)
  {
    return o == this || o instanceof DispatchResultAggregatorBooleanOr;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }
}
