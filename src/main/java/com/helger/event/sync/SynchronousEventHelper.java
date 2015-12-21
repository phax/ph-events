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
package com.helger.event.sync;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.aggregate.IAggregator;
import com.helger.event.dispatch.sync.ISynchronousEventDispatcher;
import com.helger.event.dispatch.sync.SynchronousEventDispatcher;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.EventObserverQueueSingleElement;
import com.helger.event.observerqueue.IEventObserverQueue;

@Immutable
public final class SynchronousEventHelper
{
  private SynchronousEventHelper ()
  {}

  @Nonnull
  public static ISynchronousEventDispatcher createSynchronousEventDispatcher (@Nonnull final IAggregator <Object, ?> aResultAggregator,
                                                                              @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    return new SynchronousEventDispatcher (aResultAggregator, aExceptionHandler);
  }

  @Nonnull
  public static ISynchronousEventDispatcher createSynchronousEventDispatcher ()
  {
    return createSynchronousEventDispatcher (IAggregator.createUseFirst (), null);
  }

  @Nonnull
  public static SynchronousEventManager createUnidirectionalUnicastEventManager ()
  {
    return new SynchronousEventManager (new EventObserverQueueSingleElement (), createSynchronousEventDispatcher ());
  }

  @Nonnull
  public static SynchronousEventManager createUnidirectionalMulticastEventManager ()
  {
    return new SynchronousEventManager (IEventObserverQueue.createDefault (), createSynchronousEventDispatcher ());
  }

  @Nonnull
  public static SynchronousEventManager createBidirectionalMulticastEventManager (@Nonnull final IAggregator <Object, ?> aResultAggregator)
  {
    return createBidirectionalMulticastEventManager (aResultAggregator, null);
  }

  @Nonnull
  public static SynchronousEventManager createBidirectionalMulticastEventManager (@Nonnull final IAggregator <Object, ?> aResultAggregator,
                                                                                  @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    return new SynchronousEventManager (IEventObserverQueue.createDefault (),
                                        createSynchronousEventDispatcher (aResultAggregator, aExceptionHandler));
  }
}
