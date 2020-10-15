package com.project.thor.snake.engine;
/**
 *@file GameEngine.java
 *@authot Bohomaz A.
 *@version 1
 *@date 2018.11.23
 */
import com.project.thor.snake.classes.Coordinate;
import com.project.thor.snake.enums.Direction;
import com.project.thor.snake.enums.GameState;
import com.project.thor.snake.enums.TileType;
import com.project.thor.snake.enums.WallsState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * Класс GameEngine в котором реализована физика игры
 */
public class GameEngine {

    //Размеры игрового поля
    public static final int GameWidth = 28;
    public static final int GameHeight = 42;
    //масивы для хранения координат walls, snake, apples
    private List<Coordinate> walls = new ArrayList<>();
    private List<Coordinate> snake = new ArrayList<>();
    private List<Coordinate> apples = new ArrayList<>();
    //переменая для рандомного отбражения яблока
    private Random random = new Random();
    //переменая для определения увеличения хвоста
    private boolean increaseTail = false;
    //переменіе для хранения текущего щета и лучшего результата
    private int score = 0;
    private int bestScore = 0;
    private WallsState wallsState;

    //Первоначальное направление currentDirection
    private Direction currentDirection = Direction.East;
    //Первоначальное состояние игры  CurrentGameState
    private GameState CurrentGameState = GameState.Ready;

    // -----------------------------------------------------------------------------
    //  private Coordinate getSnakeHead()
    //  геттер для получения координт головы змеи
    // -----------------------------------------------------------------------------
    //
    private Coordinate getSnakeHead(){
        return snake.get(0);
    }

    // -----------------------------------------------------------------------------
    //  public GameEngine()
    //  Конструктор
    // -----------------------------------------------------------------------------
    //
    public GameEngine(){

    }

    // -----------------------------------------------------------------------------
    //  public void initGame()
    //  init game
    // -----------------------------------------------------------------------------
    //
    public void initGame(WallsState state){
        wallsState = state;
        AddSnake();
        if(state == WallsState.WallsOn){ AddWalls();}
        AddApples();
        score = 0;
    }

    // -----------------------------------------------------------------------------
    // public void UpdateDirection(Direction newDirection)
    // Обновление направления движения
    // -----------------------------------------------------------------------------
    //
    public void UpdateDirection(Direction newDirection){
        if (Math.abs(newDirection.ordinal() - currentDirection.ordinal()) % 2 == 1){
            currentDirection = newDirection;
        }
    }

    // -----------------------------------------------------------------------------
    //  public void Update()
    //  Функция обновленияопределения направления и обработка столкновения
    // -----------------------------------------------------------------------------
    //
    public void Update(){
        // update the snake
        switch (currentDirection) {
            case North:
                UpdateSnake(0, -1);
                break;
            case East:
                UpdateSnake(1, 0);//ok
                break;
            case South:
                UpdateSnake(0, 1);
                break;
            case West:
                UpdateSnake(-1, 0);
                break;
        }
        if (wallsState == WallsState.WallsOn){
             // Проверка столкновения с стеной
            for (Coordinate w: walls){
                if ( snake.get(0).equals(w)){
                    CurrentGameState = GameState.Lost;
                }
            }
        }

        //Проверка столкновения с самим собой
        for(int i = 1; i < snake.size(); i++){
            if(getSnakeHead().equals(snake.get(i))){
                CurrentGameState = GameState.Lost;
                return;
            }
        }
        //Проверка на сьедение яблока
        Coordinate appleToRemove = null;
        for( Coordinate apple: apples){
            if (getSnakeHead().equals(apple)){
                appleToRemove = apple;
                increaseTail = true;
                score++;

            }
        }
        //удаление яблока из масива
        if (appleToRemove != null){
            apples.remove(appleToRemove);
            AddApples();
        }
    }

    // -----------------------------------------------------------------------------
    //  public TileType[][] getMap()
    //  Функция заполнения масива
    // -----------------------------------------------------------------------------
    //
    public TileType[][] getMap(){
        TileType[][] map = new TileType[GameWidth][GameHeight];
        //заполнение пустіх ячеек
        for (int x = 0; x < GameWidth; x++){
            for(int y = 0; y < GameHeight; y++){
                map[x][y] = TileType.Nothing;
            }
        }
        //заполнение ячеек змеи
        for(Coordinate s: snake){
            map[s.getX()][s.getY()] = TileType.SnakeTail;
        }
        //аполнение ячейки голові змеи
        map[snake.get(0).getX()][snake.get(0).getY()] = TileType.SnakeHead;

        if (wallsState == WallsState.WallsOn) {
            //заполнение ячеек стен
            for (Coordinate wall : walls) {
                map[wall.getX()][wall.getY()] = TileType.Wall;
            }
        }

        //заполнение ячейки яблока
        for(Coordinate a: apples){
            map[a.getX()][a.getY()] = TileType.Apple;
        }
        return map;
    }

    // -----------------------------------------------------------------------------
    //  private void UpdateSnake(int x, int y)
    //  Обновление змеи
    // -----------------------------------------------------------------------------
    //
    private void UpdateSnake(int x, int y){
        int newX = snake.get(snake.size()-1).getX();
        int newY = snake.get(snake.size()-1).getY();

        //изменение координат змеи придвижении
        for (int i = snake.size()-1; i > 0 ; i--) {
            snake.get(i).setX(snake.get(i - 1).getX());
            snake.get(i).setY(snake.get(i - 1).getY());
        }
        //добавление длины змеи
        if ( increaseTail ){
            snake.add(new Coordinate(newX, newY));
            increaseTail = false;
         }
        snake.get(0).setX(snake.get(0).getX()+x);
        snake.get(0).setY(snake.get(0).getY()+y);

        //изменение координал при достижении граничных значений матрици
        if(snake.get(0).getX() == -1 && currentDirection == Direction.West){
            snake.get(0).setX(27);
        }
        if(snake.get(0).getX() == 28 && currentDirection == Direction.East){
            snake.get(0).setX(0);
        }
        if(snake.get(0).getY() == -1 && currentDirection == Direction.North){
            snake.get(0).setY(41);
        }
        if(snake.get(0).getY() == 42 && currentDirection == Direction.South){
            snake.get(0).setY(0);
        }

    }

    // -----------------------------------------------------------------------------
    //   private void AddSnake()
    //  Добавление змеи
    // -----------------------------------------------------------------------------
    //
    private void AddSnake() {
        snake.clear();
        //координаты начального положения змеи
        snake.add(new Coordinate(7, 7));
        snake.add(new Coordinate(6, 7));
        snake.add(new Coordinate(5, 7));
        snake.add(new Coordinate(4, 7));
        snake.add(new Coordinate(3, 7));
        snake.add(new Coordinate(2, 7));
    }

    // -----------------------------------------------------------------------------
    //  private void AddWalls()
    //  Добавление стен
    // -----------------------------------------------------------------------------
    //
    private void AddWalls() {
        // Создание вверхней и нижне стены
        for (int x = 0; x < GameWidth; x++){
            walls.add(new Coordinate(x, 0));
            walls.add(new Coordinate(x, GameHeight-1));
        }

        // Создание левой и правой стены
        for (int y = 1; y < GameHeight; y++){
            walls.add(new Coordinate(0, y));
            walls.add(new Coordinate(GameWidth-1, y));
        }
    }

    // -----------------------------------------------------------------------------
    //  private void AddApples()
    //  добавление яблока на игровое поле
    // -----------------------------------------------------------------------------
    //
    private void AddApples() {
        apples.clear();
        Coordinate coordinate = null;

        boolean added = false;

        while (!added){
            int x = 1 + random.nextInt(GameWidth - 2 );
            int y = 1 + random.nextInt(GameHeight - 2 );

            coordinate = new Coordinate(x, y);
            boolean collision = false;
            for ( Coordinate s: snake){
                if (s.equals(coordinate)){
                    collision = true;
                    break;
                }
            }
            if (collision == true){
                continue;
            }
            for (Coordinate a: apples ){
                if (a.equals(coordinate)){
                    collision = true;
                    break;
                }
            }

            added = !collision;
        }

        apples.add(coordinate);
    }

    // -----------------------------------------------------------------------------
    //  public GameState getCurrentGameState()
    //  Геттер состояния игры CurrentGameState
    // -----------------------------------------------------------------------------
    //
    public GameState getCurrentGameState() {
        return CurrentGameState;
    }

    // -----------------------------------------------------------------------------
    //  public void setCurrentGameState(GameState gm)
    //  Сеттер состояния игры
    // -----------------------------------------------------------------------------
    //
    public void setCurrentGameState(GameState gm){
        CurrentGameState = gm;
    }

    // -----------------------------------------------------------------------------
    //  public int getScore()
    //  геттер щета игры
    // -----------------------------------------------------------------------------
    //
    public int getScore(){
        return score;
    }

    // -----------------------------------------------------------------------------
    //  public int getBestScore()
    //  геттер лучшего щета
    // -----------------------------------------------------------------------------
    //
    public int getBestScore() {
        return bestScore;
    }

    // -----------------------------------------------------------------------------
    //  public void setBestScore(int bestScore)
    //  Сеттер лучшего щета
    // -----------------------------------------------------------------------------
    //
    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    // -----------------------------------------------------------------------------
    //  public void updateBestScore()
    //  функция обновления лучшего щета
    // -----------------------------------------------------------------------------
    //
    public void updateBestScore(){
        if (score > bestScore){
            bestScore = score;
        }
    }


}
