package com.gaalgorithm.gaAlgorithm.domain;

import com.gaalgorithm.gaAlgorithm.util.Random;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Chromosome implements Serializable, Comparable {
  private List<Item> genes = new ArrayList<>();
  private int generation = 0;
  private int fitness = 0;

  public Chromosome( List<Item> items) {
    Set<Integer> generated = new LinkedHashSet<>();
    java.util.Random random = new java.util.Random();
    Item[] solution = new Item[6];
    for (int j = 0; j < 6; j++) {
      // garante que não haja solução em que nenhum item seja utilizado
      boolean has = Arrays.stream(solution).noneMatch(Objects::nonNull);
      if (random.nextBoolean()) {
        solution[j] = items.get(com.gaalgorithm.gaAlgorithm.util.Random.getNextRandom(generated, 6, 6, random));
      } else if (has && j == 5) {
        // caso a solução ainda não possua nenum item, adiciona um item aleatóriamente
        solution[random.nextInt(solution.length)] = items.get(com.gaalgorithm.gaAlgorithm.util.Random.getNextRandom(generated, 6, 6, random));
      } else {
        solution[j] = null;
      }
    }
    for (Item item : solution) {
      this.getGenes().add(item);
    }
    this.setFitness(this.generateFitness());
  }

  public int generateFitness() {
    int result = 0;
    int totalWight = 0;
    for (Item item : genes) {
      if (item != null) {
        result = result + item.getCoast();
        totalWight = totalWight + item.getWeight();
      }
    }
    if (totalWight == 0) {
      System.out.println("Problem here");
      return 0;
    }
    return (result / totalWight);
  }

  public int getWeight() {
    int totalWeight = 0;
    for (Item item : genes) {
      if (item != null) {
        totalWeight = totalWeight + item.getWeight();
      }
    }
    return totalWeight;
  }

  public static List<Chromosome> getRandomPopulation( List<Chromosome> population, int startIndex,
                                                      Set<Integer> generated, java.util.Random random, int total ) {
    List<Chromosome> randomSelected = new ArrayList<>();
    while (randomSelected.size() < total) {
      randomSelected.add(population.get(Random.getNextRandom(generated, population.size(), population.size(),
        startIndex, random)));
    }
    return randomSelected;
  }

  public List<Chromosome> uniformCrossover( Chromosome parent, int generation ) {
    List<Chromosome> childrens = new ArrayList<>(2);
    List<Boolean> sortedList = Random.getRandomBooleanList(this.getGenes().size());
    List<Item> genes1 = new ArrayList<>(this.getGenes().size());
    List<Item> genes2 = new ArrayList<>(this.getGenes().size());
    for (int i = 0; i < sortedList.size(); i++) {
      boolean sorted = sortedList.get(i);
      if (sorted) {
        genes1.add(parent.getGenes().get(i));
        genes2.add(this.getGenes().get(i));
      } else {
        genes1.add(this.getGenes().get(i));
        genes2.add(parent.getGenes().get(i));
      }
    }

    // Excluem soluções inválidas que não usam nenhum item
    boolean onlyNulls1 = genes1.stream().noneMatch(Objects::nonNull);
    boolean onlyNulls2 = genes2.stream().noneMatch(Objects::nonNull);
    if(onlyNulls1 || onlyNulls2) {
      return this.uniformCrossover(parent, generation);
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
      else if (this.getFitness() > c.getFitness()) return -1;
      else return 0;
    }
    return 0;
  }
}
