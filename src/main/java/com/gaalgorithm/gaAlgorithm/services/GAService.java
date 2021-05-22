package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
import com.gaalgorithm.gaAlgorithm.domain.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GAService {
private Random random = new Random();
private int bestEvaluete = 0;

/**
 * Cria itens para o problema da mochila randomicamente, somente baseado no limite da mochila.
 * Nenhum Item pode ultrapssar o limite da propria mochila por sí apenas
 *
 * @param populationLimit
 * @return
 */
private List<Item> generateRandomItems( int populationLimit, int storageLimit ) {
  // gera os items que serão usados
  List<Item> items = new ArrayList<>();
  for (int i = 0; i < populationLimit; i++) {
    items.add(new Item(random.nextInt(1000), random.nextInt(storageLimit), false));
  }
  return items;
}

private List<Item> generateItems() {
  // gera os items que serão usados
  List<Item> items = new ArrayList<>();
  items.add(new Item(12, 33, false));
  items.add(new Item(24, 66, false));
  items.add(new Item(5, 78, false));
  items.add(new Item(176, 15, false));
  items.add(new Item(90, 24, false));
  items.add(new Item(101, 49, false));
  return items;
}

/**
 * Gera uma população inicial aleatória
 *
 * @param populationLimit tamanho da poulação
 * @param items           a serem utilizados no problema da mochila
 * @return A população inicial
 */
private List<Chromosome> generateFirstPopulation( int populationLimit, List<Item> items ) {
  List<Chromosome> population = new ArrayList<>(populationLimit);
  for (int i = 0; i < populationLimit; i++) {
    Chromosome chromosome = new Chromosome();
    Item[] solution = new Item[populationLimit];
    // reinicia e gera novas posições
    Set<Integer> generated = new LinkedHashSet<>();
    for (int j = 0; j < populationLimit; j++) {
      if(random.nextBoolean()) {
        solution[j] = items.get(com.gaalgorithm.gaAlgorithm.util.Random.getNextRandom(generated, populationLimit,
          populationLimit, random));
      } else {
        solution[j] = null;
      }
    }
    for (Item item : solution) {
      chromosome.getGenes().add(item);
    }
    chromosome.setFitness(chromosome.generateFitness());
    population.add(chromosome);
  }
  return population;
}

/**
 * Avalia uma população ou parte dela
 *
 * @param population ou lista a ser avaliada
 */
private void evaluete( List<Chromosome> population ) {
  Collections.sort(population, Chromosome::compareTo);
  Chromosome best = population.get(0);
  bestEvaluete = best.generateFitness();
  log.info("Analise Melhor individuo: {}", bestEvaluete);
}

/**
 * Seleciona individuos com boas características baseado no método elitista com chance de mesclar com individuos menos
 * qualifiucados (50/50)
 *
 * @param population       população a ser avaliada
 * @param reproductionRate taxa de reprodução da população
 * @return Lista de individuos aptos a se reproduzir
 */
private List<Chromosome> select( List<Chromosome> population, int reproductionRate ) {
  log.info("Seleção, geração: {}", population.get(0).getGeneration());
  Set<Integer> generated = new LinkedHashSet<>();
  evaluete(population);
  int endElite = ((reproductionRate / 2) * 100) / population.size();
  List<Chromosome> elitePopulation = population.subList(0, endElite);
  List<Chromosome> randomPopulation = Chromosome.getRandomPopulation(population, endElite, generated, random, endElite);
  randomPopulation.addAll(elitePopulation);
  return randomPopulation;
}

private List<Chromosome> reproduce( List<Chromosome> populationToReproduce ) {
  log.info("Reproduzindo...");
  List<Chromosome> childrens = new ArrayList<>();
  for (int i = 0; i <= populationToReproduce.size() - 2; i++) {
    childrens.addAll(
      populationToReproduce.get(i).uniformCrossover(
        populationToReproduce.get(i + 1),populationToReproduce.get(i).getGeneration() + 1));
  }
  log.info("Reprodução terminada");
  evaluete(childrens);
  return childrens;
}

/**
 * Método principal da evolução, é um metodo recursivo que resultará em uma solução
 *
 * @param population       população a ser utilizada
 * @param generation       geração atual
 * @param reproductionRate taxa de reprodução
 */
private void evolve( List<Chromosome> population, int generation, int reproductionRate ) {
  log.info("Geração #{}", generation);
  List<Chromosome> populationToReproduce = select(population, reproductionRate);
  List<Chromosome> childrens = reproduce(populationToReproduce);

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
  //usando items iguais para validação
  List<Item> items = generateItems();
  //gera a primeira geração de soluções
  log.info("Gerando primeir população");
  List<Chromosome> population = generateFirstPopulation(populationLimit, items);
  evolve(population, 0, reproductionRate);
}
}
