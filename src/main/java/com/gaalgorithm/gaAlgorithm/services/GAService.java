package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
import com.gaalgorithm.gaAlgorithm.domain.GenotypeGroup;
import com.gaalgorithm.gaAlgorithm.domain.History;
import com.gaalgorithm.gaAlgorithm.domain.Item;
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
  private List defaultItems = new ArrayList<>();
  private final ProdutorServico produtorServico;
  private long execTime;
  private RequestParamsDTO params;
  History evolutionHistory = new History();;


  /**
   * Cria itens para o problema da mochila randomicamente, somente baseado no limite da mochila.
   * Nenhum Item pode ultrapssar o limite da propria mochila por sí apenas
   *
   * @param populationLimit limite da população
   * @param storageLimit    limite da mochila
   * @return itens a serem usados
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

  private List<Item> getStaticItems() {
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
    defaultItems = items;
    return items;
  }

  private List<Item> generateItems( Optional<List<Item>> items ) {
    if (defaultItems.size() > 0) return defaultItems;
    // gera os items que serão usados
    if (items.isPresent() && items.get().size() > 0) {
      defaultItems = items.get();
      return defaultItems;
    }
    return getStaticItems();
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
    Float reprodutionNumber = ((float) reproductionRate / (float) 100F) * population.size();
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
   * @return
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
          selectedToMutate.getGenes().set(random.nextInt(selectedToMutate.getGenes().size()),
            generateItems(null).get(random.nextInt(selectedToMutate.getGenes().size())));
          selectedToMutate.setFitness(selectedToMutate.generateFitness());
        }
      }
    }
  }

  /**
   * Método principal da evolução, é um metodo recursivo que resultará em uma solução
   *
   * @param population       população a ser utilizada
   * @param generation       geração atual
   */
  private void evolve( List<Chromosome> population, int generation) {
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
        evolutionHistory.getGeneticConvertion().add(generation);
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
    log.info("Ocorreu convergencia em: {}", history.getGeneticConvertion());
    log.info("Itens usados: {}", best.getGenes());
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
    emailDTO.setCorpo(
      "<html>" +
        "<header><h2>Resultado do GA: "+best.getFitness()+"</h2></header>" +
        "<main>" +
          "<section><h4>Detalhes</h4>"+
            "<p>Tempo de execução: "+ history.getTimeExec()+" segundos</p>"+
            "<p>Taxa de reprodução: "+ params.getReproductionRate()+" segundos</p>"+
            "<p>Modo de reprodução: "+ params.getReproductionMode()+" segundos</p>"+
            "<p>População inicial: "+ params.getPopulationLimit()+" segundos</p>"+
            "<p>Capacidade máxima da mochila: "+ params.getStorageLimit()+" segundos</p>"+
            "<p>Modo de seleção: "+ params.getSelectionMode()+" segundos</p>"+
            "<p>probabilidade de mutação: "+ params.getProbabilityMutation()+" segundos</p>"+
            "<p>K: "+ params.getK()+" segundos</p>"+
            "<p>Y: "+ params.getY()+" segundos</p>"+
            "<p>M: "+ params.getM()+" segundos</p>"+
            "<p>Ocorreu convergência nas gerações: "+history.getGeneticConvertion()+"</p>"+
            "<p><strong>Melhor fitness: "+best.getFitness()+"</strong> da geração #"+best.getGeneration()+" com peso total de "+best.getWeight()+" e "+best.getGenes().size()+" itens<p>" +
          "</section>" +
          "<section><h4>Melhor Individuo</h4><br><code>" + best.toHtml() + "</code></section>" +
        "</main>" +
      "</html>");
    emailDTO.setAssunto("Resultado do algoritmo GA: "+best.getFitness());
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