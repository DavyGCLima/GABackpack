package com.gaalgorithm.gaAlgorithm.domain;

import com.gaalgorithm.gaAlgorithm.util.Random;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Chromosome implements Serializable, Comparable {
  private List<Item> genes = new ArrayList<>();
  private int generation = 0;
  private float fitness = 0;

  public Chromosome( List<Item> items ) {
    Set<Integer> generated = new LinkedHashSet<>();
    java.util.Random random = new java.util.Random();
    Item[] solution = new Item[23];
    for (int j = 0; j < 6; j++) {
      // garante que não haja solução em que nenhum item seja utilizado
      boolean has = Arrays.stream(solution).noneMatch(Objects::nonNull);
      if (random.nextBoolean()) {
        solution[j] = items.get(com.gaalgorithm.gaAlgorithm.util.Random.getNextRandom(generated, 6, 6, random));
      } else if (has && j == 5) {
        // caso a solução ainda não possua nenum item, adiciona um item aleatóriamente
        solution[random.nextInt(solution.length)] =
          items.get(com.gaalgorithm.gaAlgorithm.util.Random.getNextRandom(generated, 6, 6, random));
      } else {
        solution[j] = null;
      }
    }
    for (Item item : solution) {
      this.getGenes().add(item);
    }
    this.setFitness(this.generateFitness());
  }

  /**
   * Função Objetivo
   * @return retorna o valor deste individuo
   */
  public float generateFitness() {
    float result = 0;
    float totalWight = 0;
    for (Item item : genes) {
      if (item != null) {
        result = result + (item.getUtility() / item.getCoast());
        totalWight = totalWight + item.getWeight();
      }
    }
    if (totalWight == 0) {
      System.out.println("Problem here");
      log.error("Weight 0, chromosome {} generation: {}", this, this.getGeneration());
      return 0;
    }
    return result;
  }

  /**
   * Conta os itens usados
   * @return total de itens usados
   */
  public int getCountItemUsed() {
    int total = 0;
    for (Item gene : this.getGenes()) {
      if(gene != null) total++;
    }
    return total;
  }

  /**
   * Calcula o peso total deste individuo
   * @return O peso total
   */
  public float getWeight() {
    float totalWeight = 0;
    for (Item item : genes) {
      if (item != null) {
        totalWeight = totalWeight + item.getWeight();
      }
    }
    return totalWeight;
  }

  /**
   * Cira uma sublista aleatória da popualção
   * @param population de referência
   * @param startIndex indice do inicio da sublista
   * @param generated array que guarda itens randomicos usados
   * @param random objeto que permite a aleatóriedade
   * @param total quantidade de individuos da sublista
   * @return
   */
  public static List<Chromosome> getRandomPopulation( List<Chromosome> population, int startIndex,
                                                      Set<Integer> generated, java.util.Random random, int total ) {
    List<Chromosome> randomSelected = new ArrayList<>();
    while (randomSelected.size() < total) {
      randomSelected.add(population.get(Random.getNextRandom(generated, population.size(), population.size(),
        startIndex, random)));
    }
    return randomSelected;
  }

  /**
   * Reprodução corssover uniforme, faz a troca de todos os cromossomos de um individuo com o atual
   * @param parent individuo de referência para o cruzamento
   * @param generation geração atual
   * @return dois filhos resultantes do cruzamento deste individuo com o parent
   */
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
    if (onlyNulls1 || onlyNulls2) {
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

  /**
   * Reprodução cruzada de dois pontos.
   * <p>Reproduz os individuos com base em dois pontos sortidos no cromossomo, as secções geradas serão trocadas
   * entre os individuos para gerar os descendentes</p>
   * @param parent outro pai de referência
   * @param generation geração atual
   * @return Dois filhos resultados do cruzamento deste individuo com o parent
   */
  public List<Chromosome> twoPointsCrossover( Chromosome parent, int generation ) {
    Set<Integer> generated = new LinkedHashSet<>();
    java.util.Random random = new java.util.Random();
    int point1 = Random.getNextRandom(generated, this.getGenes().size()/2, 1, 0, random);
    int point2 = Random.getNextRandom(generated, this.getGenes().size(), 2, point1, random);

    List<Item> genes1 = new ArrayList<>(this.getGenes().size());
    List<Item> genes2 = new ArrayList<>(this.getGenes().size());

    genes1.addAll(this.getGenes().subList(0, point1));
    genes1.addAll(parent.getGenes().subList(point1, point2));
    genes1.addAll(this.getGenes().subList(point2, this.getGenes().size()));

    genes2.addAll(parent.getGenes().subList(0, point1));
    genes2.addAll(this.getGenes().subList(point1, point2));
    genes2.addAll(parent.getGenes().subList(point2, this.getGenes().size()));

    List<Chromosome> childrens = new ArrayList<>(2);
    Chromosome chield1 = new Chromosome();
    chield1.setGeneration(generation);
    chield1.setGenes(genes1);
    childrens.add(chield1);

    Chromosome chield2 = new Chromosome();
    chield2.setGeneration(generation);
    chield2.setGenes(genes2);
    childrens.add(chield2);

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
      return Float.compare(c.getFitness(), this.getFitness());
    }
    return 0;
  }
}
