package gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import reader.MapData;

public class GamePanel extends JPanel implements KeyListener {

  private boolean mapLoaded = false;
  private int playerRow = -1;
  private int playerColumn = -1;
  private int rows;
  private int columns;
  private char[][] map;
  private char[][] items;

  private BufferedImage BRICK_SPRITE;
  private BufferedImage GOAL_SPRITE;
  private BufferedImage CRATE_SPRITE;
  private BufferedImage CRATE_ON_GOAL_SPRITE;
  private BufferedImage PLAYER_SPRITE;

  private final int UPPER_LEFT_X = 10;
  private final int UPPER_LEFT_Y = 10;
  private final int TILE_SIZE = 32;

  private boolean freePlay = false;

  public GamePanel() {
    this.setBackground(Color.BLACK);
    loadImages();
    this.addKeyListener(this);
    this.setFocusable(true);
  }

  private void loadImages() {
    try {
      BRICK_SPRITE = ImageIO.read(new File("src/graphics/brick.png"));
      GOAL_SPRITE = ImageIO.read(new File("src/graphics/goal.png"));
      CRATE_SPRITE = ImageIO.read(new File("src/graphics/crate.png"));
      CRATE_ON_GOAL_SPRITE = ImageIO.read(new File("src/graphics/crategoal.png"));
      PLAYER_SPRITE = ImageIO.read(new File("src/graphics/robot.png"));
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }

  public void loadMap(MapData mapData) {
    map = new char[mapData.rows][mapData.columns];
    items = new char[mapData.rows][mapData.columns];
    int playerCount = 0;
    int boxCount = 0;
    int goalCount = 0;

    for (int i = 0; i < mapData.rows; i++) {
      for (int j = 0; j < mapData.columns; j++) {
        switch (mapData.tiles[i][j]) {
          case '#':
            map[i][j] = '#';
            items[i][j] = ' ';
            break;
          case '@':
            map[i][j] = ' ';
            items[i][j] = '@';
            playerCount++;
            playerRow = i;
            playerColumn = j;
            break;
          case '$':
            map[i][j] = ' ';
            items[i][j] = '$';
            boxCount++;
            break;
          case '.':
            map[i][j] = '.';
            items[i][j] = ' ';
            goalCount++;
            break;
          case '+':
            map[i][j] = '.';
            items[i][j] = '@';
            playerRow = i;
            playerColumn = j;
            playerCount++;
            goalCount++;
            break;
          case '*':
            map[i][j] = '.';
            items[i][j] = '$';
            boxCount++;
            goalCount++;
            break;
          case ' ':
            map[i][j] = ' ';
            items[i][j] = ' ';
            break;
        }
      }
    }

    rows = mapData.rows;
    columns = mapData.columns;

    if (playerCount == 1 && boxCount == goalCount && boxCount > 0) {
      freePlay = false;
      mapLoaded = true;
      this.repaint();
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, this.getWidth(), this.getHeight());

    if (mapLoaded) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
          BufferedImage target = null;
          if (map[i][j] == '#') {
            target = BRICK_SPRITE;
          } else if (map[i][j] == '.') {
            target = GOAL_SPRITE;
          }
          if (target != null) {
            g.drawImage(target, UPPER_LEFT_X + j * TILE_SIZE,
                UPPER_LEFT_Y + i * TILE_SIZE, TILE_SIZE, TILE_SIZE,
                this);
          }
          target = null;
          if (items[i][j] == '$' && map[i][j] == '.') {
            target = CRATE_ON_GOAL_SPRITE;
          } else if (items[i][j] == '$' && map[i][j] != '.') {
            target = CRATE_SPRITE;
          } else if (items[i][j] == '@') {
            target = PLAYER_SPRITE;
          }
          if (target != null) {
            g.drawImage(target, UPPER_LEFT_X + j * TILE_SIZE,
                UPPER_LEFT_Y + i * TILE_SIZE, TILE_SIZE, TILE_SIZE,
                this);
          }
        }
      }

      g.setColor(Color.GREEN);
      g.fillRect(0, this.getHeight() - 32, this.getWidth(), 32);
      g.setColor(Color.BLACK);
    }
  }

  public void initiateFreePlay() {
    freePlay = true;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (!freePlay)
      return;
    int ptRow = -1;
    int ptCol = -1;
    int btRow = -1;
    int btCol = -1;
    if (e.getKeyCode() == KeyEvent.VK_UP) {
      ptRow = playerRow - 1;
      ptCol = playerColumn;
      btRow = playerRow - 2;
      btCol = playerColumn;
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
      ptRow = playerRow + 1;
      ptCol = playerColumn;
      btRow = playerRow + 2;
      btCol = playerColumn;
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      ptRow = playerRow;
      ptCol = playerColumn - 1;
      btRow = playerRow;
      btCol = playerColumn - 2;
    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
      ptRow = playerRow;
      ptCol = playerColumn + 1;
      btRow = playerRow;
      btCol = playerColumn + 2;
    }

    if (ptRow < 0 || ptRow >= rows || ptCol < 0 || ptCol >= columns) {
      return;
    }
    if (map[ptRow][ptCol] == '#') {
      return;
    }
    if (items[ptRow][ptCol] != '$') {
      items[playerRow][playerColumn] = ' ';
      items[ptRow][ptCol] = '@';
      playerRow = ptRow;
      playerColumn = ptCol;
    } else if (items[ptRow][ptCol] == '$') {
      if (btRow < 0 || btRow >= rows || btCol < 0 || btCol >= columns) {
        return;
      }
      if (map[btRow][btCol] == '#' || items[btRow][btCol] == '$') {
        return;
      }
      items[btRow][btCol] = '$';
      items[playerRow][playerColumn] = ' ';
      items[ptRow][ptCol] = '@';
      playerRow = ptRow;
      playerColumn = ptCol;
    }

    this.repaint();
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }

  @Override
  public void keyTyped(KeyEvent e) {

  }
}
