package com.project.thor.snake.views;
/**
 *@file SnakeView.java
 *@authot Bohomaz A.
 *@version 1
 *@date 2018.11.23
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.project.thor.snake.R;
import com.project.thor.snake.enums.ColorMode;
import com.project.thor.snake.enums.TileType;

public class SnakeView extends View{

    private Paint mPaint = new Paint();
    private TileType snakeViewMap[][];
    SnakeView view = (SnakeView)findViewById(R.id.snakeView);
    ColorMode colMode;

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    // -----------------------------------------------------------------------------
    //  public void setSnakeViewMap(TileType[][] map )
    //  сеттер переменой snakeViewMap
    // -----------------------------------------------------------------------------
    //
    public void setSnakeViewMap(TileType[][] map, ColorMode mode ){ this.snakeViewMap = map;
    this.colMode = mode;
    }
    // -----------------------------------------------------------------------------
    //  protected void onDraw(Canvas canvas)
    //  переопрелление метода в котором задані параметры отрисовки матрицы
    // -----------------------------------------------------------------------------
    //
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int darkModbg = Color.rgb(13, 28, 51);
        int darkModNothing = Color.rgb(17, 35, 63);
        int whiteModWall = Color.rgb(252, 151, 78);
        int darkModWall = Color.rgb(65, 108, 175);
        int darkModTail = Color.rgb(196, 195, 194);
        int darkModHead = Color.rgb(242, 176, 9);

        if( snakeViewMap != null){
            float tileSizeX = canvas.getWidth() / snakeViewMap.length;
            float tileSizeY = canvas.getHeight() / snakeViewMap[0].length;

            float circleSize = Math.min(tileSizeX, tileSizeY) / 2;

            for (int x = 0; x < snakeViewMap.length; x++){
                for ( int y = 0; y < snakeViewMap[x].length; y++){
                    switch (snakeViewMap[x][y]){
                        // Установка цветов для обьектов
                        case Nothing:
                            if (colMode == ColorMode.WhiteMode){
                                mPaint.setColor(Color.WHITE);
                                view.setBackgroundColor(Color.rgb(250,250,250));
                            }else {
                                mPaint.setColor(darkModNothing);
                                view.setBackgroundColor(darkModbg);
                            }
                            break;
                        case Wall:
                            if (colMode == ColorMode.WhiteMode){
                                mPaint.setColor(whiteModWall);
                            }else{
                                mPaint.setColor(darkModWall);
                            }
                            break;
                        case SnakeHead:
                            if (colMode == ColorMode.WhiteMode) {
                                mPaint.setColor(Color.RED);
                            }else {
                                mPaint.setColor(darkModHead);
                            }
                            break;
                        case SnakeTail:
                            if (colMode == ColorMode.WhiteMode) {
                                mPaint.setColor(Color.GREEN);
                            }else {
                                mPaint.setColor(darkModTail);
                            }
                            break;
                        case Apple:
                            mPaint.setColor(Color.RED);
                            break;
                    }

                    canvas.drawCircle(x * tileSizeX + tileSizeX / 2f + circleSize / 2, y * tileSizeY + tileSizeY / 2f + circleSize / 2, circleSize, mPaint);
                }
            }
        }
    }
}
