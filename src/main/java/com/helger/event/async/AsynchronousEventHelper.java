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
package com.helger.event.async;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.concurrent.IExecutorServiceFactory;
import com.helger.commons.factory.IFactory;
import com.helger.event.dispatch.async.IAsynchronousEventDispatcher;
import com.helger.event.dispatch.async.parallel.AsynchronousParallelEventDispatcher;
import com.helger.event.dispatch.async.queue.AsynchronousQueueEventDispatcher;
import com.helger.event.dispatch.async.serial.AsynchronousSerialEventDispatcher;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.IEventObserverQueue;

public final class AsynchronousEventHelper
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
  public static IFactory <IAsynchronousEventDispatcher> createEventDispFactory (@Nonnull final IFactory <IAggregator <Object, ?>> aResultAggregateFactory,
                                                                                @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    ValueEnforcer.notNull (aResultAggregateFactory, "ResultAggregateFactory");

    // switch between parallel, serial and queue
    switch (_getDefaultDispatcherType ())
    {
      case QUEUE:
        return () -> new AsynchronousQueueEventDispatcher (aResultAggregateFactory, aExceptionHandler);
      case SERIAL:
        return () -> new AsynchronousSerialEventDispatcher (aResultAggregateFactory, aExceptionHandler);
      case PARALLEL:
        return () -> new AsynchronousParallelEventDispatcher (aResultAggregateFactory,
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
    return new UnidirectionalAsynchronousMulticastEventManager (IEventObserverQueue.createDefaultFactory (),
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
    return new BidirectionalAsynchronousMulticastEventManager (IEventObserverQueue.createDefaultFactory (),
                                                               createEventDispFactory (aFactory, aExceptionHandler));
  }
}
