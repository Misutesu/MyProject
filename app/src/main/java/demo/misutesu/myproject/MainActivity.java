package demo.misutesu.myproject;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import demo.misutesu.myproject.lifecycle.ActivityLifeObserver;
import demo.misutesu.myproject.newwaveview.Wave;
import demo.misutesu.myproject.newwaveview.WaveView;


public class MainActivity extends AppCompatActivity {

    private WaveView waveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLifecycle().addObserver(new ActivityLifeObserver());

        waveView = findViewById(R.id.wave_view);

        waveView
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(2)
                        .setSizePercent(1.0f)
                        .setFromUp(false)
                        .setFromLeft(true))
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(1)
                        .setSizePercent(0.7f)
                        .setFromUp(false)
                        .setFromLeft(true))
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(3)
                        .setSizePercent(0.8f)
                        .setFromUp(false)
                        .setFromLeft(true))
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(4)
                        .setSizePercent(0.9f)
                        .setFromUp(false)
                        .setFromLeft(true))
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(4)
                        .setSizePercent(0.8f)
                        .setFromUp(false)
                        .setFromLeft(false))
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(2)
                        .setSizePercent(0.9f)
                        .setFromUp(false)
                        .setFromLeft(false))
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(6)
                        .setSizePercent(0.7f)
                        .setFromUp(false)
                        .setFromLeft(false))
                .addWave(Wave.create()
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setAlpha(0.3f)
                        .setSpeed(1)
                        .setSizePercent(1.0f)
                        .setFromUp(false)
                        .setFromLeft(false))
                .start();
    }
}
