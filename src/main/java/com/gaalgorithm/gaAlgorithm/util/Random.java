package com.gaalgorithm.gaAlgorithm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Classe utilitária
 */
public class Random {
  /**
   * Gera um número inteiro aleatório que ainda não foi gerado e guardado em generated
   *
   * @param generated Armazena os valores já já gerados
   * @param bound     Limite máximo do alcance da geração
   * @param max       Limite de número a serem gerados
   * @param random    Instancia de suporte a geração randomica
   * @return Um número aleatório ainda não gerado
   */
  public static int getNextRandom( Set<Integer> generated, int bound, int max, java.util.Random random ) {
    while (generated.size() < max) {
      Integer next = random.nextInt(bound);
      if (generated.add(next)) return next;

    }
    return random.nextInt(bound);
  }

  /**
   * Gera um número inteiro aleatório que ainda não foi gerado e guardado em generated
   *
   * @param generated Armazena os valores já já gerados
   * @param bound     Limite máximo do alcance da geração
   * @param random    Instancia de suporte a geração randomica
   * @return Um número aleatório ainda não gerado
   */
  public static int getNextInt( Set<Integer> generated, int bound, java.util.Random random ) {
    Integer next = random.nextInt(bound);
    if (generated.add(next)) return next;
    return getNextInt(generated, bound, random);
  }

  /**
   * Gera um número inteiro aleatório que ainda não foi gerado e guardado em generated
   *
   * @param generated Armazena os valores já já gerados
   * @param bound     Limite máximo do alcance da geração
   * @param max       Limite de número a serem gerados
   * @param start     Indice do inicio da geração
   * @param random    Instancia de suporte a geração randomica
   * @return Um número aleatório ainda não gerado
   */
  public static int getNextRandom( Set<Integer> generated, int bound, int max, int start, java.util.Random random ) {
    while (generated.size() < max) {
      Integer next = random.nextInt(bound);
      if (next > start && generated.add(next)) return next;

    }
    return random.nextInt(bound);
  }

  /**
   * Gera uma lista de valores booleanos randomicos
   *
   * @param size tamanho da lista
   * @return Lista com valores randomicos
   */
  public static List<Boolean> getRandomBooleanList( int size ) {
    List<Boolean> list = new ArrayList<>(size);
    java.util.Random random = new java.util.Random();
    for (int i = 0; i < size; i++) {
      list.add(random.nextBoolean());
    }
    return list;
  }
}
