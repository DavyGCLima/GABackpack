package com.gaalgorithm.gaAlgorithm.services.dto;

import com.gaalgorithm.gaAlgorithm.domain.Item;
import com.opencsv.bean.CsvToBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class RequestParamsDTO implements Serializable {
  private Integer reproductionRate;
  private List<Integer> bulkReproductionRate;
  private Integer probabilityMutation;
  private List<Integer> bulkProbabiityMutation;
  private Integer populationLimit;
  private List<Integer> bulkPopulationLimit;
  private Integer storageLimit;
  private List<Integer> bulkStorageLimit;
  private Integer selectionMode;
  private List<Integer> bulkSelectionMode;
  private Integer reproductionMode;
  private List<Integer> bulkReproductionMode;
  private String email;
  private Integer k;
  private List<Integer> bulkK;
  private Integer y;
  private List<Integer> bulkY;
  private Integer m;
  private List<Integer> bulkM;
  private Optional<List<Item>> items;

  public boolean checkBulkParams() {
    List<List<Integer>> checkList = new ArrayList<>();
    checkList.add(bulkReproductionRate);
    checkList.add(bulkProbabiityMutation);
    checkList.add(bulkPopulationLimit);
    checkList.add(bulkStorageLimit);
    checkList.add(bulkSelectionMode);
    checkList.add(bulkReproductionMode);
    checkList.add(bulkK);
    checkList.add(bulkY);
    checkList.add(bulkM);
    return !checkList.stream().allMatch(list -> list.size() == checkList.get(0).size());
  }

  public RequestParamsDTO getBulkIndex(int index) {
    RequestParamsDTO instance = new RequestParamsDTO();
    instance.setReproductionRate(this.getBulkReproductionRate().get(index));
    instance.setProbabilityMutation(this.getBulkProbabiityMutation().get(index));
    instance.setPopulationLimit(this.getBulkPopulationLimit().get(index));
    instance.setStorageLimit(this.getBulkStorageLimit().get(index));
    instance.setReproductionMode(this.getBulkReproductionMode().get(index));
    instance.setSelectionMode(this.getBulkSelectionMode().get(index));
    instance.setK(this.getBulkK().get(index));
    instance.setY(this.getBulkY().get(index));
    instance.setM(this.getBulkM().get(index));
    instance.setItems(this.getItems());
    instance.setEmail(this.getEmail());
    return instance;
  }
}
