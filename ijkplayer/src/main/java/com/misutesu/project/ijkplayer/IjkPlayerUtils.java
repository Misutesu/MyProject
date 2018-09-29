package com.misutesu.project.ijkplayer;

import android.content.Context;

import com.misutesu.project.ijkplayer.render.IjkVideoView;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by PC on 2018/3/13 0013.
 */

public class IjkPlayerUtils {

    public static void init() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    public static void destroy(IjkVideoView ijkVideoView) {
        if (ijkVideoView != null) {
            ijkVideoView.stopPlayback();
            ijkVideoView.release(true);
        }
        IjkMediaPlayer.native_profileEnd();
    }

    public static IMediaPlayer createAndroidPlayer() {
        return new AndroidMediaPlayer();
    }

    public static IMediaPlayer createIjkPlayer(Context context) {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEFAULT);//IjkMediaPlayer.IJK_LOG_SILENT
        if (isUseHard(context)) {
            //Using MediaCodec 0,1
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            //Using MediaCodec auto rotate (0,1) mediacodec=0无效
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            //Using MediaCodec handle resolution change (0,1) mediacodec=0无效
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
            //H264黑屏 (0,1) mediacodec=0无效
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1);
        } else {
            //Using MediaCodec 0,1
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        }
        //Using OpenSL ES (0,1)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        //Pixel Format
        /*
        SDL_FCC_RV32:RGBX 8888(Default)
        SDL_FCC_RV16:RGB 565
        SDL_FCC_YV12:YV12
         */
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);

        //seekTo优化
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", 4096);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

        //add Header
//        ijkMediaPlayer.setOption(IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC, "headers", "token:" + User.instance().getToken());
        return ijkMediaPlayer;
    }

    public static boolean isUseHard(Context context) {
        return true;
    }
}
