package demo.misutesu.myproject.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import demo.misutesu.myproject.R;
import demo.misutesu.myproject.calendarview.utils.DateUtils;

public class CalendarView extends ConstraintLayout {

    public interface OnCalendarClickListener {
        void onSureClick(Date startDate, Date endDate);
    }

    private final String YEAR = "%d年";
    private final String MONTH = "%d月";
    private final String[] WEEK_NAME = {"日", "一", "二", "三", "四", "五", "六"};

    private final float DEFAULT_TEXT_SIZE = 16;
    private final int LINE_NUM = 7;
    private final int COUNT = 7;

    /**
     * xml config start
     */
    private float dayTextSize;
    @ColorInt
    private int dayEnableColor = Color.WHITE;
    @ColorInt
    private int dayUnableColor = Color.TRANSPARENT;

    private boolean canSelectAfterNow;

    private boolean isEnableXMLPreview;
    /**
     * xml config end
     */

    private int leftYear, leftMonth;
    private int rightYear, rightMonth;

    private Date[] limitDates = new Date[2];
    private Date[] selectDates;

    private TextView tvLeftYear, tvLeftMonth, tvRightYear, tvRightMonth;
    private LinearLayout llLeft, llRight;

    private List<DateTextView> leftViews = new ArrayList<>();
    private List<DateTextView> rightViews = new ArrayList<>();

    private OnCalendarClickListener onCalendarClickListener;

    public CalendarView(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
            dayTextSize = array.getDimension(R.styleable.CalendarView_day_text_size, sp2px(DEFAULT_TEXT_SIZE));
            dayUnableColor = array.getColor(R.styleable.CalendarView_text_color_unable, dayUnableColor);
            dayEnableColor = array.getColor(R.styleable.CalendarView_text_color_unable, dayEnableColor);
            canSelectAfterNow = array.getBoolean(R.styleable.CalendarView_text_color_unable, canSelectAfterNow);
            isEnableXMLPreview = array.getBoolean(R.styleable.CalendarView_is_enable_xml_preview_temp, isEnableXMLPreview);
            array.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true);

        tvLeftYear = findViewById(R.id.tv_left_year);
        tvLeftMonth = findViewById(R.id.tv_left_month);
        tvRightYear = findViewById(R.id.tv_right_year);
        tvRightMonth = findViewById(R.id.tv_right_month);
        llLeft = findViewById(R.id.ll_left);
        llRight = findViewById(R.id.ll_right);

        findViewById(R.id.btn_left_year_last).setOnClickListener(onDateChangeListener);
        findViewById(R.id.btn_left_year_next).setOnClickListener(onDateChangeListener);
        findViewById(R.id.btn_left_month_last).setOnClickListener(onDateChangeListener);
        findViewById(R.id.btn_left_month_next).setOnClickListener(onDateChangeListener);
        findViewById(R.id.btn_right_year_last).setOnClickListener(onDateChangeListener);
        findViewById(R.id.btn_right_year_next).setOnClickListener(onDateChangeListener);
        findViewById(R.id.btn_right_month_last).setOnClickListener(onDateChangeListener);
        findViewById(R.id.btn_right_month_next).setOnClickListener(onDateChangeListener);

        initGird(llLeft, leftViews);
        initGird(llRight, rightViews);

        if (canSelectAfterNow) {
            limitDates[1] = DateUtils.getNowDate();
        }

        if (isEnableXMLPreview) {
            show();
        }
    }

    private void initGird(LinearLayout viewParent, List<DateTextView> list) {
        for (int i = 0; i < LINE_NUM; i++) {
            LinearLayout ll = new LinearLayout(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp.weight = 1;
            viewParent.addView(ll, lp);

            for (int n = 0; n < COUNT; n++) {
                DateTextView tv = new DateTextView(getContext());
                tv.setTextSize(dayTextSize);
                tv.setColor(dayEnableColor, dayUnableColor);
                tv.setOnClickListener(onDayClickListener);

                LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                lpTv.weight = 1;
                ll.addView(tv, lpTv);

                if (i == 0) {
                    tv.setText(WEEK_NAME[n]);
                } else {
                    list.add(tv);
                }
            }
        }
    }

    private void show(int year, int month, List<DateTextView> list) {
        int[] days = DateUtils.getDayOfMonthFormat(year, month);
        for (int i = 0; i < days.length; i++) {
            DateTextView tv = list.get(i);
            Date date = DateUtils.getDate(year, month, days[i]);
            tv.setTag(date);
            tv.setText(String.valueOf(days[i]));
            tv.setEnable(days[i] != -1);
            if (date != null) {
                if ((limitDates[0] != null && date.getTime() < limitDates[0].getTime())
                        || (limitDates[1] != null && date.getTime() > limitDates[1].getTime())) {
                    tv.setEnable(false);
                    tv.setTag(null);
                }
            }
            if (date != null) {
                if (selectDates == null) {
                    tv.setSelect(false);
                    tv.setContain(false);
                } else {
                    if (date.getTime() == selectDates[0].getTime()) {
                        tv.setSelect(true);
                    } else if (selectDates[1] == null) {
                        tv.setSelect(false);
                        tv.setContain(false);
                    } else if (date.getTime() == selectDates[1].getTime()) {
                        tv.setSelect(true);
                    } else if ((date.getTime() > selectDates[0].getTime() && date.getTime() < selectDates[1].getTime())
                            || (date.getTime() > selectDates[1].getTime() && date.getTime() < selectDates[0].getTime())) {
                        tv.setContain(true);
                    } else {
                        tv.setSelect(false);
                        tv.setContain(false);
                    }
                }
            }
        }
    }

    public CalendarView showWithSelect(int selectLeftYear, int selectLeftMonth, int selectLeftDay
            , int selectRightYear, int selectRightMonth, int selectRightDay) {
        if (selectDates == null) {
            selectDates = new Date[2];
        }
        selectDates[0] = DateUtils.getDate(selectLeftYear, selectLeftMonth, selectLeftDay);
        selectDates[1] = DateUtils.getDate(selectRightYear, selectRightMonth, selectRightDay);
        return show(selectLeftYear, selectLeftMonth, selectRightYear, selectRightMonth);
    }

    public CalendarView show(int leftYear, int leftMonth, int rightYear, int rightMonth) {
        this.leftYear = leftYear;
        this.leftMonth = leftMonth;
        this.rightYear = rightYear;
        this.rightMonth = rightMonth;
        show();
        return this;
    }

    public CalendarView show() {
        tvLeftYear.setText(String.format(Locale.getDefault(), YEAR, leftYear));
        tvLeftMonth.setText(String.format(Locale.getDefault(), MONTH, leftMonth));
        tvRightYear.setText(String.format(Locale.getDefault(), YEAR, rightYear));
        tvRightMonth.setText(String.format(Locale.getDefault(), MONTH, rightMonth));

        show(leftYear, leftMonth, leftViews);
        show(rightYear, rightMonth, rightViews);
        return this;
    }

    public CalendarView setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        this.onCalendarClickListener = onCalendarClickListener;
        return this;
    }

    public CalendarView setStartLimitDate(int year, int month, int day) {
        limitDates[0] = DateUtils.getDate(year, month, day);
        return this;
    }

    public CalendarView setEndLimitDate(int year, int month, int day) {
        limitDates[1] = DateUtils.getDate(year, month, day);
        return this;
    }

    public CalendarView clearLimit() {
        limitDates[0] = null;
        limitDates[1] = null;
        return this;
    }

    public CalendarView setCanSelectAfterNow(boolean isCan) {
        canSelectAfterNow = isCan;
        limitDates[1] = isCan ? null : DateUtils.getNowDate();
        return this;
    }

    private OnClickListener onDayClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                Date date = (Date) v.getTag();
                if (selectDates == null || selectDates[1] != null) {
                    selectDates = new Date[2];
                    selectDates[0] = date;
                } else {
                    selectDates[1] = date;
                    if (selectDates[0].getTime() < selectDates[1].getTime()) {
                        Date temp = selectDates[0];
                        selectDates[0] = date;
                        selectDates[1] = temp;
                    }
                }
                show();
            }
        }
    };

    private OnClickListener onDateChangeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int limitStartYear = DateUtils.getYear(limitDates[0]);
            int limitStartMonth = DateUtils.getMonth(limitDates[0]);
            int limitEndYear = DateUtils.getYear(limitDates[1]);
            int limitEndMonth = DateUtils.getMonth(limitDates[1]);
            int id = v.getId();
            switch (id) {
                case R.id.btn_left_year_last:
                    if (leftYear == limitStartYear || (leftYear - 1 == limitStartYear && leftMonth < limitStartMonth)) {
                        leftYear = limitStartYear;
                        leftMonth = limitStartMonth;
                    } else {
                        leftYear--;
                    }
                    show();
                    break;
                case R.id.btn_left_year_next:
                    if ((leftYear + 1 == rightYear && leftMonth >= rightMonth) || (leftYear == rightYear)) {
                        leftYear = rightYear;
                        leftMonth = rightMonth - 1;
                    } else {
                        leftYear++;
                    }
                    show();
                    break;
                case R.id.btn_left_month_last:
                    if (leftYear == limitStartYear && leftMonth == limitStartMonth) {
                        return;
                    }
                    if (leftMonth == 1) {
                        leftYear--;
                        leftMonth = 12;
                    } else {
                        leftMonth--;
                    }
                    show();
                    break;
                case R.id.btn_left_month_next:
                    if (leftYear == rightYear && leftMonth + 1 == rightMonth) {
                        return;
                    }
                    if (leftMonth == 12) {
                        leftYear++;
                        leftMonth = 1;
                    } else {
                        leftMonth++;
                    }
                    show();
                    break;
                case R.id.btn_right_year_last:
                    if (leftYear == limitStartYear || (leftYear - 1 == limitStartYear && leftMonth < limitStartMonth)) {
                        leftYear = limitStartYear;
                        leftMonth = limitStartMonth;
                        if (leftMonth == 12) {
                            rightYear = leftYear + 1;
                            rightMonth = 1;
                        } else {
                            rightYear = leftYear;
                            rightMonth = leftMonth + 1;
                        }
                    } else {
                        rightYear--;
                        if ((leftYear > rightYear) || (leftYear == rightYear && leftMonth >= rightMonth)) {
                            leftYear = rightYear;
                            leftMonth = rightMonth - 1;
                        }
                    }
                    show();
                    break;
                case R.id.btn_right_year_next:
                    if ((rightYear == limitEndYear) || (rightYear + 1 == limitEndYear && rightMonth > limitEndMonth)) {
                        rightYear = limitEndYear;
                        rightMonth = limitEndMonth;
                    } else {
                        rightYear++;
                    }
                    show();
                    break;
                case R.id.btn_right_month_last:
                    if (leftYear == limitStartYear && leftMonth == limitStartMonth) {
                        return;
                    }
                    if (rightMonth == 1) {
                        rightYear--;
                        rightMonth = 12;
                    } else {
                        rightMonth--;
                    }
                    if (leftYear == rightYear && leftMonth == rightMonth) {
                        if (leftMonth == 1) {
                            leftYear--;
                            leftMonth = 12;
                        } else {
                            leftMonth--;
                        }
                    }
                    show();
                    break;
                case R.id.btn_right_month_next:
                    if (rightYear == limitEndYear && rightMonth == limitEndMonth) {
                        return;
                    }
                    if (rightMonth == 12) {
                        rightYear++;
                        rightMonth = 1;
                    } else {
                        rightMonth++;
                    }
                    show();
                    break;
                case R.id.btn_sure:
                    if (onCalendarClickListener != null) {
                        if (selectDates == null) {
                            onCalendarClickListener.onSureClick(null, null);
                        } else {
                            onCalendarClickListener.onSureClick(selectDates[0], selectDates[1]);
                        }
                    }
                    break;
            }
        }
    };

    private float dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private float sp2px(float sp) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }
}
