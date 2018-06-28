package demo.misutesu.myproject.newwaveview;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/6/13 0013 11:41
 * @description :
 */
public class Wave {
    /**
     * Wave颜色
     */
    @ColorInt
    private int color = -1;
    /**
     * Wave渐变色
     */
    @ColorInt
    private int startColor;
    @ColorInt
    private int endColor;
    /**
     * 渐变角度
     */
    private int angle = -1;
    /**
     * WaveView宽度中包含多少个波峰
     */
    private float peakNum = 1f;
    /**
     * 移动速度
     */
    private long speed = 1;
    /**
     * 透明度
     */
    private float alpha = 1f;
    /**
     * Wave高度占整个View多少百分比
     */
    private float sizePercent = 1f;
    /**
     * 是否从左往右移动
     */
    private boolean isFromLeft = true;
    /**
     * 开口是否往下
     */
    private boolean isFromUp = true;
    /**
     * 是否自动移动
     */
    private boolean canMove = true;

    private boolean isInitPoint;
    private int moveLen;
    private int allMoveLen;

    private List<Point> points;
    private Path path;
    private Matrix matrix;

    public static Wave create() {
        return new Wave();
    }

    private Wave() {
    }

    public Wave setColor(@ColorInt int color) {
        this.color = color;
        return this;
    }

    public Wave setColor(@ColorInt int startColor, @ColorInt int endColor, int angle) {
        if (angle < 0 || angle > 360) {
            throw new RuntimeException("angle must between 0 and 360");
        }
        this.startColor = startColor;
        this.endColor = endColor;
        this.angle = angle;
        return this;
    }

    public Wave setPeakNum(float peakNum) {
        this.peakNum = peakNum;
        return this;
    }

    public Wave setSpeed(long speed) {
        this.speed = speed;
        return this;
    }

    public Wave setAlpha(float alpha) {
        if (alpha < 0f || alpha > 1f) {
            throw new RuntimeException("alpha must between 0.0f and 1.0f");
        }
        this.alpha = alpha;
        return this;
    }

    public Wave setSizePercent(float sizePercent) {
        if (sizePercent < 0f || sizePercent > 1f) {
            throw new RuntimeException("sizePercent must between 0.0f and 1.0f");
        }
        this.sizePercent = sizePercent;
        return this;
    }

    public Wave setFromLeft(boolean fromLeft) {
        isFromLeft = fromLeft;
        return this;
    }

    public Wave setFromUp(boolean fromUp) {
        isFromUp = fromUp;
        return this;
    }

    public Wave setCanMove(boolean canMove) {
        this.canMove = canMove;
        return this;
    }

    public int getColor() {
        return color;
    }

    public int getStartColor() {
        return startColor;
    }

    public int getEndColor() {
        return endColor;
    }

    public int getAngle() {
        return angle;
    }

    public float getPeakNum() {
        return peakNum;
    }

    public long getSpeed() {
        return speed;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getSizePercent() {
        return sizePercent;
    }

    public boolean isFromLeft() {
        return isFromLeft;
    }

    public boolean isFromUp() {
        return isFromUp;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public int getMoveLen() {
        return moveLen;
    }

    public void addSpeed(Paint paint) {
        moveLen += speed;
        if (matrix != null) {
            matrix.reset();
            matrix.setTranslate(moveLen, 0);
            paint.getShader().setLocalMatrix(matrix);
        }
    }

    public void reset() {
        moveLen = moveLen - allMoveLen;
    }

    public int getAllMoveLen() {
        return allMoveLen;
    }

    public Path init(int width, int height, Paint paint) {
        if (!isInitPoint) {
            //initColor
            paint.reset();
            if (angle == -1) {
                paint.setColor(ColorUtils.setAlphaComponent(color, (int) (alpha * 255)));
            } else {
                int x1, y1, x2, y2;
                if (angle > 315 || angle < 45) {
                    x1 = (angle > 315 ? angle - 315 : 90 + angle) * width / 180;
                    y1 = 0;
                    x2 = (angle > 315 ? 180 - (angle - 315) : 90 - angle) * width / 180;
                    y2 = height;
                } else if (angle < 135) {
                    x1 = width;
                    y1 = (angle - 90) * height / 180;
                    x2 = 0;
                    y2 = (180 - (angle - 90)) * height / 180;
                } else if (angle < 225) {
                    x1 = (180 - (angle - 135)) * width / 180;
                    y1 = height;
                    x2 = (135 - angle) * width / 180;
                    y2 = 0;
                } else {
                    x1 = 0;
                    y1 = (180 - (angle - 225)) * height / 180;
                    x2 = width;
                    y2 = (angle - 225) * height / 180;
                }
                paint.setShader(new LinearGradient(x1, y1, x2, y2, ColorUtils.setAlphaComponent(startColor, (int) (alpha * 255)), ColorUtils.setAlphaComponent(endColor, (int) (alpha * 255)), Shader.TileMode.CLAMP));
                matrix = new Matrix();
                matrix.setTranslate(0, 0);
                paint.getShader().setLocalMatrix(matrix);
            }

            //initPath
            allMoveLen = width;

            points = new ArrayList<>();
            for (int i = 0; i < (((int) Math.ceil(2.0f * peakNum)) * 4) + 1; i++) {
                points.add(new Point());
            }

            int waveHeight = (int) (height * sizePercent) / 2;

            path = new Path();

            for (int i = 0; i < points.size(); i++) {
                /*
                 * quadTo
                 */
                Point point = points.get(i);
                //initX
                int x = 2 * width / (points.size() - 1) * i;
                point.setX(isFromLeft ? x : -x);

                //initY
                int y = 0;
                switch (i % 4) {
                    case 0:
                    case 2:
                        y = 0;
                        break;
                    case 1:
                        y = isFromUp ? waveHeight : -waveHeight;
                        break;
                    case 3:
                        y = isFromUp ? -waveHeight : waveHeight;
                        break;
                }
                point.setY(y);

                if (i == 0) {
                    path.moveTo(point.getX(), point.getY());
                } else if (i % 2 == 0) {
                    Point lastPoint = points.get(i - 1);
                    path.quadTo(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY());
                }
            }
            path.lineTo(isFromLeft ? width * 2 : -width * 2, isFromUp ? -(height - waveHeight) : waveHeight);
            path.lineTo(0, isFromUp ? -(height - waveHeight) : waveHeight);
            path.lineTo(0, 0);
            path.close();

            isInitPoint = true;
        }

        return path;
    }
}
