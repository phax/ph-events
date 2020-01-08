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
package com.helger.event.dispatch.async;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.collection.impl.CommonsVector;
import com.helger.commons.collection.impl.ICommonsList;

public final class AsynchronousEventResultCollectorThread extends Thread implements Consumer <Object>
{

  private final CountDownLatch m_aCountDown;
  private final IAggregator <Object, ?> m_aResultAggregator;
  private final Consumer <Object> m_aResultConsumer;
  private final ICommonsList <Object> m_aResults = new CommonsVector <> ();

  public AsynchronousEventResultCollectorThread (@Nonnegative final int nObserversWithReturn,
                                                 @Nonnull final IAggregator <Object, ?> aResultAggregator,
                                                 @Nonnull final Consumer <Object> aResultConsumer)
  {
    super ("event-result-collector-thread");
    ValueEnforcer.isGT0 (nObserversWithReturn, "ObserversWithReturn");
    ValueEnforcer.notNull (aResultAggregator, "ResultAggregator");
    ValueEnforcer.notNull (aResultConsumer, "ResultCallback");

    m_aCountDown = new CountDownLatch (nObserversWithReturn);
    m_aResultAggregator = aResultAggregator;
    m_aResultConsumer = aResultConsumer;
  }

  // Called from each observer upon completion
  public void accept (final Object aObserverResult)
  {
    m_aResults.add (aObserverResult);
    m_aCountDown.countDown ();
  }

  @Override
  public void run ()
  {
    try
    {
      // Wait until all results are present
      m_aCountDown.await ();

      // We have all - aggregate
      final Object aAggregatedResults = m_aResultAggregator.apply (m_aResults);

      // Call result consumer
      m_aResultConsumer.accept (aAggregatedResults);
    }
    catch (final InterruptedException ex)
    {
      ex.printStackTrace ();
    }
  }
}
