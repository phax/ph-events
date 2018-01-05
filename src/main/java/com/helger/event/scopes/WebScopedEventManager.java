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
package com.helger.event.scopes;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.event.IEvent;
import com.helger.event.observer.IEventObserver;
import com.helger.web.scope.IWebScope;
import com.helger.web.scope.mgr.EWebScope;

/**
 * Scope aware event manager for web scopes.
 *
 * @author Philip Helger
 */
public final class WebScopedEventManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WebScopedEventManager.class);
  private static final String ATTR_EVENT_MANAGER = WebScopedEventManager.class.getName ();

  private WebScopedEventManager ()
  {}

  @Nullable
  private static IWebScope _getScope (@Nonnull final EWebScope eScope, final boolean bCreateIfNotExisting)
  {
    try
    {
      return eScope.getScope (bCreateIfNotExisting);
    }
    catch (final RuntimeException ex)
    {
      if (bCreateIfNotExisting)
      {
        // Scope was required - rethrow
        throw ex;
      }
      return null;
    }
  }

  @Nullable
  private static InternalScopedEventManager _getEventMgr (@Nonnull final IWebScope aScope)
  {
    ValueEnforcer.notNull (aScope, "Scope");

    return aScope.attrs ().getCastedValue (ATTR_EVENT_MANAGER);
  }

  @Nonnull
  private static InternalScopedEventManager _getOrCreateEventMgr (@Nonnull final IWebScope aScope)
  {
    ValueEnforcer.notNull (aScope, "Scope");

    return (InternalScopedEventManager) aScope.attrs ().computeIfAbsent (ATTR_EVENT_MANAGER,
                                                                         k -> new InternalScopedEventManager ());
  }

  @Nonnull
  public static EChange registerObserver (@Nonnull final EWebScope eScope, final IEventObserver aObserver)
  {
    IWebScope aScope = _getScope (eScope, false);
    if (aScope == null)
    {
      s_aLogger.warn ("Creating scope of type " + eScope + " because of event observer registration");
      aScope = _getScope (eScope, true);
    }
    return registerObserver (aScope, aObserver);
  }

  @Nonnull
  public static EChange registerObserver (@Nonnull final IWebScope aScope, @Nonnull final IEventObserver aObserver)
  {
    return _getOrCreateEventMgr (aScope).registerObserver (aObserver);
  }

  @Nonnull
  public static EChange unregisterObserver (@Nonnull final EWebScope eScope, @Nonnull final IEventObserver aObserver)
  {
    final IWebScope aScope = _getScope (eScope, false);
    if (aScope != null)
    {
      final InternalScopedEventManager aEventMgr = _getEventMgr (aScope);
      if (aEventMgr != null)
        return aEventMgr.unregisterObserver (aObserver);
    }
    return EChange.UNCHANGED;
  }

  /**
   * Notify observers without sender and without parameter.
   *
   * @param aEvent
   *        The event on which observers should be notified.
   * @return The aggregated result object.
   */
  @Nullable
  public static Object triggerSynchronous (@Nonnull final IEvent aEvent)
  {
    ValueEnforcer.notNull (aEvent, "Event");

    final ICommonsList <Object> aRetValues = new CommonsArrayList <> ();
    // for all scopes
    for (final EWebScope eCurrentScope : EWebScope.values ())
    {
      // get current instance of scope
      final IWebScope aScope = _getScope (eCurrentScope, false);
      if (aScope != null)
      {
        // get event manager (may be null)
        final InternalScopedEventManager aEventMgr = _getEventMgr (aScope);
        if (aEventMgr != null)
        {
          // main event trigger
          aRetValues.add (aEventMgr.triggerSynchronous (aEvent));
        }
      }
    }
    return aEvent.getResultAggregator ().apply (aRetValues);
  }

  /**
   * Notify observers without sender and without parameter.
   *
   * @param aEvent
   *        The event on which observers should be notified.
   * @param aResultCallback
   *        Optional result callback
   */
  public static void triggerAsynchronous (@Nonnull final IEvent aEvent,
                                          @Nonnull final Consumer <Object> aResultCallback)
  {
    ValueEnforcer.notNull (aEvent, "Event");
    ValueEnforcer.notNull (aResultCallback, "ResultCallback");

    // for all scopes
    for (final EWebScope eCurrentScope : EWebScope.values ())
    {
      // get current instance of scope
      final IWebScope aScope = _getScope (eCurrentScope, false);
      if (aScope != null)
      {
        // get event manager (may be null)
        final InternalScopedEventManager aEventMgr = _getEventMgr (aScope);
        if (aEventMgr != null)
        {
          // main event trigger
          aEventMgr.triggerAsynchronous (aEvent, aResultCallback);
        }
      }
    }
  }
}
