package com.project.thor.snake;
/**
 *@file MainActivity.java
 *@authot Bohomaz A.
 *@version 1
 *@date 2018.11.23
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.project.thor.snake.engine.GameEngine;
import com.project.thor.snake.enums.ColorMode;
import com.project.thor.snake.enums.Direction;
import com.project.thor.snake.enums.GameState;
import com.project.thor.snake.enums.WallsState;
import com.project.thor.snake.views.SnakeView;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    SharedPreferences sPref;
    final String BEST_SCORE = "best_score";

    private GameEngine gameEngine;
    private SnakeView snakeView;
    private final Handler handler = new Handler();



    private ColorMode mode = ColorMode.WhiteMode;
    private WallsState state = WallsState.WallsOn;

    // updateDelay переменая указывающая на задержку обновлений
    private long updateDelay = 360;

    private int score = 0;
    private Menu menu;
    //Animation anim_re = AnimationUtils.loadAnimation(this, R.anim.alpha_re);



    private float prevX, prevY;
    // -----------------------------------------------------------------------------
    //  protected void onCreate(Bundle savedInstanceState)
    //  переопрелление метода в котором запускаются основне функции игры
    // -----------------------------------------------------------------------------
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameEngine = new GameEngine();
        gameEngine.initGame(state);

        snakeView = (SnakeView)findViewById(R.id.snakeView);
        snakeView.setOnTouchListener(this);

        loadBestScore();


        startUpdateHandler();
        Toast.makeText(this,"Для начала нажмите старт.", Toast.LENGTH_SHORT).show();
    }



    // -----------------------------------------------------------------------------
    //  private void startUpdateHandler()
    //  функция обновления обработчка
    // -----------------------------------------------------------------------------
    //
    private void startUpdateHandler(){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameEngine.Update();

                if (gameEngine.getCurrentGameState() == GameState.Running){
                    handler.postDelayed(this, updateDelay );
                    updateScore();
                }
                if ( gameEngine.getCurrentGameState() == GameState.Lost){
                    vibrate(50);
                    OnGameLost();

                }


                snakeView.setSnakeViewMap(gameEngine.getMap(), mode);
                snakeView.invalidate();
            }
        }, updateDelay);

    }
    // -----------------------------------------------------------------------------
    //  private void OnGameLost()()
    //  функция которая выводит результат при проиграше
    // -----------------------------------------------------------------------------
    //
    private void OnGameLost(){
        gameEngine.updateBestScore();
        Toast.makeText(this, "You lose.\nYour score: " + gameEngine.getScore() + "\nBest score: " + gameEngine.getBestScore(), Toast.LENGTH_LONG).show();
    }

    // -----------------------------------------------------------------------------
    //  public boolean onTouch(View v, MotionEvent event)
    //  переопределеная функция обрабатывающая свайпы по экрану и задающая новое направление движения змейке
    // -----------------------------------------------------------------------------
    //
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                prevX = event.getX();
                prevY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float newX = event.getX();
                float newY = event.getY();
                //ращет направления движения
                if(Math.abs(newX - prevX) > Math.abs(newY - prevY)){
                    //определение влево или вправо
                    if(newX > prevX){
                        //вправо
                        gameEngine.UpdateDirection(Direction.East);
                    }else{
                        //влево
                        gameEngine.UpdateDirection(Direction.West);
                    }
                }else{
                    // определение вверх вниз
                    if(newY > prevY){
                        //верх
                        gameEngine.UpdateDirection(Direction.South);
                    }else{
                        //вниз
                        gameEngine.UpdateDirection(Direction.North);
                    }
                }
                if (gameEngine.getCurrentGameState() == GameState.Lost ){
                    updateDelay = 360;
                    handler.removeCallbacksAndMessages(null);
                    //OnGameLost();
                    gameEngine.initGame(state);
                    gameEngine.setCurrentGameState(GameState.Running);
                    startUpdateHandler();
                }
                if (gameEngine.getCurrentGameState() == GameState.Ready){
                    gameEngine.setCurrentGameState(GameState.Running);
                    startUpdateHandler();
                }
                break;
        }
        return true;
    }
    // -----------------------------------------------------------------------------
    //  public boolean onCreateOptionsMenu(Menu menu)
    //  переопределеная функция создания меню опций
    // -----------------------------------------------------------------------------
    //
     @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        return true;
     }
    // -----------------------------------------------------------------------------
    //  public boolean onOptionsItemSelected(MenuItem item)
    //  переопределеная функция обрабатывающая нажатие на кнопки меню опций
    // -----------------------------------------------------------------------------
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuItem colorMode = menu.findItem(R.id.setWhiteOrDarkMode);
        MenuItem wallsState = menu.findItem(R.id.setWallsState);

        //Animation
        //------
        //Animation anim = null;
        Animation anim_re = null;
        anim_re = AnimationUtils.loadAnimation(this, R.anim.alpha_re);
        //anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        //------

        switch (item.getItemId()) {
            case R.id.pauseGame:
                gameEngine.setCurrentGameState(GameState.Ready);
                //checkPressStart = false;
                return true;
            case R.id.setWhiteOrDarkMode:
                   if ( mode == ColorMode.WhiteMode){
                       mode = ColorMode.DarkMode;
                       colorMode.setTitle("WhiteMode");
                       updateDelay = 360;
                       handler.removeCallbacksAndMessages(null);
                       snakeView.startAnimation(anim_re);
                       //snakeView.startAnimation(anim);
                       gameEngine.initGame(state);
                       //snakeView.startAnimation(anim);
                       startUpdateHandler();
                       gameEngine.setCurrentGameState(GameState.Ready);

                   }else {
                       mode = ColorMode.WhiteMode;
                       colorMode.setTitle("DarkMode");
                       updateDelay = 360;
                       handler.removeCallbacksAndMessages(null);
                       snakeView.startAnimation(anim_re);
                       //snakeView.startAnimation(anim);
                       gameEngine.initGame(state);
                       //snakeView.startAnimation(anim);
                       startUpdateHandler();
                       gameEngine.setCurrentGameState(GameState.Ready);
                   }
                return true;
            case R.id.setWallsState:
                if ( state == WallsState.WallsOn){
                    state = WallsState.WallsOff;
                    wallsState.setTitle("Walls On");
                    updateDelay = 360;
                    handler.removeCallbacksAndMessages(null);
                    snakeView.startAnimation(anim_re);
                    gameEngine.initGame(state);
                    startUpdateHandler();
                    gameEngine.setCurrentGameState(GameState.Ready);
                }else {
                    state = WallsState.WallsOn;
                    wallsState.setTitle("Walls Off");
                    updateDelay = 360;
                    handler.removeCallbacksAndMessages(null);
                    snakeView.startAnimation(anim_re);
                    gameEngine.initGame(state);
                    startUpdateHandler();
                    gameEngine.setCurrentGameState(GameState.Ready);
                }
                return true;
            case R.id.exitGame:
                handler.removeCallbacksAndMessages(null);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // -----------------------------------------------------------------------------
    //  public void updateScore()
    //  функция отображения щета и увеличения скорости змейки по ходу игры
    // -----------------------------------------------------------------------------
    //
    public void updateScore(){
        MenuItem scoreGame = menu.findItem(R.id.gameScore);
        scoreGame.setTitle("Score: " + gameEngine.getScore());
        if(updateDelay > 100){
            if(score < gameEngine.getScore() ){
                vibrate(50);
                updateDelay = updateDelay -20;
                score++;
            }
        }
    }
    // -----------------------------------------------------------------------------
    //  public void saveBestScore()
    //  функция сохранения в память лучшего результата
    // -----------------------------------------------------------------------------
    //
    public void saveBestScore() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(BEST_SCORE, gameEngine.getBestScore());
        ed.commit();
    }
    // -----------------------------------------------------------------------------
    //  public void loadBestScore()
    //  функция загрузки лучшего результата из памяти
    // -----------------------------------------------------------------------------
    //
    public void loadBestScore() {
        sPref = getPreferences(MODE_PRIVATE);
        gameEngine.setBestScore(sPref.getInt(BEST_SCORE, gameEngine.getScore()));
    }
    // -----------------------------------------------------------------------------
    //  protected void onStop()
    //  переопределеная функция с сохранением лучшего результата
    // -----------------------------------------------------------------------------
    //
    @Override
    protected void onStop() {
        super.onStop();
        saveBestScore();
        gameEngine.setCurrentGameState(GameState.Ready);
    }

    public ColorMode getMode() {
        return mode;
    }

    public void setMode(ColorMode mode) {
        this.mode = mode;
    }

    public void vibrate(int duration)
    {
        Vibrator vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibs.vibrate(duration);
    }


}
