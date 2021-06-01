package com.gaalgorithm.gaAlgorithm.domain;

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
  private Float coast;
  private Float utility;
  private Float weight;
  private Boolean used;

  public Item( float coast, float weight, float utility, Boolean used ) {
    this.coast = coast;
    this.weight = weight;
    this.utility = utility;
    this.used = used;
  }

  @Override
  public String toString() {
    return "Item{" + "coast=" + coast + ", utility=" + utility + ", weight=" + weight + ", used=" + used + '}';
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
