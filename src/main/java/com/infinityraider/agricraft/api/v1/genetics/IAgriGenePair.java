package com.infinityraider.agricraft.api.v1.genetics;

/**
 * Gene Pair for two alleles of a gene in a genome
 * @param <T> the type of the gene
 */
public interface IAgriGenePair<T> {
    /**
     * @return the gene for this gene pair
     */
    IAgriGene<T> getGene();

    /**
     * @return The apparent trait resulting form the two alleles (by default this is the dominant allel)
     */
    default T getTrait() {
        return this.getDominant().trait();
    }

    /**
     * @return the dominant allel
     */
    IAllele<T> getDominant();

    /**
     * @return the recessive allel
     */
    IAllele<T> getRecessive();

    /**
     * Clones this gene pair
     * @return a new gene pair identical to this one
     */
    IAgriGenePair<T> clone();

    /**
     * Checks if another gene pair is equal to this one
     * @param other the other gene pair
     * @return true if these are equal
     */
    default boolean equals(IAgriGenePair<T> other) {
        if(this == other) {
            return true;
        }
        return this.getDominant().equals(other.getDominant()) && this.getRecessive().equals(other.getRecessive());
    }
}
