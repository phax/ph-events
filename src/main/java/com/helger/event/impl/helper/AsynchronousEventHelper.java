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
package com.helger.event.impl.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.concurrent.IExecutorServiceFactory;
import com.helger.commons.factory.IFactory;
import com.helger.event.IEventObservingExceptionCallback;
import com.helger.event.async.dispatch.IAsynchronousEventDispatcherFactory;
import com.helger.event.async.dispatch.impl.parallel.DefaultAsynchronousParallelEventDispatcherFactory;
import com.helger.event.async.dispatch.impl.queue.DefaultAsynchronousQueueEventDispatcherFactory;
import com.helger.event.async.dispatch.impl.serial.DefaultAsynchronousSerialEventDispatcherFactory;
import com.helger.event.async.impl.NewThreadPoolExecutorServiceFactory;
import com.helger.event.async.mgr.impl.BidirectionalAsynchronousMulticastEventManager;
import com.helger.event.async.mgr.impl.BidirectionalAsynchronousUnicastEventManager;
import com.helger.event.async.mgr.impl.UnidirectionalAsynchronousMulticastEventManager;
import com.helger.event.async.mgr.impl.UnidirectionalAsynchronousUnicastEventManager;

public final class AsynchronousEventHelper extends AbstractEventHelper
{
  private enum EAsyncDispatcher
  {
    QUEUE,
    SERIAL,
    PARALLEL;
  }

  private AsynchronousEventHelper ()
  {}

  @Nonnull
  public static IExecutorServiceFactory createExecutorServiceFactory ()
  {
    return new NewThreadPoolExecutorServiceFactory (10);
  }

  @Nonnull
  private static EAsyncDispatcher _getDefaultDispatcherType ()
  {
    return EAsyncDispatcher.QUEUE;
  }

  @Nonnull
  public static IAsynchronousEventDispatcherFactory createEventDispFactory (@Nonnull final IFactory <IAggregator <Object, ?>> aFactory,
                                                                            @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    // switch between parallel, serial and queue
    switch (_getDefaultDispatcherType ())
    {
      case QUEUE:
        return new DefaultAsynchronousQueueEventDispatcherFactory (aFactory, aExceptionHandler);
      case SERIAL:
        return new DefaultAsynchronousSerialEventDispatcherFactory (aFactory, aExceptionHandler);
      case PARALLEL:
        return new DefaultAsynchronousParallelEventDispatcherFactory (aFactory,
                                                                      createExecutorServiceFactory (),
                                                                      aExceptionHandler);
      default:
        throw new IllegalStateException ("Illegal event dispatcher type!");
    }
  }

  @Nonnull
  public static UnidirectionalAsynchronousUnicastEventManager createUnidirectionalUnicastEventManager ()
  {
    // No need for aggregation here
    return new UnidirectionalAsynchronousUnicastEventManager (createEventDispFactory ( () -> IAggregator.createUseFirst (),
                                                                                       null));
  }

  @Nonnull
  public static BidirectionalAsynchronousUnicastEventManager createBidirectionalUnicastEventManager ()
  {
    return new BidirectionalAsynchronousUnicastEventManager (createEventDispFactory ( () -> IAggregator.createUseFirst (),
                                                                                      null));
  }

  @Nonnull
  public static UnidirectionalAsynchronousMulticastEventManager createUnidirectionalMulticastEventManager ()
  {
    // No need for aggregation here
    return new UnidirectionalAsynchronousMulticastEventManager (getObserverQueueFactory (),
                                                                createEventDispFactory ( () -> IAggregator.createUseFirst (),
                                                                                         null));
  }

  @Nonnull
  public static BidirectionalAsynchronousMulticastEventManager createBidirectionalMulticastEventManager (@Nonnull final IFactory <IAggregator <Object, ?>> aFactory)
  {
    return createBidirectionalMulticastEventManager (aFactory, null);
  }

  @Nonnull
  public static BidirectionalAsynchronousMulticastEventManager createBidirectionalMulticastEventManager (@Nonnull final IFactory <IAggregator <Object, ?>> aFactory,
                                                                                                         @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    return new BidirectionalAsynchronousMulticastEventManager (getObserverQueueFactory (),
                                                               createEventDispFactory (aFactory, aExceptionHandler));
  }
}
