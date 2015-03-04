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
package com.helger.event;

/**
 * Marker interface to identify observers that are interested only once in a
 * plugin. Simply implement this interface besides the regular observer
 * interface. The rest is handled automatically within the event engine (the
 * dispatcher to be exact).
 *
 * @author Philip Helger
 */
public interface IOnlyOnceEventObserver extends IEventObserver
{
  /* empty */
}
