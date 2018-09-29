package com.misutesu.project.ijkplayer;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

public abstract class PlayerController extends FrameLayout implements IMediaController {

    private static final long AUTO_HIDE_TIME = 10 * 1000;

    private static final int SHOW = 0;
    private static final int HIDE = 1;
    private static final int PROGRESS = 2;

    private View mRootView;

    private Handler mHandler;

    protected MediaController.MediaPlayerControl mPlayer;

    public PlayerController(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View clickView = new View(context);
        addView(clickView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mRootView = LayoutInflater.from(context).inflate(getControllerLayout(), this, false);
        addView(mRootView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mHandler = new Handler(msg -> {
            switch (msg.what) {
                case SHOW:
                    showWithAutoHide();
                    break;
                case HIDE:
                    hide();
                    break;
                case PROGRESS:
                    progress();
                    break;
                default:
            }
            return false;
        });

        clickView.setOnClickListener(v -> {
            if (!isEnabled()) {
                return;
            }
            if (isShowing()) {
                mHandler.sendEmptyMessage(HIDE);
            } else {
                mHandler.sendEmptyMessage(VISIBLE);
            }
        });
    }

    protected abstract int getControllerLayout();

    @Override
    public void start() {

    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        mPlayer = player;
    }

    @Override
    public boolean isShowing() {
        return mRootView.getVisibility() == VISIBLE;
    }

    @Override
    public void show() {
        mHandler.removeMessages(HIDE);
        mRootView.setVisibility(VISIBLE);
    }

    @Override
    public void showWithAutoHide() {
        show();
        mHandler.sendEmptyMessageDelayed(HIDE, AUTO_HIDE_TIME);
    }

    @Override
    public void hide() {
        mHandler.removeMessages(HIDE);
        mRootView.setVisibility(INVISIBLE);
    }

    @Override
    public void progress() {
        mHandler.sendEmptyMessageDelayed(PROGRESS, 100);
    }

    @Override
    public void stop() {
        mRootView.setVisibility(VISIBLE);
    }
}
