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
package com.helger.event.dispatch.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.concurrent.IExecutorServiceFactory;
import com.helger.commons.concurrent.ManagedExecutorService;
import com.helger.commons.concurrent.SimpleLock;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.IEvent;
import com.helger.event.dispatch.AbstractEventDispatcher;
import com.helger.event.dispatch.EffectiveEventObserverList;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observer.exception.EventObservingExceptionWrapper;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;
import com.helger.event.observerqueue.IEventObserverQueue;

/**
 * Event dispatcher that spawns a thread for each event and each observer. So if
 * you have 2 events with 7 observers a total of 2 * 7 threads are spawned.
 *
 * @author Philip Helger
 */
public class AsynchronousParallelEventDispatcher extends AbstractEventDispatcher implements IAsynchronousEventDispatcher
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AsynchronousParallelEventDispatcher.class);
  private final SimpleLock m_aLock = new SimpleLock ();
  private final IExecutorServiceFactory m_aExecutorServiceFactory;

  public AsynchronousParallelEventDispatcher (@Nonnull final IExecutorServiceFactory aExecutorServiceFactory,
                                              @Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    super (aExceptionHandler);
    m_aExecutorServiceFactory = ValueEnforcer.notNull (aExecutorServiceFactory, "ExecutorServiceFactory");
  }

  public void dispatch (@Nonnull final IEvent aEvent,
                        @Nonnull final IEventObserverQueue aObservers,
                        @Nullable final INonThrowingRunnableWithParameter <Object> aOverallResultCallback)
  {
    ValueEnforcer.notNull (aEvent, "Event");
    ValueEnforcer.notNull (aObservers, "Observers");

    // find all observers that can handle the passed event
    final EffectiveEventObserverList aHandlingInfo = EffectiveEventObserverList.getListOfObserversThatCanHandleTheEvent (aEvent,
                                                                                                                         aObservers);

    final int nHandlingObserverCountWithReturnValue = aHandlingInfo.getHandlingObserverCountWithReturnValue ();
    final Map <IEventObserver, EEventObserverHandlerType> aHandlingObservers = aHandlingInfo.getObservers ();
    if (!aHandlingObservers.isEmpty ())
    {
      // At least one handler was found
      AsynchronousEventResultCollector aLocalResultCallback = null;
      if (nHandlingObserverCountWithReturnValue > 0)
      {
        // If we have handling observers, we need an overall result callback!
        if (aOverallResultCallback == null)
          throw new IllegalStateException ("Are you possibly using a unicast event manager and sending an event that has a return value?");

        // Create collector and start thread only if we expect a result
        aLocalResultCallback = new AsynchronousEventResultCollector (nHandlingObserverCountWithReturnValue,
                                                                     aEvent.getResultAggregator (),
                                                                     aOverallResultCallback);
        aLocalResultCallback.start ();
      }

      // Create the Callable's for the ExecutorService
      final List <Callable <Object>> aCallables = new ArrayList <> ();
      for (final Map.Entry <IEventObserver, EEventObserverHandlerType> aEntry : aHandlingObservers.entrySet ())
      {
        final IEventObserver aObserver = aEntry.getKey ();
        final INonThrowingRunnableWithParameter <Object> aLocalResult = aEntry.getValue ().hasReturnValue ()
                                                                                                             ? aLocalResultCallback
                                                                                                             : null;
        aCallables.add (Executors.callable ( () -> {
          try
          {
            aObserver.onEvent (aEvent, aLocalResult);
          }
          catch (final Throwable t)
          {
            getExceptionCallback ().handleObservingException (t);
            s_aLogger.error ("Failed to notify " + aObserver + " on " + aEvent, t);

            // Notify on exception
            if (aLocalResult != null)
            {
              // Put exception in result list
              aLocalResult.run (new EventObservingExceptionWrapper (aObserver, aEvent, t));
            }
          }
        }));
      }

      m_aLock.locked ( () -> {
        // Create a thread pool with at maximum the number of observers
        final ExecutorService aExecutor = m_aExecutorServiceFactory.getExecutorService (aHandlingObservers.size ());
        try
        {
          aExecutor.invokeAll (aCallables);
        }
        catch (final InterruptedException ex)
        {
          throw new RuntimeException (ex);
        }
        finally
        {
          ManagedExecutorService.shutdownAndWaitUntilAllTasksAreFinished (aExecutor, 20, TimeUnit.MILLISECONDS);
        }
      });
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("ExecutorServiceFactory", m_aExecutorServiceFactory)
                            .toString ();
  }
}
