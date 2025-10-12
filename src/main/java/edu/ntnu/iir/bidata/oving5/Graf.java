package edu.ntnu.iir.bidata.oving5;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

  public Graf lesGrafFraFil(Path p) {

    try (BufferedReader br = Files.newBufferedReader(p)){
      String[] noderOgKanter = br.readLine().trim().split("\\s+");
      int noder = Integer.parseInt(noderOgKanter[0]);
      int kanter = Integer.parseInt(noderOgKanter[1]);
      Graf g = new Graf(noder);

      for (int i = 0; i < kanter; i++) {
        String[] tilFra = br.readLine().trim().split("\\s+");
        int fra = Integer.parseInt(tilFra[0]);
        int til = Integer.parseInt(tilFra[1]);
        g.leggTilKant(fra, til);
      }
      if (kanter != g.getAntKant()) {
        throw new IllegalArgumentException("fallback");
      }
      return g;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
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

class GrafAlgoritme {

  public int[] distanse;
  public int[] forgjenger;

  public int[] status;
  public Deque<Integer> rekkefolge;
  public boolean harSyklus;

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

  public int[] getDistanse() {
    return distanse;
  }

  public int[] getForgjenger() {
    return forgjenger;
  }

  public void topoAlgoritme(Graf g) {
    topoInitializer(g);

    for (int i = 0; i < g.antNoder; i++) {
      if (ubesokt(i)) {
        dfs(i, g);
        if (harSyklus) {
          throw new IllegalStateException("Grafen kan ikke sorteres topologisk.");
        }
      }
    }
  }

  private void topoInitializer(Graf g) {
    harSyklus = false;
    int n = g.antNoder;
    status = new int[n];
    for (int i = 0; i < n; i++) {
      status[i] = 0; // 0 = ikke besøkt
    }
    rekkefolge = new ArrayDeque<>(n);
  }

  private boolean underBehandling(int node) {
    return status[node] == 1;
  }

  private boolean ferdig(int node) {
    return status[node] == 2;
  }

  private boolean ubesokt(int node) {
    return status[node] == 0;
  }

  private void dfs(int node, Graf g) {

     if (harSyklus || ferdig(node)) { return; }
     if (underBehandling(node)) { harSyklus = true; return; }

     status[node] = 1;

     for (int nabo : g.naboer(node)) {
       if (harSyklus) { return; }
       if (underBehandling(nabo)) { harSyklus = true; return; }
       if (ubesokt(nabo)) { dfs(nabo, g);
        if (harSyklus) { return; }
       }
    }
     status[node] = 2;
     rekkefolge.addFirst(node);
  }

  public Deque<Integer> getRekkefolge() {
    return rekkefolge;
  }

  public boolean getSyklus(){
    return harSyklus;
  }

  public bfsResultat getResultat(Graf g, int start) {
    bfsAlgoritme(g, start);
    int[] d = getDistanse();
    int[] f = getForgjenger();
    return new bfsResultat(start, d, f);
  }

  public topoResultat getResultat(Graf g) {
    topoAlgoritme(g);
    Deque<Integer> r = getRekkefolge();
    return new topoResultat(r);
  }

  public static class bfsResultat {

    public int start;
    public int[] distanse;
    public int[] forgjenger;

    public bfsResultat(int start, int[] distanse, int[] forgjenger) {
      this.start = start;
      this.distanse = distanse.clone();
      this.forgjenger = forgjenger.clone();
    }
  }
  public static class topoResultat {

    public int[] rekkefolge;

    public topoResultat(Deque<Integer> rekkefolge) {
      this.rekkefolge = dequeTilArray(rekkefolge);
    }

    private static int[] dequeTilArray(Deque<Integer> dq) {
      return dq.stream().mapToInt(Integer::intValue).toArray();
    }
  }
}


