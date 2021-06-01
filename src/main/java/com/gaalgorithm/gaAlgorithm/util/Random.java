package com.gaalgorithm.gaAlgorithm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Classe utilitária
 */
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

  public static List<Boolean> getRandomBooleanList(int size) {
    List<Boolean> list = new ArrayList<>(size);
    java.util.Random random = new java.util.Random();
    for (int i = 0; i < size; i++) {
      list.add(random.nextBoolean());
    }
    return list;
  }
}
