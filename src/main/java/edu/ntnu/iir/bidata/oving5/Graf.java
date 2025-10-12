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

  public int getAntKant() { return antKant; }

  public int getAntNoder() { return antNoder; }

  public int getRandomNode() {
    Random rand = new Random();
    return rand.nextInt(antNoder);
  }

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

  public String getResultatBFS(Graf g, int start) {
    bfsAlgoritme(g, start);
    int[] d = getDistanse();
    int[] f = getForgjenger();
    return new bfsResultat(start, d, f).toString();
  }

  public String getResultatTOPO(Graf g) {
    topoAlgoritme(g);
    Deque<Integer> r = getRekkefolge();
    return new topoResultat(r).toString();
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

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Node  Forgj  Dist\n");
      for (int v = 0; v < distanse.length; v++) {
        sb.append(String.format("%4d  %5s  %4s",
          v,
          (forgjenger[v] == -1 ? "-" : Integer.toString(forgjenger[v])),
          (distanse[v]   == -1 ? "-" : Integer.toString(distanse[v]))
        ));
        if (v < distanse.length - 1) sb.append('\n');
      }
      return sb.toString();
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

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < rekkefolge.length; i++) {
        if (i > 0) sb.append(' ');
        sb.append(rekkefolge[i]);
      }
      return sb.toString();
    }
  }
}

class Main {

  public static Graf lesGrafFraFil(Path p) {

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
        throw new IllegalStateException("Feil under lesing.");
      }
      return g;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {

    GrafAlgoritme ga = new GrafAlgoritme();

    /**
     * Nedenfor er kommentert ut kode som printer resultatene for alle
     * grafene fra oppgaven i ett kjør. g1, g2 osv..
     *
     * Ellers kan den enklere varianten (som ikke er kommentert ut) brukes,
     * hvor man bare justerer på filene som blir lest i koden.
     *
     * Whatever floats your boat
     *
     * Startnoden for BFS blir satt av en random-metode for å
     * illustrere at søket blir utført for vilkålige noder.
     *
     * Denne kan også eventuelt justeres ved retting.
     */

    try {

    /*
    for (int i = 1; i <= 7; i++) {
      if (i == 4 || i == 6) { continue; }
      Graf gBFS = lesGrafFraFil(Path.of("ø5g" + i + ".txt"));
      assert gBFS != null;
      int startNode = gBFS.getRandomNode();
      System.out.println(ga.getResultatBFS(gBFS, startNode);
    }

    for (int i = 5; i <= 7; i++) {
      if (i == 6) { continue; }
      Graf gTOPO = lesGrafFraFil(Path.of("ø5g" + i + ".txt"));
      System.out.println(ga.getResultatTOPO(gTOPO));
    }
     */

      //Enkel variant:):)
      Graf gBFS = lesGrafFraFil(Path.of("ø5g2.txt"));

      //Vilkålig node
      int startNode = gBFS.getRandomNode();
      System.out.println("Startnode: " + startNode);
      System.out.println(ga.getResultatBFS(gBFS, startNode));

      Graf gTOPO = lesGrafFraFil(Path.of("ø5g7.txt"));
      System.out.println("Topologisk sortert graf: " + ga.getResultatTOPO(gTOPO));

      Graf grafVilFeile = lesGrafFraFil(Path.of("ø5g2.txt"));
      System.out.println("Topologisk sortert graf: " + ga.getResultatTOPO(grafVilFeile));

    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}


