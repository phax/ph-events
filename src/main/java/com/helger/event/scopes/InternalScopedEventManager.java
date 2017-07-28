/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
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

import com.helger.event.mgr.EventManager;
import com.helger.scope.IScope;
import com.helger.scope.IScopeDestructionAware;

/**
 * Wraps the main event manager so that it becomes scope destruction aware
 *
 * @author Philip Helger
 */
final class InternalScopedEventManager extends EventManager implements IScopeDestructionAware
{
  public InternalScopedEventManager ()
  {
    super (new ScopedEventObservingExceptionCallback ());
  }

  public void onScopeDestruction (@Nonnull final IScope aScopeInDestruction) throws Exception
  {
    // Stop the event manager
    close ();
  }
}
