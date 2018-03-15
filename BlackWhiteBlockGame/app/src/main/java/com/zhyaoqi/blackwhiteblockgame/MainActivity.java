package com.zhyaoqi.blackwhiteblockgame;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.zhyaoqi.blackwhiteblockgame.view.GameSurfaceView;

/**
 * Created by zhyaoqi on 2016/10/25.
 * 邮箱:924690337@qq.com
 * 描述：
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        GameSurfaceView game = new GameSurfaceView(this);

//        setValues(game);

        setContentView(game);
    }


    //设置自定义的参数
    private void setValues(GameSurfaceView game){
        //设置初始速度
        game.setSpeedInit(10);
        //设置是否加速
        game.setSpeedUp(true);
        //设置加速度
        game.setAcceleration(10);
        //设置屏幕纵向有几个格子
        game.setVerticalCount(5);
        //设置屏幕横向有几个格子
        game.setHorizontalCount(5);
    }

}
