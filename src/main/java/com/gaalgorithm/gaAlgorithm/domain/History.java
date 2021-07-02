package com.gaalgorithm.gaAlgorithm.domain;

import com.gaalgorithm.gaAlgorithm.domain.history.DominanceHistory;
import com.gaalgorithm.gaAlgorithm.domain.history.GeneticConvertionHistory;
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
public class History {
  List<Chromosome> bests = new ArrayList<>();
  List<GeneticConvertionHistory> geneticConvertion = new ArrayList<>();
  List<DominanceHistory> dominanceHistories = new ArrayList<>();
  Double timeExec = 0D;
  Chromosome best;
}
