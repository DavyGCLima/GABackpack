package com.gaalgorithm.gaAlgorithm.services.dto;

import com.gaalgorithm.gaAlgorithm.domain.Item;
import com.opencsv.bean.CsvToBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class RequestParamsDTO implements Serializable {
  private Integer reproductionRate;
  private Integer probabilityMutation;
  private Integer populationLimit;
  private Integer storageLimit;
  private Integer selectionMode;
  private Integer reproductionMode;
  private String email;
  private Integer k;
  private Integer y;
  private Integer m;
  private Optional<List<Item>> items;
}
