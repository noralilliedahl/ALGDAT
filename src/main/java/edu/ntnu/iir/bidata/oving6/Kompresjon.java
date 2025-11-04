package edu.ntnu.iir.bidata.oving6;

import java.io.*;
import java.util.Arrays;


class BitWriter {

  private int buffer;
  private int usedBits;
  private BufferedOutputStream out;

  public BitWriter(OutputStream out) {
    this.out = new BufferedOutputStream(out);
    this.usedBits = 0;
    this.buffer = 0;
  }

  void writeBit(int bit) throws IOException {
    bit = (bit & 1);
    buffer = ((buffer << 1) | bit) & 0xFF;
    usedBits ++;

    if (usedBits == 8) {
      out.write(buffer);
      buffer = 0;
      usedBits = 0;
    }
  }

  void writeBits(long value, int length) throws IOException {
    if (length < 0) throw new IllegalArgumentException("length < 0");
    for (int i = length - 1; i >= 0; i--) {
      int bit = (int) ((value >> i) & 1L);
      writeBit(bit);
    }
  }

  public void flush() throws IOException {
    if (usedBits > 0) {
      buffer = buffer << (8 - usedBits) & 0xFF;
      out.write(buffer);
      buffer = 0;
      usedBits = 0;
    }
    out.flush();
  }

  void close() throws IOException {
    flush();
    out.close();
  }
}

class BitReader {

  private int buffer;
  private int remainingBits;
  private BufferedInputStream in;

  public BitReader(InputStream in) {
    this.in = new BufferedInputStream(in);
    this.buffer = 0;
    this.remainingBits = 0;
  }

  int readBit() throws IOException {
    if (remainingBits == 0) {
      int b = in.read();
      if (b == -1) throw new EOFException();

      buffer = b & 0xFF;
      remainingBits = 8;
    }

    int bit = (buffer >> 7) & 1;
    buffer = (buffer << 1) & 0xFF;
    remainingBits --;
    return bit;
  }

  long readBits(int length) throws IOException {
    if (length < 0) throw new IllegalArgumentException("length < 0");
    long acc = 0;
    for (int i = 0; i < length; i++) {
      acc = (acc << 1) | readBit();
    }
    return acc;
  }

  void close() throws IOException {
    in.close();
  }
}

class FrequencyTable {

  private final int[] counts = new int[256]; //2^8

  public FrequencyTable() {

  }

  void reset() {
    Arrays.fill(counts, 0);
  }

  void count(InputStream in) throws IOException {
    int b;
    while ((b = in.read()) != -1) { counts[b & 0xFF]++; }
  }

  long total() {
    long sum = 0;
    for (int i = 0; i < 256; i++) {
      sum += counts[i];
    }
    return sum;
  }

  void writeTable(OutputStream out) throws IOException {
    DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(out));
    for (int i = 0; i < 256; i++) {
      dout.writeInt(counts[i]);
    }
    dout.flush();
  }

  void readTable(InputStream in) throws IOException {
    DataInputStream din = new DataInputStream(new BufferedInputStream(in));
    for (int i = 0; i < 256; i++) {
      counts[i] = din.readInt();
    }
  }
}

class Huffman {

  class Node {

    long frequency;
    int symbol;
    Node left, right;

    public Node(long frequency, int symbol) {
      this.frequency = frequency;
      this.symbol = symbol;
      this.left = null;
      this.right = null;
    }

    public Node(Node left, Node right) {
      this.frequency = left.frequency + right.frequency;
      this.symbol = -1;
      this.left = left;
      this.right = right;
    }

    boolean isLeaf() { return left == null && right == null; }

  }
}


