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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.exception.mock.MockRuntimeException;
import com.helger.event.BaseEvent;
import com.helger.event.EventTypeRegistry;
import com.helger.event.IEvent;
import com.helger.event.IEventType;
import com.helger.event.observer.AbstractEventObserver;

public final class SynchronousEventHelperTest
{
  private static IAggregator <Object, ?> RES_AGG = IAggregator.createUseAll ();
  private static final IEventType EV_TYPE = EventTypeRegistry.createEventType (SynchronousEventHelperTest.class.getName ());
  private static final Logger s_aLogger = LoggerFactory.getLogger (SynchronousEventHelperTest.class);

  @Test
  public void testUnidirectionalUnicastEventManager ()
  {
    final UnidirectionalSynchronousUnicastEventManager mgr = SynchronousEventHelper.createUnidirectionalUnicastEventManager ();
    mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
        s_aLogger.info ("onEvent uni sync");
      }
    });
    mgr.trigger (new BaseEvent (EV_TYPE));
  }

  @Test
  public void testUnidirectionalMulticastEventManager ()
  {
    final UnidirectionalSynchronousMulticastEventManager mgr = SynchronousEventHelper.createUnidirectionalMulticastEventManager ();
    mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
        s_aLogger.info ("onEvent multi sync 1");
      }
    });
    mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
        s_aLogger.info ("onEvent multi sync 2");
      }
    });
    mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNotNull (aResultCallback);
        throw new MockRuntimeException ();
      }
    });
    mgr.trigger (new BaseEvent (EV_TYPE));
  }

  @Test
  public void testBidirectionalMulticastEventManager ()
  {
    final BidirectionalSynchronousMulticastEventManager mgr = SynchronousEventHelper.createBidirectionalMulticastEventManager (RES_AGG);
    mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
        s_aLogger.info ("onEvent multi sync 1");
      }
    });
    mgr.registerObserver (new AbstractEventObserver (false, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
        s_aLogger.info ("onEvent multi sync 2");
      }
    });
    mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNotNull (aResultCallback);
        assertEquals (EV_TYPE, aEvent.getEventType ());
        s_aLogger.info ("onEvent multi sync 3");
        aResultCallback.run ("My return value");
      }
    });
    mgr.registerObserver (new AbstractEventObserver (true, EV_TYPE)
    {
      public void onEvent (@Nonnull final IEvent aEvent,
                           @Nullable final INonThrowingRunnableWithParameter <Object> aResultCallback)
      {
        assertNotNull (aResultCallback);
        throw new MockRuntimeException ();
      }
    });
    s_aLogger.info ("Trigger result = " + mgr.trigger (new BaseEvent (EV_TYPE)));
  }
}
