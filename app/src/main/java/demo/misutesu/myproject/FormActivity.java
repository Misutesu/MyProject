package demo.misutesu.myproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import demo.misutesu.myproject.calendarview.CalendarView;

public class FormActivity extends AppCompatActivity {

//    private FormView formView;

    private CalendarView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        calendarView = findViewById(R.id.calendar_view);

        calendarView.setCanSelectAfterNow(false)
                .showWithSelect(2018, 8, 15, 2018, 9, 10);

//        formView = findViewById(R.id.form_view);
//
//        formView.setOnFormClickListener(new FormView.OnFormClickListener() {
//            @Override
//            public void onPointClick(float x, float y, int position) {
//                Log.d("TAG", "position : " + position);
//            }
//        });
//
//        formView.setCoordinateName("(小时)", "(℃)")
//                .clearYPoint()
//                .addYPoint(0.3f, "10")
//                .addYPoint(0.4f, "20")
//                .addYPoint(0.5f, "30")
//                .addYPoint(0.6f, "40")
//                .addYPoint(0.7f, "50")
//                .addYPoint(0.8f, "60")
//                .addYPoint(0.9f, "70")
//                .clearXPoint()
//                .addXPoint(0.6f, "1")
//                .addXPoint(0.5f, "2")
//                .addXPoint(0.7f, "3")
//                .addXPoint(0.6f, "4")
//                .addXPoint(0.9f, "5")
//                .addXPoint(1.0f, "6")
//                .addXPoint(0.3f, "7")
//                .addXPoint(0.8f, "8")
//                .addXPoint(0.9f, "9")
//                .addXPoint(0.6f, "10")
//                .addXPoint(0.4f, "11")
//                .addXPoint(0.5f, "12")
//                .addXPoint(0.7f, "13")
//                .addXPoint(0.2f, "14")
//                .addXPoint(0.4f, "15")
//                .addXPoint(0.5f, "16")
//                .addXPoint(0.6f, "17")
//                .addXPoint(0.7f, "18")
//                .addXPoint(0.2f, "19")
//                .addXPoint(0.9f, "20")
//                .addXPoint(0.6f, "21")
//                .addXPoint(0.2f, "22")
//                .addXPoint(0.4f, "23")
//                .addXPoint(0.6f, "24")
//                .setLimitPercent(0.41548f, 0.3f)
//                .setOnFormClickListener((x, y, position) -> Log.d("TAG", "position : " + position))
//                .show();
    }
}
