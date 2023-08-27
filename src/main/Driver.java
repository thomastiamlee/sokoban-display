package main;

import gui.GameFrame;
import reader.FileReader;
import reader.MapData;

public class Driver {
  public static void main(String[] args) {
    FileReader fileReader = new FileReader();
    MapData mapData = fileReader.readFile("maptest");

    GameFrame gameFrame = new GameFrame(mapData);
  }
}
