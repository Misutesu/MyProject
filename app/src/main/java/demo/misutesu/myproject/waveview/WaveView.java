package demo.misutesu.myproject.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import demo.misutesu.myproject.R;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/4/20 0020 11:45
 * @description :
 */
public class WaveView extends View {

    private final String TAG = "WaveView";

    private final int GROUP_POINT_NUM = 4;

    private final int INVALIDATE_TIME = 10;
    private final int MOVE = 1;

    private float speed = 2.0f;
    private int peakNum = 1;
    private int bgColor;

    private Context mContext;

    private List<Point> mPoints;

    private boolean isInitPoints;

    private Paint mPaint;

    private Path mPath;

    private Handler mHandler;

    private int mMoveLen;
    private int mAllWidth;

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

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.OldWaveView);
            peakNum = array.getInteger(R.styleable.OldWaveView_wave_peak_num, peakNum);
            speed = array.getFloat(R.styleable.OldWaveView_wave_move_speed, speed);
            bgColor = array.getColor(R.styleable.OldWaveView_wave_peak_color, bgColor);
            array.recycle();
        }

        mPaint = new Paint();

        mPath = new Path();

        mPoints = new ArrayList<>();

        for (int i = 0; i < (GROUP_POINT_NUM * peakNum * 2) + 1; i++) {
            mPoints.add(new Point());
        }

        mHandler = new Handler(msg -> {
            switch (msg.what) {
                case MOVE:
                    if (mMoveLen < mAllWidth) {
                        mMoveLen += speed;
                        for (int i = 0; i < mPoints.size(); i++) {
                            Point point = mPoints.get(i);
                            point.setX(point.getX() + speed);
                        }
                    } else {
                        mMoveLen = 0;
                        isInitPoints = false;
                    }

                    invalidate();
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultSize = getResources().getDimensionPixelOffset(R.dimen.dp_36);
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

        mAllWidth = width;

        mPaint.setColor(bgColor);
        mPaint.setAntiAlias(true);

        mPath.rewind();

        canvas.translate(-width, height / 2);

        for (int i = 0; i < mPoints.size(); i++) {
            Point point = mPoints.get(i);
            if (!isInitPoints) {
                point.setX(width * 2 * i / (mPoints.size() - 1));
            }
            switch (i % GROUP_POINT_NUM) {
                case 0:
                case 2:
                    point.setY(0);
                    break;
                case 1:
                    point.setY(-height / 2);
                    break;
                case 3:
                    point.setY(height / 2);
                    break;
            }
            if (i == 0) {
                mPath.moveTo(point.getX(), point.getY());
            } else if (i % 2 == 0) {
                mPath.quadTo(mPoints.get(i - 1).getX(), mPoints.get(i - 1).getY(), point.getX(), point.getY());
            }
        }

        mPath.lineTo(width * 2, -height / 2);
        mPath.lineTo(0, -height / 2);
        mPath.lineTo(0, 0);

        canvas.drawPath(mPath, mPaint);

        isInitPoints = true;

        start();
    }

    public void start() {
        mHandler.sendEmptyMessageDelayed(MOVE, INVALIDATE_TIME);
    }

    public void stop() {
        mHandler.removeMessages(MOVE);
    }
}
