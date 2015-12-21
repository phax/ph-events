package com.helger.event.dispatch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.event.IEvent;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observerqueue.IEventObserverQueue;

public final class EffectiveEventObserverList
{
  private final int m_nHandlingObserverCountWithReturnValue;
  private final Map <IEventObserver, EEventObserverHandlerType> m_aHandlers;

  EffectiveEventObserverList (@Nonnegative final int nHandlingObserverCountWithReturnValue,
                              @Nonnull final Map <IEventObserver, EEventObserverHandlerType> aHandlers)
  {
    m_nHandlingObserverCountWithReturnValue = nHandlingObserverCountWithReturnValue;
    m_aHandlers = aHandlers;
  }

  public int getHandlingObserverCountWithReturnValue ()
  {
    return m_nHandlingObserverCountWithReturnValue;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public Map <IEventObserver, EEventObserverHandlerType> getObservers ()
  {
    return m_aHandlers;
  }

  @Nonnull
  public static final EffectiveEventObserverList getListOfObserversThatCanHandleTheEvent (@Nonnull final IEvent aEvent,
                                                                                          @Nonnull final IEventObserverQueue aObservers)
  {
    // find all handling observers
    final Map <IEventObserver, EEventObserverHandlerType> aHandler = new LinkedHashMap <> ();
    final List <IEventObserver> aObserversToRemove = new ArrayList <> ();
    int nHandlingObserverCountWithReturnValue = 0;
    for (final IEventObserver aObserver : aObservers.getAllObservers ())
    {
      final EEventObserverHandlerType eHandleType = aObserver.canHandleEvent (aEvent);
      if (eHandleType.isHandling ())
      {
        aHandler.put (aObserver, eHandleType);
        if (eHandleType.hasReturnValue ())
          nHandlingObserverCountWithReturnValue++;

        // "Only once" observer?
        if (aObserver.isOnlyOnce ())
          aObserversToRemove.add (aObserver);
      }
    }

    // remove all "only once" observers
    for (final IEventObserver aObserver : aObserversToRemove)
      if (aObservers.removeObserver (aObserver).isUnchanged ())
        throw new IllegalStateException ("Failed to remove only-omce observer " + aObserver + " from " + aObservers);

    // return number of handling + handling observer map
    return new EffectiveEventObserverList (nHandlingObserverCountWithReturnValue, aHandler);
  }
}
