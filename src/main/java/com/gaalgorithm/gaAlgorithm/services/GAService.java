package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
import com.gaalgorithm.gaAlgorithm.domain.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GAService {
private Set<Integer> generated = new LinkedHashSet<>();
private Random random = new Random();

private List<Item> generateRandomItems( int populationLimit ) {
  // gera os items que serão usados
  List<Item> items = new ArrayList<>();
  for (int i = 0; i < populationLimit; i++) {
    items.add(new Item(random.nextInt(100), random.nextFloat(), false));
  }
  return items;
}

private List<Chromosome> generateFirstPopulation( int populationLimit, List<Item> items ) {
  List<Chromosome> population = new ArrayList<>();
  for (int i = 0; i < populationLimit; i++) {
    Chromosome chromosome = new Chromosome();
    Item[] solution = new Item[populationLimit];
    // reinicia e gera novas posições
    generated = new LinkedHashSet<Integer>();
    for (int j = 0; j < populationLimit; j++) {
      solution[com.gaalgorithm.gaAlgorithm.util.Random.getNextRandom(generated, populationLimit, populationLimit,
        random)] = items.get(j);
    }
    for (Item item : solution) {
      chromosome.getGenes().add(item);
    }
    population.add(chromosome);
  }
  return population;
}

private void evaluete( List<Chromosome> population ) {
  Collections.sort(population);
}

private List<Chromosome> select( List<Chromosome> population, int reproductionRate ) {
  evaluete(population);
  int endElit = ((reproductionRate / 2) * 100) / population.size();
  List<Chromosome> elitPopulation = population.subList(0, endElit);
  List<Chromosome> randomPopulation = Chromosome.getRandomPopulation(population, endElit, generated, random,
    endElit);
  randomPopulation.addAll(elitPopulation);
  return randomPopulation;
}

private void reproduce(List<Chromosome> populationToReproduce) {

}

private void evolve( List<Chromosome> population, int generation, int reproductionRate ) {
  generated = new LinkedHashSet<>();
  log.info("Geração #%d", generation);
  List<Chromosome> populationToReproduce = select(population, reproductionRate);
}

/**
 * Inicia o algoritimo
 *
 * @param reproductionRate    taxa de reprodução
 * @param probabilityMutation probabilidade de mutação de um individuo
 * @param populationLimit     tamanho da população
 * @param storageLimit        capacidade da mochila
 */
public void start( int reproductionRate, float probabilityMutation, int populationLimit, int storageLimit ) {
  log.info("Gerando items");
  List<Item> items = generateRandomItems(populationLimit);
  //gera a primeira geração de soluções
  log.info("Gerando primeir população");
  List<Chromosome> population = generateFirstPopulation(populationLimit, items);
  evolve(population, 0, reproductionRate);
}
}
