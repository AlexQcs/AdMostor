package com.hc.admc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


import java.util.List;

/**
 * Created by Alex on 2018/1/5.
 * 备注:
 */

public class CustomTextView extends View {
    /**
     * 界面刷新时间(ms)
     */
    public static final int INVALIDATE_TIME = 40;
    /**
     * 每次移动的像素点(px)
     */
    public static final int INVALIDATE_STEP = 1;
    /**
     * 一次移动完成后等待的时间(ms)
     */
    public static final int WAIT_TIME = 1;

    private float y = 0f;// 文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变量

    /**
     * 滚动文字前后的间隔
     */
    private String space = "";
    private String drawingText = "";
    private TextPaint paint;
    public boolean exitFlag;
    private float textWidth;
    private String _mText;
    private int posX = 0;
    private float posY;
    private int width;
    private RectF rf;

    private Handler mHandler = new Handler();

    private onMoveOver mOnMoveOver;

    private List<String> mList;
    private volatile int index = 0;

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setTextSize(30F);
        rf = new RectF(0, 0, 0, 0);

    }

    public void setText(String text) {
        this._mText = text;
        this.drawingText = _mText;
        layoutView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//        layoutView();
    }

    private void layoutView() {
        width = getWidth();
        rf.right = width;
        rf.bottom = getHeight();
        textWidth = paint.measureText(_mText, 0, _mText.length());
        posY = getTextDrawingBaseline(paint, rf);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getVisibility() != View.VISIBLE || TextUtils.isEmpty(drawingText)) {
            return;
        }
        canvas.save();
        paint.setColor(Color.BLACK);
        canvas.drawText(drawingText, 0, drawingText.length(), posX + getWidth(), posY, paint);
        canvas.restore();

    }



    private Runnable moveRun = new Runnable() {

        @Override
        public void run() {
//            if(width >= textWidth){
//                return;
//            }
            layoutView();
            drawingText = _mText;
            posX -= INVALIDATE_STEP;
            if (posX >= -1 * INVALIDATE_STEP / 2 && posX <= INVALIDATE_STEP / 2) {
//            if(posX >= -1 * INVALIDATE_STEP / 2){
                mHandler.postDelayed(this, WAIT_TIME);
                invalidate();
                return;
            }
//            if (posX < -1 * textWidth - paint.measureText(space, 0, space.length())) {
            float movelenth = textWidth + getWidth();
            if (textWidth == 0) movelenth = 0;
            if (posX < -1 * movelenth) {

                mOnMoveOver.onOver();

                posX = INVALIDATE_STEP;
            }
            invalidate();
            if (!exitFlag) {
                mHandler.postDelayed(this, INVALIDATE_TIME);
                return;
            }
            posX = 0;
        }
    };

    public List<String> getList() {
        return mList;
    }

    public void setList(List<String> list) {
        mList = list;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        index = 0;
        stopMove();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
//        layoutView();
//        startMove();
    }



    public void stopMove() {
        exitFlag = true;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void startMove() {
        exitFlag = false;
        mHandler.post(moveRun);
    }


    /**
     * 获取绘制文字的baseline
     *
     * @param paint
     * @param targetRect
     * @return
     */
    public static float getTextDrawingBaseline(Paint paint, RectF targetRect) {
        if (paint == null || targetRect == null) {
            return 0;
        }
        Paint.FontMetrics fontMetric = paint.getFontMetrics();
        return targetRect.top + (targetRect.height() - fontMetric.bottom + fontMetric.top) / 2.0f - fontMetric.top;
    }

    public void setOnMoveOver(onMoveOver oveOver) {
        this.mOnMoveOver = oveOver;
    }

    public interface onMoveOver {
        void onOver();
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
