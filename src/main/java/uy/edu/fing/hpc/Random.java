package uy.edu.fing.hpc;

public class Random {
    public static int getRandomIndex(int size) {
        return nextInt(size);
    }

    public static int nextInt(int bound) {
        return (int) (Math.random() * bound);
    }

    public static double nextDouble() {
        return Math.random();
    }
}
