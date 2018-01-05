/**
 * Copyright (C) 2015-2018 Philip Helger (www.helger.com)
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
package com.helger.event.dispatch.async;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.exception.mock.IMockException;
import com.helger.commons.state.ESuccess;
import com.helger.event.IEvent;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.EventObservingExceptionWrapper;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;

/**
 * This thread class is instantiated once in {@link AsynchronousEventDispatcher}
 * and manages the asynchronous dispatching of the events.
 *
 * @author Philip Helger
 */
final class AsyncQueueDispatcherThread extends Thread
{
  private static final class EventItem
  {
    private final IEvent m_aEvent;
    private final IEventObserver m_aEventObserver;
    private final AsynchronousEventResultCollectorThread m_aCollector;

    public EventItem (@Nonnull final IEvent aEvent,
                      @Nonnull final IEventObserver aEventObserver,
                      @Nullable final AsynchronousEventResultCollectorThread aCollector)
    {
      m_aEvent = aEvent;
      m_aEventObserver = aEventObserver;
      m_aCollector = aCollector;
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (AsyncQueueDispatcherThread.class);
  private final BlockingQueue <EventItem> m_aEventQueue = new LinkedBlockingQueue <> ();
  private final IEventObservingExceptionCallback m_aExceptionCallback;

  public AsyncQueueDispatcherThread (@Nonnull final IEventObservingExceptionCallback aExceptionCallback)
  {
    super ("async-queue-dispatcher-thread");
    m_aExceptionCallback = aExceptionCallback;
  }

  @Nonnull
  public ESuccess addEventToQueue (@Nonnull final IEvent aEvent,
                                   @Nonnull final IEventObserver aObserver,
                                   @Nullable final AsynchronousEventResultCollectorThread aResultCollector)
  {
    try
    {
      m_aEventQueue.put (new EventItem (aEvent, aObserver, aResultCollector));
      return ESuccess.SUCCESS;
    }
    catch (final InterruptedException ex)
    {
      s_aLogger.error ("Failed to add event to queue", ex);
      return ESuccess.FAILURE;
    }
  }

  @Override
  public void run ()
  {
    try
    {
      while (!isInterrupted ())
      {
        // get current element
        final EventItem aItem = m_aEventQueue.take ();
        final IEvent aEvent = aItem.m_aEvent;
        final IEventObserver aEventObserver = aItem.m_aEventObserver;
        final AsynchronousEventResultCollectorThread aCollector = aItem.m_aCollector;

        try
        {
          // main dispatch
          aEventObserver.onEvent (aEvent, aCollector);
        }
        catch (final Throwable t)
        {
          m_aExceptionCallback.handleObservingException (t);
          s_aLogger.error ("Failed to asynchronously notify " +
                           aEventObserver +
                           " on " +
                           aEvent +
                           " because of " +
                           t.getClass ().getName (),
                           t instanceof IMockException ? null : t);

          // Notify on exception
          if (aCollector != null)
          {
            // Put exception in result consumer
            aCollector.accept (new EventObservingExceptionWrapper (aEventObserver, aEvent, t));
          }
        }
      }
    }
    catch (final InterruptedException ex)
    {
      // OK, gracefully stopped
      if (false)
        s_aLogger.error ("Failed to execute queued event dispatcher thread", ex);
    }
  }
}
