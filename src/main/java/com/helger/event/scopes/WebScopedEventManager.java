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
package com.helger.event.scopes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EChange;
import com.helger.event.IEvent;
import com.helger.event.IEventObserver;
import com.helger.event.IEventType;
import com.helger.event.impl.BaseEvent;
import com.helger.web.scopes.IWebScope;
import com.helger.web.scopes.mgr.EWebScope;

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
  private static MainEventManager _getEventMgr (@Nonnull final IWebScope aScope)
  {
    ValueEnforcer.notNull (aScope, "Scope");

    return aScope.getCastedAttribute (ATTR_EVENT_MANAGER);
  }

  @Nonnull
  private static MainEventManager _getOrCreateEventMgr (@Nonnull final IWebScope aScope)
  {
    ValueEnforcer.notNull (aScope, "Scope");

    // Does the scope already contain an event manager?
    MainEventManager aEventMgr = _getEventMgr (aScope);
    if (aEventMgr == null)
    {
      // Build the event manager
      aEventMgr = new MainEventManager ();

      // put it in scope and register the cleanup handler
      aScope.setAttribute (ATTR_EVENT_MANAGER, aEventMgr);
    }
    return aEventMgr;
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
      final MainEventManager aEventMgr = _getEventMgr (aScope);
      if (aEventMgr != null)
        return aEventMgr.unregisterObserver (aObserver);
    }
    return EChange.UNCHANGED;
  }

  /**
   * Notify observers without sender and without parameter.
   *
   * @param aEventType
   *        The event type for which an event should be triggered
   * @return <code>true</code> if no observer vetoed against the event
   */
  public static boolean notifyObservers (final @Nonnull IEventType aEventType)
  {
    return notifyObservers (new BaseEvent (aEventType));
  }

  /**
   * Notify observers without sender and without parameter.
   *
   * @param aEvent
   *        The event on which observers should be notified.
   * @return <code>true</code> if no observer vetoed against the event
   */
  public static boolean notifyObservers (@Nonnull final IEvent aEvent)
  {
    boolean bReturn = true;

    // for all scopes
    for (final EWebScope eCurrentScope : EWebScope.values ())
    {
      // get current instance of scope
      final IWebScope aScope = _getScope (eCurrentScope, false);
      if (aScope != null)
      {
        // get event manager (may be null)
        final MainEventManager aEventMgr = _getEventMgr (aScope);
        if (aEventMgr != null)
        {
          // main event trigger
          final Object aReturn = aEventMgr.trigger (aEvent);
          if (aReturn instanceof Boolean)
            bReturn = ((Boolean) aReturn).booleanValue ();
        }
      }
    }
    return bReturn;
  }
}
