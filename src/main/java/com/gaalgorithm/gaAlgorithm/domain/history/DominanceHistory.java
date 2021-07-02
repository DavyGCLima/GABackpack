package com.gaalgorithm.gaAlgorithm.domain.history;

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
public class DominanceHistory {
  List<ParetoFrontHistory> history = new ArrayList<>();
  int generation;
}
