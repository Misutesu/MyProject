package demo.misutesu.myproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/5/22 0022 16:41
 * @description :
 */
public class DownloadView extends View {

    private Context mContext;

    private int strokeWidth;
    private int progressBgColor;
    private int progressColor;

    private int defaultPadding;

    private int progress;

    private Paint mPaint;

    private Path mProgressBgPath;
    private Path mProgressPath;

    private Point[] points;

    public DownloadView(Context context) {
        super(context);
        init(context, null);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        strokeWidth = mContext.getResources().getDimensionPixelSize(R.dimen.dp_8);

        progressBgColor = Color.parseColor("#70d61517");
        progressColor = Color.parseColor("#d61517");

        defaultPadding = mContext.getResources().getDimensionPixelSize(R.dimen.dp_8);

        progress = 0;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);

        mProgressBgPath = new Path();
        mProgressPath = new Path();

        points = new Point[]{new Point(), new Point(), new Point()};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = getResources().getDimensionPixelOffset(R.dimen.dp_200);
        int defaultHeight = getResources().getDimensionPixelOffset(R.dimen.dp_120);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSpecMode == MeasureSpec.AT_MOST ? defaultWidth : widthSpecSize,
                heightSpecMode == MeasureSpec.AT_MOST ? defaultHeight : heightSpecSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(0, 0);

        int width = getWidth();
        int height = getHeight();

        points[0].set(defaultPadding, height / 2);
        points[2].set(width - defaultPadding, height / 2);

        int progressWidth = (width - (defaultPadding * 2)) * progress / 100;
        int progressHeight = (50 - Math.abs(progress - 50)) * (height / 3) / 50;
        points[1].set(defaultPadding + progressWidth, (height / 2) + progressHeight);

        mProgressBgPath.rewind();
        mProgressPath.rewind();

        //drawBgProgress
        mProgressBgPath.moveTo(points[0].x, points[0].y);
        mProgressBgPath.lineTo(points[1].x, points[1].y);
        mProgressBgPath.lineTo(points[2].x, points[2].y);

        mPaint.setColor(progressBgColor);

        canvas.drawPath(mProgressBgPath, mPaint);

        //drawProgress
        mProgressPath.moveTo(points[0].x, points[0].y);
        mProgressPath.lineTo(points[1].x, points[1].y);

        mPaint.setColor(progressColor);

        canvas.drawPath(mProgressPath, mPaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}
