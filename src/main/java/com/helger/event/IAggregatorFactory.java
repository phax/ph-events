package com.helger.event;

import com.helger.commons.aggregate.IAggregator;

public interface IAggregatorFactory <SRCTYPE, DSTTYPE>
{
  IAggregator <? super SRCTYPE, ? super DSTTYPE> create ();
}
