package com.gaalgorithm.gaAlgorithm.services.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestParamsDTO {
  private Integer reproductionRate;
  private Integer probabilityMutation;
  private Integer populationLimit;
  private Integer storageLimit;
  private Integer selectionMode;
}
