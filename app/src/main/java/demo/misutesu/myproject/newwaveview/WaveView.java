package demo.misutesu.myproject.newwaveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import demo.misutesu.myproject.R;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/6/13 0013 15:44
 * @description :
 */
public class WaveView extends View {

    private final long DRAW_TIME = 10;

    private final int MOVE = 1;

    private Context mContext;

    private List<Wave> mWaveList;
    private Paint mPaint;

    private Handler mHandler;

    public WaveView(Context context) {
        super(context);
        init(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MOVE);
        mContext = null;
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        initDefaultValue();

        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.WaveView);
            array.recycle();
        }

        mHandler = new Handler(message -> {
            for (Wave wave : mWaveList) {
                if (wave.getMoveLen() >= wave.getAllMoveLen()) {
                    wave.reset();
                } else {
                    wave.addSpeed(mPaint);
                }
            }

            invalidate();
            start();
            return false;
        });
    }

    private void initDefaultValue() {
        mWaveList = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public WaveView addWave(Wave wave) {
        mWaveList.add(wave);
        invalidate();
        return this;
    }

    public void start() {
        mHandler.sendEmptyMessageDelayed(MOVE, DRAW_TIME);
    }

    public void pause() {
        mHandler.removeMessages(MOVE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultSize = getResources().getDimensionPixelOffset(R.dimen.dp_72);
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

        for (Wave wave : mWaveList) {
            int waveHeight = (int) (height * wave.getSizePercent()) / 2;

            int x, y;
            if (wave.isFromLeft()) {
                x = -wave.getMoveLen();
            } else {
                x = width + wave.getMoveLen();
            }

            if (wave.isFromUp()) {
                y = waveHeight;
            } else {
                y = height - waveHeight;
            }

            canvas.save();
            canvas.translate(x, y);
            canvas.drawPath(wave.init(width, height, mPaint), mPaint);
            canvas.restore();
        }
    }
}
