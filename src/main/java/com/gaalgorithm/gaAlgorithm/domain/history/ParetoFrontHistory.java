package com.gaalgorithm.gaAlgorithm.domain.history;

import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
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
public class ParetoFrontHistory {
  List<Chromosome> front = new ArrayList<>();
  Chromosome best;
  float crowdingDistance;
}
