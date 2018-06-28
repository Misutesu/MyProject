package demo.misutesu.myproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/5/22 0022 9:44
 * @description :
 */
public class ProgressBar extends View {

    private final String TAG = "ProgressBar";

    private final int START_ANGLE = 0;
    private final int MIN_ANGEL = 10;
    private final int MAX_ANGLE = 270;

    private final long ROTATE_TIME = 1000;
    private final long SCALE_TIME = 500;
    private final long WAIT_TIME = 200;

    private Context mContext;

    private int strokeWidth;
    private int progressColor;

    private int startAngle;
    private int lastAngle;
    private int sweepAngle;

    private Paint mPaint;
    private RectF mOval;

    private ValueAnimator rotateAnim;
    private AnimatorSet progressAnim;

    private AnimatorListenerAdapter listenerAdapter;

    public ProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        strokeWidth = mContext.getResources().getDimensionPixelSize(R.dimen.dp_4);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            progressColor = context.getResources().getColor(R.color.colorPrimary, context.getTheme());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressColor = context.getResources().getColor(R.color.colorPrimary);
        } else {
            progressColor = Color.parseColor("#d61517");
        }

        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.ProgressBar);
            strokeWidth = array.getDimensionPixelSize(R.styleable.ProgressBar_stroke_width, strokeWidth);
            progressColor = array.getColor(R.styleable.ProgressBar_progress_color, progressColor);
            array.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(progressColor);

        mOval = new RectF();

        startAngle = START_ANGLE;
        sweepAngle = MAX_ANGLE;

        rotateAnim = ValueAnimator.ofFloat(getRotation() + 0f, 360f);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setDuration(ROTATE_TIME);
        rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnim.addUpdateListener(valueAnimator -> setRotation((Float) valueAnimator.getAnimatedValue()));
        rotateAnim.start();

        startProgressAnim();
    }

    private void startProgressAnim() {
        lastAngle = startAngle;
        ValueAnimator narrowAnim = ValueAnimator.ofInt(MAX_ANGLE, MIN_ANGEL);
        narrowAnim.setDuration(SCALE_TIME);
        narrowAnim.addUpdateListener(valueAnimator -> {
            int value = (int) valueAnimator.getAnimatedValue();
            startAngle = lastAngle + MAX_ANGLE - value;
            sweepAngle = value;
            invalidate();
        });

        ValueAnimator enlargeAnim = ValueAnimator.ofInt(MIN_ANGEL, MAX_ANGLE);
        enlargeAnim.setDuration(SCALE_TIME);
        enlargeAnim.addUpdateListener(valueAnimator -> {
            sweepAngle = (int) valueAnimator.getAnimatedValue();
            invalidate();
        });

        listenerAdapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startProgressAnim();
            }
        };

        progressAnim = new AnimatorSet();
        progressAnim.playSequentially(narrowAnim, ValueAnimator.ofInt(0, 0).setDuration(WAIT_TIME), enlargeAnim, ValueAnimator.ofInt(0, 0).setDuration(WAIT_TIME));
        progressAnim.addListener(listenerAdapter);
        progressAnim.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultSize = getResources().getDimensionPixelOffset(R.dimen.dp_50);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSpecMode == MeasureSpec.AT_MOST ? defaultSize : widthSpecSize,
                heightSpecMode == MeasureSpec.AT_MOST ? defaultSize : heightSpecSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) / 2;
        mOval.left = (width < height ? 0 : (width / 2) - size) + strokeWidth;
        mOval.top = (width < height ? (height / 2) - size : 0) + strokeWidth;
        mOval.right = (width < height ? width : (width / 2) + size) - strokeWidth;
        mOval.bottom = (width < height ? (height / 2) + size : height) - strokeWidth;
        canvas.drawArc(mOval, startAngle, sweepAngle, false, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (rotateAnim != null) {
            rotateAnim.end();
        }
        if (progressAnim != null) {
            progressAnim.removeAllListeners();
            progressAnim.end();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                rotateAnim.resume();
                progressAnim.resume();
            } else {
                progressAnim.addListener(listenerAdapter);
                rotateAnim.start();
                progressAnim.start();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                rotateAnim.pause();
                progressAnim.pause();
            } else {
                progressAnim.removeAllListeners();
                rotateAnim.end();
                progressAnim.end();
                lastAngle = START_ANGLE;
                sweepAngle = MAX_ANGLE;
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (getVisibility() != View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    rotateAnim.resume();
                    progressAnim.resume();
                } else {
                    progressAnim.addListener(listenerAdapter);
                    rotateAnim.start();
                    progressAnim.start();
                }
            }
        } else {
            if (getVisibility() == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    rotateAnim.pause();
                    progressAnim.pause();
                } else {
                    progressAnim.removeAllListeners();
                    rotateAnim.end();
                    progressAnim.end();
                    lastAngle = START_ANGLE;
                    sweepAngle = MAX_ANGLE;
                }
            }
        }
        super.setVisibility(visibility);
    }
}
