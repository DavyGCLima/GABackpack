package com.gaalgorithm.gaAlgorithm.services.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RequestParamsDTO implements Serializable {
  private Integer reproductionRate;
  private Integer probabilityMutation;
  private Integer populationLimit;
  private Integer storageLimit;
  private Integer selectionMode;
  private String email;
}
