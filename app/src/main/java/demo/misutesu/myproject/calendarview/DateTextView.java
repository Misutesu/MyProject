package demo.misutesu.myproject.calendarview;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import demo.misutesu.myproject.R;

public class DateTextView extends FrameLayout {

    private boolean isEnable = true;
    private boolean isSelect;
    private boolean isContain;

    @ColorInt
    private int enableColor;
    @ColorInt
    private int unableColor;

    private AppCompatTextView tv;
    private AppCompatImageView iv;

    public DateTextView(Context context) {
        super(context);
        init(context);
    }

    public DateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        tv = new AppCompatTextView(context);
        tv.setGravity(Gravity.CENTER);

        iv = new AppCompatImageView(context);

        addView(iv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(tv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setTextSize(float size) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setText(CharSequence text) {
        tv.setText(text);
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
        tv.setTextColor(isEnable ? enableColor : unableColor);
        if (!isEnable) {
            iv.setImageResource(0);
        }
    }

    public void setSelect(boolean select) {
        if (isEnable) {
            isSelect = select;
            iv.setImageResource(isSelect ? R.drawable.bg_calendar_selected : 0);
        } else {
            iv.setImageResource(0);
        }
    }

    public void setContain(boolean contain) {
        if (isEnable) {
            isContain = contain;
            iv.setImageResource(isContain ? R.drawable.bg_calendar_cantained : 0);
        } else {
            iv.setImageResource(0);
        }
    }

    public void setColor(int enableColor, int unableColor) {
        this.enableColor = enableColor;
        this.unableColor = unableColor;
    }
}
