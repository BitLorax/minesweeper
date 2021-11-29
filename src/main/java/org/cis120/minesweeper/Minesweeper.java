package org.cis120.minesweeper;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */


import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * This class is a model for TicTacToe.
 * 
 * This game adheres to a Model-View-Controller design framework.
 * This framework is very effective for turn-based games. We
 * STRONGLY recommend you review these lecture slides, starting at
 * slide 8, for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec36.pdf
 * 
 * This model is completely independent of the view and controller.
 * This is in keeping with the concept of modularity! We can play
 * the whole game from start to finish without ever drawing anything
 * on a screen or instantiating a Java Swing object.
 * 
 * Run this file to see the main method play a game of TicTacToe,
 * visualized with Strings printed to the console.
 */
public class Minesweeper {

    public static final int GRID_WIDTH = 16;
    public static final int GRID_HEIGHT = 16;
    private int[][] gameBoard;
    private boolean[][] bombLocations;
    public static final int NUM_MINES = 32;

    private final int[] dx = {1, 1, 1, 0, 0, -1, -1, -1};
    private final int[] dy = {1, 0, -1, 1, -1, 1, 0, -1};
    private final int[] cdx = {1, -1, 0, 0};
    private final int[] cdy = {0, 0, 1, -1};

    public static final int HIDDEN = -1;
    public static final int FLAG = -2;
    public static final int BOMB = -3;

    public static int curState = 0; // 0 if game is ongoing, 1 if win, -1 if loss

    /**
     * Constructor sets up game state.
     */
    public Minesweeper() {
        reset();
    }

    /**
     * playTurn allows players to play a turn. Returns true if the move is
     * successful and false if a player tries to play in a location that is
     * taken or after the game has ended. If the turn is successful and the game
     * has not ended, the player is changed. If the turn is unsuccessful or the
     * game has ended, the player is not changed.
     *
     * @param x x coordinate of tile
     * @param y y coordinate of tile
     * @return whether the turn was successful
     */
    public boolean playTurn(int x, int y, MouseEvent event) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return false;
        }
        if (gameBoard[x][y] != FLAG && gameBoard[x][y] != HIDDEN) {
            return false;
        }
        if (curState != 0) {
            return false;
        }

        if (SwingUtilities.isLeftMouseButton(event)) {
            if (gameBoard[x][y] == FLAG) {
                return true;
            }
            if (bombLocations[x][y]) {
                gameBoard[x][y] = -3;
            } else {
                gameBoard[x][y] = HIDDEN;
                int num = getNum(x, y);
                if (num == 0) {
                    dfs(x, y);
                } else {
                    gameBoard[x][y] = num;
                }
            }
        } else {
            if (gameBoard[x][y] == FLAG) {
                gameBoard[x][y] = HIDDEN;
            } else {
                gameBoard[x][y] = FLAG;
            }
        }
        updateState();
        return true;
    }

    private void dfs(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return;
        }
        if (bombLocations[x][y] || gameBoard[x][y] != HIDDEN) {
            return;
        }
        int num = getNum(x, y);
        gameBoard[x][y] = num;
        if (num == 0) {
            for (int k = 0; k < 4; k++) {
                dfs(x + cdx[k], y + cdy[k]);
            }
        }
    }

    private int getNum(int x, int y) {
        int ret = 0;
        for (int k = 0; k < 8; k++) {
            int nx = x + dx[k];
            int ny = y + dy[k];
            if (nx < 0 || nx >= GRID_WIDTH || ny < 0 || ny >= GRID_HEIGHT) {
                continue;
            }
            if (bombLocations[nx][ny]) {
                ret += 1;
            }
        }
        return ret;
    }

    public int getState() {
        return curState;
    }

    /**
     * checkWin checks whether the game has reached a win condition.
     *
     * @return 1 if the player won, 0 if the game is still ongoing, and -1 if the player lost
     */
    public void updateState() {
        int res = 1;
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                // player revealed bomb tile
                if (gameBoard[x][y] == BOMB) {
                    curState = -1;
                    return;
                } else {
                    // non-bomb tile still hidden
                    if (gameBoard[x][y] == HIDDEN && !bombLocations[x][y]) {
                        res = 0;
                    }
                }
            }
        }
        curState = res;
    }

    public boolean isBomb(int x, int y) {
        return bombLocations[x][y];
    }

    /**
     * printGameBoard prints the current game board for debugging.
     */
    public void printGameBoard() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                System.out.print(gameBoard[x][y]);
                if (x < GRID_WIDTH - 1) {
                    System.out.print(" | ");
                }
            }
            if (y < GRID_HEIGHT - 1) {
                System.out.println("\n---------");
            }
        }
    }

    /**
     * printBombLocations prints the bomb locations for debugging.
     */
    public void printBombLocations() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (bombLocations[x][y]) {
                    System.out.print("B");
                } else {
                    System.out.print("_");
                }
                if (x < GRID_WIDTH - 1) {
                    System.out.print(" | ");
                }
            }
            if (y < GRID_HEIGHT - 1) {
                System.out.println("\n---------");
            }
        }
    }

    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        gameBoard = new int[GRID_WIDTH][GRID_HEIGHT];
        bombLocations = new boolean[GRID_WIDTH][GRID_HEIGHT];
        for (int i = 0; i < NUM_MINES; i++) {
            int x = (int)(Math.random() * 16);
            int y = (int)(Math.random() * 16);
            if (bombLocations[x][y]) {
                i--;
            } else {
                bombLocations[x][y] = true;
            }
        }
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                gameBoard[x][y] = -1;
            }
        }
        curState = 0;
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param x x coordinate of tile
     * @param y y coordinate of tile
     * @return an integer denoting the contents of the corresponding cell on the
     *         game board. 0 = hidden, >0 = number of bombs around tile,
     *         -1 = blank, -2 = flag, -3 = bomb
     */
    public int getCell(int x, int y) {
        return gameBoard[x][y];
    }

    /**
     * This main method illustrates how the model is completely independent of
     * the view and controller. We can play the game from start to finish
     * without ever creating a Java Swing object.
     *
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     *
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) {
        Minesweeper t = new Minesweeper();
    }
}
