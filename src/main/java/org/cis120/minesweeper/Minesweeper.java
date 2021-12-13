package org.cis120.minesweeper;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Minesweeper {

    // parameters for the game
    public static final int GRID_WIDTH = 16;
    public static final int GRID_HEIGHT = 16;
    public static final int NUM_MINES = 24;
    private int[][] gameBoard;
    private boolean[][] bombLocations;

    // arrays used for neighbor-checking, looping over arrays and adding their values to current
    // position gives the coordinates to a neighbor (cdx and cdy for cardinal directions, dx and dy
    // for cardinal directions and diagonals)
    private final int[] dx = {1, 1, 1, 0, 0, -1, -1, -1};
    private final int[] dy = {1, 0, -1, 1, -1, 1, 0, -1};
    private final int[] cdx = {1, -1, 0, 0};
    private final int[] cdy = {0, 0, 1, -1};

    // constants that represent what's on each tile in the board
    public static final int HIDDEN = -1;
    public static final int FLAG = -2;
    public static final int BOMB = -3;

    private static int curState = 0; // 0 if game is ongoing, 1 if win, -1 if loss,
                                     // 2 if replaying win, -2 if replaying loss, 3 if replaying
                                     // ongoing game
    private List<Integer[]> moves = new ArrayList<>(); // stores the moves the player made in
                                                       // the current game
    private static int curMove = 0;  // current move in replay

    /**
     * Constructor sets up game state.
     */
    public Minesweeper() {
        reset(false);
    }

    /**
     * playTurn allows players to play a turn. Returns true if the move is
     * successful and false if a player selects a tile that is already revealed.
     *
     * @param x x coordinate of tile
     * @param y y coordinate of tile
     * @return whether the turn was successful
     */
    public boolean playTurn(int x, int y, boolean isLeftClick) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) { // tile location out of bounds
            return false;
        }
        if (gameBoard[x][y] != FLAG && gameBoard[x][y] != HIDDEN) { // tile already revealed
            return false;
        }

        if (isLeftClick) {
            if (gameBoard[x][y] == FLAG) { // prevent player from revealing flags (need to
                                           // un-flag first
                return true;
            }
            if (bombLocations[x][y]) { // player clicked on a tile that has a bomb
                if (moves.size() == 0) { // if this is the player's first move, regenerate the bomb
                                         // locations so the player's first move isn't on a bomb
                    reset(false);
                    return playTurn(x, y, isLeftClick);
                }
                gameBoard[x][y] = BOMB;
            } else { // player clicked on a tile that doesn't have a bomb
                gameBoard[x][y] = HIDDEN;
                int num = getNum(x, y); // get number of bombs around tile
                if (num == 0) { // empty tile, reveal all adjacent non-bomb tiles
                    dfs(x, y);
                } else {
                    gameBoard[x][y] = num;
                }
            }
        } else {
            if (gameBoard[x][y] == FLAG) { // un-flag tile
                gameBoard[x][y] = HIDDEN;
            } else { // flag tile
                gameBoard[x][y] = FLAG;
            }
        }
        if (curState == 0) { // current game is still in progress, add move to list of moves
            moves.add(new Integer[]{x, y, isLeftClick ? 1 : 0});
        }
        updateState();
        saveGame();
        return true;
    }

    /**
     * dfs performs flood-fill through the board to reveal all adjacent empty tiles as well as the
     * tiles next to empty tiles that don't contain bombs.
     *
     * @param x current x coordinate
     * @param y current y coordinate
     */
    private void dfs(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) { // x or y out of bounds
            return;
        }
        if (bombLocations[x][y] || gameBoard[x][y] != HIDDEN) { // current position is bomb or not
                                                                // hidden
            return;
        }
        int num = getNum(x, y);
        gameBoard[x][y] = num;
        if (num == 0) { // current position is an empty tile, recurse to neighbors
            for (int k = 0; k < 4; k++) { // call dfs on neighbors in the 4 cardinal directions
                dfs(x + cdx[k], y + cdy[k]);
            }
        }
    }

    /**
     * Checks the 8 neighbors around a tile and counts the number of bombs.
     *
     * @param x x coordinate of tile
     * @param y y coordinate of tile
     * @return number of bombs around tile (x, y)
     */
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

    /**
     * Gets current state of the game.
     *
     * @return an integer representing current game state (key on line 33)
     */
    public int getState() {
        return curState;
    }

    /**
     * updateState updates the curState variable with 1 if the player won (revealed all non-bomb
     * tiles), 0 if the game is still ongoing, and -1 if the player lost (revealed bomb).
     */
    public void updateState() {
        if (curState == 2 || curState == -2 || curState == 3) { // replay in progress
            return;
        }
        int res = 1;
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (gameBoard[x][y] == BOMB) { // player revealed bomb tile
                    curState = -1;
                    return;
                } else { // non-bomb tile still hidden
                    if (gameBoard[x][y] == HIDDEN && !bombLocations[x][y]) {
                        res = 0;
                    }
                }
            }
        }
        curState = res;
    }

    /**
     * Checks if there's a bomb on tile (x, y).
     *
     * @param x x coordinate of tile
     * @param y y coordinate of tile
     * @return true if there's a bomb on tile (x, y), false otherwise
     */
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
        System.out.println();
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
        System.out.println();
    }

    /**
     * reset resets the game state to start a new game. If preserveData is true, then bomb
     * locations, the moves list, and game state are not reset (the only thing changing is the
     * board state).
     *
     * @param preserveData Whether the method should reset all data or not
     */
    public void reset(boolean preserveData) {
        gameBoard = new int[GRID_WIDTH][GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                gameBoard[x][y] = HIDDEN; // reset all tiles to hidden
            }
        }
        if (!preserveData) {
            bombLocations = new boolean[GRID_WIDTH][GRID_HEIGHT];
            for (int i = 0; i < NUM_MINES; i++) {  // set bomb locations randomly
                int x = (int)(Math.random() * 16);
                int y = (int)(Math.random() * 16);
                if (bombLocations[x][y]) {
                    i--;
                } else {
                    bombLocations[x][y] = true;
                }
            }
            moves = new ArrayList<>();
            curState = 0;
        }
        curMove = 0;
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param x x coordinate of tile
     * @param y y coordinate of tile
     * @return an integer denoting the contents of the corresponding cell on the
     *         game board.
     */
    public int getCell(int x, int y) {
        return gameBoard[x][y];
    }

    /**
     * Plays the current move and increases curMove by one.
     *
     * @return false if it just played the last move, true otherwise
     */
    public boolean replayStepForward() {
        if (curMove >= moves.size()) {
            return false;
        }
        Integer[] move = moves.get(curMove);
        curMove++;
        playTurn(move[0], move[1], (move[2] == 1));
        return curMove != moves.size();
    }

    /**
     * Starts replaying by setting curState (key on line 33).
     */
    public void startReplay() {
        if (curState == 0) {
            curState = 3;
        } else {
            curState *= 2;
        }
    }

    /**
     * Finishes replaying by setting curState (key on line 33) and setting curMove back to 0.
     */
    public void finishReplay() {
        if (curState == 3) {
            curState = 0;
        } else {
            curState /= 2;
        }
        curMove = 0;
    }

    /**
     * Write the curState, board width and height, number of bombs, board, bomb locations, and
     * list of moves into minesweeper_save.txt
     */
    public void saveGame() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("files/minesweeper_save.txt"));
            bw.write(String.valueOf(curState));
            bw.newLine();
            bw.write(GRID_WIDTH + " " + GRID_HEIGHT + " " + NUM_MINES);
            bw.newLine();
            for (int y = 0; y < GRID_HEIGHT; y++) {
                for (int x = 0; x < GRID_WIDTH; x++) {
                    bw.write(String.valueOf(gameBoard[x][y]));
                    bw.write(" ");
                }
                bw.newLine();
            }
            for (int y = 0; y < GRID_HEIGHT; y++) {
                for (int x = 0; x < GRID_WIDTH; x++) {
                    bw.write(String.valueOf(bombLocations[x][y] ? 1 : 0));
                    bw.write(" ");
                }
                bw.newLine();
            }
            for (Integer[] move : moves) {
                bw.write(String.valueOf(move[0]));
                bw.write(" ");
                bw.write(String.valueOf(move[1]));
                bw.write(" ");
                bw.write(String.valueOf(move[2]));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("IOException: Save game failed");
        }
    }

    /**
     * Loads game save data from minesweeper_save.txt.
     *
     * @return true if the load is successful, false otherwise (if this returns false, then we
     * have a new game)
     */
    public boolean loadGame() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("files/minesweeper_save.txt"));
            curState = Integer.parseInt(br.readLine());
            String[] parameters = br.readLine().split(" ");
            if (Integer.parseInt(parameters[0]) != GRID_WIDTH ||
                Integer.parseInt(parameters[1]) != GRID_HEIGHT ||
                Integer.parseInt(parameters[2]) != NUM_MINES) {
                return false;
            }

            for (int y = 0; y < GRID_HEIGHT; y++) {
                String[] row = br.readLine().split(" ");
                for (int x = 0; x < GRID_WIDTH; x++) {
                    gameBoard[x][y] = Integer.parseInt(row[x]);
                }
            }
            for (int y = 0; y < GRID_HEIGHT; y++) {
                String[] row = br.readLine().split(" ");
                for (int x = 0; x < GRID_WIDTH; x++) {
                    bombLocations[x][y] = Integer.parseInt(row[x]) == 1;
                }
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] move = line.split(" ");
                moves.add(new Integer[]{Integer.parseInt(move[0]), Integer.parseInt(move[1]),
                    Integer.parseInt(move[2])});
            }
            return true;
        } catch (IOException e) {
            System.out.println("IOException: Load game failed");
            return false;
        }
    }
}
