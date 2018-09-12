package demo.misutesu.myproject.formview.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;

public class YPoints {

    private final float TEXT_MARGIN = 20;

    private List<YPoint> yPoints = new ArrayList<>();

    private Paint paint = new Paint();

    private float rightX;
    private float minY, maxY;

    public  YPoints() {
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
    }

    public List<YPoint> getYPoints() {
        return yPoints;
    }

    public void addYPoint(float percent, String text) {
        yPoints.add(new YPoint(percent, text));
    }

    public void init(Coordinate coordinate) {
        rightX = coordinate.originX;
        minY = coordinate.originY;
        maxY = minY - coordinate.height;
    }

    public YPoints setTextColor(@ColorInt int color) {
        paint.setColor(color);
        return this;
    }

    public YPoints setTextSize(float textSize) {
        paint.setTextSize(textSize);
        return this;
    }

    public void draw(Canvas canvas) {
        for (YPoint yPoint : yPoints) {
            float textWidth = paint.measureText(yPoint.text);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float textHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;

            float x = rightX - TEXT_MARGIN - textWidth;
            float y = (minY - ((minY - maxY) * yPoint.percent)) - (textHeight / 2);
            canvas.drawText(yPoint.text, x, y, paint);
        }
    }

    private class YPoint {

        private float percent;
        private String text;

        YPoint(float percent, String text) {
            this.percent = percent;
            this.text = text;
        }

        public float getPercent() {
            return percent;
        }

        public String getText() {
            return text;
        }
    }
}
