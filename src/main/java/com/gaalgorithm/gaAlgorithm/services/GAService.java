package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
import com.gaalgorithm.gaAlgorithm.domain.GenotypeGroup;
import com.gaalgorithm.gaAlgorithm.domain.History;
import com.gaalgorithm.gaAlgorithm.domain.Item;
import com.gaalgorithm.gaAlgorithm.domain.history.GeneticConvertionHistory;
import com.gaalgorithm.gaAlgorithm.services.dto.EmailDTO;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GAService {
  private final Random random = new Random();
  private List<Item> defaultItems = new ArrayList<>();
  private final ProdutorServico produtorServico;
  private long execTime;
  private RequestParamsDTO params;
  History evolutionHistory = new History();

  private List<Item> generateItems( List<Item> items ) {
    if (defaultItems.size() > 0) return defaultItems;
    defaultItems = items;
    return defaultItems;
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
      if(i <= 0.66 * populationLimit) {
        population.add(Chromosome.buildValidChromosome(items, storageLimit));
        continue;
      }
      Chromosome chromosome = new Chromosome(items);
      population.add(chromosome);
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
    log.debug("Ordenando individuos");
    Collections.sort(population);
    Chromosome best = population.get(0);
    float bestEvaluete = best.generateFitness();
    log.debug("Melhor individuo: {}", bestEvaluete);
    return best;
  }

  /**
   * Seleciona individuos com boas características baseado no método elitista com chance de mesclar com individuos menos
   * qualifiucados (50/50)
   * Também elimina os individuos que não atendem as restrições
   *
   * @param population       população a ser avaliada
   * @param reproductionRate taxa de reprodução da população
   * @param selectionMode    modo de seleção, operador de seleção
   * @return Lista de individuos aptos a se reproduzir
   */
  private List<Chromosome> select( List<Chromosome> population, Integer reproductionRate, int selectionMode ) {
    log.info("Seleção, Modo: {}", selectionMode);
    evaluete(population);
    if (selectionMode != 0) {
      return tournamentSelection(population, reproductionRate);
    }
    return rankingSelection(population, reproductionRate);
  }

  /**
   * Seleção por torneio
   *
   * @param population       população a participar
   * @param reproductionRate taxa de reprodução
   * @return população selecionada
   */
  private List<Chromosome> tournamentSelection( List<Chromosome> population, Integer reproductionRate ) {
    log.info("Selação por torneio");
    float reprodutionNumber = ((float) reproductionRate / 100F) * population.size();
    List<Chromosome> winners = new ArrayList<>();
    for (int i = 0; i < reprodutionNumber; i++) {
      Set<Integer> generated = new LinkedHashSet<>();
      List<Chromosome> selectedForTournment = Chromosome.getRandomPopulation(population, 0, generated, random, 3);
      winners.add(evaluete(selectedForTournment));
    }
    return winners;
  }

  /**
   * Método de seleção por ranking
   *
   * @param population       população a ser avaliada
   * @param reproductionRate taxa de reprodução da população
   * @return Lista de individuos aptos a se reproduzir
   */
  private List<Chromosome> rankingSelection( List<Chromosome> population, int reproductionRate ) {
    log.info("Seleção por ranking");
    Set<Integer> generated = new LinkedHashSet<>();
    Float endElite = (((float) reproductionRate / 2) / 100F) * population.size();
    log.info("Indice da elite: {}", endElite);
    List<Chromosome> elitePopulation = population.subList(0, endElite.intValue());
    log.info("Tamanho dapoulação elite: {}", elitePopulation.size());
    List<Chromosome> randomPopulation = Chromosome.getRandomPopulation(population, endElite.intValue(), generated,
      random, endElite.intValue());
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

  /**
   * Retira os piores individuos de uma popualção
   *
   * @param population população a ser limpa
   * @return Nov apopulação
   */
  private List<Chromosome> killWorstChromossomes( List<Chromosome> population, int limit ) {
    log.info("Eliminando piores da população");
    int prevTotal = population.size();
    population = population.stream().limit(limit).collect(Collectors.toList());
    log.info("Total de elimninações: {}", prevTotal - population.size());
    return population;
  }

  /**
   * Reproduz a população recebida
   *
   * @param populationToReproduce população para reproduzir
   * @param generation            geração atual
   * @param reproductionMode      Modo de reprodução, 1 para cruzamento de dois pontos, 0 para cruzamento uniforme
   * @return Filhos gerados
   */
  private List<Chromosome> reproduce( List<Chromosome> populationToReproduce, int generation, int reproductionMode ) {
    log.info("Reproduzindo... tamanho da população para reproduzir: {}", populationToReproduce.size());
    List<Chromosome> childrens = new ArrayList<>();
    for (int i = 0; i <= populationToReproduce.size() - 2; i++) {
      if (reproductionMode != 0) {
        childrens.addAll(populationToReproduce.get(i).twoPointsCrossover(populationToReproduce.get(i + 1),
          generation + 1));
      } else {
        childrens.addAll(populationToReproduce.get(i).uniformCrossover(populationToReproduce.get(i + 1),
          generation + 1));
      }
    }
    log.info("Reprodução terminada, quantidade de filhos: {}", childrens.size());
    childrens.forEach(chromosome -> chromosome.setFitness(chromosome.generateFitness()));
    evaluete(childrens);
    return childrens;
  }

  /**
   * Pode realizar uma mutação sobre uma lista de individuos
   *
   * @param mutableList         individuos que podem sofrer mutação
   * @param probabilityMutation probabilidade de mutação (referência)
   */
  private void mutate( List<Chromosome> mutableList, int probabilityMutation ) {
    float prob = ((float) probabilityMutation / 100);
    log.info("Mutação, probabiliade de: {}", prob);
    Set<Integer> generated = new LinkedHashSet<>();
    Random random = new Random();
    // verifica se um individuo deve sofrer mutação
    for (Chromosome selectedToMutate : mutableList) {
      int sorted = random.nextInt(100);
      if (sorted > prob) {
        for (int j = 0; j < (0.5 * selectedToMutate.getGenes().size()); j++) {
          int index = random.nextInt(selectedToMutate.getGenes().size());
          selectedToMutate.getGenes().set(index, !selectedToMutate.getGenes().get(index));
          selectedToMutate.setFitness(selectedToMutate.generateFitness());
        }
      }
    }
  }

  /**
   * Método principal da evolução, é um metodo recursivo que resultará em uma solução
   *
   * @param population população a ser utilizada
   * @param generation geração atual
   */
  private void evolve( List<Chromosome> population, int generation ) {
    // Critério de parada por número de gerações
    int total = 1000;
    if (generation > total) {
      findResult(population, generation, params.getEmail(), evolutionHistory);
      return;
    }
    log.info("Geração #{} tamanho da População: {}", generation, population.size());
    if (generation % (0.1 * total) == 0) { // 10% of total
      List<GenotypeGroup> genotypeGroups = detectGeneticConvergence(population, params.getK(), params.getY(),
        params.getM());
      if (genotypeGroups != null) {
        GeneticConvertionHistory history = new GeneticConvertionHistory();
        history.setGeneration(generation);
        history.setGenotypeGroups(genotypeGroups);
        evolutionHistory.getGeneticConvertion().add(history);
        genotypeGroups.forEach(genotypeGroup -> mutate(genotypeGroup.getPopulationGrouped(), 100));
      }
    }
    List<Chromosome> populationToReproduce = select(population, params.getReproductionRate(),
      params.getSelectionMode());
    List<Chromosome> childrens = reproduce(populationToReproduce, generation, params.getReproductionMode());
    mutate(childrens, params.getProbabilityMutation());
    log.info("Avaliando filhos");
    removeInvalidChromossomes(childrens, params.getStorageLimit());
    removeInvalidChromossomes(population, params.getStorageLimit());
    log.info("Adicionando {} filhos", childrens.size());
    population.addAll(childrens);
    evaluete(population);
    population = killWorstChromossomes(population, params.getPopulationLimit() * 2);
    evolutionHistory.getBests().add(evaluete(population).clone());
    log.info("Evoluindo população");
    log.info(
      "============================================================================================================");
    evolve(population, generation + 1);
  }

  /**
   * Fim do Algoritimo genético, encontra a melhor solução
   *
   * @param population população
   * @param generation geração atual
   * @param email      de referencia para envio
   */
  private void findResult( List<Chromosome> population, int generation, String email, History history ) {
    long endTime = System.nanoTime();
    history.setTimeExec((double) (endTime - execTime) / 1_000_000_000);
    log.info("Fim do GA, geração: {}, tempo de execução: {}", generation, history.getTimeExec());
    Chromosome best = evaluete(population);
    history.setBest(best);
    log.info("Melhor individuo: {} da geração #{} com peso total de {} e {} itens", best.getFitness(),
      best.getGeneration(), best.getWeight(), best.getCountItemUsed());
    log.info("Ocorreu convergencia em: {}", history.getGeneticConvertion().stream().map(GeneticConvertionHistory::getGeneration));
    log.info("Itens usados: {}", best.getItemsUsed());
    enviarEmail(email, best, history);
  }

  /**
   * Envia um email com o resultado
   *
   * @param email   de destino
   * @param best    melhor solução
   * @param history histórico
   */
  private void enviarEmail( String email, Chromosome best, History history ) {
    EmailDTO emailDTO = new EmailDTO();
    emailDTO.setDestinatario(email);
    emailDTO.setCorpo("<html>" + "<header><h2>Resultado do GA: " + best.getFitness() + "</h2></header>" + "<main>" +
      "<section><h4>Detalhes</h4>" + "<p>Tempo de execução: " + history.getTimeExec() + " segundos</p>" + "<p>Taxa " + "de" + " reprodução: " + params.getReproductionRate() + " segundos</p>" + "<p>Modo de reprodução: " + params.getReproductionMode() + " segundos</p>" + "<p>População inicial: " + params.getPopulationLimit() + " segundos</p>" + "<p>Capacidade máxima da mochila: " + params.getStorageLimit() + " segundos</p>" + "<p>Modo de seleção: " + params.getSelectionMode() + " segundos</p>" + "<p>probabilidade de mutação: " + params.getProbabilityMutation() + " segundos</p>" + "<p>K: " + params.getK() + " segundos</p>" + "<p>Y: " + params.getY() + " segundos</p>" + "<p>M: " + params.getM() + " segundos</p>" + "<p>Ocorreu convergência nas gerações: " + history.getGeneticConvertion().stream().map(geneticHistory -> " " + geneticHistory.getGeneration()) + "</p>" + "<p><strong>Melhor fitness: " + best.getFitness() + "</strong> da geração #" + best.getGeneration() + " com peso total de " + best.getWeight() + " e " + best.getGenes().size() + " itens<p>" + "</section>" + "<section><h4>Melhor Individuo</h4><br><code>" + best.toHtml() + "</code></section>" + "</main>" + "</html>");
    emailDTO.setAssunto("Resultado do algoritmo GA: " + best.getFitness());
    produtorServico.enviarEmail(emailDTO);
  }

  /**
   * Inicia o Algoritimo gerando a primeira população
   *
   * @param paramsDTO Parametros do algoritimo
   */
  public void start( RequestParamsDTO paramsDTO ) {
    params = paramsDTO;
    execTime = System.nanoTime();
    log.info("Gerando items");
    //usando items iguais para validação
    List<Item> items = generateItems(paramsDTO.getItems());
    //gera a primeira geração de soluções
    List<Chromosome> population = generateFirstPopulation(paramsDTO.getPopulationLimit(), items,
      paramsDTO.getStorageLimit());
    evolutionHistory.getBests().add(evaluete(population).clone());
    evolve(population, 0);
  }

  /**
   * Detecta se está ocorrendo conversão genética
   *
   * @param population
   * @param k          quantidade máxima de grupos
   * @param y          distância entre os individuos
   * @param m          máximo de individuos em um grupo
   * @return A lista de grupos se ocorreu covnersão. Caso contrário retorna null
   */
  private List<GenotypeGroup> detectGeneticConvergence( List<Chromosome> population, Integer k, Integer y, Integer m ) {
    log.debug("Testando conversão genética");
    List<GenotypeGroup> groups = new ArrayList<>();
    groups.add(GenotypeGroup.addToGroup(new GenotypeGroup(), population.get(0)));
    for (Chromosome chromosome : population) {
      boolean added = false;
      for (GenotypeGroup group : groups) {
        if (GenotypeGroup.evalueteGenotype(group, chromosome, y)) {
          GenotypeGroup.addToGroup(group, chromosome);
          if (group.getPopulationGrouped().size() > m) {
            log.info("Conversão genética detectada por super grupo!");
            log.debug("Genotipo: {}", group.getGenotype());
            List<GenotypeGroup> toReturn = new ArrayList<>();
            toReturn.add(group);
            return toReturn;
          }
          added = true;
        }
      }
      if (!added) {
        groups.add(GenotypeGroup.addToGroup(new GenotypeGroup(), chromosome));
      }
    }
    if (groups.size() < k) {
      log.info("Conversão genética detectada por número de conjuntos!");
      return groups;
    }
    return null;
  }

}