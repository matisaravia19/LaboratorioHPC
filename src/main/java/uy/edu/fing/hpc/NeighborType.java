package uy.edu.fing.hpc;

public enum NeighborType {
    SWITCH_TWO_RANDOM_CONTAINERS_IN_CIRCUIT,
    SWITCH_TWO_RANDOM_CONTAINERS_BETWEEN_CIRCUITS,
    MOVE_CONTAINER_BETWEEN_CIRCUITS,
    SPLIT_CIRCUITS,
    MERGE_CIRCUITS,
    ;

    public static NeighborType getRandom() {
        return values()[Random.getRandomIndex(values().length)];
    }
}
