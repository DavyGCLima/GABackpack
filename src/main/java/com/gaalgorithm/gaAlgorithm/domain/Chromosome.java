package com.gaalgorithm.gaAlgorithm.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Chromosome implements Serializable{
    private List<Item> genes = new ArrayList<>();

    public int generateFitness() {
        int result = 0;
        for (Item item : genes) {
            result = result + item.getCoast();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chromosome that = (Chromosome) o;
        return genes.equals(that.genes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genes);
    }
}
