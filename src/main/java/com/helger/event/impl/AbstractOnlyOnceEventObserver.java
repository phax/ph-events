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
package com.helger.event.impl;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.DevelopersNote;
import com.helger.event.IEventType;
import com.helger.event.IOnlyOnceEventObserver;

/**
 * Abstract base class for a simple event observer that can be handled only
 * once.
 *
 * @author Philip Helger
 */
public abstract class AbstractOnlyOnceEventObserver extends AbstractEventObserver implements IOnlyOnceEventObserver
{
  @Deprecated
  @DevelopersNote ("Just to avoid the instantiation of the below constructor without an event type.")
  protected AbstractOnlyOnceEventObserver (final boolean bWithReturnValue)
  {
    super (bWithReturnValue);
  }

  public AbstractOnlyOnceEventObserver (final boolean bWithReturnValue, @Nonnull final IEventType... aHandledEventTypes)
  {
    super (bWithReturnValue, aHandledEventTypes);
  }

  public AbstractOnlyOnceEventObserver (final boolean bWithReturnValue,
                                        @Nonnull final Iterable <IEventType> aHandledEventTypes)
  {
    super (bWithReturnValue, aHandledEventTypes);
  }
}
