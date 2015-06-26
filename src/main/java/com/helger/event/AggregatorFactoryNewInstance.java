package com.helger.event;

import javax.annotation.Nullable;

import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.GenericReflection;

public class AggregatorFactoryNewInstance <DSTTYPE> implements IAggregatorFactory <Object, DSTTYPE>
{
  private final Class <? extends IAggregator <Object, ? extends DSTTYPE>> m_aClass;

  public AggregatorFactoryNewInstance (final Class <? extends IAggregator <Object, ? extends DSTTYPE>> aClass)
  {
    if (!ClassHelper.isInstancableClass (aClass))
      throw new IllegalArgumentException ("The passed class '" +
                                          aClass +
                                          "' is not instancable or doesn't have a no-argument constructor!");
    m_aClass = aClass;
  }

  @SuppressWarnings ("unchecked")
  @Nullable
  public IAggregator <Object, DSTTYPE> create ()
  {
    return (IAggregator <Object, DSTTYPE>) GenericReflection.newInstance (m_aClass);
  }
}
