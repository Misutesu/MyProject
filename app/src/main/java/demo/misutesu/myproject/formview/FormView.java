package demo.misutesu.myproject.formview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import demo.misutesu.myproject.R;
import demo.misutesu.myproject.formview.bean.Coordinate;
import demo.misutesu.myproject.formview.bean.LimitShape;
import demo.misutesu.myproject.formview.bean.XPoints;
import demo.misutesu.myproject.formview.bean.YPoints;

public class FormView extends View {

    private final float DEFAULT_WIDTH = 240;
    private final float DEFAULT_HEIGHT = 120;
    private final float DEFAULT_COORDINATE_TEXT_SIZE = 14;
    private final float DEFAULT_COORDINATE_NAME_TEXT_SIZE = 12;
    private final float DEFAULT_PEAK_LINE_WIDTH = 2;
    private final float DEFAULT_CIRCLE_RADIUS = 3;
    private final float DEFAULT_CIRCLE_STROKE_WIDTH = 2;

    public interface OnFormClickListener {
        void onPointClick(float x, float y, int position);
    }

    private int mWidth, mHeight;

    /**
     * 坐标线颜色
     */
    @ColorInt
    private int coordinateLineColor = Color.BLUE;
    /**
     * 坐标线字体颜色
     */
    @ColorInt
    private int coordinateTextColor = Color.BLUE;
    /**
     * 坐标线字体大小
     */
    private float coordinateTextSize;
    /**
     * 坐标说明字体大小
     */
    private float coordinateNameTextSize;
    /**
     * 波线颜色
     */
    @ColorInt
    private int peakLineColor = Color.BLUE;
    /**
     * 波线宽
     */
    private float peakLineWidth;
    /**
     * X点线颜色
     */
    @ColorInt
    private int pointLineColor = Color.BLUE;
    /**
     * 图标区域渐变下方颜色
     */
    @ColorInt
    private int shapeStartColor = Color.YELLOW;
    /**
     * 图标区域渐变上方颜色
     */
    @ColorInt
    private int shapeEndColor = Color.RED;
    /**
     * 图标区域颜色(设置后渐变色无效)
     */
    @ColorInt
    private int shapeColor = Integer.MAX_VALUE;
    /**
     * 点正常时颜色
     */
    @ColorInt
    private int circleNormalColor = Color.TRANSPARENT;
    /**
     * 点异常时颜色
     */
    @ColorInt
    private int circleErrorColor = Color.RED;
    /**
     * 点描边色
     */
    @ColorInt
    private int circleStrokeColor = Color.WHITE;
    /**
     * 点半径
     */
    private float circleRadius;
    /**
     * 点描边宽度
     */
    private float circleWidth;
    /**
     * 限位颜色
     */
    @ColorInt
    private int limitShapeColor = Color.BLUE;
    /**
     * 自动判断限位(=false时根据XPoint的isError判断)
     */
    private boolean isAutoJudgeError = true;
    /**
     * 是否开启xml预览(浪费性能)
     */
    private boolean isEnablePreview = false;

    private Coordinate mCoordinate;
    private LimitShape mLimitShape;
    private YPoints mYPoints;
    private XPoints mXPoints;

    private OnFormClickListener mOnFormClickListener;

    public FormView(Context context) {
        super(context);
        init(context, null);
    }

    public FormView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FormView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FormView);
            coordinateLineColor = array.getColor(R.styleable.FormView_coordinate_line_color, coordinateLineColor);
            coordinateTextColor = array.getColor(R.styleable.FormView_coordinate_text_color, coordinateTextColor);
            coordinateTextSize = array.getDimension(R.styleable.FormView_coordinate_text_size, sp2px(DEFAULT_COORDINATE_TEXT_SIZE));
            coordinateNameTextSize = array.getDimension(R.styleable.FormView_coordinate_name_text_size, sp2px(DEFAULT_COORDINATE_NAME_TEXT_SIZE));
            peakLineColor = array.getColor(R.styleable.FormView_peak_line_color, peakLineColor);
            peakLineWidth = array.getDimension(R.styleable.FormView_peak_line_width, dp2px(DEFAULT_PEAK_LINE_WIDTH));
            pointLineColor = array.getColor(R.styleable.FormView_point_line_color, pointLineColor);
            shapeStartColor = array.getColor(R.styleable.FormView_shape_start_color, shapeStartColor);
            shapeEndColor = array.getColor(R.styleable.FormView_shape_end_color, shapeEndColor);
            shapeColor = array.getColor(R.styleable.FormView_shape_color, shapeColor);
            circleNormalColor = array.getColor(R.styleable.FormView_circle_normal_color, circleNormalColor);
            circleErrorColor = array.getColor(R.styleable.FormView_circle_error_color, circleErrorColor);
            circleStrokeColor = array.getColor(R.styleable.FormView_circle_stroke_color, circleStrokeColor);
            circleRadius = array.getDimension(R.styleable.FormView_circle_radius, dp2px(DEFAULT_CIRCLE_RADIUS));
            circleWidth = array.getDimension(R.styleable.FormView_circle_width, dp2px(DEFAULT_CIRCLE_STROKE_WIDTH));
            limitShapeColor = array.getColor(R.styleable.FormView_limit_line_color, limitShapeColor);
            isAutoJudgeError = array.getBoolean(R.styleable.FormView_auto_judge_error, isAutoJudgeError);
            isEnablePreview = array.getBoolean(R.styleable.FormView_is_enable_xml_preview, isEnablePreview);
            array.recycle();
        }

        mCoordinate = new Coordinate();
        mLimitShape = new LimitShape();
        mYPoints = new YPoints();
        mXPoints = new XPoints();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = (int) dp2px(DEFAULT_WIDTH);
        int defaultHeight = (int) dp2px(DEFAULT_HEIGHT);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSpecMode == MeasureSpec.AT_MOST ? defaultWidth : widthSpecSize
                , heightSpecMode == MeasureSpec.AT_MOST ? defaultHeight : heightSpecSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mCoordinate.init(mWidth, mHeight);
        mYPoints.init(mCoordinate);
        mXPoints.init(mCoordinate);
        mLimitShape.init(mCoordinate);

        if (isEnablePreview) {
            getTestData();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCoordinate.setLineColor(coordinateLineColor)
                .setCoordinateNameTextSize(coordinateNameTextSize)
                .setCoordinateNameTextColor(coordinateTextColor)
                .draw(canvas);

        mYPoints.setTextColor(coordinateTextColor)
                .setTextSize(coordinateTextSize)
                .draw(canvas);

        mXPoints.setTextColor(coordinateTextColor)
                .setTextSize(coordinateTextSize)
                .setPeakLineColor(peakLineColor)
                .setPeakLineWidth(peakLineWidth)
                .setPointLineColor(pointLineColor)
                .setShapeColor(shapeColor, shapeStartColor, shapeEndColor)
                .setCircleNormalColor(circleNormalColor)
                .setCircleErrorColor(circleErrorColor)
                .setCircleStrokeColor(circleStrokeColor)
                .setCircleRadius(circleRadius)
                .setCircleWidth(circleWidth)
                .setAutoJudgeError(isAutoJudgeError)
                .draw(canvas);

        mLimitShape.setShapeColor(limitShapeColor)
                .draw(canvas);

        mXPoints.drawCircle(mLimitShape, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                int position = mXPoints.getCirclePosition(event.getX(), event.getY());
                if (position == -1) {
                    performClick();
                } else {
                    if (mOnFormClickListener != null) {
                        mOnFormClickListener.onPointClick(x, y, position);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void getTestData() {
        setCoordinateName("(小时)", "(℃)")
                .clearYPoint()
                .addYPoint(0.3f, "10")
                .addYPoint(0.4f, "20")
                .addYPoint(0.5f, "30")
                .addYPoint(0.6f, "40")
                .addYPoint(0.7f, "50")
                .addYPoint(0.8f, "60")
                .addYPoint(0.9f, "70")
                .clearXPoint()
                .addXPoint(0.6f, "1")
                .addXPoint(0.5f, "2")
                .addXPoint(0.7f, "3")
                .addXPoint(0.6f, "4")
                .addXPoint(0.9f, "5")
                .addXPoint(1.0f, "6")
                .addXPoint(0.3f, "7")
                .addXPoint(0.8f, "8")
                .addXPoint(0.9f, "9")
                .addXPoint(0.6f, "10")
                .addXPoint(0.4f, "11")
                .addXPoint(0.5f, "12")
                .addXPoint(0.7f, "13")
                .addXPoint(0.2f, "14")
                .addXPoint(0.4f, "15")
                .addXPoint(0.5f, "16")
                .addXPoint(0.6f, "17")
                .addXPoint(0.7f, "18")
                .addXPoint(0.2f, "19")
                .addXPoint(0.9f, "20")
                .addXPoint(0.6f, "21")
                .addXPoint(0.2f, "22")
                .addXPoint(0.4f, "23")
                .addXPoint(0.6f, "24")
                .setLimitPercent(0.41548f, 0.3f);
    }

    private float dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private float sp2px(float sp) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    /**
     * 设置坐标系说明文字
     *
     * @param xName
     * @param yName
     */
    public FormView setCoordinateName(String xName, String yName) {
        mCoordinate.setCoordinateName(xName, yName);
        return this;
    }

    /**
     * 设置坐标线颜色
     *
     * @param color
     */
    public FormView setCoordinateLineColor(@ColorInt int color) {
        coordinateLineColor = color;
        return this;
    }

    /**
     * 设置坐标文字大小
     *
     * @param textSize
     */
    public FormView setCoordinateTextSize(float textSize) {
        coordinateTextSize = textSize;
        return this;
    }

    /**
     * 设置限位线颜色
     *
     * @param color
     */
    public FormView setLimitShapeColor(@ColorInt int color) {
        limitShapeColor = color;
        return this;
    }

    /**
     * 设置限位线位置
     *
     * @param percent
     */
    public FormView setLimitPercent(@FloatRange(from = 0.0f, to = 1.0f) float percent, boolean isErrorToTop) {
        mLimitShape.setPercent(percent, isErrorToTop);
        return this;
    }

    /**
     * 设置限位范围位置
     *
     * @param percent
     */
    public FormView setLimitPercent(@FloatRange(from = 0.0f, to = 1.0f) float percent, @FloatRange(from = 0.0f, to = 1.0f) float occupyPercent) {
        mLimitShape.setPercent(percent, occupyPercent);
        return this;
    }

    /**
     * 设置波线颜色
     *
     * @param color
     */
    public FormView setPeakLineColor(@ColorInt int color) {
        peakLineColor = color;
        return this;
    }

    /**
     * 设置波线宽度
     *
     * @param width
     */
    public FormView setPeakLineWidth(float width) {
        peakLineWidth = width;
        return this;
    }

    /**
     * 设置图标区域颜色
     *
     * @param color
     */
    public FormView setShapeColor(@ColorInt int color) {
        shapeColor = color;
        return this;
    }

    /**
     * 设置图标区域渐变色
     *
     * @param startColor
     * @param endColor
     */
    public FormView setShapeColor(@ColorInt int startColor, @ColorInt int endColor) {
        shapeColor = Integer.MAX_VALUE;
        shapeStartColor = startColor;
        shapeEndColor = endColor;
        return this;
    }

    /**
     * 设置点到X轴连线颜色
     *
     * @param color
     */
    public FormView setPointLineColor(@ColorInt int color) {
        pointLineColor = color;
        return this;
    }

    /**
     * 设置点正常时填充色
     *
     * @param color
     */
    public FormView setCircleNormalColor(@ColorInt int color) {
        circleNormalColor = color;
        return this;
    }

    /**
     * 设置点异常时填充色
     *
     * @param color
     */
    public FormView setCircleErrorColor(@ColorInt int color) {
        circleErrorColor = color;
        return this;
    }

    /**
     * 设置点描边色
     *
     * @param color
     */
    public FormView setCircleStrokeColor(@ColorInt int color) {
        circleStrokeColor = color;
        return this;
    }

    public FormView clearYPoint() {
        mYPoints.getYPoints().clear();
        return this;
    }

    public FormView addYPoint(@FloatRange(from = 0.0f, to = 1.0f) float percent, String text) {
        mYPoints.addYPoint(percent, text);
        return this;
    }

    public FormView clearXPoint() {
        mXPoints.getXPoints().clear();
        return this;
    }

    public FormView addXPoint(@FloatRange(from = 0.0f, to = 1.0f) float percent, String text) {
        return addXPoint(percent, text, false);
    }

    public FormView addXPoint(@FloatRange(from = 0.0f, to = 1.0f) float percent, String text, boolean isError) {
        mXPoints.addXPoint(percent, text, isError);
        return this;
    }

    public FormView setOnFormClickListener(OnFormClickListener onFormClickListener) {
        mOnFormClickListener = onFormClickListener;
        return this;
    }

    public void show() {
        invalidate();
    }
}
