/**
 * Copyright (C) 2015-2016 Philip Helger (www.helger.com)
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
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.scope.IScope;
import com.helger.commons.scope.mgr.EScope;
import com.helger.commons.state.EChange;
import com.helger.event.IEvent;
import com.helger.event.observer.IEventObserver;

/**
 * Scope aware event manager for non-web scopes.
 *
 * @author Philip Helger
 */
public final class ScopedEventManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopedEventManager.class);
  private static final String ATTR_EVENT_MANAGER = ScopedEventManager.class.getName ();

  private ScopedEventManager ()
  {}

  @Nullable
  private static IScope _getScope (@Nonnull final EScope eScope, final boolean bCreateIfNotExisting)
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
  private static InternalScopedEventManager _getEventMgr (@Nonnull final IScope aScope)
  {
    ValueEnforcer.notNull (aScope, "Scope");

    return aScope.getCastedAttribute (ATTR_EVENT_MANAGER);
  }

  @Nonnull
  private static InternalScopedEventManager _getOrCreateEventMgr (@Nonnull final IScope aScope)
  {
    ValueEnforcer.notNull (aScope, "Scope");

    // Does the scope already contain an event manager?
    InternalScopedEventManager aEventMgr = _getEventMgr (aScope);
    if (aEventMgr == null)
    {
      // Build the event manager
      aEventMgr = new InternalScopedEventManager ();

      // put it in scope and register the cleanup handler
      aScope.setAttribute (ATTR_EVENT_MANAGER, aEventMgr);
    }
    return aEventMgr;
  }

  @Nonnull
  public static EChange registerObserver (@Nonnull final EScope eScope, final IEventObserver aObserver)
  {
    IScope aScope = _getScope (eScope, false);
    if (aScope == null)
    {
      s_aLogger.warn ("Creating scope of type " + eScope + " because of event observer registration");
      aScope = _getScope (eScope, true);
    }
    return registerObserver (aScope, aObserver);
  }

  @Nonnull
  public static EChange registerObserver (@Nonnull final IScope aScope, @Nonnull final IEventObserver aObserver)
  {
    return _getOrCreateEventMgr (aScope).registerObserver (aObserver);
  }

  @Nonnull
  public static EChange unregisterObserver (@Nonnull final EScope eScope, @Nonnull final IEventObserver aObserver)
  {
    final IScope aScope = _getScope (eScope, false);
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

    final ICommonsList <Object> aRetValues = new CommonsArrayList<> ();
    // for all scopes
    for (final EScope eCurrentScope : EScope.values ())
    {
      // get current instance of scope
      final IScope aScope = _getScope (eCurrentScope, false);
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
    for (final EScope eCurrentScope : EScope.values ())
    {
      // get current instance of scope
      final IScope aScope = _getScope (eCurrentScope, false);
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
