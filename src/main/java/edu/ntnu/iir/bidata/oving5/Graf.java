package edu.ntnu.iir.bidata.oving5;

import java.util.ArrayList;
import java.util.List;

abstract class Graf {

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
  public void leggTilKant(Kant kant) {
    if (kant.tilNode >= antNoder || kant.fraNode >= antNoder) throw new IllegalArgumentException("fallback");
    kanter.add(kant);
    antKant++;
    naboListe[kant.fraNode].add(kant.tilNode);
  }
}
