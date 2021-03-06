/**
 * Copyright (C) 2015-2020 Philip Helger (www.helger.com)
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

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.event.EventTypeRegistry;
import com.helger.event.IEvent;
import com.helger.event.IEventType;
import com.helger.event.observer.AbstractEventObserver;

/**
 * Dummy observer.
 *
 * @author Philip Helger
 */
public final class MockCountingObserver extends AbstractEventObserver
{
  public static final IEventType TOPIC = EventTypeRegistry.createEventType ("mytopic");

  private int m_nInvokeCount = 0;

  public MockCountingObserver ()
  {
    super (false, TOPIC);
  }

  public void onEvent (@Nonnull final IEvent aEvent, @Nullable final Consumer <Object> aResultCallback)
  {
    assertEquals (aEvent.getEventType (), TOPIC);
    ++m_nInvokeCount;
  }

  public int getInvocationCount ()
  {
    return m_nInvokeCount;
  }
}
