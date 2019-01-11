/**
 * Copyright (C) 2015-2019 Philip Helger (www.helger.com)
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
package com.helger.event.observer;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.event.IEvent;
import com.helger.event.IEventType;

/**
 * Abstract base class for a simple event observer.
 *
 * @author Philip Helger
 */
public abstract class AbstractEventObserver implements IEventObserver
{
  private final EEventObserverHandlerType m_eHandlerType;
  private final ICommonsSet <IEventType> m_aHandledEventTypes;

  /**
   * @param bWithReturnValue
   *        does not matter
   */
  @Deprecated
  @DevelopersNote ("Just to avoid the instantiation of the below constructor without an event type.")
  @UnsupportedOperation
  protected AbstractEventObserver (final boolean bWithReturnValue)
  {
    throw new UnsupportedOperationException ("No event type passed!");
  }

  public AbstractEventObserver (final boolean bWithReturnValue, @Nonnull final IEventType... aHandledEventTypes)
  {
    ValueEnforcer.notEmptyNoNullValue (aHandledEventTypes, "HandledEventTypes");

    m_eHandlerType = bWithReturnValue ? EEventObserverHandlerType.HANDLE_RETURN_VALUE
                                      : EEventObserverHandlerType.HANDLE_NO_RETURN;
    m_aHandledEventTypes = new CommonsHashSet <> (aHandledEventTypes);
  }

  public AbstractEventObserver (final boolean bWithReturnValue,
                                @Nonnull final Iterable <? extends IEventType> aHandledEventTypes)
  {
    ValueEnforcer.notEmptyNoNullValue (aHandledEventTypes, "HandledEventTypes");

    m_eHandlerType = bWithReturnValue ? EEventObserverHandlerType.HANDLE_RETURN_VALUE
                                      : EEventObserverHandlerType.HANDLE_NO_RETURN;
    m_aHandledEventTypes = new CommonsHashSet <> (aHandledEventTypes);
  }

  @Nonnull
  public final EEventObserverHandlerType canHandleEvent (@Nonnull final IEvent aEvent)
  {
    return m_aHandledEventTypes.contains (aEvent.getEventType ()) ? m_eHandlerType
                                                                  : EEventObserverHandlerType.CANNOT_HANDLE;
  }
}
