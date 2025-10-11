package edu.ntnu.iir.bidata.oving5;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

class Graf {

  protected int antNoder;
  protected int antKant;
  protected List<Kant> kanter;
  protected List<Integer>[] naboListe;

  public static class Kant {

    public final int fraNode;
    public final int tilNode;

    public Kant(int fraNode, int tilNode) {
      this.fraNode = fraNode;
      this.tilNode = tilNode;
    }
  }

  public Graf(int antNoder) {
    this.antNoder = antNoder;
    this.antKant = 0;
    this.kanter = new ArrayList<>();
    this.naboListe = new List[antNoder];
    for (int i = 0; i < antNoder; i++)
      naboListe[i] = new ArrayList<>();
  }

  public void leggTilKant(int fraNode, int tilNode) {
    if (tilNode >= antNoder || fraNode >= antNoder) throw new IllegalArgumentException("fallback");
    Kant kant = new Kant(fraNode, tilNode);
    kanter.add(kant);
    antKant++;
    naboListe[kant.fraNode].add(kant.tilNode);
  }

  public int getAntKant() { return antKant; }

  public int getAntNoder() { return antNoder; }

  public List<Integer> naboer(int id) {
    return List.copyOf(naboListe[id]);
  }

  public int antallNaboer(int id) {
    return naboListe[id].size();
  }
}

class GrafAlgoritmer {

  public int[] distanse;
  public int[] forgjenger;

  public void bfsAlgoritme(Graf g, int start) {

    bfsInitializer(g);
    Queue<Integer> ko  = new ArrayDeque<>();
    distanse[start] = 0;
    ko.offer(start);

    while (!ko.isEmpty()) {
      int node = ko.poll();
      for (int nabo : g.naboer(node)) {
        if (distanse[nabo] == -1) { //hvis ikke besøkt
          distanse[nabo] = distanse[node] + 1; //sett nabodistanse lik forgjenger + 1.
          forgjenger[nabo] = node; // sett forgjenger lik forrige
          ko.offer(nabo); //ferdig med nabo -> bakerst i kø
        }
      }
    }
  }

  private void bfsInitializer(Graf g) {
    distanse = new int[g.antNoder];
    forgjenger = new int[g.antNoder];
    for (int i = 0; i < g.antNoder; i++) {
      distanse[i] = -1; //ikke besøkt
      forgjenger[i] = -1; //uvisst
    }
  }

  public void topoAlgoritme(Graf g) {

  }

  public static bfsResultat getResultat(Graf g, int start) {
    return null;
  }

  public static topoResultat getResultat(Graf g) {
    return null;
  }

  public static class bfsResultat {}
  public static class topoResultat {}

}


