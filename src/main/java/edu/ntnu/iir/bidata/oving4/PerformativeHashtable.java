package edu.ntnu.iir.bidata.oving4;

abstract class PerformativeHashtable {

  protected int[] table;
  protected int capacity;
  protected int size = 0;
  protected int collisions = 0;
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

  public boolean insert(int key) {

    if (key == TOM) {
      throw new IllegalArgumentException("fallback");
    }

    int hashedValue = Math.floorMod(hash(key), capacity); // First position
    int tries = 0;
    int step = collisionStep(key);
    if (step == 0) step = 1;


    while (tries < capacity) {

      if (table[hashedValue] == key) {
        return false;
      }

      if (table[hashedValue] == TOM) {
        table[hashedValue] = key;
        size++;
        return true;
      }

      if (table[hashedValue] != TOM) {
        hashedValue = Math.floorMod((hashedValue + step), capacity);
        collisions++;
        tries++;
      }
    }
    return false;


  }
}

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


class Main {
  public static void main(String[] args) {

  }
}


