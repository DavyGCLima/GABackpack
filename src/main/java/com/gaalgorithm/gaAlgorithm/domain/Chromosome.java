package com.gaalgorithm.gaAlgorithm.domain;

import com.gaalgorithm.gaAlgorithm.util.Random;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.fill;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
@ToString
public class Chromosome implements Serializable, Comparable {
  private List<Boolean> genes;
  private List<Item> itemsRef;
  private int generation = 0;
  private float fitness = 0;

  public Chromosome clone() {
    return SerializationUtils.clone(this);
  }

  public Chromosome( List<Item> items ) {
    this.setGenes(new ArrayList<>(items.size()));
    java.util.Random random = new java.util.Random();
    for (int i = 0; i < items.size(); i++) {
      this.getGenes().add(random.nextBoolean());
    }
    this.setItemsRef(items);
    this.setFitness(this.generateFitness());
  }

  public static Chromosome buildValidChromosome( List<Item> items, int sotorageLimit ) {
    Chromosome chromosome = new Chromosome();
    Boolean[] values = new Boolean[items.size()];
    fill(values, Boolean.FALSE);
    chromosome.setGenes(Arrays.stream(values).collect(Collectors.toList()));
    chromosome.setItemsRef(items);
    java.util.Random random = new java.util.Random();
    Set<Integer> generated = new HashSet<>(items.size());
    for (int i = 0; i < items.size(); i++) {
      int target = Random.getNextInt(generated, items.size(), random);
      if (chromosome.getWeight() + items.get(target).getWeight() > sotorageLimit) continue;
      chromosome.getGenes().set(target, random.nextBoolean());
    }

    chromosome.setFitness(chromosome.generateFitness());
    return chromosome;
  }

  /**
   * Função Objetivo
   *
   * @return retorna o valor deste individuo
   */
  public float generateFitness() {
    float result = 0;
    float totalWight = 0;
    for (int i = 0; i < genes.size(); i++) {
      if (genes.get(i)) {
        result = result + (this.getItemsRef().get(i).getUtility() / this.getItemsRef().get(i).getCoast());
        totalWight = totalWight + this.getItemsRef().get(i).getWeight();
      }
    }
    return result;
  }

  /**
   * Conta os itens usados
   *
   * @return total de itens usados
   */
  public int getCountItemUsed() {
    int total = 0;
    for (Boolean gene : this.getGenes()) {
      if (gene) total++;
    }
    return total;
  }

  /**
   * Calcula o peso total deste individuo
   *
   * @return O peso total
   */
  public float getWeight() {
    if (genes == null || itemsRef == null) return 0;
    float totalWeight = 0;
    for (int i = 0; i < genes.size(); i++) {
      if (genes.get(i)) {
        totalWeight = totalWeight + this.itemsRef.get(i).getWeight();
      }
    }
    return totalWeight;
  }

  /**
   * Cira uma sublista aleatória da popualção
   *
   * @param population de referência
   * @param startIndex indice do inicio da sublista
   * @param generated  array que guarda itens randomicos usados
   * @param random     objeto que permite a aleatóriedade
   * @param total      quantidade de individuos da sublista
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
   *
   * @param parent     individuo de referência para o cruzamento
   * @param generation geração atual
   * @return dois filhos resultantes do cruzamento deste individuo com o parent
   */
  public List<Chromosome> uniformCrossover( Chromosome parent, int generation ) {
    List<Chromosome> childrens = new ArrayList<>(2);
    List<Boolean> sortedList = Random.getRandomBooleanList(this.getGenes().size());
    List<Boolean> genes1 = new ArrayList<>(this.getGenes().size());
    List<Boolean> genes2 = new ArrayList<>(this.getGenes().size());
    for (int i = 0; i < (this.getGenes().size() - 1 / 2); i++) {
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

    Chromosome child = new Chromosome(parent.getItemsRef());
    child.setGenes(genes1);
    child.setGeneration(generation);
    childrens.add(child);

    Chromosome child2 = new Chromosome(parent.getItemsRef());
    child2.setGenes(genes2);
    child2.setGeneration(generation);
    childrens.add(child2);

    return childrens;
  }

  /**
   * Reprodução cruzada de dois pontos.
   * <p>Reproduz os individuos com base em dois pontos sortidos no cromossomo, as secções geradas serão trocadas
   * entre os individuos para gerar os descendentes</p>
   *
   * @param parent     outro pai de referência
   * @param generation geração atual
   * @return Dois filhos resultados do cruzamento deste individuo com o parent
   */
  public List<Chromosome> twoPointsCrossover( Chromosome parent, int generation ) {
    Set<Integer> generated = new LinkedHashSet<>();
    java.util.Random random = new java.util.Random();
    int point1 = Random.getNextRandom(generated, this.getGenes().size() / 2, 1, 0, random);
    int point2 = Random.getNextRandom(generated, this.getGenes().size(), 2, point1, random);

    List<Boolean> genes1 = new ArrayList<>(this.getGenes().size());
    List<Boolean> genes2 = new ArrayList<>(this.getGenes().size());

    if (point1 >= 0) System.arraycopy(this.getGenes(), 0, genes1, 0, point1);
    if (point2 - point1 >= 0) System.arraycopy(parent.getGenes(), point1, genes1, point1, point2 - point1);
    if (this.getGenes().size() - point2 >= 0)
      System.arraycopy(this.getGenes(), point2, genes1, point2, this.getGenes().size() - point2);

    if (point1 >= 0) System.arraycopy(parent.getGenes(), 0, genes2, 0, point1);
    if (point2 - point1 >= 0) System.arraycopy(this.getGenes(), point1, genes2, point1, point2 - point1);
    if (this.getGenes().size() - point2 >= 0)
      System.arraycopy(parent.getGenes(), point2, genes2, point2, this.getGenes().size() - point2);

    List<Chromosome> childrens = new ArrayList<>(2);
    Chromosome chield1 = new Chromosome(parent.getItemsRef());
    chield1.setGeneration(generation);
    chield1.setGenes(genes1);
    childrens.add(chield1);

    Chromosome chield2 = new Chromosome(parent.getItemsRef());
    chield2.setGeneration(generation);
    chield2.setGenes(genes2);
    childrens.add(chield2);

    return childrens;
  }

  public List<Integer> getItemsUsed() {
    List<Integer> used = new ArrayList<>(this.getGenes().size());
    for (int i = 0; i < this.getGenes().size(); i++) {
      if(this.getGenes().get(i)) used.add(this.getItemsRef().get(i).getId());
    }
    return used;
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

  public String toHtml() {
    List<Item> parsed = new ArrayList<>(this.getGenes().size());
    for (int i = 0; i < this.genes.size(); i++) {
      if (this.genes.get(i)) parsed.add(this.itemsRef.get(i));
    }
    return "<pre>\"Chromosome\":{" + "\"genes\":" + "<br>" + parsed.stream().filter(Objects::nonNull).map(item -> "  "
      + item.toHtml() + "<br>").collect(Collectors.toList()) + "<br>, \"generation\":" + generation + ", \"fitness" +
      "\":" + fitness + "} </pre><br>";
  }
}
