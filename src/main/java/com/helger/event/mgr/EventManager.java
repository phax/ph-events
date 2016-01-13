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

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EChange;
import com.helger.event.IEvent;
import com.helger.event.dispatch.async.AsynchronousQueueEventDispatcher;
import com.helger.event.dispatch.async.IAsynchronousEventDispatcher;
import com.helger.event.dispatch.sync.ISynchronousEventDispatcher;
import com.helger.event.dispatch.sync.SynchronousEventDispatcher;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.EventObserverQueueOrderedSet;
import com.helger.event.observerqueue.IEventObserverQueue;

public class EventManager implements IEventManager
{
  private final IEventObserverQueue m_aObserverQueue;
  private final ISynchronousEventDispatcher m_aSyncEventDispatcher;
  private final IAsynchronousEventDispatcher m_aAsyncEventDispatcher;

  public EventManager ()
  {
    this (null);
  }

  public EventManager (@Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    this (new EventObserverQueueOrderedSet (),
          new SynchronousEventDispatcher (aExceptionHandler),
          new AsynchronousQueueEventDispatcher (aExceptionHandler));
  }

  public EventManager (@Nonnull final IEventObserverQueue aObserverQueue,
                       @Nonnull final ISynchronousEventDispatcher aSyncEventDispatcher,
                       @Nonnull final IAsynchronousEventDispatcher aAsyncEventDispatcher)
  {
    ValueEnforcer.notNull (aObserverQueue, "ObserverQueue");
    ValueEnforcer.notNull (aSyncEventDispatcher, "SyncEventDispatcher");
    ValueEnforcer.notNull (aAsyncEventDispatcher, "AsyncEventDispatcher");

    m_aObserverQueue = aObserverQueue;
    m_aSyncEventDispatcher = aSyncEventDispatcher;
    m_aAsyncEventDispatcher = aAsyncEventDispatcher;
  }

  @Nonnull
  protected final IEventObserverQueue getObserverQueue ()
  {
    return m_aObserverQueue;
  }

  @Nonnull
  protected final ISynchronousEventDispatcher getSyncEventDispatcher ()
  {
    return m_aSyncEventDispatcher;
  }

  @Nonnull
  protected final IAsynchronousEventDispatcher getAsyncEventDispatcher ()
  {
    return m_aAsyncEventDispatcher;
  }

  @Nonnull
  public final EChange registerObserver (@Nonnull final IEventObserver aObserver)
  {
    return m_aObserverQueue.addObserver (aObserver);
  }

  @Nonnull
  public final EChange unregisterObserver (@Nonnull final IEventObserver aObserver)
  {
    return m_aObserverQueue.removeObserver (aObserver);
  }

  @Nonnull
  public final EChange stop ()
  {
    EChange eChange = m_aSyncEventDispatcher.stop ();
    eChange = eChange.or (m_aAsyncEventDispatcher.stop ());
    return eChange;
  }

  @Nullable
  public Object triggerSynchronous (@Nonnull final IEvent aEvent)
  {
    // Default return value is null
    Object ret = null;
    final IEventObserverQueue aObserverQueue = getObserverQueue ();
    if (!aObserverQueue.isEmpty ())
    {
      // At least one observer is present
      aObserverQueue.beforeDispatch ();
      ret = getSyncEventDispatcher ().dispatch (aEvent, aObserverQueue);
      aObserverQueue.afterDispatch ();
    }
    return ret;
  }

  public void triggerAsynchronous (@Nonnull final IEvent aEvent,
                                   @Nonnull final Consumer <Object> aOverallResultConsumer)
  {
    final IEventObserverQueue aObserverQueue = getObserverQueue ();
    if (!aObserverQueue.isEmpty ())
    {
      // At least one observer is present
      aObserverQueue.beforeDispatch ();
      getAsyncEventDispatcher ().dispatch (aEvent, aObserverQueue, aOverallResultConsumer);
      aObserverQueue.afterDispatch ();
    }
  }
}
