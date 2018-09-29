/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.misutesu.project.ijkplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.misutesu.project.ijkplayer.render.IRenderView;
import com.misutesu.project.ijkplayer.render.SurfaceRenderView;
import com.misutesu.project.ijkplayer.render.TextureRenderView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl {
    private String TAG = this.getClass().getSimpleName();
    /**
     * settable by the client  播放地址通过客户端可设置
     */
    private Uri mUri;
    /**
     * 播放器的一些基本配置
     */
    private Map<String, String> mHeaders;

    private int mCurrentState = PlayStateParams.STATE_IDLE;
    private int mTargetState = PlayStateParams.STATE_IDLE;

    /**
     * All the stuff we need for playing and showing a video 所有的东西我们需要播放和显示视频
     */
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    /**
     * 视频宽度
     */
    private int mVideoWidth;
    /**
     * 视频高度
     */
    private int mVideoHeight;
    /**
     * 窗口宽度
     */
    private int mSurfaceWidth;
    /**
     * 窗口高度
     */
    private int mSurfaceHeight;
    /**
     * 视频旋转角度
     */
    private int mVideoRotationDegree;
    /**
     * 媒体播放器
     */
    private IMediaPlayer mMediaPlayer;
    /**
     * 媒体控制器
     */
    private IMediaController mMediaController;
    /**
     * 播放完成监听
     */
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    /**
     * 播放准备监听
     */
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    /**
     * 播放缓冲监听
     */
    private int mCurrentBufferPercentage;
    /**
     * 播放错误监听
     */
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    /**
     * 播放其他信息监听
     */
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    /**
     * 跳转完毕监听
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener;
    /**
     * recording the seek position while preparing 记录寻求位置而做准备
     */
    private long mSeekWhenPrepared;
    /**
     * 是否可以暂停
     */
    private boolean mCanPause = true;

    private Context mAppContext;
    private IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;
    /**
     * 使用Android播放器
     */
    private boolean usingAndroidPlayer = false;

    public IjkVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    /**
     * 初始化视频view
     */
    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();

        initRenders();

        mVideoWidth = 0;
        mVideoHeight = 0;

//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        requestFocus();

        mCurrentState = PlayStateParams.STATE_IDLE;
        mTargetState = PlayStateParams.STATE_IDLE;
    }

    /**
     * 设置渲染器
     */
    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            removeView(renderUIView);
        }

        if (renderView == null) {
            return;
        }

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        }
        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
        }

        View renderUIView = mRenderView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    /**
     * 设置旋转角度
     */
    public void setPlayerRotation(int rotation) {
        mVideoRotationDegree = rotation;
        if (mRenderView != null) {
            mRenderView.setVideoRotation(mVideoRotationDegree);
        }
    }

    /**
     * 旋转渲染器
     */
    public void setRender(int render) {
        switch (render) {
            case RENDER_TEXTURE_VIEW: {
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mCurrentAspectRatio);
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);
                break;
            }
            default:
                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", render));
                break;
        }
    }

    public void setVideoUrl(String url) {
        Map<String, String> headers = new HashMap<>();
//        headers.put("token", "header");
        setVideoUrl(url, headers);
    }

    public void setVideoUrl(String url, Map<String, String> headers) {
        setVideoURI(Uri.parse(url), headers);
    }

    public void setVideoUrlUseCache(String url) {
        setVideoURI(Uri.parse("async:" + url), null);
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        initRenders();
        openVideo();
        requestLayout();
        invalidate();
    }

    /**
     * 停止视频
     */
    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = PlayStateParams.STATE_IDLE;
            mTargetState = PlayStateParams.STATE_IDLE;
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    /**
     * 打开视频
     */
    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            return;
        }
        release(false);

        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        try {
            if (usingAndroidPlayer) {
                mMediaPlayer = IjkPlayerUtils.createAndroidPlayer();
            } else {
                mMediaPlayer = IjkPlayerUtils.createIjkPlayer(mAppContext);
            }

            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (TextUtils.isEmpty(scheme) || "file".equalsIgnoreCase(scheme))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

            // REMOVED: mPendingSubtitleTracks

            // we don't set the target state here either, but preserve the target state that was there before.我们这里不设置目标状态,但保护的目标状态
            mCurrentState = PlayStateParams.STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = PlayStateParams.STATE_ERROR;
            mTargetState = PlayStateParams.STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = PlayStateParams.STATE_ERROR;
            mTargetState = PlayStateParams.STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    public void setMediaController(IMediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            (mp, width, height, sarNum, sarDen) -> {
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
                mVideoSarNum = mp.getVideoSarNum();
                mVideoSarDen = mp.getVideoSarDen();
                if (mVideoWidth != 0 && mVideoHeight != 0) {
                    if (mRenderView != null) {
                        mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                        mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    }
                    // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                    requestLayout();
                }
            };

    IMediaPlayer.OnPreparedListener mPreparedListener = mp -> {
        mCurrentState = PlayStateParams.STATE_PREPARED;

        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(mMediaPlayer);
        }
        if (mMediaController != null) {
            mMediaController.setEnabled(true);
            mMediaController.progress();
        }
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(mMediaPlayer, mCurrentState, 0);
        }
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();

        long seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
        if (seekToPosition != 0) {
            seekTo((int) seekToPosition);
        }
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            if (mRenderView != null) {
                mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    if (mTargetState == PlayStateParams.STATE_PLAYING) {
                        start();
                        if (mMediaController != null) {
                            mMediaController.showWithAutoHide();
                        }
                    } else if (!isPlaying() &&
                            (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null) {
                            mMediaController.showWithAutoHide();
                        }
                    }
                }
            }
        } else {
            if (mTargetState == PlayStateParams.STATE_PLAYING) {
                start();
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener = mp -> {
        mCurrentState = PlayStateParams.STATE_COMPLETED;
        mTargetState = PlayStateParams.STATE_COMPLETED;
        if (mMediaController != null) {
            mMediaController.hide();
        }
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mMediaPlayer);
        }
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(mMediaPlayer, PlayStateParams.STATE_COMPLETED, 0);
        }
    };

    private IMediaPlayer.OnInfoListener mInfoListener =
            (IMediaPlayer.OnInfoListener) (mp, arg1, arg2) -> {
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(mp, arg1, arg2);
                }
                switch (arg1) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        mVideoRotationDegree = arg2;
                        Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                        if (mRenderView != null) {
                            mRenderView.setVideoRotation(arg2);
                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                        break;
                    default:
                }
                return true;
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            (IMediaPlayer.OnErrorListener) (mp, framework_err, impl_err) -> {
                Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                mCurrentState = PlayStateParams.STATE_ERROR;
                mTargetState = PlayStateParams.STATE_ERROR;
                if (mMediaController != null) {
                    mMediaController.hide();
                    mMediaController.stop();
                }

                if (mOnErrorListener != null) {
                    if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                        return true;
                    }
                }
                if (mOnInfoListener != null) {
                    if (mOnInfoListener.onInfo(mMediaPlayer, framework_err, 0)) {
                        return true;
                    }
                }

                if (getWindowToken() != null) {
                    String message = "Unknown stop";

                    if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                        message = "Invalid progressive playback";
                    }

                    new AlertDialog.Builder(getContext())
                            .setMessage(message)
                            .setPositiveButton("stop",
                                    (dialog, whichButton) -> {

                                    })
                            .setCancelable(false)
                            .show();
                }
                return true;
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = (iMediaPlayer, percent) -> {
        mCurrentBufferPercentage = percent;
    };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = iMediaPlayer -> {
        if (onSeekCompleteListener != null) {
            onSeekCompleteListener.onSeekComplete(iMediaPlayer);
        }
        start();
    };

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        this.onSeekCompleteListener = onSeekCompleteListener;
    }

    // REMOVED: mSHCallback
    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null) {
            return;
        }

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == PlayStateParams.STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo((int) mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {
                bindSurfaceHolder(mMediaPlayer, holder);
            } else {
                openVideo();
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }

            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hideWithAlpha();
            // REMOVED: release(true);
            releaseWithoutStop();
        }
    };

    public void releaseWithoutStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(null);
        }
    }

    /*
     * release the media player in any state
     */
    public void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = PlayStateParams.STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = PlayStateParams.STATE_IDLE;
            }
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisibility();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisibility();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.showWithAutoHide();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.showWithAutoHide();
                }
                return true;
            } else {
                toggleMediaControlsVisibility();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisibility() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.showWithAutoHide();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mMediaController.start();
            mCurrentState = PlayStateParams.STATE_PLAYING;
        }
        mTargetState = PlayStateParams.STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = PlayStateParams.STATE_PAUSED;
            }
        }
        mTargetState = PlayStateParams.STATE_PAUSED;
    }

    public void onPause() {
        release(false);
    }

    public void onResume() {
        openVideo();
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != PlayStateParams.STATE_ERROR &&
                mCurrentState != PlayStateParams.STATE_IDLE &&
                mCurrentState != PlayStateParams.STATE_PREPARING);
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];

    public int toggleAspectRatio() {
        mCurrentAspectRatioIndex++;
        mCurrentAspectRatioIndex %= s_allAspectRatio.length;

        mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
        if (mRenderView != null) {
            mRenderView.setAspectRatio(mCurrentAspectRatio);
        }
        return mCurrentAspectRatio;
    }

    //-------------------------
    // Extend: Render
    //-------------------------
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    private List<Integer> mAllRenders = new ArrayList<Integer>();
    private int mCurrentRenderIndex = 0;
    private int mCurrentRender = RENDER_SURFACE_VIEW;

    /**
     * 初始化渲染器
     */
    private void initRenders() {
        mAllRenders.clear();
        /*添加surface渲染*/
        mAllRenders.add(RENDER_SURFACE_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            /*添加texture渲染*/
            mAllRenders.add(RENDER_TEXTURE_VIEW);
            mCurrentRenderIndex = 1;
        } else {
            mCurrentRenderIndex = 0;
        }
        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
    }

    /**
     * 设置播放区域拉伸类型
     */
    public void setAspectRatio(int aspectRatio) {
        for (int i = 0; i < s_allAspectRatio.length; i++) {
            if (s_allAspectRatio[i] == aspectRatio) {
                mCurrentAspectRatioIndex = i;
                if (mRenderView != null) {
                    mRenderView.setAspectRatio(mCurrentAspectRatio);
                }
                break;
            }
        }
    }

    public void refreshPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

        if (mRenderView != null) {
            mRenderView.getView().invalidate();
        }

        openVideo();
    }
}
