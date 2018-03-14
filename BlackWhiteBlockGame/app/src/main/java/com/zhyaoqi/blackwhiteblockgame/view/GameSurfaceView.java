package com.zhyaoqi.blackwhiteblockgame.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.zhyaoqi.blackwhiteblockgame.MainActivity;
import com.zhyaoqi.blackwhiteblockgame.R;
import com.zhyaoqi.blackwhiteblockgame.util.DensityUtils;
import com.zhyaoqi.blackwhiteblockgame.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhyaoqi on 2016/10/25.
 * 邮箱:924690337@qq.com
 * 描述：
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable,View.OnTouchListener{
    private int horizontalCount;
    private int verticalCount;
    private final int HORIZONTAL_COUNT_DEFAULT = 6;
    private final int VERTICAL_COUNT_DEFAULT = 4;
    private SurfaceHolder mHolder;

    private Canvas mCanvas;

    private Thread t;
    private boolean isRunning;

    private int mWidth;
    private int mHeight;

    private List<BlockGroup> blockGroupList ;
    private Bitmap blackBitmap;
    private Bitmap blackPressBitmap;
    private Bitmap whiteBitmap;

    private boolean speedUp = true;
    private int mSpeed;

    private final int SPEED_DEFAULT = 10;

    private int speedInit ;

    private int acceleration;

    private final int ACCELERATION_DEFAULT = 3;

    private Rect gameRect = new Rect();

    private Rect scorePaintBound = new Rect();

    private int score;
    private int scoreTextSize;
    private final int SCORE_TEXT_SIZE_DEFAULT = 60;
    private int scoreColor;
    private final int SCORE_COLOR_DEFAULT = Color.parseColor("#ff4848");
    private Paint scorePaint;

    private Context context;


    public enum GameStatus
    {
        WAITTING, RUNNING, STOP;
    }
    private GameStatus mGameStatus = GameStatus.WAITTING;

    private Vibrator vibrator;
    private int vibratorDuration = 300;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    showResult("没点到");
                    break;
                case 2:
                    showResult("点到白色块了");
                    break;
            }
        }
    };

    public GameSurfaceView(Context context) {
        this(context,null);
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);// 设置画布 背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        // 设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常亮
        this.setKeepScreenOn(true);

        this.setOnTouchListener(this);

        horizontalCount = HORIZONTAL_COUNT_DEFAULT;
        verticalCount = VERTICAL_COUNT_DEFAULT;

        mSpeed = DensityUtils.dp2px(getContext(),SPEED_DEFAULT);

        acceleration = DensityUtils.dp2px(getContext(),ACCELERATION_DEFAULT);


        scoreTextSize = SCORE_TEXT_SIZE_DEFAULT;
        scorePaint = new Paint();
        scorePaint.setStyle(Paint.Style.FILL);
        scorePaint.setAntiAlias(true);
        scoreColor = SCORE_COLOR_DEFAULT;
        scorePaint.setColor(scoreColor);
        scorePaint.setTextSize(DensityUtils.dp2px(getContext(),scoreTextSize));
        scorePaint.setStrokeWidth(DensityUtils.dp2px(getContext(),3));

        vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);

        initBitmap();
        blockGroupList = new ArrayList<>();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gameRect.set(0,0,width,height);

        mWidth = width;
        mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning)
        {
            long start = System.currentTimeMillis();
            logic();
            draw();
            long end = System.currentTimeMillis();

            try
            {
                if (end - start < 30)
                {
                    Thread.sleep(30 - (end - start));
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    }

    private void logic(){
        if (blockGroupList.size()<=0) {
            for (int i = 0; i < verticalCount; i++) {
                BlockGroup blockGroup = new BlockGroup(getContext(), horizontalCount,mWidth, mHeight / verticalCount, mHeight / verticalCount * (verticalCount-1 - i),blackBitmap,blackPressBitmap,whiteBitmap);
                blockGroupList.add(blockGroup);
            }
            return;
        }
        if (mGameStatus == GameStatus.RUNNING){
            if (blockGroupList.get(blockGroupList.size()-1).getY()>0){
                BlockGroup blockGroup = new BlockGroup(getContext(),horizontalCount, mWidth, mHeight / verticalCount, blockGroupList.get(blockGroupList.size()-1).getY()-mHeight / verticalCount,blackBitmap,blackPressBitmap,whiteBitmap);
                blockGroupList.add(blockGroup);
            }
            if (blockGroupList.get(0).getY()>=mHeight){
                if (!blockGroupList.get(0).isHasTrueClick()){
                    mGameStatus = GameStatus.STOP;
                    vibrate();
                    handler.sendEmptyMessage(1);
                    return;
                }
                blockGroupList.remove(0);
            }

            for (int i =0;i<blockGroupList.size();i++){
                blockGroupList.get(i).setY(blockGroupList.get(i).getY()+mSpeed);
            }
            speedUp();
        }
    }
    private void draw(){
        try
        {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null)
            {
                drawBg();
                drawBlockGroup();
                drawScore();
            }
        } catch (Exception e)
        {
        } finally
        {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    /**
     * 加速
     */
    private void speedUp(){
        if (isSpeedUp()) {
            if (score % acceleration == 0) {
                mSpeed++;
            }
        }
    }
    private void drawBg() {
        mCanvas.drawBitmap(loadBitmapByResId(R.mipmap.white_block), null, gameRect, null);
    }
    private void drawBlockGroup(){
        for (BlockGroup blockGroup : blockGroupList){
            blockGroup.draw(mCanvas);
        }
    }
    private void initBitmap(){
        blackBitmap = loadBitmapByResId(R.mipmap.black_block);
        blackPressBitmap = loadBitmapByResId(R.mipmap.black_block_p);
        whiteBitmap = loadBitmapByResId(R.mipmap.white_block);
    }
    private Bitmap loadBitmapByResId(int resId){
        return BitmapFactory.decodeResource(getResources(),resId);
    }

    private void drawScore(){
        String scoreStr = score+"";
        scorePaint.getTextBounds(scoreStr,0,scoreStr.length(),scorePaintBound);
        mCanvas.drawText(scoreStr,mWidth/2-scorePaintBound.width()/2,DensityUtils.dp2px(getContext(),30)+scorePaintBound.height(),scorePaint);
    }
    private void vibrate(){
        if (vibratorDuration>0) {
            vibrator.vibrate(vibratorDuration);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGameStatus ==GameStatus.STOP){
            return true;
        }
        if (mGameStatus == GameStatus.WAITTING){
            mGameStatus = GameStatus.RUNNING;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                int size = blockGroupList.size();
                for (int i =0;i<size;i++) {
                    BlockGroup blockGroup = blockGroupList.get(i);
                    int bgIndex = blockGroup.getBlackIndex();
                    int x = mWidth / horizontalCount * bgIndex;
                    int y = blockGroup.getY();
                    if (!blockGroup.isHasTrueClick()) {
                        if (downX > x && downX < x + mWidth / horizontalCount && downY > y && downY < y + mHeight) {
                            blockGroup.setHasTrueClick(true);
                            ++score;
                        } else {
                            mGameStatus = GameStatus.STOP;
                            vibrate();
                            handler.sendEmptyMessage(2);
                        }
                        break;
                    }
                }
        }
        return true;
    }

    private void showResult(String reason){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("游戏结束!")
                .setMessage(reason)
                .setPositiveButton("重新玩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reset();
                        mGameStatus = GameSurfaceView.GameStatus.WAITTING;
                    }
                })
                .setNegativeButton("结束游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                }).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void reset(){
        mSpeed = DensityUtils.dp2px(getContext(), speedInit);
        score = 0;
        blockGroupList = new ArrayList<>();
    };

    public boolean isSpeedUp() {
        return speedUp;
    }

    public void setSpeedUp(boolean speedUp) {
        this.speedUp = speedUp;
    }

    public int getVibratorDuration() {
        return vibratorDuration;
    }

    public void setVibratorDuration(int vibratorDuration) {
        this.vibratorDuration = vibratorDuration;
    }

    public int getScoreTextSize() {
        return scoreTextSize;
    }

    public void setScoreTextSize(int scoreTextSize) {
        this.scoreTextSize = scoreTextSize;
        scorePaint.setTextSize(DensityUtils.dp2px(getContext(),scoreTextSize));
    }

    public int getScoreColor() {
        return scoreColor;
    }

    public void setScoreColor(int scoreColor) {
        this.scoreColor = scoreColor;
        scorePaint.setColor(scoreColor);
    }

    public Bitmap getWhiteBitmap() {
        return whiteBitmap;
    }

    public void setWhiteBitmap(Bitmap whiteBitmap) {
        this.whiteBitmap = whiteBitmap;
    }

    public Bitmap getBlackPressBitmap() {
        return blackPressBitmap;
    }

    public void setBlackPressBitmap(Bitmap blackPressBitmap) {
        this.blackPressBitmap = blackPressBitmap;
    }

    public int getSpeedInit() {
        return speedInit;
    }

    public void setSpeedInit(int speedInit) {
        this.speedInit = speedInit;
        mSpeed = speedInit;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public Bitmap getBlackBitmap() {
        return blackBitmap;
    }

    public void setBlackBitmap(Bitmap blackBitmap) {
        this.blackBitmap = blackBitmap;
    }

    public int getVerticalCount() {
        return verticalCount;
    }

    public void setVerticalCount(int verticalCount) {
        this.verticalCount = verticalCount;
    }

    public int getHorizontalCount() {
        return horizontalCount;
    }

    public void setHorizontalCount(int horizontalCount) {
        this.horizontalCount = horizontalCount;
    }
}
