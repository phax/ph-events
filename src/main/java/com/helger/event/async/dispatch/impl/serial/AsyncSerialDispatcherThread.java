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
package com.helger.event.async.dispatch.impl.serial;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.event.EEventObserverHandlerType;
import com.helger.event.IEvent;
import com.helger.event.IEventObserver;
import com.helger.event.IEventObservingExceptionHandler;
import com.helger.event.async.dispatch.impl.AsynchronousEventResultCollector;
import com.helger.event.impl.EventObservingExceptionHandler;
import com.helger.event.impl.EventObservingExceptionWrapper;

final class AsyncSerialDispatcherThread extends Thread
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AsyncSerialDispatcherThread.class);
  private final IEvent m_aEvent;
  private final Map <IEventObserver, EEventObserverHandlerType> m_aHandlingObservers;
  private final AsynchronousEventResultCollector m_aLocalResultCallback;
  private final IEventObservingExceptionHandler m_aExceptionHandler;

  AsyncSerialDispatcherThread (@Nonnull final IEvent aEvent,
                               @Nonnull final Map <IEventObserver, EEventObserverHandlerType> aHandlingObservers,
                               final AsynchronousEventResultCollector aLocalResultCallback,
                               @Nullable final IEventObservingExceptionHandler aExceptionHandler)
  {
    super ("serial-event-dispatcher-thread");
    if (aEvent == null)
      throw new NullPointerException ("event");
    if (aHandlingObservers == null)
      throw new NullPointerException ("handlingObservers");
    m_aEvent = aEvent;
    m_aHandlingObservers = aHandlingObservers;
    m_aLocalResultCallback = aLocalResultCallback;
    m_aExceptionHandler = aExceptionHandler != null ? aExceptionHandler : EventObservingExceptionHandler.getInstance ();
  }

  private void _callSingleObserver (final IEventObserver aObserver, final boolean bHasReturnValue)
  {
    try
    {
      aObserver.onEvent (m_aEvent, bHasReturnValue ? m_aLocalResultCallback : null);
    }
    catch (final Throwable t)
    {
      m_aExceptionHandler.handleObservingException (t);
      s_aLogger.error ("Failed to notify " + aObserver + " on " + m_aEvent, t);

      if (bHasReturnValue && m_aLocalResultCallback != null)
      {
        m_aLocalResultCallback.run (new EventObservingExceptionWrapper (aObserver, m_aEvent, t));
      }
    }
  }

  @Override
  public void run ()
  {
    // Iterate all handling observers
    for (final Map.Entry <IEventObserver, EEventObserverHandlerType> aEntry : m_aHandlingObservers.entrySet ())
      _callSingleObserver (aEntry.getKey (), aEntry.getValue ().hasReturnValue ());
  }
}
