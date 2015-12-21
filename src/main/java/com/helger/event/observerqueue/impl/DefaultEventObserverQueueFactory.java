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
package com.helger.event.observerqueue.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.string.ToStringGenerator;
import com.helger.event.observerqueue.IEventObserverQueue;
import com.helger.event.observerqueue.IEventObserverQueueFactory;

/**
 * Default implementation of {@link IEventObserverQueueFactory} always returning
 * an {@link EventObserverQueueOrderedSet}.
 *
 * @author Philip Helger
 */
@Immutable
public final class DefaultEventObserverQueueFactory implements IEventObserverQueueFactory
{
  private static final DefaultEventObserverQueueFactory s_aInstance = new DefaultEventObserverQueueFactory ();

  private DefaultEventObserverQueueFactory ()
  {}

  @Nonnull
  public static DefaultEventObserverQueueFactory getInstance ()
  {
    return s_aInstance;
  }

  @Nonnull
  public IEventObserverQueue get ()
  {
    // By default a non-weak set is used, because observers are quite regular
    // inline classes which tend to be garbage collected very easily!
    return new EventObserverQueueOrderedSet ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
