package edu.ntnu.iir.bidata.oving5;

abstract class Graf {

  public int dist;

  public static class Kant {

    public int fraNode;
    public int tilNode;

    public Kant(int fraNode, int tilNode) {
      this.fraNode = fraNode;
      this.tilNode = tilNode;
    }
  }
}
