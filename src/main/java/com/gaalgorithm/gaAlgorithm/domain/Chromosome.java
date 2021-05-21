package com.gaalgorithm.gaAlgorithm.domain;

import com.gaalgorithm.gaAlgorithm.util.Random;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class Chromosome implements Serializable, Comparable {
private List<Item> genes = new ArrayList<>();
private int generation = 0;

public int generateFitness() {
  int result = 0;
  for (Item item : genes) {
    result = result + item.getCoast();
  }
  return result;
}

public static List<Chromosome> getRandomPopulation( List<Chromosome> population, int startIndex,
                                                    Set<Integer> generated, java.util.Random random, int total ) {
  List<Chromosome> randomSelected = new ArrayList<>();
  while (randomSelected.size() >= total ) {
    randomSelected.add(population.get(Random.getNextRandom(generated, population.size(), population.size(), startIndex, random)));
  }
  return randomSelected;
}

@Override
public boolean equals( Object o ) {
  if (this == o) return true;
  if (o == null || getClass() != o.getClass()) return false;
  Chromosome that = (Chromosome) o;
  return genes.equals(that.genes);
}

@Override
public int hashCode() {
  return Objects.hash(genes);
}

@Override
public int compareTo( Object o ) {
  if (o instanceof Chromosome) {
    Chromosome c = (Chromosome) o;
    if (this.generateFitness() < c.generateFitness()) return -1;
    else return 1;
  }
  return 0;
}
}
