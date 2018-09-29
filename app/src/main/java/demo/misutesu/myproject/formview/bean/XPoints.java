package demo.misutesu.myproject.formview.bean;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class XPoints {

    private final float TEXT_MARGIN = 12;
    private final float CLICK_MIN_RADIUS = 24;

    private List<XPoint> xPoints = new ArrayList<>();

    private float startY;
    private float height;
    private float minX, maxX;

    private float circleRadius;

    private boolean isAutoJudgeError;

    @ColorInt
    private int circleNormalColor;
    @ColorInt
    private int circleErrorColor;
    @ColorInt
    private int circleStrokeColor;

    private Path path = new Path();

    private Paint peakLinePaint = new Paint();
    private Paint pointLinePaint = new Paint();
    private Paint shapePaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint circlePaint = new Paint();

    public XPoints() {
        peakLinePaint.setAntiAlias(true);

        pointLinePaint.setAntiAlias(true);

        shapePaint.setAntiAlias(true);

        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        circlePaint.setAntiAlias(true);
    }

    public void init(Coordinate coordinate) {
        startY = coordinate.originY;
        height = coordinate.height;
        minX = coordinate.originX;
        maxX = minX + coordinate.width;
    }

    public List<XPoint> getXPoints() {
        return xPoints;
    }

    public void addXPoint(float percent, String text, boolean isError) {
        xPoints.add(new XPoint(percent, text, isError));
    }

    public XPoints setPeakLineColor(@ColorInt int color) {
        peakLinePaint.setColor(color);
        return this;
    }

    public XPoints setPeakLineWidth(float size) {
        peakLinePaint.setStrokeWidth(size);
        return this;
    }

    public XPoints setPointLineColor(@ColorInt int color) {
        pointLinePaint.setColor(color);
        return this;
    }

    public XPoints setTextColor(@ColorInt int color) {
        textPaint.setColor(color);
        return this;
    }

    public XPoints setTextSize(float textSize) {
        textPaint.setTextSize(textSize);
        return this;
    }

    public XPoints setShapeColor(@ColorInt int color, @ColorInt int startColor, @ColorInt int endColor) {
        if (color == Integer.MAX_VALUE) {
            shapePaint.setColor(Color.WHITE);
            float midX = (maxX + minX) / 2;
            shapePaint.setShader(new LinearGradient(midX, startY, midX, startY - height, startColor, endColor, Shader.TileMode.CLAMP));
        } else {
            shapePaint.setShader(null);
            shapePaint.setColor(color);
        }
        return this;
    }

    public XPoints setCircleNormalColor(@ColorInt int color) {
        circleNormalColor = color;
        return this;
    }

    public XPoints setCircleErrorColor(@ColorInt int color) {
        circleErrorColor = color;
        return this;
    }

    public XPoints setCircleStrokeColor(@ColorInt int color) {
        circleStrokeColor = color;
        return this;
    }

    public XPoints setCircleRadius(float radius) {
        circleRadius = radius;
        return this;
    }

    public XPoints setCircleWidth(float width) {
        circlePaint.setStrokeWidth(width);
        return this;
    }

    public XPoints setAutoJudgeError(boolean isEnable) {
        isAutoJudgeError = isEnable;
        return this;
    }

    public void draw(Canvas canvas) {
        //Draw Shader
        path.reset();
        path.moveTo(minX, startY);
        for (int position = 0; position < xPoints.size(); position++) {
            XPoint xPoint = xPoints.get(position);

            float endX = minX + ((maxX - minX) * ((float) position / xPoints.size()));
            float endY = this.startY - (xPoint.percent * height);

            path.lineTo(endX, endY);
            if (position == xPoints.size() - 1) {
                path.lineTo(endX, startY);
            }
        }
        path.lineTo(minX, startY);
        path.close();
        canvas.drawPath(path, shapePaint);

        //Draw Line & Text
        for (int position = 0; position < xPoints.size(); position++) {
            XPoint xPoint = xPoints.get(position);

            float endX = minX + ((maxX - minX) * ((float) position / xPoints.size()));
            float endY = this.startY - (xPoint.percent * height);

            float textWidth = textPaint.measureText(xPoint.text);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float textHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;

            float x = endX - (textWidth / 2);
            float y = startY + textHeight + TEXT_MARGIN;

            canvas.drawText(xPoint.text, x, y, textPaint);

            if (position != 0) {
                int lastPosition = position - 1;
                XPoint lastXPoint = xPoints.get(lastPosition);

                float startX = minX + ((maxX - minX) * ((float) lastPosition / xPoints.size()));
                float startY = this.startY - (lastXPoint.percent * height);

                canvas.drawLine(startX, startY, endX, endY, peakLinePaint);

                canvas.drawLine(endX, this.startY, endX, endY, pointLinePaint);
            }
        }
    }

    public void drawCircle(LimitShape limitShape, Canvas canvas) {
        float[] errorRange = limitShape.getLimitRange();

        //Draw Circle
        for (int position = 0; position < xPoints.size(); position++) {
            XPoint xPoint = xPoints.get(position);

            float endX = minX + ((maxX - minX) * ((float) position / xPoints.size()));
            float endY = this.startY - (xPoint.percent * height);

            circlePaint.setStyle(Paint.Style.FILL);
            int circleColor;
            if (isAutoJudgeError) {
                if (errorRange == null || (xPoint.percent >= errorRange[0] && xPoint.percent <= errorRange[1])) {
                    circleColor = circleNormalColor;
                } else {
                    circleColor = circleErrorColor;
                }
            } else {
                circleColor = xPoint.isError ? circleErrorColor : circleNormalColor;
            }
            circlePaint.setColor(circleColor);
            canvas.drawCircle(endX, endY, circleRadius, circlePaint);

            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setColor(circleStrokeColor);

            canvas.drawCircle(endX, endY, circleRadius, circlePaint);
        }
    }

    public int getCirclePosition(float x, float y) {
        for (int position = 0; position < xPoints.size(); position++) {
            XPoint xPoint = xPoints.get(position);

            float centerX = minX + ((maxX - minX) * ((float) position / xPoints.size()));
            float centerY = startY - (xPoint.percent * height);

            float radius = circleRadius + circlePaint.getStrokeWidth();
            radius = radius < CLICK_MIN_RADIUS ? CLICK_MIN_RADIUS : radius;

            if (Math.abs(x - centerX) <= radius && Math.abs(y - centerY) <= radius) {
                return position;
            }
        }
        return -1;
    }

    private class XPoint {
        private float percent;
        private String text;
        private boolean isError = false;

        XPoint(float percent, String text, boolean isError) {
            this.percent = percent;
            this.text = text;
            this.isError = isError;
        }
    }
}
