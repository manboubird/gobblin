/* (c) 2014 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

package gobblin.runtime;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;


/**
 * An implementation of {@link Limiter} that limits the rate of some events. This implementation uses
 * Guava's {@link RateLimiter}.
 *
 * <p>
 *   {@link #acquirePermits(int)} is blocking and will always return {@link true} after the permits
 *   are successfully acquired (probably after being blocked for some amount of time). Permit refills
 *   are not supported in this implementation and {@link #releasePermits(int)} is a no-op.
 * </p>
 *
 * @author ynli
 */
public class RateBasedLimiter implements Limiter {

  private final RateLimiter rateLimiter;

  public RateBasedLimiter(double rateLimit) {
    this(rateLimit, TimeUnit.SECONDS);
  }

  public RateBasedLimiter(double rateLimit, TimeUnit timeUnit) {
    this.rateLimiter = RateLimiter.create(convertRate(rateLimit, timeUnit, TimeUnit.SECONDS));
  }

  @Override
  public void start() {
    // Nothing to do
  }

  @Override
  public boolean acquirePermits(int permits) throws InterruptedException {
    this.rateLimiter.acquire(permits);
    return true;
  }

  @Override
  public void releasePermits(int permits) {
    throw new UnsupportedOperationException("Permit refills are not supported in " +
        RateBasedLimiter.class.getSimpleName());
  }

  @Override
  public void stop() {
    // Nothing to do
  }

  private double convertRate(double originalRate, TimeUnit originalTimeUnit, TimeUnit targetTimeUnit) {
    return originalRate / targetTimeUnit.convert(1, originalTimeUnit);
  }
}
