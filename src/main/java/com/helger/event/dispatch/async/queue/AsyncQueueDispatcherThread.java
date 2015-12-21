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
package com.helger.event.dispatch.async.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.event.IEvent;
import com.helger.event.dispatch.async.AsynchronousEventResultCollector;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.EventObservingExceptionCallback;
import com.helger.event.observer.exception.EventObservingExceptionWrapper;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;

final class AsyncQueueDispatcherThread extends Thread
{
  private static final class Triple
  {
    private final IEvent m_aEvent;
    private final IEventObserver m_aEventObserver;
    private final AsynchronousEventResultCollector m_aCollector;

    public Triple (@Nonnull final IEvent aEvent,
                   @Nonnull final IEventObserver aEventObserver,
                   @Nullable final AsynchronousEventResultCollector aCollector)
    {
      m_aEvent = aEvent;
      m_aEventObserver = aEventObserver;
      m_aCollector = aCollector;
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (AsyncQueueDispatcherThread.class);
  private final BlockingQueue <Triple> m_aQueue = new LinkedBlockingQueue <> ();
  private final IEventObservingExceptionCallback m_aExceptionHandler;

  public AsyncQueueDispatcherThread (@Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    super ("async-queue-dispatcher-thread");
    m_aExceptionHandler = aExceptionHandler != null ? aExceptionHandler
                                                    : EventObservingExceptionCallback.getInstance ();
  }

  public void addToQueue (@Nonnull final IEvent aEvent,
                          @Nonnull final IEventObserver aObserver,
                          @Nullable final AsynchronousEventResultCollector aResultCollector)
  {
    try
    {
      m_aQueue.put (new Triple (aEvent, aObserver, aResultCollector));
    }
    catch (final InterruptedException ex)
    {
      s_aLogger.error ("Failed to add event to queue", ex);
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
        final Triple aElement = m_aQueue.take ();
        final IEvent aEvent = aElement.m_aEvent;
        final IEventObserver aObserver = aElement.m_aEventObserver;
        final AsynchronousEventResultCollector aCollector = aElement.m_aCollector;

        try
        {
          // main dispatch
          aObserver.onEvent (aEvent, aCollector);
        }
        catch (final Throwable t)
        {
          m_aExceptionHandler.handleObservingException (t);
          s_aLogger.error ("Failed to notify " + aObserver + " on " + aEvent, t);

          // Notify on exception
          if (aCollector != null)
          {
            // Put exception in result list
            aCollector.run (new EventObservingExceptionWrapper (aObserver, aEvent, t));
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
