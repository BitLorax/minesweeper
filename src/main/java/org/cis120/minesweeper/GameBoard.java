package org.cis120.minesweeper;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.cis120.minesweeper.Minesweeper.*;


public class GameBoard extends JPanel {

    private Minesweeper ms; // model for the game
    private JButton status; // reset/status button

    // Game constants
    public static final int TILE_LEN = 32;
    public static final int BOARD_WIDTH = GRID_WIDTH * TILE_LEN;
    public static final int BOARD_HEIGHT = GRID_HEIGHT * TILE_LEN;

    private static Image hidden = null;
    private static Image bomb = null; // shows when game over
    private static Image bombRed = null;  // shows when game over, shows on bomb tile player clicks
    private static Image bombCrossed = null;  // shows when game over, replaces flags that were on
                                             // non-bomb tiles
    private static Image flag = null;
    private static Image[] nums = new Image[9];

    // images for reset/status button
    private static Image smile = null;
    private static Image smilePressed = null;
    private static Image surprised = null;
    private static Image sunglasses = null;
    private static Image dead = null;

    // images for replay button
    private static Image replay = null;
    private static Image replayPressed = null;

    // images for help/instructions button
    private static Image help = null;
    private static Image helpPressed = null;

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
         * Listens for mouse clicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (ms.getState() != 0) { // if game is not current ongoing, ignore mouse clicks
                    return;
                }
                Point p = e.getPoint();

                // updates the model given the coordinates of the mouse click
                ms.playTurn(p.x / TILE_LEN, p.y / TILE_LEN, SwingUtilities.isLeftMouseButton(e));

                updateStatus(); // updates status
                repaint(); // repaints the game board
            }
            @Override
            public void mousePressed(MouseEvent e) { // checks if mouse is pressed down
                if (SwingUtilities.isLeftMouseButton(e) && ms.getState() == 0) {
                    status.setIcon(new ImageIcon(surprised)); // set status to surprised face if
                                                              // user is about to reveal a tile
                }
            }
        });

        // read in all sprite images
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

            replay = spritesheet.getSubimage(139, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
            replayPressed = spritesheet.getSubimage(164, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);

            help = spritesheet.getSubimage(189, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
            helpPressed = spritesheet.getSubimage(214, 170, 24, 24).
                    getScaledInstance(48, 48, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("IOException: Sprite-sheet not found");
        }
    }

    /**
     * Resets the game to its initial state.
     */
    public void reset() {
        ms.reset(false);
        repaint();
        updateStatus();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Loads game, if load fails, reset the board again (just in case).
     */
    public void loadGame() {
        ms.reset(false);
        if (!ms.loadGame()) {
            ms.reset(false);
        }
        repaint();
        updateStatus();
        requestFocusInWindow();
    }

    /**
     * Set status/reset button.
     *
     * @param s new status/reset button
     */
    public void setStatus(JButton s) {
        status = s;
        updateStatus();
    }

    /**
     * Updates the status/reset button icon to reflect status of game.
     */
    private void updateStatus() {
        int state = ms.getState();
        if (state == 0) { // ongoing
            status.setIcon(new ImageIcon(smile));
        } else if (state == -1) { // loss
            status.setIcon(new ImageIcon(dead));
        } else if (state == 1) { // win
            status.setIcon(new ImageIcon(sunglasses));
        }
        status.setPressedIcon(new ImageIcon(smilePressed));
    }

    /**
     * Set replay button.
     *
     * @param r new replay button
     */
    public void setReplay(JButton r) {
        r.setIcon(new ImageIcon(replay));
        r.setPressedIcon(new ImageIcon(replayPressed));
    }

    /**
     * Set help/instructions button.
     *
     * @param h new help/instructions button
     */
    public void setHelp(JButton h) {
        h.setIcon(new ImageIcon(help));
        h.setPressedIcon(new ImageIcon(helpPressed));
    }

    /**
     * Draws the game board.
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

        // Draws tile for each cell in grid
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                int cell = ms.getCell(x, y);
                int state = ms.getState();
                Image toDraw = null;
                if (cell >= 0) { // draw number
                    toDraw = nums[cell];
                } else if (cell == HIDDEN) {
                    if (state == -1 && ms.isBomb(x, y)) { // reveal bombs when game over
                        toDraw = bomb;
                    } else { // draw hidden tile
                        toDraw = hidden;
                    }
                } else if (cell == FLAG) {
                    if (state == -1 && !ms.isBomb(x, y)) { // reveal wrong flags when game over
                        toDraw = bombCrossed;
                    } else { // draw flag
                        toDraw = flag;
                    }
                } else if (cell == BOMB) { // draw red bomb (the bomb the player revealed)
                    toDraw = bombRed;
                }
                g.drawImage(toDraw, x * TILE_LEN, y * TILE_LEN, TILE_LEN, TILE_LEN, null);
            }
        }
    }

    /**
     * Replays the current game starting from a completely-hidden board.
     */
    public void replay() {
        ms.reset(true); // reset with preserveData so only board state is reset
        repaint();
        updateStatus();

        final Timer timer = new Timer(500, null); // timer that plays the next move in the list of
                                                  // moves every half second
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean res = ms.replayStepForward();
                updateStatus();
                repaint();
                if (!res) { // if there are no more moves, stop the timer and end replay
                    timer.stop();
                    ms.finishReplay();
                }
            }
        });
        timer.setRepeats(true); // automatically restart the timer when time's up
        ms.startReplay();
        timer.start();
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
