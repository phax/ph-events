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
package com.helger.event.scopes;

import javax.annotation.Nonnull;

import com.helger.commons.scope.IScope;
import com.helger.commons.scope.IScopeDestructionAware;
import com.helger.event.impl.helper.SynchronousEventHelper;
import com.helger.event.observerqueue.IEventObserverQueue;
import com.helger.event.resultaggregator.impl.DispatchResultAggregatorBooleanAnd;
import com.helger.event.sync.mgr.impl.BidirectionalSynchronousMulticastEventManager;

/**
 * Wraps the main event manager so that it becomes scope destruction aware
 *
 * @author Philip Helger
 */
final class MainEventManager extends BidirectionalSynchronousMulticastEventManager implements IScopeDestructionAware
{
  public MainEventManager ()
  {
    super (IEventObserverQueue.createDefaultFactory (),
           SynchronousEventHelper.createSynchronousEventDispatcherFactory ( () -> new DispatchResultAggregatorBooleanAnd (),
                                                                            new ScopedEventObservingExceptionCallback ()));
  }

  public void onBeforeScopeDestruction (@Nonnull final IScope aScopeToBeDestroyed) throws Exception
  {}

  public void onScopeDestruction (@Nonnull final IScope aScopeInDestruction) throws Exception
  {
    // Stop the event manager
    stop ();
  }
}
