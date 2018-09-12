package demo.misutesu.myproject.formview.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.text.TextUtils;

public class Coordinate {

    private final float TEXT_MARGIN = 12;

    private final float OCCUPY_PERCENT = 0.8f;
    private final float ARROW_PERCENT = 1f / 60;

    private Paint linePaint = new Paint();
    private Paint textPaint = new Paint();

    private String XName, YName;

    protected float width, height;
    protected float originX, originY;

    public Coordinate() {
        linePaint.setAntiAlias(true);

        textPaint.setAntiAlias(true);
    }

    public void init(int width, int height) {
        this.width = OCCUPY_PERCENT * width;
        this.height = OCCUPY_PERCENT * height;

        originX = ((1 - OCCUPY_PERCENT) / 2) * width;
        originY = (((1 - OCCUPY_PERCENT) / 2) + OCCUPY_PERCENT) * height;
    }

    public Coordinate setLineColor(@ColorInt int lineColor) {
        linePaint.setColor(lineColor);
        return this;
    }

    public Coordinate setCoordinateNameTextSize(float textSize) {
        textPaint.setTextSize(textSize);
        return this;
    }

    public Coordinate setCoordinateNameTextColor(@ColorInt int color) {
        textPaint.setColor(color);
        return this;
    }

    public Coordinate setCoordinateName(String xName, String yName) {
        XName = xName;
        YName = yName;
        return this;
    }

    public void draw(Canvas canvas) {
        float arrowSize = Math.min(width, height) * ARROW_PERCENT;

        float endXX = originX;
        float endXY = originY - height;
        canvas.drawLine(originX, originY, endXX, endXY, linePaint);
        canvas.drawLine(endXX, endXY, endXX - arrowSize, endXY + arrowSize, linePaint);
        canvas.drawLine(endXX, endXY, endXX + arrowSize, endXY + arrowSize, linePaint);

        float endYX = originX + width;
        float endYY = originY;
        canvas.drawLine(originX, originY, endYX, endYY, linePaint);
        canvas.drawLine(endYX, endYY, endYX - arrowSize, endYY - arrowSize, linePaint);
        canvas.drawLine(endYX, endYY, endYX - arrowSize, endYY + arrowSize, linePaint);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;

        if (!TextUtils.isEmpty(XName)) {
            float x = endYX + TEXT_MARGIN;
            float y = originY + textHeight + TEXT_MARGIN;
            canvas.drawText(XName, x, y, textPaint);
        }
        if (!TextUtils.isEmpty(YName)) {
            float textWidth = textPaint.measureText(YName);
            float x = endXX - textWidth - TEXT_MARGIN;
            float y = endXY - TEXT_MARGIN;
            canvas.drawText(YName, x, y, textPaint);
        }
    }
}
