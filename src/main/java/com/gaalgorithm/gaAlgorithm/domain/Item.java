package com.gaalgorithm.gaAlgorithm.domain;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Define um gene
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Item implements Serializable {

  @CsvBindByName
  private int id;

  @CsvBindByName
  private Float weight;

  @CsvBindByName
  private Float utility;

  @CsvBindByName
  private Float coast;

  public String toHtml() {
    return "\"Item\": { \"id\":" + id + "\", coast\":" + coast + ", \"utility\":" + utility + ", \"weight\":" + weight + '}';
  }

  @Override
  public boolean equals( Object o ) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Item item = (Item) o;
    return coast.equals(item.coast) && weight.equals(item.weight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coast, weight);
  }
}
