package com.helger.event.dispatch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.event.IEvent;
import com.helger.event.observer.EEventObserverHandlerType;
import com.helger.event.observer.IEventObserver;
import com.helger.event.observerqueue.IEventObserverQueue;

public final class EffectiveEventObserverList
{
  private final Map <IEventObserver, EEventObserverHandlerType> m_aObservers;
  private final int m_nHandlingObserverCountWithReturnValue;

  EffectiveEventObserverList (@Nonnull final Map <IEventObserver, EEventObserverHandlerType> aObservers,
                              @Nonnegative final int nHandlingObserverCountWithReturnValue)
  {
    ValueEnforcer.notNull (aObservers, "Observers");
    ValueEnforcer.isGE0 (nHandlingObserverCountWithReturnValue, "HandlingObserverCountWithReturnValue");
    ValueEnforcer.isTrue (aObservers.size () >= nHandlingObserverCountWithReturnValue, "Internal inconsistency");

    m_aObservers = aObservers;
    m_nHandlingObserverCountWithReturnValue = nHandlingObserverCountWithReturnValue;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public Map <IEventObserver, EEventObserverHandlerType> getObservers ()
  {
    return m_aObservers;
  }

  @Nonnegative
  public int getHandlingObserverCountWithReturnValue ()
  {
    return m_nHandlingObserverCountWithReturnValue;
  }

  @Nonnull
  public static final EffectiveEventObserverList getListOfObserversThatCanHandleTheEvent (@Nonnull final IEvent aEvent,
                                                                                          @Nonnull final IEventObserverQueue aObserverQueue)
  {
    // find all handling observers
    final Map <IEventObserver, EEventObserverHandlerType> aObservers = new LinkedHashMap <> ();
    final List <IEventObserver> aObserversToRemove = new ArrayList <> ();
    int nHandlingObserverCountWithReturnValue = 0;
    for (final IEventObserver aObserver : aObserverQueue.getAllObservers ())
    {
      final EEventObserverHandlerType eHandleType = aObserver.canHandleEvent (aEvent);
      if (eHandleType.isHandling ())
      {
        aObservers.put (aObserver, eHandleType);
        if (eHandleType.hasReturnValue ())
          nHandlingObserverCountWithReturnValue++;

        // "Only once" observer?
        if (aObserver.isOnlyOnce ())
          aObserversToRemove.add (aObserver);
      }
    }

    // remove all "only once" observers
    for (final IEventObserver aObserver : aObserversToRemove)
      if (aObserverQueue.removeObserver (aObserver).isUnchanged ())
        throw new IllegalStateException ("Failed to remove only-omce observer " +
                                         aObserver +
                                         " from " +
                                         aObserverQueue);

    // return number of handling + handling observer map
    return new EffectiveEventObserverList (aObservers, nHandlingObserverCountWithReturnValue);
  }
}
