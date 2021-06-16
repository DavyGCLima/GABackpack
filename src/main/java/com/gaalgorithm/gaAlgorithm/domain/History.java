package com.gaalgorithm.gaAlgorithm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class History {
  List<Chromosome> bests = new ArrayList<>();
  List<Integer> geneticConvertion = new ArrayList<>();
}
