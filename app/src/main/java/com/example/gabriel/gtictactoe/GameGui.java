/**
 * Class that implement a graphical user interface compatible with TicTacToeEngine.
 * This class communicate to engine with object "game". The current state is shown using guiBoard
 */
package com.example.gabriel.gtictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import engine.TicTacToeEngine;
import engine.TicTacToeEngineSignal;

public class GameGui extends Activity {

    /* Keys to retrieve data over activity life cycle */
    private static final String GAME_ENGINE = "gameEngine";

    // Game engine.
    TicTacToeEngine game;

    // Used to show game state and as input
    ImageButton[][] guiBoard;

    // Make a 3x3 game.
    // TODO // FIXME - Gui don't support others game size. Fix this!
    int gameSize = 3;

    // This will show current player and victory!
    TextView lblStatus;

    // This will show current player and victory!
    ImageView imageCurrent;

    // Utilized to make device vibrate.
    Vibrator vibrator;

    /* Save game progress to avoid game loss when activity is destroyed */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(GAME_ENGINE, game);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* If game start from begin a new GameEngine is created. Else it recovery from saved
         * instance */

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        lblStatus = (TextView) findViewById(R.id.lblStatus);
        imageCurrent = (ImageView) findViewById(R.id.imageView);
        vibrator = (Vibrator) this.getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);

        generateGuiBoard();

        if (savedInstanceState != null) {
            /* Load game progress */
            game = (TicTacToeEngine) savedInstanceState.getSerializable(GAME_ENGINE);
        } else {
            /* Create a new game */
            game = new TicTacToeEngine(gameSize);
        }

        drawGameBoard();
        showPlayer();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Map buttons to a matrix used to represent on GUI. This function works with any game size.
     */
    private void generateGuiBoard() {

        guiBoard = new ImageButton[gameSize][gameSize];

        LinearLayout lgl = (LinearLayout) findViewById(R.id.linearGameLayout);

        int rows = lgl.getChildCount();

        for (int row = 0; row < rows; row++) {

            LinearLayout tmpRow = (LinearLayout) lgl.getChildAt(row);

            int cols = tmpRow.getChildCount();

            for (int col = 0; col < cols; col++) {
                guiBoard[row][col] = (ImageButton) tmpRow.getChildAt(col);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_new_game:
                newGame();
                break;
            case R.id.menu_about:
                showAbout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Just start About Activity */
    public void showAbout()
    {
        Intent intent = new Intent(this,About.class);
        startActivity(intent);
    }

    /* Return a Point object with coordinates in game of a ImageButton */
    private Point getButtonCoordinates(ImageButton button) {

        for (int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {
                if (button == guiBoard[i][j]) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }


    private void showPlayer() {
        if (game.getCurrentPlayer() == 1) {
            imageCurrent.setImageResource(R.drawable.player1);
        } else {
            imageCurrent.setImageResource(R.drawable.player2);
        }
    }

    /**
     * This function show game current state on Gui
     */
    public void drawGameBoard() {

        // Load game current state from game engine.
        int board[][] = game.getBoard();

        for (int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {

                ImageButton b = guiBoard[i][j];

                switch (board[i][j]) {

                    case TicTacToeEngine.PLAYER_1:
                        b.setImageResource(R.drawable.player1);
                        break;
                    case TicTacToeEngine.PLAYER_2:
                        b.setImageResource(R.drawable.player2);
                        break;
                    default:
                        b.setImageResource(0);
                }

            }
        }
    }


    /* Function called when user press a button on board */
    public void play(View view) {

        int player = game.getCurrentPlayer();

        ImageButton b = (ImageButton) view;

        Point p = getButtonCoordinates(b);

        TicTacToeEngineSignal signal = game.play(p.x, p.y);

        drawGameBoard();
        showPlayer();

        switch (signal) {

            case INVALID_POSITION:
                showInvalidPosition();
                break;

            case USED_POSITION:
                showUsedPosition();
                break;
            case GAME_ENDED:
                showGameDraw();
                break;

            case VICTORY:
                showVictory();
                break;

        }
    }


    /* Inform user that game is draw!*/
    void showGameDraw()
    {
        vibrator.vibrate(600);
        lblStatus.setText(R.string.game_draw);
        setGameLocked(true);

    }

    /* Change image of victorious row */
    private void paintVictory() {

        int player = game.getCurrentPlayer();

        int d;
        if (player == game.PLAYER_1) {
            d = R.drawable.player1_victory;
        } else {
            d = R.drawable.player2_victory;
        }

        /* First N positions are rows, N = gameSize,
         * next N positions are cols,
         * next are main diagonal, and them second diagonal*/
        int position = game.getWinPosition();


        /* Invalid paint position */
        if (position < 0) {
            return;
        }

        // Check if is in a row...
        if (position < gameSize) {
            int row = position;

            for (int j = 0; j < gameSize; j++) {
                guiBoard[row][j].setImageResource(d);
            }
            return;
        }

        // Check if is in a column
        position -= gameSize;

        if (position < gameSize) {
            int col = position;

            for (int i = 0; i < gameSize; i++) {
                guiBoard[i][col].setImageResource(d);
            }
            return;
        }

        // Check if is in main diagonal...
        position -= gameSize;

        System.out.println(position + "");

        if (position == 0) {
            for (int i = 0; i < gameSize; i++) {
                guiBoard[i][i].setImageResource(d);
            }
            return;
        } else {
            for (int i = 0; i < gameSize; i++) {
                guiBoard[i][gameSize - i - 1].setImageResource(d);
            }
            return;
        }
    }


    private void showVictory() {
        vibrator.vibrate(300);
        lblStatus.setText(R.string.game_victory);
        paintVictory();
        setGameLocked(true);

    }

    private void showInvalidPosition() {
        vibrator.vibrate(100);
        lblStatus.setText(R.string.index_out_of_bounds);
    }

    private void showUsedPosition() {
        vibrator.vibrate(100);
        lblStatus.setText(R.string.used_position);

    }

    /* Lock and Unlock game buttons! */
    private void setGameLocked(boolean status) {
        for (ImageButton[] row : guiBoard) {
            for (ImageButton col : row) {
                col.setEnabled(!status);
            }
        }
    }

    /* Start a new Game! */
    public void newGame() {
        game = new TicTacToeEngine(gameSize);
        setGameLocked(false);
        drawGameBoard();
        lblStatus.setText("");
        imageCurrent.setImageResource(R.drawable.player1);
    }
}
