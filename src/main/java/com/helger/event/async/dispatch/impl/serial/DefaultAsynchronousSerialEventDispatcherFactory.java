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
package com.helger.event.async.dispatch.impl.serial;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.factory.IFactory;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcher;
import com.helger.event.observer.IEventObservingExceptionCallback;

public class DefaultAsynchronousSerialEventDispatcherFactory implements IFactory <IAsynchronousEventDispatcher>
{
  private final IFactory <IAggregator <Object, ?>> m_aResultAggregateFactory;
  private final IEventObservingExceptionCallback m_aExceptionHandler;

  public DefaultAsynchronousSerialEventDispatcherFactory (@Nonnull final IFactory <IAggregator <Object, ?>> aResultAggregateFactory,
                                                          @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    ValueEnforcer.notNull (aResultAggregateFactory, "ResultAggregateFactory");
    m_aResultAggregateFactory = aResultAggregateFactory;
    m_aExceptionHandler = aExceptionHandler;
  }

  @Nonnull
  public IAsynchronousEventDispatcher get ()
  {
    return new AsynchronousSerialEventDispatcher (m_aResultAggregateFactory, m_aExceptionHandler);
  }
}
