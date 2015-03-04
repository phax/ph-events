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

import static org.junit.Assert.assertEquals;

import javax.annotation.Nullable;

import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.event.IEvent;
import com.helger.event.IEventType;
import com.helger.event.impl.AbstractEventObserver;
import com.helger.event.impl.EventTypeRegistry;

/**
 * Dummy observer.
 * 
 * @author philip
 */
public final class MockCountingObserver extends AbstractEventObserver
{
  public static final IEventType TOPIC = EventTypeRegistry.createEventType ("mytopic");

  private int m_nInvokeCount = 0;

  public MockCountingObserver ()
  {
    super (false, TOPIC);
  }

  public void onEvent (final IEvent aEvent, @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
  {
    assertEquals (aEvent.getEventType (), TOPIC);
    ++m_nInvokeCount;
  }

  public int getInvocationCount ()
  {
    return m_nInvokeCount;
  }
}
