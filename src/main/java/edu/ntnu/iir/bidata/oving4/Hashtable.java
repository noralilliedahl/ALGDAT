package edu.ntnu.iir.bidata.oving4;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Manual implementation of a Hash Table for Strings
 */
public class Hashtable {

  private Node[] table;
  private int size;
  private int putCollisions = 0;
  private int searchCollisions = 0;

  /**
   * Prime number non-equal to the capacity and big enough
   * for collision avoidance
   */
  private static final int A = 151;

  /**
   * Manual implementation of LinkedList
   */
  public static class Node{

    public String key;
    public Node next;

    Node(String k, Node n){
      key = k;
      next = n;
    }
  }


  public Hashtable(int capacity) {
    table = new Node[capacity];
  }

  private int index(String k) {
    long h = 0;
    for (int i = 0; i < k.length(); i++) {
      h = h * A + k.charAt(i);
    }
    return Math.floorMod(h, table.length);
  }

  public void put(String k){
    int i = index(k);
    Node head = table[i];

    if (head != null && !head.key.equals(k)) {
      System.out.println("Collision (insert): " + k + " and " + head.key);
      putCollisions++;
    }

    Node cur = head;
    while (cur != null) {
      if (cur.key.equals(k)) {
        return;
      } else {
        cur = cur.next;
      }
    }
    table[i] = new Node(k, head);
    size++;
  }

  public boolean containsKey(String k) {
    int i = index(k);
    Node cur = table[i];

    if (cur != null && !cur.key.equals(k)) {
      System.out.println("Collision (search): " + k + " and " + cur.key);
      searchCollisions++;
    }

    while (cur != null) {
      if (cur.key.equals(k)) {
        return true;
      } else {
        cur = cur.next;
      }
    }
    return false;
  }

  /**
   * Getters
   */

  public int getSize() {
    return size;
  }

  public int getCapacity() {
    return table.length;
  }

  public double getLoadFactor() {
    return (double) size / table.length;
  }

  public long getPutCollisions() {
    return putCollisions;
  }

  public long getSearchCollisions() {
    return searchCollisions;
  }

  /**
   * Method for finding the nearest upper prime for a number.
   * Used to set capacity.
   */
  private static int findNearestUpperPrime(int n) {
    if (n < 2) {
      n = 2;
    } while (true) {
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
   * Main method
   */
  public static void main(String[] args) {

    try {

      List<String> file = Files.readAllLines(Paths.get("navn.txt"), UTF_8);
      List<String> names = new ArrayList<>();
      for (String name : file) {
        name = name.replace(",", " ");
        names.add(name);
      }

      int n = names.size();
      int capacity = findNearestUpperPrime((int) Math.ceil(n * 1.30));

      Hashtable h = new Hashtable(capacity);

      for (String name : names) {
        h.put(name);
      }

      System.out.println(h.getSize());
      System.out.println(h.getCapacity());
      System.out.println(h.getLoadFactor());
      System.out.println(h.getPutCollisions());
      System.out.println(h.getPutCollisions() / (double) h.getSize());
      System.out.println(h.containsKey("Nora Lilliedahl Kirknes"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}



