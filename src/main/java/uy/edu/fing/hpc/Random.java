package uy.edu.fing.hpc;

import java.util.concurrent.ThreadLocalRandom;

public class Random {
    public static int getRandomIndex(int size) {
        return nextInt(size);
    }

    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }
}
