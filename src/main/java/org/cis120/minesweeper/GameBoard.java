package org.cis120.minesweeper;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.cis120.minesweeper.Minesweeper.*;

/**
 * This class instantiates a TicTacToe object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * 
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * 
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private Minesweeper ms; // model for the game
    private JButton status; // current status text

    // Game constants
    public static final int TILE_LEN = 32;
    public static final int BOARD_WIDTH = GRID_WIDTH * TILE_LEN;
    public static final int BOARD_HEIGHT = GRID_HEIGHT * TILE_LEN;

    public static Image hidden = null;
    public static Image bomb = null; // shows when game over
    public static Image bombRed = null;  // shows when game over, shows on bomb tile player clicks
    public static Image bombCrossed = null;  // shows when game over, replaces flags that were on
                                             // non-bomb tiles
    public static Image flag = null;
    public static Image[] nums = new Image[9];

    public static Image smile = null;
    public static Image smilePressed = null;
    public static Image surprised = null;
    public static Image sunglasses = null;
    public static Image dead = null;

    /**
     * Initializes the game board.
     */
    public GameBoard() {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        ms = new Minesweeper(); // initializes model for the game

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                // updates the model given the coordinates of the mouseclick
                ms.playTurn(p.x / TILE_LEN, p.y / TILE_LEN, e);

                updateStatus(); // updates the status JLabel
                repaint(); // repaints the game board
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    status.setIcon(new ImageIcon(surprised));
                }
            }
        });

        try {
            File f = new File("files/minesweeper_spritesheet.png");
            BufferedImage spritesheet = ImageIO.read(f);
            hidden = spritesheet.getSubimage(14, 195, 16, 16);
            bomb = spritesheet.getSubimage(99, 195, 16, 16);
            bombRed = spritesheet.getSubimage(116, 195, 16, 16);
            bombCrossed = spritesheet.getSubimage(133, 195, 16, 16);
            flag = spritesheet.getSubimage(48, 195, 16, 16);
            nums[0] = spritesheet.getSubimage(31, 195, 16, 16);
            for (int i = 1; i <= 8; i++) {
                nums[i] = spritesheet.getSubimage(14 + 17 * (i - 1), 212, 16, 16);
            }

            smile = spritesheet.getSubimage(14, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
            smilePressed = spritesheet.getSubimage(39, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
            surprised = spritesheet.getSubimage(64, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
            sunglasses = spritesheet.getSubimage(89, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
            dead = spritesheet.getSubimage(114, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
        } catch (IOException e) {
        }
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
//        status.setIcon(new ImageIcon(smileClicked));
        ms.reset();
        ms.printBombLocations();
        repaint();
        updateStatus();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void setStatus(JButton s) {
        status = s;
        updateStatus();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        int state = ms.getState();
        if (state == 0) {
            status.setIcon(new ImageIcon(smile));
        } else if (state == -1) {
            status.setIcon(new ImageIcon(dead));
        } else if (state == 1) {
            status.setIcon(new ImageIcon(sunglasses));
        }
        status.setPressedIcon(new ImageIcon(smilePressed));
    }

    /**
     * Draws the game board.
     * 
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draws board grid
        for (int i = 0; i < GRID_WIDTH; i++) {
            g.drawLine(i * TILE_LEN, 0, i * TILE_LEN, BOARD_HEIGHT);
        }
        for (int i = 0; i < GRID_HEIGHT; i++) {
            g.drawLine(0, i * TILE_LEN, BOARD_WIDTH, i * TILE_LEN);
        }

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                int cell = ms.getCell(x, y);
                int state = ms.getState();
                Image toDraw = null;
                if (cell >= 0) {
                    toDraw = nums[cell];
                } else if (cell == HIDDEN) {
                    if (state == -1 && ms.isBomb(x, y)) {
                        toDraw = bomb;
                    } else {
                        toDraw = hidden;
                    }
                } else if (cell == FLAG) {
                    if (state == -1 && !ms.isBomb(x, y)) {
                        toDraw = bombCrossed;
                    } else {
                        toDraw = flag;
                    }
                } else if (cell == BOMB) {
                    toDraw = bombRed;
                }
                g.drawImage(toDraw, x * TILE_LEN, y * TILE_LEN, TILE_LEN, TILE_LEN, null);
            }
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
