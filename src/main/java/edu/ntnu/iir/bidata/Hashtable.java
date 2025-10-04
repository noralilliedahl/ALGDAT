package edu.ntnu.iir.bidata;

import java.util.List;

public class Hashtable {

  private Node[] table;
  private List<String> names;
  private int size;
  private int collisions = 0;

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
      h = h * 151 + k.charAt(i);
    }
    return Math.floorMod(h, table.length);
  }

  public void put(String k){
    int i = index(k);
    Node head = table[i];

    if (head != null && !head.key.equals(k)) {
      collisions++;
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

    while (cur != null) {
      if (cur.key.equals(k)) {
        return true;
      } else {
        cur = cur.next;
      }
    }
    return false;
  }
}
