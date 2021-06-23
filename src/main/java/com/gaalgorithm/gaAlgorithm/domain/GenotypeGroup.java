package com.gaalgorithm.gaAlgorithm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class GenotypeGroup {
  private List<Boolean> genotype;
  private List<Chromosome> populationGrouped = new ArrayList<>();

  /**
   * Adiciona um individuo no grupo
   *
   * @param group      alvo
   * @param chromosome a ser adicionado
   * @return grupo
   */
  public static GenotypeGroup addToGroup( GenotypeGroup group, Chromosome chromosome) {
    group.setGenotype(chromosome.getGenes());
    group.getPopulationGrouped().add(chromosome);
    return group;
  }

  /**
   * Avalia se o cromossome se encaixa no grupo
   *
   * @param group       alvo
   * @param chromosome  a ser avaliado
   * @param maxDistance distancia entre os individuos
   * @return verdadeiro se o individuo se classifica no grupo
   */
  public static boolean evalueteGenotype( GenotypeGroup group, Chromosome chromosome, int maxDistance ) {
    int count = 0;
    for (int i = 0; i < group.getGenotype().size(); i++) {
      if ( chromosome.getGenes().get(i) != group.getGenotype().get(i) ) count++;
    }
    return count < maxDistance;
  }
}
