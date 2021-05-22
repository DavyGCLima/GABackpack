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
private int fitness = 0;

public int generateFitness() {
  int result = 0;
  float totalWight = 0;
  for (Item item : genes) {
    if(item != null) {
      result = result + item.getCoast();
      totalWight = totalWight + item.getWeight();
    }
  }
  return (int) (result/totalWight);
}

public static List<Chromosome> getRandomPopulation( List<Chromosome> population, int startIndex,
                                                    Set<Integer> generated, java.util.Random random, int total ) {
  List<Chromosome> randomSelected = new ArrayList<>();
  while (randomSelected.size() < total ) {
    randomSelected.add(population.get(Random.getNextRandom(generated, population.size(), population.size(), startIndex, random)));
  }
  return randomSelected;
}

public List<Chromosome> uniformCrossover(Chromosome parent, int generation) {
  List<Chromosome> childrens = new ArrayList<>(2);
  List<Boolean> sortedList = Random.getRandomBooleanList(this.getGenes().size());
  List<Item> genes1 = new ArrayList<>(this.getGenes().size());
  List<Item> genes2 = new ArrayList<>(this.getGenes().size());
  for (int i = 0; i < sortedList.size(); i++) {
    boolean sorted = sortedList.get(i);
    if(sorted) {
      genes1.add(parent.getGenes().get(i));
      genes2.add(this.getGenes().get(i));
    } else {
      genes1.add(this.getGenes().get(i));
      genes2.add(parent.getGenes().get(i));
    }
  }

  Chromosome child = new Chromosome();
  child.setGenes(genes1);
  child.setGeneration(generation);
  childrens.add(child);

  Chromosome child2 = new Chromosome();
  child2.setGenes(genes2);
  child2.setGeneration(generation);
  childrens.add(child2);

  return childrens;
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
    c.setFitness(c.generateFitness());
    this.setFitness(this.generateFitness());
    if (this.getFitness() < c.getFitness()) return 1;
    else return -1;
  }
  return 0;
}
}
