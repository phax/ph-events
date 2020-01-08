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
package com.helger.event.mgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.concurrent.ThreadHelper;
import com.helger.commons.exception.mock.MockRuntimeException;
import com.helger.event.BaseEvent;
import com.helger.event.EventTypeRegistry;
import com.helger.event.IEvent;
import com.helger.event.IEventType;
import com.helger.event.observer.AbstractEventObserver;

public final class AsyncFuncTest
{
  private static class MockObserverMultiple extends AbstractEventObserver
  {
    private final String m_sText;

    public MockObserverMultiple (final String sText)
    {
      super (true, EV_TYPE);
      m_sText = sText;
    }

    public void onEvent (@Nonnull final IEvent aEvent, @Nullable final Consumer <Object> aResultCallback)
    {
      if (aResultCallback != null)
        aResultCallback.accept (m_sText);
    }
  }

  private static class MockObserverOnlyOnce extends MockObserverMultiple
  {
    public MockObserverOnlyOnce (final String sText)
    {
      super (sText);
    }

    public boolean isOnlyOnce ()
    {
      return true;
    }
  }

  private static final IEventType EV_TYPE = EventTypeRegistry.createEventType (AsyncFuncTest.class.getName ());
  private static final Logger LOGGER = LoggerFactory.getLogger (AsyncFuncTest.class);

  @Test
  public void testUnidirectionalUnicastEventManager ()
  {
    try (final EventManager mgr = new EventManager ())
    {
      mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
      {
        public void onEvent (final IEvent aEvent, @Nullable final Consumer <Object> aResultCallback)
        {
          assertNull (aResultCallback);
          assertEquals (EV_TYPE, aEvent.getEventType ());
        }
      });
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), c -> {});
    }
  }

  @Test
  public void testBidirectionalUnicastEventManager () throws InterruptedException
  {
    try (final EventManager mgr = new EventManager ())
    {
      final CountDownLatch aCountDown = new CountDownLatch (1);
      mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
      {
        public void onEvent (final IEvent aEvent, @Nullable final Consumer <Object> aResultCallback)
        {
          assertNotNull (aResultCallback);
          assertEquals (EV_TYPE, aEvent.getEventType ());
          aResultCallback.accept ("onEvent called!");
          aCountDown.countDown ();
        }
      });
      final Consumer <Object> aOverallCB = currentObject -> LOGGER.info ("Got: " + currentObject);
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), aOverallCB);
      aCountDown.await ();

      // Try triggering the event that throws an exception
      final CountDownLatch aCountDown2 = new CountDownLatch (1);
      mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
      {
        public void onEvent (@Nonnull final IEvent aEvent, @Nullable final Consumer <Object> aResultCallback)
        {
          aCountDown2.countDown ();
          throw new MockRuntimeException ();
        }
      });
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), aOverallCB);
      aCountDown2.await ();
    }
  }

  @Test
  public void testBidirectionalMulticastEventManager () throws InterruptedException
  {
    final int EXECUTIONS = 100000;
    final CountDownLatch aCountDown = new CountDownLatch (EXECUTIONS);

    try (final EventManager mgr = new EventManager ())
    {
      for (int i = 0; i < EXECUTIONS; ++i)
        mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
        {
          public void onEvent (@Nonnull final IEvent aEvent, @Nullable final Consumer <Object> aResultCallback)
          {
            // Ensure we're called for the correct event type
            assertNotNull (aEvent);
            assertEquals (EV_TYPE, aEvent.getEventType ());

            // Check that the callback for the result is present
            assertNotNull (aResultCallback);

            aResultCallback.accept ("onEvent1 called!");
            aCountDown.countDown ();
          }
        });

      final Consumer <Object> aOverallCB = currentObject -> LOGGER.info ("Got: " +
                                                                         ((List <?>) currentObject).size () +
                                                                         " results");
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE, x -> x), aOverallCB);
      aCountDown.await ();
    }
  }

  @Test
  public void testAsyncanagerMultipleObservers ()
  {
    try (final EventManager mgr = new EventManager ())
    {
      final AtomicInteger aInvocationCount = new AtomicInteger (0);
      mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
      {
        public void onEvent (@Nonnull final IEvent aEvent, @Nullable final Consumer <Object> aResultCallback)
        {
          assertNull (aResultCallback);
          assertEquals (EV_TYPE, aEvent.getEventType ());
          aInvocationCount.incrementAndGet ();
        }
      });
      final int nMax = 200;
      for (int i = 0; i < nMax; ++i)
        mgr.triggerAsynchronous (new BaseEvent (EV_TYPE), c -> {});
      ThreadHelper.sleep (100);
      assertEquals ("You may need to increase the sleep length", nMax, aInvocationCount.get ());
    }
  }

  @Test
  public void testAsyncOnlyOnce ()
  {
    try (final EventManager mgr = new EventManager ())
    {
      mgr.registerObserver (new MockObserverMultiple ("Hallo"));
      mgr.registerObserver (new MockObserverOnlyOnce ("Welt"));

      // trigger for the first time
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE, x -> x), currentObject -> {
        assertTrue (currentObject instanceof List <?>);
        // -> expect 2 results
        assertEquals (2, ((List <?>) currentObject).size ());
        LOGGER.info ("1. Got: " + currentObject);
      });

      // trigger for the second time - the OnlyOnce should not be contained
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE, x -> x), currentObject -> {
        assertTrue (currentObject instanceof List <?>);
        // -> expect 1 result
        assertEquals (1, ((List <?>) currentObject).size ());
        LOGGER.info ("2. Got: " + currentObject);
      });

      // trigger for the third time
      mgr.triggerAsynchronous (new BaseEvent (EV_TYPE, x -> x), currentObject -> {
        assertTrue (currentObject instanceof List <?>);
        // -> expect 1 result
        assertEquals (1, ((List <?>) currentObject).size ());
        LOGGER.info ("3. Got: " + currentObject);
      });
    }
  }
}
