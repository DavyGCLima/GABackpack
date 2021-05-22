package com.gaalgorithm.gaAlgorithm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Item implements Serializable {
    private Integer coast;
    private Integer weight;
    private Boolean used;

    public Item(int coast, int weight, Boolean used) {
        this.coast = coast;
        this.weight = weight;
        this.used = used;
    }

    @Override
    public boolean equals(Object o) {
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
