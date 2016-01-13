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
package com.helger.event.mgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.exception.mock.MockRuntimeException;
import com.helger.event.BaseEvent;
import com.helger.event.EventTypeRegistry;
import com.helger.event.IEvent;
import com.helger.event.IEventType;
import com.helger.event.observer.AbstractEventObserver;

public final class AsyncFuncTest
{
  private static final IEventType EV_TYPE = EventTypeRegistry.createEventType (AsyncFuncTest.class.getName ());
  private static final Logger s_aLogger = LoggerFactory.getLogger (AsyncFuncTest.class);

  @Test
  public void testUnidirectionalUnicastEventManager ()
  {
    final EventManager mgr = new EventManager ();
    mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
    {
      public void onEvent (final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
      }
    });
    mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), c -> {});
  }

  @Test
  public void testBidirectionalUnicastEventManager () throws InterruptedException
  {
    final CountDownLatch aCountDown = new CountDownLatch (1);
    final EventManager mgr = new EventManager ();
    mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
    {
      public void onEvent (final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNotNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
        aResultCallback.run ("onEvent called!");
        aCountDown.countDown ();
      }
    });
    final Consumer <Object> aOverallCB = currentObject -> s_aLogger.info ("Got: " + currentObject);
    mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), aOverallCB);
    aCountDown.await ();

    // Try triggering the event that throws an exception
    final CountDownLatch aCountDown2 = new CountDownLatch (1);
    mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        aCountDown2.countDown ();
        throw new MockRuntimeException ();
      }
    });
    mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), aOverallCB);
    aCountDown2.await ();
  }

  @Test
  public void testBidirectionalMulticastEventManager () throws InterruptedException
  {
    final int EXECUTIONS = 100000;
    final CountDownLatch aCountDown = new CountDownLatch (EXECUTIONS);
    final EventManager mgr = new EventManager ();
    for (int i = 0; i < EXECUTIONS; ++i)
      mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
      {
        public void onEvent (@Nonnull final IEvent aEvent,
                             @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
        {
          // Ensure we're called for the correct event type
          assertNotNull (aEvent);
          assertEquals (EV_TYPE, aEvent.getEventType ());

          // Check that the callback for the result is present
          assertNotNull (aResultCallback);

          aResultCallback.run ("onEvent1 called!");
          aCountDown.countDown ();
        }
      });

    final Consumer <Object> aOverallCB = currentObject -> s_aLogger.info ("Got: " +
                                                                          ((List <?>) currentObject).size () +
                                                                          " results");
    mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), aOverallCB);
    aCountDown.await ();
  }

  @Test
  public void testUnidirectionalUnicastEventManagerMultiple ()
  {
    final EventManager mgr = new EventManager ();
    mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
      }
    });
    for (int i = 0; i < 100; ++i)
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), c -> {});
  }

  private static class MockAsyncObserver extends AbstractEventObserver
  {
    private final String m_sText;

    public MockAsyncObserver (final String sText)
    {
      super (true, EV_TYPE);
      m_sText = sText;
    }

    public void onEvent (@Nonnull final IEvent aEvent,
                         @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
    {
      aResultCallback.run (m_sText);
    }
  }

  private static class MockAsyncObserverOnlyOnce extends MockAsyncObserver
  {
    public MockAsyncObserverOnlyOnce (final String sText)
    {
      super (sText);
    }

    public boolean isOnlyOnce ()
    {
      return true;
    }
  }

  @Test
  public void testBidirectionalMulticastEventManagerOnlyOnce ()
  {
    final EventManager mgr = new EventManager ();
    mgr.registerObserver (new MockAsyncObserver ("Hallo"));
    mgr.registerObserver (new MockAsyncObserverOnlyOnce ("Welt"));
    // trigger for the first time
    mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), currentObject -> {
      assertTrue (currentObject instanceof List <?>);
      // -> expect 2 results
      assertEquals (2, ((List <?>) currentObject).size ());
      s_aLogger.info ("1. Got: " + currentObject);
    });

    // trigger for the second time
    mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), currentObject -> {
      assertTrue (currentObject instanceof List <?>);
      // -> expect 1 result
      assertEquals (1, ((List <?>) currentObject).size ());
      s_aLogger.info ("2. Got: " + currentObject);
    });

    // trigger for the third time
    mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), currentObject -> {
      assertTrue (currentObject instanceof List <?>);
      // -> expect 1 result
      assertEquals (1, ((List <?>) currentObject).size ());
      s_aLogger.info ("3. Got: " + currentObject);
    });
  }
}
