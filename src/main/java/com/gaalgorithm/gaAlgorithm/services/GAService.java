package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.domain.BorderMethod;
import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
import com.gaalgorithm.gaAlgorithm.domain.history.DominanceHistory;
import com.gaalgorithm.gaAlgorithm.domain.history.GenotypeGroup;
import com.gaalgorithm.gaAlgorithm.domain.History;
import com.gaalgorithm.gaAlgorithm.domain.Item;
import com.gaalgorithm.gaAlgorithm.domain.history.GeneticConvertionHistory;
import com.gaalgorithm.gaAlgorithm.domain.history.ParetoFrontHistory;
import com.gaalgorithm.gaAlgorithm.services.dto.EmailDTO;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
  private History evolutionHistory = new History();
  private final int totalgeneration = 1000;
  private final String AHP = "multi-critério método AHP";
  private final String BORDER = "multi-critério método Borda";

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
      if (i <= 0.66 * populationLimit) {
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

  private void evalueteByBorder( List<Chromosome> population ) {
    log.info("Buscando resultado pelo método de borda");
    nsga(population, totalgeneration + 1);
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
    for (Chromosome chromosome : childrens) {
      chromosome.setFitness(chromosome.generateFitness());
      chromosome.calcTotalCost();
      chromosome.calcTotalUtility();
    }
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
        for (int j = 0; j < (0.01 * selectedToMutate.getGenes().size()); j++) {
          int index = random.nextInt(selectedToMutate.getGenes().size());
          selectedToMutate.getGenes().set(index, !selectedToMutate.getGenes().get(index));
          selectedToMutate.setFitness(selectedToMutate.generateFitness());
          selectedToMutate.calcTotalUtility();
          selectedToMutate.calcTotalCost();
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
    if (generation > totalgeneration) {
      if (params.getNsga()) findResultNSGA(population, params.getEmail(), evolutionHistory);
      else findResult(population, generation, params.getEmail(), evolutionHistory);
      return;
    }
    log.info("Geração #{} tamanho da População: {}", generation, population.size());
    if (generation % (0.1 * totalgeneration) == 0) { // 10% of total
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
    population.addAll(childrens);
    log.info("Adicionando {} filhos", childrens.size());
    if (params.getNsga()) {
      nsga(population, generation);
    } else {
      removeInvalidChromossomes(population, params.getStorageLimit());
      evaluete(population);
      population = killWorstChromossomes(population, params.getPopulationLimit() * 2);
    }
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
    getResultGA(best, email, history, "método comum");
  }

  private void getResultGA( Chromosome best, String email, History history, String method ) {
    history.setBest(best);
    log.info("Melhor individuo: {} da geração #{} com peso total de {} e {} itens", best.getFitness(),
      best.getGeneration(), best.getWeight(), best.getCountItemUsed());
    log.info("Ocorreu convergencia em: {}",
      history.getGeneticConvertion().stream().map(GeneticConvertionHistory::getGeneration)
        .collect(Collectors.toList()));
    log.info("Itens usados: {}", best.getItemsUsed());
    enviarEmail(email, best, history, method);
  }

  private void findResultNSGA( List<Chromosome> population, String email, History history ) {
    long endTime = System.nanoTime();
    history.setTimeExec((double) (endTime - execTime) / 1_000_000_000);
    List<List<Chromosome>> fronts = groupByDominance(population);
    List<Chromosome> selected = new ArrayList<>();
    for (List<Chromosome> front : fronts) {
      if (front.size() > 0) {
        selected = front;
        break;
      }
    }
    history.setParetoFront(selected);
    findResultBorder(email, history, selected);
    findResultAHP(email, history, selected);
  }

  private void findResultAHP( String email, History history, List<Chromosome> selected ) {
    log.info("Buscando resultador por metodo de AHP");
    List<List<Float>> judgeMatrix = new ArrayList<>();

    // Preenche a matriz de julgamento
    List<Float> utility = new ArrayList<>(2);
    utility.add(1F); // utility | utility
    utility.add(5F); // utility | cost
    judgeMatrix.add(utility);
    List<Float> cost = new ArrayList<>(2);
    cost.add(1 / utility.get(1)); // cost | utility
    cost.add(1F); // cost | cost
    judgeMatrix.add(cost);

    List<List<Float>> normalized = new ArrayList<>(4);
    for (int i = 0; i < judgeMatrix.size(); i++) {
      List<Float> line = judgeMatrix.get(i);
      normalized.add(new ArrayList<>());
      for (int i1 = 0; i1 < line.size(); i1++) {
        Float item = line.get(i1);
        normalized.get(i).add(
          item / line.stream().reduce(0F, Float::sum));
      }
    }

    List<Float> criteria = new ArrayList<>(2);
    for (List<Float> line : normalized) {
      criteria.add(line.stream().reduce(0F, Float::sum));
    }

    List<Chromosome> result = new ArrayList<>(selected.size());
    for (int i = 0; i < selected.size(); i++) {
      Chromosome actual = selected.get(i);
      float totalCost = actual.getTotalCost() * criteria.get(1);
      float totalUtil = actual.getTotalUtility() * criteria.get(0);
      actual.setFitness(totalUtil + totalCost);
      result.add(actual);
    }
    result.sort(( o1, o2 ) -> Float.compare(o2.getFitness(), o1.getFitness()));
    Chromosome best = result.get(0);
    history.setBest(best);
    getResultGA(best, email, history, AHP);
  }

  private void findResultBorder( String email, History history, List<Chromosome> selected ) {
    log.info("Buscando resultador por metodo de BORDA");

    selected.sort(Chromosome.BY_COST);
    List<BorderMethod> costList = new ArrayList<>(selected.size());
    addToBorderList(selected, costList);

    selected.sort(Chromosome.BY_UTILITY);
    List<BorderMethod> utilityList = new ArrayList<>(selected.size());
    addToBorderList(selected, utilityList);

    List<BorderMethod> totalList = new ArrayList<>(selected.size());
    for (Chromosome chromosome : selected) {
      BorderMethod finded =
        costList.stream().filter(borderMethod -> borderMethod.getChromosome().equals(chromosome))
          .collect(Collectors.toList()).get(0);
      BorderMethod total = new BorderMethod();
      total.setPoints(utilityList.stream().filter(borderMethod -> borderMethod.getChromosome().equals(chromosome))
        .collect(Collectors.toList()).get(0).getPoints() + finded.getPoints());
      total.setChromosome(chromosome);
      totalList.add(total);
    }

    totalList.sort(Comparator.comparingInt(BorderMethod::getPoints));
    Chromosome best = totalList.get(0).getChromosome();
    history.setBest(best);
    getResultGA(best, email, history, BORDER);
  }

  private void addToBorderList( List<Chromosome> selected, List<BorderMethod> borderMethodList ) {
    for (int i = 0; i < selected.size(); i++) {
      BorderMethod objective = new BorderMethod();
      objective.setPoints(i);
      objective.setChromosome(selected.get(i));
      borderMethodList.add(objective);
    }
  }

  /**
   * Envia um email com o resultado
   *
   * @param email   de destino
   * @param best    melhor solução
   * @param history histórico
   */
  private void enviarEmail( String email, Chromosome best, History history, String method ) {
    best.generateFitness();
    EmailDTO emailDTO = new EmailDTO();
    emailDTO.setDestinatario(email);
    emailDTO
      .setCorpo("<html>" + "<header><h2>Resultado do GA por " + method + ": " + best.getFitness() + "</h2></header>" +
        "<main" +
        ">" +
        "<section><h4>Detalhes</h4>" + "<p>Tempo de execução: " + history
        .getTimeExec() + " segundos</p>" + "<p>Taxa de reprodução: " + params
        .getReproductionRate() + "%</p>" + "<p>Modo de reprodução: " + params
        .getReproductionMode() + "</p>" + "<p>População: " + params
        .getPopulationLimit() + "</p>" + "<p>Capacidade máxima da mochila: " + params
        .getStorageLimit() + "</p>" + "<p>Modo de seleção: " + params
        .getSelectionMode() + "</p>" + "<p>probabilidade de mutação: " + params
        .getProbabilityMutation() + "%</p>" + "<p>K: " + params.getK() + "</p>" + "<p>Y: " + params
        .getY() + "</p>" + "<p>M: " + params
        .getM() + "</p>" + "<p>Ocorreu convergência nas gerações: " + history.getGeneticConvertion().stream()
        .map(geneticHistory -> " " + geneticHistory.getGeneration()) + "</p>" + "<p><strong>Melhor fitness: " + best
        .getFitness() + "</strong> da geração #" + best.getGeneration() + " com peso total de " + best
        .getWeight() + "<p>custo de:" + best.getTotalCost() + " </p>" + "<p>utilidade de: " + best
        .getDeltaUtility() + " </p>" + " e " + best.getGenes()
        .size() + " itens<p>" + "</section>" + "<section><h4>Melhor Individuo</h4><br><code>" + best
        .toHtml() + "</code></section>" +
        "<section><h4>Frente de pareto</h4><code>" +
        history.getParetoFront().stream().map(chromosome -> "&ensp; <p>" + chromosome.toHtml() + "<br></p>")
          .collect(Collectors.toList())
        + "</code></section>"
        + "</main>" + "</html>");
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

  /**
   * Agrupo por dominancia os individuos <br>
   * Individuos com menor grau de dominacia vem em primeiro
   *
   * @param population População a ser analisada
   * @return A população organizadas em grupos de dominancia
   */
  private List<List<Chromosome>> groupByDominance( List<Chromosome> population ) {
    List<Chromosome> notDominated = new ArrayList<>(population.size() / 3);
    List<Chromosome> partialDominated = new ArrayList<>(population.size() / 3);
    List<Chromosome> dominated = new ArrayList<>(population.size() / 3);
    List<Chromosome> invalids = new ArrayList<>(population.size() / 3);
    for (Chromosome actual : population) {
      if (actual.getWeight() > params.getStorageLimit()) invalids.add(actual);
      else if (population.stream().noneMatch(
        chromosome -> chromosome.getTotalCost() < actual.getTotalCost() && chromosome.getTotalUtility() > actual
          .getTotalUtility()))
        notDominated.add(actual);
      else if (population.stream().noneMatch(
        chromosome -> chromosome.getTotalCost() < actual.getTotalCost() || chromosome.getTotalUtility() > actual
          .getTotalUtility()))
        partialDominated.add(actual);
      else dominated.add(actual);
    }
    log.info("Quantidade de individuos não dominados: {}", notDominated.size());
    log.info("Quantidade de individuos não parcialmente dominados: {}", partialDominated.size());
    log.info("Quantidade de individuos não completamente dominados: {}", dominated.size());
    log.info("Quantidade de individuos não inv[alidos: {}", invalids.size());
    List<List<Chromosome>> fronts = new ArrayList<>(3);
    dominated.addAll(invalids);
    fronts.add(notDominated);
    fronts.add(partialDominated);
    fronts.add(dominated);
    return fronts;
  }

  /**
   * Calcula a distancia entre os individuos nos objetivos
   *
   * @param front Conjunto de individuos a serem analisados
   * @return O individuo com a maior distancia entre os objetivos no grupo
   */
  private Chromosome calculateCrowdingDistance( List<Chromosome> front ) {
    if (front.size() == 1) return front.get(0);
    front.sort(Chromosome.BY_UTILITY);
    int last = front.size() - 1;
    for (int i = 0; i < front.size(); i++) {
      if (i == 0)
        front.get(i).setDeltaUtility(
          front.get(i + 1).getTotalUtility() / (front.get(0).getTotalUtility() - front.get(last).getTotalUtility()));
      else if (i == last)
        front.get(i).setDeltaUtility(
          front.get(i - 1).getTotalUtility() / (front.get(0).getTotalUtility() - front.get(last).getTotalUtility()));
      else
        front.get(i).setDeltaUtility(
          front.get(i - 1).getTotalUtility() - front.get(i + 1).getTotalUtility() / (front.get(0)
            .getTotalUtility() - front.get(last).getTotalUtility()));
    }
    front.sort(Chromosome.BY_COST);
    for (int i = 0; i < front.size(); i++) {
      if (i == 0)
        front.get(i).setDeltaCost(
          front.get(i + 1).getTotalCost() / (front.get(0).getTotalCost() - front.get(last).getTotalCost()));
      else if (i == last)
        front.get(i).setDeltaCost(
          front.get(i - 1).getTotalCost() / (front.get(0).getTotalCost() - front.get(last).getTotalCost()));
      else
        front.get(i).setDeltaCost(
          front.get(i - 1).getTotalCost() - front.get(i + 1).getTotalCost() / (front.get(0).getTotalCost() - front
            .get(last).getTotalCost()));
    }

    return front.stream().max(Chromosome.BY_CROWDING_DISTANCE).get();
  }

  /**
   * Elimina métade da popualção com a dominancia como primeiro critério e pela distancia entre  eles sendo o segundo
   * critério
   *
   * @param fronts     Grupos a serem analisqados
   * @param population População a ser limpa
   */
  private void killByDominance( List<List<Chromosome>> fronts, List<Chromosome> population ) {
    log.info("Eliminando individuos por dominancia");
    int prev = population.size();
    List<Chromosome> newPopulation = new ArrayList<>(params.getPopulationLimit() * 2);
    fronts.forEach(newPopulation::addAll);
    population.retainAll(newPopulation.subList(0, params.getPopulationLimit()));
    log.info("{} Individuos elimados", prev - population.size());
  }

  /**
   * Algoritimo de análise multicritério para evolução da popualção
   *
   * @param population População a ser analisada
   * @param generation Geração atual
   */
  private void nsga( List<Chromosome> population, int generation ) {
    DominanceHistory dominanceHistory = new DominanceHistory();
    dominanceHistory.setGeneration(generation);
    List<List<Chromosome>> fronts = groupByDominance(population);
    for (List<Chromosome> front : fronts) {
      if (front.size() > 0) {
        ParetoFrontHistory paretoFrontHistory = new ParetoFrontHistory();
        //        paretoFrontHistory.setFront(front);
        paretoFrontHistory.setBest(calculateCrowdingDistance(front));
        paretoFrontHistory.setCrowdingDistance(
          paretoFrontHistory.getBest().getDeltaCost() + paretoFrontHistory.getBest().getDeltaUtility());
        dominanceHistory.getHistory().add(paretoFrontHistory);
      }
    }
    evolutionHistory.getDominanceHistories().add(dominanceHistory);
    killByDominance(fronts, population);
  }
}