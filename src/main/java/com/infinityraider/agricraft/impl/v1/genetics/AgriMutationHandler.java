package com.infinityraider.agricraft.impl.v1.genetics;

import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationEngine;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import net.minecraft.util.Tuple;

import java.util.Optional;
import java.util.Random;

/**
 * This class decides whether a plant is spreading or mutating and also calculates the new stats
 * (growth, gain, strength, fertility, resistance, etc.) of the new plant based on the 4 neighbours.
 */
public final class AgriMutationHandler implements IAgriMutationHandler {
    private static final AgriMutationHandler INSTANCE = new AgriMutationHandler();

    public static AgriMutationHandler getInstance() {
        return INSTANCE;
    }

    // Encapsulates the logic
    private final IAgriMutationEngine defaultEngine;

    // Mutates individual genes
    private final IMutator<IAgriPlant> defaultPlantMutator;
    private final IMutator<Integer> defaultStatMutator;

    private IAgriMutationEngine engine;
    private IMutator<IAgriPlant> plantMutator;
    private IMutator<Integer> statMutator;

    private AgriMutationHandler() {
        this.defaultEngine = new AgriMutationEngine();
        this.defaultPlantMutator = new AgriPlantMutator();
        this.defaultStatMutator = new AgriStatMutator();
        this.setActiveMutationEngine(this.getDefaultMutationEngine())
                .setActivePlantMutator(this.getDefaultPlantMutator())
                .setActiveStatMutator(this.getDefaultStatMutator());
    }

    public IAgriMutationEngine getEngine() {
        return this.engine;
    }

    public void setEngine(IAgriMutationEngine engine) {
        this.engine = engine;
    }

    @Override
    public IAgriMutationHandler setActiveMutationEngine(IAgriMutationEngine engine) {
        this.engine = engine;
        return this;
    }

    @Override
    public IAgriMutationEngine getActiveMutationEngine() {
        return this.engine;
    }

    @Override
    public IAgriMutationEngine getDefaultMutationEngine() {
        return this.defaultEngine;
    }

    @Override
    public IAgriMutationHandler setActivePlantMutator(IMutator<IAgriPlant> mutator) {
        this.plantMutator = mutator;
        return this;
    }

    @Override
    public IMutator<IAgriPlant> getActivePlantMutator() {
        return this.plantMutator;
    }

    @Override
    public IMutator<IAgriPlant> getDefaultPlantMutator() {
        return this.defaultPlantMutator;
    }

    @Override
    public IAgriMutationHandler setActiveStatMutator(IMutator<Integer> mutator) {
        this.statMutator = mutator;
        return this;
    }

    @Override
    public IMutator<Integer> getActiveStatMutator() {
        return this.statMutator;
    }

    @Override
    public IMutator<Integer> getDefaultStatMutator() {
        return this.defaultStatMutator;
    }

    // Default AgriCraft plant mutation logic
    public static class AgriPlantMutator implements IMutator<IAgriPlant> {
        public AgriPlantMutator() {}

        @Override
        public IAgriGenePair<IAgriPlant> pickOrMutate(IAgriGene<IAgriPlant> gene, IAllel<IAgriPlant> first, IAllel<IAgriPlant> second,
                                                      Tuple<IAgriGenome, IAgriGenome> parents, Random random) {

            return AgriMutationRegistry.getInstance().stream()
                    // scan the mutation registry
                    .filter(mut -> mut.hasParent(first.trait()) && mut.hasParent(second.trait()))
                    // find a matching mutation
                    .findAny()
                    // map it to its child, or to nothing based on the mutation success rate
                    .flatMap(mutation -> Optional.ofNullable(mutation.getChance() > random.nextDouble() ? mutation.getChild() : null))
                    // map the result to a new gene pair with either of its parents as second gene
                    .map(plant -> gene.generateGenePair(gene.getAllel(plant), random.nextBoolean() ? first : second))
                    // if no mutation was found or if the mutation was unsuccessful, return a gene pair of the parents
                    .orElse(gene.generateGenePair(first, second));
        }
    }

    // Default AgriCraft stat mutation logic
    public static class AgriStatMutator implements IMutator<Integer> {
        public AgriStatMutator() {}

        @Override
        public IAgriGenePair<Integer> pickOrMutate(IAgriGene<Integer> gene, IAllel<Integer> first, IAllel<Integer> second,
                                                Tuple<IAgriGenome, IAgriGenome> parents, Random random) {
            // return new gene pair with or without mutations, based on mutativity stat
            return gene.generateGenePair(
                    this.rollAndExecuteMutation(gene, first, parents.getA().getStats().getValue(AgriStatRegistry.getInstance().mutativityStat()), random),
                    this.rollAndExecuteMutation(gene, second, parents.getB().getStats().getValue(AgriStatRegistry.getInstance().mutativityStat()), random)
            );
        }

        protected IAllel<Integer> rollAndExecuteMutation(IAgriGene<Integer> gene, IAllel<Integer> allel, int stat, Random random) {
            // Mutativity stat of 1 results in 25/50/25 probability of positive/no/negative mutation
            // Mutativity stat of 10 results in 100/0/0 probability of positive/no/negative mutation
            int max = AgriStatRegistry.getInstance().defaultMax();
            if(random.nextInt(AgriStatRegistry.getInstance().defaultMax()) < stat) {
                int delta = random.nextInt(max) < (max + stat)/2 ? 1 : -1;
                return gene.getAllel(allel.trait() + delta);
            } else {
                return allel;
            }
        }
    }
}