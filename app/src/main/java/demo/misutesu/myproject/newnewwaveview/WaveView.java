package demo.misutesu.myproject.newnewwaveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import demo.misutesu.myproject.R;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/6/21 0021 11:54
 * @description :
 */
public class WaveView extends View {

    private static String TAG = WaveView.class.getName();

    private Context mContext;

    private int color;
    private int startColor;
    private int endColor;
    private int angle = -1;
    private int progress = 0;
    private boolean isFromUp = true;
    private boolean isFromLeft = true;

    private List<Wave> mWaveList = new ArrayList<>();

    private Paint mPaint = new Paint();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        initDefaultValue();

        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.WaveView);
            array.recycle();
        }
    }

    private void initDefaultValue() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = mContext.getResources().getColor(R.color.colorPrimary, mContext.getTheme());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            color = mContext.getResources().getColor(R.color.colorPrimary);
        } else {
            color = Color.parseColor("#3F51B5");
        }

        mPaint.setAntiAlias(true);

        mPaint.setColor(color);
    }

    private void updateWavePath() {
        int width = getWidth();
        int height = getHeight();

        for (Wave wave : mWaveList) {
            wave.initPath(width, height, isFromUp, isFromLeft);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateWavePath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        for (Wave wave : mWaveList) {
            canvas.save();
            canvas.translate(isFromLeft ? 0 : width, height / 2);
            canvas.drawPath(wave.path, mPaint);
            canvas.restore();
        }

//        invalidate();
    }

    public void addWave(Wave wave) {
        mWaveList.add(wave);
        if (getWidth() != 0) {
            updateWavePath();
        }
    }
}
