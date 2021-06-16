package com.gaalgorithm.gaAlgorithm.domain;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Define um gene
 */
@Getter
@Setter
@NoArgsConstructor
public class Item implements Serializable {

  @CsvBindByName
  private int id;

  @CsvBindByName
  private Float weight;

  @CsvBindByName
  private Float utility;

  @CsvBindByName
  private Float coast;

  public Item( float coast, float weight, float utility, Boolean used ) {
    this.coast = coast;
    this.weight = weight;
    this.utility = utility;
  }

  @Override
  public String toString() {
    return "Item{ id=" + id + "coast=" + coast + ", utility=" + utility + ", weight=" + weight + '}';
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
