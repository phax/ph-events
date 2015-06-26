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
package com.helger.event.async.dispatch.impl.parallel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.concurrent.IExecutorServiceFactory;
import com.helger.event.IAggregatorFactory;
import com.helger.event.IEventObservingExceptionHandler;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcher;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcherFactory;

public class DefaultAsynchronousParallelEventDispatcherFactory implements IAsynchronousEventDispatcherFactory
{
  private final IAggregatorFactory <Object, Object> m_aResultAggregateFactory;
  private final IExecutorServiceFactory m_aExecutorServiceFactory;
  private final IEventObservingExceptionHandler m_aExceptionHandler;

  public DefaultAsynchronousParallelEventDispatcherFactory (@Nonnull final IAggregatorFactory <Object, Object> aResultAggregateFactory,
                                                            @Nonnull final IExecutorServiceFactory aExecutorServiceFactory,
                                                            @Nullable final IEventObservingExceptionHandler aExceptionHandler)
  {
    if (aResultAggregateFactory == null)
      throw new NullPointerException ("resultAggregatorFactory");
    if (aExecutorServiceFactory == null)
      throw new NullPointerException ("executorServiceFactory");

    m_aResultAggregateFactory = aResultAggregateFactory;
    m_aExecutorServiceFactory = aExecutorServiceFactory;
    m_aExceptionHandler = aExceptionHandler;
  }

  @Nonnull
  public IAsynchronousEventDispatcher create ()
  {
    return new AsynchronousParallelEventDispatcher (m_aResultAggregateFactory,
                                                    m_aExecutorServiceFactory,
                                                    m_aExceptionHandler);
  }
}
