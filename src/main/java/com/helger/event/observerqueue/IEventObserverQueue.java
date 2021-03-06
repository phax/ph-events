/**
 * Copyright (C) 2015-2020 Philip Helger (www.helger.com)
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
package com.helger.event.observerqueue;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.event.observer.IEventObserver;

/**
 * Interface for keeping a list of event observers.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IEventObserverQueue extends Serializable
{
  /**
   * Add a new observer.
   *
   * @param aObserver
   *        The observer to add. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange addObserver (@Nonnull IEventObserver aObserver);

  /**
   * Remove a previously added observer.
   *
   * @param aObserver
   *        The observer to remove. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange removeObserver (@Nonnull IEventObserver aObserver);

  /**
   * @return A list of all observers. Never <code>null</code>
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <IEventObserver> getAllObservers ();

  /**
   * @return <code>true</code> if not a single observer is contained,
   *         <code>false</code> if at least one observer is contained.
   */
  boolean isEmpty ();

  /**
   * This method is called before the main dispatching takes place. It is called
   * before each dispatch.
   */
  default void beforeDispatch ()
  {}

  /**
   * This method is called after the main dispatching took place. It is called
   * after each dispatch.
   */
  default void afterDispatch ()
  {}

  @Nonnull
  static IEventObserverQueue createDefault ()
  {
    // By default a non-weak set is used, because observers are quite regular
    // inline classes which tend to be garbage collected very easily!
    return new EventObserverQueueOrderedSet ();
  }
}
