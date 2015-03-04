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
package com.helger.event.async.dispatch.impl.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collections.triple.IReadonlyTriple;
import com.helger.commons.collections.triple.ReadonlyTriple;
import com.helger.event.IEvent;
import com.helger.event.IEventObserver;
import com.helger.event.IEventObservingExceptionHandler;
import com.helger.event.async.dispatch.impl.AsynchronousEventResultCollector;
import com.helger.event.impl.EventObservingExceptionHandler;
import com.helger.event.impl.EventObservingExceptionWrapper;

final class AsyncQueueDispatcherThread extends Thread
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AsyncQueueDispatcherThread.class);
  private final BlockingQueue <IReadonlyTriple <IEvent, IEventObserver, AsynchronousEventResultCollector>> m_aQueue;
  private final IEventObservingExceptionHandler m_aExceptionHandler;

  public AsyncQueueDispatcherThread (@Nullable final IEventObservingExceptionHandler aExceptionHandler)
  {
    super ("async-queue-dispatcher-thread");
    m_aQueue = new LinkedBlockingQueue <IReadonlyTriple <IEvent, IEventObserver, AsynchronousEventResultCollector>> ();
    m_aExceptionHandler = aExceptionHandler != null ? aExceptionHandler : EventObservingExceptionHandler.getInstance ();
  }

  public void addToQueue (final IEvent aEvent,
                          final IEventObserver aObserver,
                          final AsynchronousEventResultCollector aResultCollector)
  {
    try
    {
      m_aQueue.put (ReadonlyTriple.create (aEvent, aObserver, aResultCollector));
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
        final IReadonlyTriple <IEvent, IEventObserver, AsynchronousEventResultCollector> aElement = m_aQueue.take ();
        final IEvent aEvent = aElement.getFirst ();
        final IEventObserver aObserver = aElement.getSecond ();
        final AsynchronousEventResultCollector aCollector = aElement.getThird ();

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
