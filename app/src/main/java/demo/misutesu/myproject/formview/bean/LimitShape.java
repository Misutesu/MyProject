package demo.misutesu.myproject.formview.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;

public class LimitShape {

    private float startX;
    private float width;
    private float minY, maxY;

    private float percent;
    private float occupyPercent;

    private boolean isErrorToTop = true;

    private Paint paint = new Paint();

    public  LimitShape() {
        paint.setAntiAlias(true);
    }

    public void init(Coordinate coordinate) {
        startX = coordinate.originX;
        width = coordinate.width;
        minY = coordinate.originY;
        maxY = minY - coordinate.height;
    }

    public LimitShape setPercent(@FloatRange(from = 0.0f, to = 1.0f) float percent, boolean isErrorToTop) {
        this.percent = percent;
        occupyPercent = 0;
        this.isErrorToTop = isErrorToTop;
        return this;
    }

    public LimitShape setPercent(@FloatRange(from = 0.0f, to = 1.0f) float percent, @FloatRange(from = 0.0f, to = 1.0f) float occupyPercent) {
        this.percent = percent;
        this.occupyPercent = occupyPercent;
        return this;
    }

    public LimitShape setShapeColor(@ColorInt int color) {
        paint.setColor(color);
        return this;
    }

    public float[] getLimitRange() {
        if (percent == 0) {
            return null;
        } else if (occupyPercent == 0) {
            if (isErrorToTop) {
                return new float[]{0.0f, percent};
            } else {
                return new float[]{percent, 1.0f};
            }
        } else {
            return new float[]{percent, percent + occupyPercent};
        }
    }

    public void draw(Canvas canvas) {
        if (percent > 0) {
            float y = minY - ((minY - maxY) * percent);
            if (occupyPercent == 0) {
                canvas.drawLine(startX, y, startX + width, y, paint);
            } else {
                float toY = y - ((minY - maxY) * occupyPercent);
                canvas.drawRect(startX, y, startX + width, toY, paint);
            }
        }
    }
}
