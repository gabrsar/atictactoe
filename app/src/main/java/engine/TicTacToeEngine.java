/*
 * This is a generic TIC-TAC-TOE game engine! To use this with any type of UI!!! =)
 * Game start with board filled with zeros, and every player play writing your number in board.
 * After each turn, check for a winner. If nobody wins, game change player to the next, and
 * start next round. Game has size^2 rounds, and if no one wins after this rounds game is
 * declared draw.
 */

package engine;

import java.io.Serializable;
import java.util.Arrays;

public class TicTacToeEngine implements Serializable {

    /* Game engine for a TIC-TAC-TOE GAME which supports more than 3x3 and 2 players. */

    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int EMPTY = 0;
    public static final int NO_VICTORY = 0;

    //This is used to know where VICTORY happened  - This is not THREAD SAFE!!!
    private int winPosition = -1;

    //The is the game board. Current game state will be stored here. Any type of UI use this.
    private int[][] board;

    // Player 1 START! Bitch!
    private int currentPlayer = PLAYER_1;

    // Turns remaining... If it be 0, game is draw
    private int rounds;


    /* Create a new game (empty) with size x size turns (and blocks) */
    public TicTacToeEngine(int size) {

        board = new int[size][size];

        rounds = size * size;

        // Start a new game.
        for (int[] row : board) {
            Arrays.fill(row, 0);
        }

    }

    /* Do a move with current player, in board position [row][col], and change player.
     * Return a signal to caller know if move is done with success or if has some problem */
    public TicTacToeEngineSignal play(int row, int col) {

        // So simply, so pretty :)

        /* Check if is all right... */
        if (rounds <= 0 || winPosition != -1) {
            return TicTacToeEngineSignal.GAME_ENDED;
        }

        if (!testInside(row, col)) {
            return TicTacToeEngineSignal.INVALID_POSITION;
        }

        if (board[row][col] != EMPTY) {
            return TicTacToeEngineSignal.USED_POSITION;
        }

        // Write player move...
        board[row][col] = currentPlayer;

        rounds--;

        if (getWinner() != NO_VICTORY) {
            return TicTacToeEngineSignal.VICTORY;
        } else if (rounds == 0) {
            return TicTacToeEngineSignal.GAME_ENDED;
        }

        currentPlayer = getNextPlayer();

        return TicTacToeEngineSignal.CONTINUE_GAME;
    }


    /**
     * Calculate if any body won the game!
     * IMPORTANT: It also update winPosition member
     *
     * Return 0 if nobody won the game.
     * 1 if Player 1 won and...
     * 2 if Player 2 won.
     */

    public int getWinner() {

        /* This function use points to know if any body wins.
         * If a player get enough points in same row, column, or diagonal, he won the game!
         * Points to win the game are equal to board row size.
         *
         * This function could be much more simple, but I don't like to know current state of game
         * to do this...
         *
         * The variable winPosition is used as:
         * first N values, starting from ZERO are for ROWS, (N = board row size)
         * next N values are for COLS,
         * next value are for MAIN DIAGONAL, and the last is for SECOND DIAGONAL,
         * and if nobody wins it is -1.
         *
         * I do it for the game can support more than default 3x3 sizes...
         * I hope you understand this! It is like a XGH but it really works!
         * I'm not proud of this. Sorry!
         */

        // Used to know how much points are needed
        int boardSize = board.length;

        // Current player of check...
        int player;

        for (player = 1; player <= 2; player++) {

            winPosition = 0;


            /* Check if victory happened in ROWS */
            for (int[] row : board) {

                int points = 0; // For each row reset points... same for cols

                for (int j = 0; j < boardSize; j++) {

                    if (row[j] == player) {
                        points++;
                    }
                }

                if (points == boardSize) {
                    // "Bing Bing Bing, We have a Winner!" (Cortana)
                    return player;
                }

                winPosition++;
            }

            /* Check if victory happened in COLS */
            for (int i = 0; i < boardSize; i++) {

                int points = 0;

                for (int j = 0; j < boardSize; j++) {

                    // To test COLS instead of ROWS just swap indexes i and j
                    if (board[j][i] == player) {
                        points++;
                    }
                }

                if (points == boardSize) {
                    return player;
                }
                winPosition++;
            }

            /* Here game check for victory in diagonals. This code calculate for two diagonals at
             * same time. This avoid 2 loops with almost same code, using only a loop with 2
             * times more code... Oh god... why...
             */
            int pointsMain = 0;
            int pointsSecond = 0;

            for (int i = 0; i < boardSize; i++) {

                // Test for main diagonal...
                if (board[i][i] == player) {
                    pointsMain++;
                }

                // Test for second diagonal...
                if (board[i][boardSize - i - 1] == player) {
                    pointsSecond++;
                }
            }

            if (pointsMain == boardSize) {
                return player;
            }

            winPosition++;
            if (pointsSecond == boardSize) {
                return player;
            }
        }

        winPosition = -1;

        /* No body won =( */
        return NO_VICTORY;

    }


    /**
     * Test if an ordered pair is inside board.
     */
    private boolean testInside(int row, int col) {

        if (row < 0 || row > board.length) {
            return false;
        } else if (col < 0 || col > board[0].length) {
            return false;
        }
        return true;

    }

    /* GETTERS AND SETTERS */

    public int getNextPlayer() {
        return (currentPlayer == PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getWinPosition() {
        return winPosition;
    }
}
