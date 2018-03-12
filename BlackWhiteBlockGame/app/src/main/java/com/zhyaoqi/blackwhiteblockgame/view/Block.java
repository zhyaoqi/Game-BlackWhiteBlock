package com.zhyaoqi.blackwhiteblockgame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.zhyaoqi.blackwhiteblockgame.util.DensityUtils;

/**
 * Created by zhyaoqi on 2016/10/25.
 * 邮箱:924690337@qq.com
 * 描述：
 */
public class Block  {
    private Context mContext;
    private int x;
    private int y;
    private int mWidth;
    private int mHeight;

    private boolean isBlack;

    private Bitmap blackBitmap;
    private Bitmap whiteBitmap;

    private RectF rect = new RectF();

    private Paint mPaint;

    public Block(Context context,boolean isBlack, int x, int y, int mWidth, int mHeight,Bitmap blackBitmap,Bitmap whiteBitmap){
        this.mContext = context;
        this.isBlack = isBlack;
        this.x = x;
        this.y = y;
        this.mWidth = mWidth;
        this.mHeight = mHeight;

        this.blackBitmap = blackBitmap;
        this.whiteBitmap = whiteBitmap;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor("#7f7f7f"));
        mPaint.setStrokeWidth(DensityUtils.dp2px(context,1));
    }

    public void draw(Canvas canvas){
        rect.set(x,y,x+mWidth,y+mHeight);
        if (isBlack){
            canvas.drawBitmap(blackBitmap,null,rect,null);
        }else {
            canvas.drawBitmap(whiteBitmap,null,rect,null);
        }
        //画4条边
        canvas.drawLine(x,y,x,y+mHeight,mPaint);
        canvas.drawLine(x,y,x+mWidth,y,mPaint);
        canvas.drawLine(x+mWidth,y,x+mWidth,y+mHeight,mPaint);
        canvas.drawLine(x,y+mHeight,x+mWidth,y+mHeight,mPaint);
}

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setBlackBitmap(Bitmap blackBitmap) {
        this.blackBitmap = blackBitmap;
    }
}