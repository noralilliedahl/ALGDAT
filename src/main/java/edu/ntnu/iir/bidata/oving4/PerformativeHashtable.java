package edu.ntnu.iir.bidata.oving4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Abstract class for performative hash tables
 */

abstract class PerformativeHashtable {

  protected int[] table;
  protected int capacity;
  protected int size = 0;
  protected long collisions = 0L;
  protected static final int TOM = Integer.MIN_VALUE;

  public PerformativeHashtable(int capacity) {
    capacity = findNearestUpperPrime(capacity);
    this.capacity = capacity;
    this.table = new int[this.capacity];

    for (int i = 0; i < this.capacity; i++) {
      table[i] = TOM;
    }
  }

  public abstract int hash(int key);

  public abstract int collisionStep(int key);

  protected static int findNearestUpperPrime(int n) {
    if (n < 2) {
      n = 2;
    }
    while (true) {
      if (primeCheck(n)) {
        return n;
      }
      n++;
    }
  }

  /**
   * Prime-check; helper for {@code findNearestUpperPrime()}
   */
  private static boolean primeCheck(int n) {
    for (int i = 2; i <= Math.sqrt(n); i++) {
      if (n % i == 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Insert method for performative hash tables
   */

  public boolean insert(int key) {
    if (key == TOM) throw new IllegalArgumentException("key cannot be empty");

    final int[] t = this.table;
    final int cap = this.capacity;

    int pos = hash(key) % cap;
    if (pos < 0) pos += cap;

    int tries = 0;
    int step = 0;

    while (tries < cap) {
      final int cur = t[pos];

      if (cur == TOM) {
        t[pos] = key;
        size++;
        return true;
      }
      if (cur == key) {
        return false;
      }

      collisions++;

      if (step == 0) {
        int s = collisionStep(key) % cap;
        if (s <= 0) s = 1;
        step = s;
      }

      if (step == 1) {
        pos++;
        if (pos == cap) pos = 0;
      } else {
        pos += step;
        if (pos >= cap) pos -= cap;
      }

      tries++;
    }
    return false;
  }


  public int getCapacity() {
    return capacity;
  }

  public int getSize() {
    return size;
  }

  public long getCollisions() {
    return collisions;
  }

  public double getLoadFactor() {
    return size / (double) capacity;
  }

  public void clear() {
    java.util.Arrays.fill(table, TOM);
    size = 0;
    collisions = 0L;
  }
}

/**
 * Double Hashing
 */

  class DoubleHashing extends PerformativeHashtable {

    public DoubleHashing(int capacity) {
      super(capacity);
    }

    @Override
    public int hash(int key) {
      return Math.floorMod(key, capacity);
    }


    @Override
    public int collisionStep(int key) {
      return Math.floorMod(key, capacity - 1) + 1;
    }
  }

/**
 * Linear probing
 */
class LinearProbing extends PerformativeHashtable {

    public LinearProbing(int capacity) {
      super(capacity);
    }

    @Override
    public int hash(int key) {
      return Math.floorMod(key, capacity);
    }


    @Override
    public int collisionStep(int key) {
      return 1;
    }
  }

/**
 * Main method for performative hash tables (double and linear)
 */

class Main {

  private static int[] makeCumulativeShuffledKeys(int m, long seed) {

    int[] keys = new int[m];
    java.util.SplittableRandom rng = new java.util.SplittableRandom(seed);

    int v = 0;
    for (int i = 0; i < m; i++) {
      v += rng.nextInt(1, 101);
      keys[i] = v;
    }


    for (int i = m - 1; i > 0; i--) {
      int j = rng.nextInt(i + 1);
      int t = keys[i]; keys[i] = keys[j]; keys[j] = t;
    }
    return keys;
  }

  private static void doWarmup(PerformativeHashtable table, int[] keys, int n) {
    table.clear();
    for (int i = 0; i < n; i++) table.insert(keys[i]);
  }

  private static void runOnce(String method, PerformativeHashtable table, int[] keys, int n, int m) {
    table.clear();

    long t0 = System.nanoTime();
    for (int i = 0; i < n; i++) table.insert(keys[i]);
    long t1 = System.nanoTime();

    long timeNs = t1 - t0;
    double timeMs = timeNs / 1e6;
    long collisions = table.getCollisions();
    double alpha = n / (double) m;
    double cpi = collisions / (double) n;
    double nsPerInsert = timeNs / (double) n;

    System.out.printf(java.util.Locale.US,
      "%s,%.2f,%d,%d,%d,%.3f,%.6f,%.1f%n",
      method, alpha, m, n, collisions, timeMs, cpi, nsPerInsert);
  }

  public static void main(String[] args) {

    int m = 10_000_000;

    LinearProbing lp = new LinearProbing(m);
    DoubleHashing dh = new DoubleHashing(m);

    int[] keys = makeCumulativeShuffledKeys(lp.getCapacity(), 42L);

    int warm = Math.min(200_000, Math.max(50_000, m / 20));
    doWarmup(lp, keys, warm);
    doWarmup(dh, keys, warm);

    System.out.println("method,alpha,m,inserts,collisions,time_ms,collisions_per_insert,ns_per_insert");

    double[] ALPHAS = {0.50, 0.80, 0.90, 0.99, 1.00};
    for (double a : ALPHAS) {
      int n = (int) Math.round(a * m);

      runOnce("linear", lp, keys, n, lp.getCapacity());
      runOnce("double", dh, keys, n, lp.getCapacity());
    }
  }
}




