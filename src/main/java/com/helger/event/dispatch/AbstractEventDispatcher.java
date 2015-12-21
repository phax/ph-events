package com.helger.event.dispatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.event.observer.exception.EventObservingExceptionCallback;
import com.helger.event.observer.exception.IEventObservingExceptionCallback;

public abstract class AbstractEventDispatcher implements IEventDispatcher
{
  private final IEventObservingExceptionCallback m_aExceptionCallback;

  public AbstractEventDispatcher (@Nullable final IEventObservingExceptionCallback aExceptionHandler)
  {
    m_aExceptionCallback = aExceptionHandler != null ? aExceptionHandler : new EventObservingExceptionCallback ();
  }

  @Nonnull
  protected final IEventObservingExceptionCallback getExceptionCallback ()
  {
    return m_aExceptionCallback;
  }

  public EChange stop ()
  {
    // Nothing to do in here
    return EChange.UNCHANGED;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ExceptionCallback", m_aExceptionCallback).toString ();
  }
}
