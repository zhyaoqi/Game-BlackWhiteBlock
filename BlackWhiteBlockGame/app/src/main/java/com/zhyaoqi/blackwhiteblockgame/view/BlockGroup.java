package com.zhyaoqi.blackwhiteblockgame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhyaoqi on 2016/10/25.
 * 邮箱:924690337@qq.com
 * 描述：
 */
public class BlockGroup {
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private int y;

    private List<Block> blockList ;

    private int blackIndex;

    private boolean hasTrueClick;

    private Bitmap blackPressBitmap;
    public BlockGroup(Context context, int horizontalCount,int mWidth, int mHeight, int y, Bitmap blackBitmap,Bitmap blackPressBitmap,Bitmap whiteBitmap){
        this.mContext = context;

        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.y = y;

        this.blackPressBitmap = blackPressBitmap;

        blockList = new ArrayList<>();
        blackIndex = new Random().nextInt(horizontalCount);
        for (int i =0;i<horizontalCount;i++){
            boolean isBlack = false;
            if (i == blackIndex){
                isBlack = true;
            }
            Block block = new Block(mContext,isBlack,mWidth/horizontalCount*i,y,mWidth/horizontalCount,mHeight,blackBitmap,whiteBitmap);
            blockList.add(block);
        }
    }

    public void draw(Canvas canvas){
        for (Block block:blockList){
            block.setY(y);
            block.draw(canvas);
        }
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getBlackIndex() {
        return blackIndex;
    }

    public boolean isHasTrueClick() {
        return hasTrueClick;
    }

    public void setHasTrueClick(boolean hasTrueClick) {
        this.hasTrueClick = hasTrueClick;
        blockList.get(blackIndex).setBlackBitmap(blackPressBitmap);
    }
}
