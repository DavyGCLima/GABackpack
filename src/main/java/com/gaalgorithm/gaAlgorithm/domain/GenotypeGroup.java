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
  private List<Item> genotype = new ArrayList<>();
  private List<Chromosome> populationGrouped = new ArrayList<>();

  public static GenotypeGroup addToGroup(GenotypeGroup group, Chromosome chromosome) {
    group.setGenotype(chromosome.getGenes());
    group.getPopulationGrouped().add(chromosome);
    return group;
  }

  public static boolean evalueteGenotype(GenotypeGroup group, Chromosome chromosome, int maxDistance) {
    int count = 0;
    for (Item gene : group.getGenotype()) {
      if(!chromosome.getGenes().contains(gene)) count++;
    }
    if (count < maxDistance) return true;
    return false;
  }
}
