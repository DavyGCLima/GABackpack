package com.gaalgorithm.gaAlgorithm.services;
import com.gaalgorithm.gaAlgorithm.domain.Chromosome;
import com.gaalgorithm.gaAlgorithm.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GAService {
     private Set<Integer> generated = new LinkedHashSet<Integer>();
     private Random random = new Random();

    private int getNextRandom(int bound, int max) {
        while (generated.size() < max) {
            Integer next = random.nextInt(bound);
            if(generated.add(next))
                return next;

        }
        return random.nextInt(bound);
    }

    public void start(int reproductionRate, float probabilityMutation, int populationLimit, int maxStorageWight, int storageLimit) {
        // generate existed items
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < populationLimit; i++) {
            items.add(new Item(random.nextInt(100), random.nextFloat(), false));
        }
        //generate first population
        Set<Chromosome> population = new HashSet<>();
        for (int i = 0; i < populationLimit; i++) {
            Chromosome chromosome = new Chromosome();
            Item[] solution = new Item[populationLimit];
            // reset to genrate new positions
            generated = new LinkedHashSet<Integer>();
            for (int j = 0; j < populationLimit; j++) {
                solution[getNextRandom(populationLimit, populationLimit)] = items.get(j);
            }
            for (Item item : solution) {
                chromosome.getGenes().add(item);
            }
            population.add(chromosome);
        }
        System.out.println(population);
    }
}
