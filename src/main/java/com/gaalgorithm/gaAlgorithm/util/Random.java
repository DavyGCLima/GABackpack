package com.gaalgorithm.gaAlgorithm.util;

import java.util.Set;

public class Random {
  public static int getNextRandom( Set<Integer> generated, int bound, int max, java.util.Random random ) {
    while (generated.size() < max) {
      Integer next = random.nextInt(bound);
      if(generated.add(next))
        return next;

    }
    return random.nextInt(bound);
  }

  public static int getNextRandom( Set<Integer> generated, int bound, int max, int start, java.util.Random random ) {
    while (generated.size() < max) {
      Integer next = random.nextInt(bound);
      if(next > start && generated.add(next))
        return next;

    }
    return random.nextInt(bound);
  }
}
