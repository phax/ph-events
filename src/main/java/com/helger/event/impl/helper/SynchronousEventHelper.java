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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.factory.IFactory;
import com.helger.event.IEventObservingExceptionCallback;
import com.helger.event.sync.dispatch.ISynchronousEventDispatcherFactory;
import com.helger.event.sync.dispatch.impl.DefaultSynchronousEventDispatcherFactory;
import com.helger.event.sync.mgr.impl.BidirectionalSynchronousMulticastEventManager;
import com.helger.event.sync.mgr.impl.BidirectionalSynchronousUnicastEventManager;
import com.helger.event.sync.mgr.impl.UnidirectionalSynchronousMulticastEventManager;
import com.helger.event.sync.mgr.impl.UnidirectionalSynchronousUnicastEventManager;

@Immutable
public final class SynchronousEventHelper extends AbstractEventHelper
{
  private SynchronousEventHelper ()
  {}

  @Nonnull
  public static ISynchronousEventDispatcherFactory createSynchronousEventDispatcherFactory (@Nonnull final IFactory <IAggregator <Object, ?>> aFactory,
                                                                                            @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    return new DefaultSynchronousEventDispatcherFactory (aFactory, aExceptionHandler);
  }

  @Nonnull
  public static ISynchronousEventDispatcherFactory createSynchronousEventDispatcherFactory ()
  {
    return createSynchronousEventDispatcherFactory ( () -> IAggregator.createUseFirst (), null);
  }

  @Nonnull
  public static UnidirectionalSynchronousUnicastEventManager createUnidirectionalUnicastEventManager ()
  {
    // No need for aggregation here
    return new UnidirectionalSynchronousUnicastEventManager (createSynchronousEventDispatcherFactory ());
  }

  @Nonnull
  public static BidirectionalSynchronousUnicastEventManager createBidirectionalUnicastEventManager ()
  {
    // No need for aggregation here
    return new BidirectionalSynchronousUnicastEventManager (createSynchronousEventDispatcherFactory ());
  }

  @Nonnull
  public static UnidirectionalSynchronousMulticastEventManager createUnidirectionalMulticastEventManager ()
  {
    return new UnidirectionalSynchronousMulticastEventManager (getObserverQueueFactory (),
                                                               createSynchronousEventDispatcherFactory ());
  }

  @Nonnull
  public static BidirectionalSynchronousMulticastEventManager createBidirectionalMulticastEventManager (@Nonnull final IFactory <IAggregator <Object, ?>> aFactory)
  {
    return createBidirectionalMulticastEventManager (aFactory, null);
  }

  @Nonnull
  public static BidirectionalSynchronousMulticastEventManager createBidirectionalMulticastEventManager (@Nonnull final IFactory <IAggregator <Object, ?>> aFactory,
                                                                                                        @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    return new BidirectionalSynchronousMulticastEventManager (getObserverQueueFactory (),
                                                              createSynchronousEventDispatcherFactory (aFactory,
                                                                                                       aExceptionHandler));
  }
}
