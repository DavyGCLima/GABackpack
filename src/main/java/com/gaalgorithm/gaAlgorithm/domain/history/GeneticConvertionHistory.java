package com.gaalgorithm.gaAlgorithm.domain.history;

import com.gaalgorithm.gaAlgorithm.domain.GenotypeGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GeneticConvertionHistory {
  List<GenotypeGroup> genotypeGroups = new ArrayList<>();
  Integer generation;
}
