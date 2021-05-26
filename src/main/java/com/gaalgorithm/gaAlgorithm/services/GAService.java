package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
import com.gaalgorithm.gaAlgorithm.domain.Item;
import com.gaalgorithm.gaAlgorithm.services.dto.EmailDTO;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
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
  private float bestEvaluete = 0;

  private final ProdutorServico produtorServico;

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
      items.add(new Item(random.nextFloat() + random.nextInt(1000), random.nextFloat() + random.nextInt(storageLimit)
        , random.nextFloat() + random.nextInt(1000), false));
    }
    return items;
  }

  private List<Item> generateItems() {
    // gera os items que serão usados
    List<Item> items = new ArrayList<>();
    items.add(new Item(12, 33, 200, false));
    items.add(new Item(24, 66.2f, 309, false));
    items.add(new Item(5, 78.1f, 190, false));
    items.add(new Item(176, 15.8f, 525, false));
    items.add(new Item(90, 24.23f, 602, false));
    items.add(new Item(101, 49.9f, 808, false));
    items.add(new Item(78, 90.22f, 209, false));
    items.add(new Item(209, 25.482f, 738, false));
    items.add(new Item(728, 69.289f, 28, false));
    items.add(new Item(28, 35.290f, 93, false));
    items.add(new Item(90, 9.290f, 873, false));
    items.add(new Item(28, 24.2978f, 189, false));
    items.add(new Item(1, 42.9802f, 34, false));
    items.add(new Item(3, 93.16f, 437, false));
    items.add(new Item(58, 19.278f, 46, false));
    items.add(new Item(99, 83.389f, 84, false));
    items.add(new Item(87, 16.167f, 923, false));
    items.add(new Item(27, 29.2f, 123, false));
    items.add(new Item(12, 7.378f, 324, false));
    items.add(new Item(62, 18.9f, 75, false));
    items.add(new Item(78, 27.190f, 458, false));
    items.add(new Item(55, 40.38f, 857, false));
    items.add(new Item(12, 77.87f, 973, false));
    return items;
  }

  /**
   * Gera uma população inicial aleatória
   *
   * @param populationLimit tamanho da poulação
   * @param items           a serem utilizados no problema da mochila
   * @param storageLimit    Limite da mochila
   * @return A população inicial
   */
  private List<Chromosome> generateFirstPopulation( int populationLimit, List<Item> items, int storageLimit ) {
    log.info("Gerando primeira população");
    List<Chromosome> population = new ArrayList<>(populationLimit);
    for (int i = 0; i < populationLimit; i++) {
      Chromosome chromosome = new Chromosome(items);
      // individuos que não atendem as restrições não são considerados na população
      if (chromosome.getWeight() > storageLimit || chromosome.getWeight() == 0) {
        // individuos não considerados devem ser repostos para tender o tamanho da poulação
        i--;
      } else {
        population.add(chromosome);
      }
    }
    return population;
  }

  /**
   * Avalia uma população ou parte dela
   *
   * @param population ou lista a ser avaliada
   * @return Chromosome O melhor individuo
   */
  private Chromosome evaluete( List<Chromosome> population ) {
    log.info("Ordenando individuos");
    Collections.sort(population);
    Chromosome best = population.get(0);
    bestEvaluete = best.generateFitness();
    log.info("Melhor individuo: {}", bestEvaluete);
    return best;
  }

  /**
   * Seleciona individuos com boas características baseado no método elitista com chance de mesclar com individuos menos
   * qualifiucados (50/50)
   * Também elimina os individuos que não atendem as restrições
   *
   * @param population       população a ser avaliada
   * @param reproductionRate taxa de reprodução da população
   * @return Lista de individuos aptos a se reproduzir
   */
  private List<Chromosome> select( List<Chromosome> population, int reproductionRate ) {
    log.info("Seleção, geração");
    evaluete(population);
    return rankingSelection(population, reproductionRate);
  }

  private List<Chromosome> tournamentSelection( List<Chromosome> population, int reproductionRate ) {
    log.info("Selação por torneio");
    int reprodutionNumber = ((reproductionRate) * 100) / population.size();
    List<Chromosome> winners = new ArrayList<>();
    for (int i = 0; i < reprodutionNumber; i++) {
      Set<Integer> generated = new LinkedHashSet<>();
      List<Chromosome> selectedForTournment = Chromosome.getRandomPopulation(population, 0, generated, random,
        3);
      winners.add(evaluete(selectedForTournment));
    }
    return winners;
  }

  /**
   * Método de seleção por ranking
   * @param population       população a ser avaliada
   * @param reproductionRate taxa de reprodução da população
   * @return Lista de individuos aptos a se reproduzir
   */
  private List<Chromosome> rankingSelection( List<Chromosome> population, int reproductionRate ) {
    log.info("Seleção por ranking");
    Set<Integer> generated = new LinkedHashSet<>();
    int endElite = ((reproductionRate / 2) * 100) / population.size();
    log.info("Indice da elite: {}", endElite);
    List<Chromosome> elitePopulation = population.subList(0, endElite);
    log.info("Tamanho dapoulação elite: {}", elitePopulation.size());
    List<Chromosome> randomPopulation = Chromosome.getRandomPopulation(population, endElite, generated, random,
      endElite);
    log.info("Tamanho dapoulação randomica: {}", randomPopulation.size());
    randomPopulation.addAll(elitePopulation);
    return randomPopulation;
  }

  /**
   * Remove os individuos inválidos
   *
   * @param population   população a ser reduzida
   * @param storageLimit limite da mochila, critério de remoção
   */
  private void removeInvalidChromossomes( List<Chromosome> population, int storageLimit ) {
    log.info("Eliminando individuos inválidos");
    int originalSize = population.size();
    population.removeIf(item -> item.getWeight() > storageLimit);
    log.info("Individuos inválidos elimados {}", originalSize - population.size());
  }

  private void killWorstChromossomes( List<Chromosome> population ) {
    log.info("Eliminando piores da população");
    for (int i = population.size() / 2; i < population.size(); i++) {
      population.remove(i);
    }
  }

  /**
   * Reproduz a população recebida
   *
   * @param populationToReproduce população para reproduzir
   * @param generation            geração atual
   * @return Filhos gerados
   */
  private List<Chromosome> reproduce( List<Chromosome> populationToReproduce, int generation ) {
    log.info("Reproduzindo... tamanho da população para reproduzir: {}", populationToReproduce.size());
    List<Chromosome> childrens = new ArrayList<>();
    for (int i = 0; i <= populationToReproduce.size() - 2; i++) {
      childrens.addAll(populationToReproduce.get(i).uniformCrossover(populationToReproduce.get(i + 1), generation + 1));
    }
    log.info("Reprodução terminada, quantidade de filhos: {}", childrens.size());
    evaluete(childrens);
    return childrens;
  }

  private void mutate( List<Chromosome> mutableList, int probabilityMutation ) {
    float prob = (probabilityMutation * 100) / mutableList.size();
    log.info("Mutação, probabiliade de: {}", prob);
    Set<Integer> generated = new LinkedHashSet<>();
    Random random = new Random();
    // verifica se um individuo deve sofrer mutação
    for (int i = 0; i < prob; i++) {
      int sorted = random.nextInt(100);
      if (sorted > prob) {
        Chromosome selectedToMutate = mutableList.get(com.gaalgorithm.gaAlgorithm.util.Random.getNextRandom(generated
          , mutableList.size(), 1, random));
        selectedToMutate.getGenes().set(random.nextInt(selectedToMutate.getGenes().size()),
          generateItems().get(random.nextInt(selectedToMutate.getGenes().size())));
      }
    }
  }

  /**
   * Método principal da evolução, é um metodo recursivo que resultará em uma solução
   *
   * @param population          população a ser utilizada
   * @param generation          geração atual
   * @param reproductionRate    taxa de reprodução
   * @param probabilityMutation probabilidade de mutação
   * @param storageLimit        capacidade maxima da mochila
   * @param evolutionHistory    Histórico de evoluções
   */
  private void evolve( List<Chromosome> population, int generation, int reproductionRate, int probabilityMutation,
                       int storageLimit, List<Float> evolutionHistory, int selectionMode, String email ) {
    // Critério de parada por número de gerações
    if (generation > 500) {
      findResult(population, generation, email);
      return;
    }
    log.info("Geração #{} tamanho da População: {}", generation, population.size());
    List<Chromosome> populationToReproduce = select(population, reproductionRate);
    List<Chromosome> childrens = reproduce(populationToReproduce, generation);
    mutate(childrens, probabilityMutation);
    log.info("Avaliando filhos");
    evaluete(childrens);
    removeInvalidChromossomes(childrens, storageLimit);
    removeInvalidChromossomes(population, storageLimit);
    population.addAll(childrens);
    killWorstChromossomes(population);
    float bestValue = evaluete(population).getFitness();
    evolutionHistory.add(bestValue);
    log.info("Evoluindo população");
    log.info(
      "============================================================================================================");
    evolve(population, generation + 1, reproductionRate, probabilityMutation, storageLimit, evolutionHistory, selectionMode, email);
  }

  private void findResult( List<Chromosome> population, int generation, String email ) {
    log.info("Fim do GA, geração: {}", generation);
    Chromosome best = evaluete(population);
    log.info("Melhor individuo: {} da geração #{} com peso total de {}", best.getFitness(), best.getGeneration(),
      best.getWeight());
    log.info("Quantidade de itens usados: {}", best.getGenes().size());
    log.info("Itens usados: {}", best.getGenes());
    enviarEmail(email, best);
  }

  private void enviarEmail(String email, Chromosome best) {
    EmailDTO emailDTO = new EmailDTO();
    emailDTO.setDestinatario(email);
    emailDTO.setCorpo(best.toString());
    emailDTO.setAssunto("Resultado do algoritmo");
    produtorServico.enviarEmail(emailDTO);
  }

  public void start(RequestParamsDTO paramsDTO) {
    log.info("Gerando items");
    //usando items iguais para validação
    List<Item> items = generateItems();
    //gera a primeira geração de soluções

    List<Chromosome> population = generateFirstPopulation(paramsDTO.getPopulationLimit(), items, paramsDTO.getStorageLimit());
    float best = evaluete(population).getFitness();
    List<Float> evolutionHistory = new ArrayList<>();
    evolutionHistory.add(best);
    evolve(population, 0,
            paramsDTO.getReproductionRate(),
            paramsDTO.getProbabilityMutation(),
            paramsDTO.getStorageLimit(),
            evolutionHistory, paramsDTO.getSelectionMode(), paramsDTO.getEmail());
  }

}