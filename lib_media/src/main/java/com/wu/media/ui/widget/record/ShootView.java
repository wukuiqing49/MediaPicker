package com.wu.media.ui.widget.record;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.wu.media.PickerConfig;
import com.wu.media.R;
import com.wu.media.ui.widget.record.listener.ShootViewClickListener;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/27 9:38
 * <p>
 * 名 字 : ShootView
 * <p>
 * 简 介 :
 */
public class ShootView extends View implements Runnable {


    Context mContext;
    float outScal = 1.0f;
    float innerScal = 1.0f;

    public static final int STATE_IDLE = 0x001;        //空闲状态
    public static final int STATE_RECORDERING = 0x004; //录制状态
    public static final int STATE_PHOTO = 0x006;

    int state = STATE_IDLE;

    //内圆半径
    int innerRadius = 60;
    //外圆半径
    int outerRadius = 100;
    //内圆颜色
    int innerColor = Color.WHITE;
    //外圆颜色
    int outerColor = Color.GRAY;
    //进度条颜色
    int progressColor = Color.GREEN;
    //进度条宽度
    int progressWidth = 20;
    //当前进度
    int curProgress = 0;
    //最大进度
    int maxTime = com.wu.media.PickerConfig.RECODE_MAX_TIME;
    //倒计时时间

    Paint innerPaint;
    Paint outerPaint;
    Paint progressPaint;
    private int contentWidthSize;
    //倒计时
    private CountDownTimer timer;
    //倒计时进度比
    float cutdownRatio = 0f;
    private boolean isShowProgress;
    ShootViewClickListener mListener;
    boolean recordStart = false;
    //拍摄类型 0  拍照  1 照片  2 视频
    private int recordType = 0;

    public ShootView(Context context) {
        this(context, null);
    }

    public ShootView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShootView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.MediaShootViewStyle);

        innerColor = typedArray.getColor(R.styleable.MediaShootViewStyle_innerColor, Color.WHITE);
        outerColor = typedArray.getColor(R.styleable.MediaShootViewStyle_outerColor, Color.GRAY);
        progressColor = typedArray.getColor(R.styleable.MediaShootViewStyle_mediaProgressColor, Color.GREEN);

        innerRadius = typedArray.getDimensionPixelSize(R.styleable.MediaShootViewStyle_innerRadius, 30);
        outerRadius = typedArray.getDimensionPixelSize(R.styleable.MediaShootViewStyle_outerRadius, 60);
        progressWidth = typedArray.getDimensionPixelSize(R.styleable.MediaShootViewStyle_mediaProgressWidth, 10);

        curProgress = typedArray.getInt(R.styleable.MediaShootViewStyle_curProgress, 0);
        maxTime = typedArray.getInt(R.styleable.MediaShootViewStyle_maxProgress, PickerConfig.RECODE_MAX_TIME);

        typedArray.recycle();

        innerPaint = new Paint();
        innerPaint.setAntiAlias(false);
        innerPaint.setColor(innerColor);

        outerPaint = new Paint();
        outerPaint.setAntiAlias(false);
        outerPaint.setColor(outerColor);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(false);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
    }


    public void setOnShootListener(ShootViewClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getCustomSize(100, widthMeasureSpec);
        int height = getCustomSize(100, heightMeasureSpec);

        if (width < height) {
            height = width;
        } else {
            width = height;
        }
        contentWidthSize = width;
        setMeasuredDimension(width, height);
    }

    private int getCustomSize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {
                //如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST: {
                //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
        }
        return mySize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(contentWidthSize / 2, contentWidthSize / 2, outerRadius * outScal, outerPaint);
        canvas.drawCircle(contentWidthSize / 2, contentWidthSize / 2, innerRadius * innerScal, innerPaint);
        if (isShowProgress) {
            float top = (contentWidthSize / 2 - outerRadius * outScal) + progressWidth / 2;
            float right = contentWidthSize / 2 + outerRadius * outScal - progressWidth / 2;
            RectF rectF = new RectF(top, top, right, right);
            //绘制圆弧进度
            canvas.drawArc(rectF, -90, cutdownRatio * 360, false, progressPaint);
        }


    }

    //处理长按事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                postDelayed(this, 500);
                break;
            case MotionEvent.ACTION_UP:
                if (timer != null) timer.cancel();
                progressUp();
                removeCallbacks(this);
                break;

        }
        return true;
    }

    /**
     * 处理拍摄的结果
     */
    private void progressUp() {
        outScal = 1.0f;
        innerScal = 1.0f;
        isShowProgress = false;
        cutdownRatio = 0f;
        invalidate();
        if (state == STATE_PHOTO) {
            if (recordType == 2) {
                mListener.onRecordVideoFail("录制时间过短");
                state = STATE_IDLE;
            } else {
                if (mListener != null) mListener.onPhotograph();
            }

        } else if (state == STATE_RECORDERING) {
            if (mListener != null && recordStart) {
                mListener.onFinishRecordVideo();
            } else {
                mListener.onRecordVideoFail("录制时间过短");
                state = STATE_IDLE;
            }
            recordStart = false;
        } else if (state == STATE_IDLE) {
            if (recordType == 2) {
                mListener.onRecordVideoFail("录制时间过短");
                state = STATE_IDLE;
            } else {
                if (mListener != null) mListener.onPhotograph();
            }
        }
    }


    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    //延迟  500 展示
    @Override
    public void run() {
        outScal = 1.1f;
        innerScal = 1f;
        if (recordType != 1) {
            //倒计时更新进度
            timer = new CountDownTimer(maxTime, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished / 5 != 0) {
                        upDateProgress(millisUntilFinished);
                    }
                    if (!recordStart && (maxTime - millisUntilFinished) > 5000) {
                        recordStart = true;
                    }
                }

                @Override
                public void onFinish() {
                    isShowProgress = false;
                    if (mListener != null) mListener.onFinishRecordVideo();
                }

            };
            if (timer != null) {
                timer.start();
            }
        }

        invalidate();
    }

    /**
     * 更新进度条状态
     *
     * @param millisUntilFinished
     */
    private void upDateProgress(long millisUntilFinished) {
        if (maxTime != 0) cutdownRatio = 1 - (float) millisUntilFinished / maxTime;

        if (maxTime - millisUntilFinished > 500) {
            //长按
            state = STATE_RECORDERING;
            if (mListener != null && !isShowProgress) {
                mListener.onStartRecordVideo();
                isShowProgress = true;
            }
            invalidate();

        }

    }

    public void reset() {
        cutdownRatio = 0f;
        isShowProgress=false;
        recordStart=false;
        curProgress = 0;
        if (timer != null) timer.cancel();
        state = STATE_IDLE;
        invalidate();
    }

    /**
     * 设置拍摄类型
     *
     * @param type
     */
    public void setRecodeType(int type) {
        recordType = type;
    }
}
