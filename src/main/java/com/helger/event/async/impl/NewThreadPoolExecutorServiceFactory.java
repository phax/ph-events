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
package com.helger.event.async.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.helger.commons.concurrent.IExecutorServiceFactory;

public class NewThreadPoolExecutorServiceFactory implements IExecutorServiceFactory
{
  private final int m_nThreadPoolSize;

  public NewThreadPoolExecutorServiceFactory (final int nThreadPoolSize)
  {
    if (nThreadPoolSize < 1)
      throw new IllegalArgumentException ("The passed number of threads in the pool is illegal: " + nThreadPoolSize);
    m_nThreadPoolSize = nThreadPoolSize;
  }

  public ExecutorService getExecutorService (final int nParallelTasks)
  {
    if (nParallelTasks < 1)
      throw new IllegalArgumentException ("The number of parallel tasks is invalid: " + nParallelTasks);
    return Executors.newFixedThreadPool (Math.min (m_nThreadPoolSize, nParallelTasks));
  }
}
