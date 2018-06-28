package demo.misutesu.myproject.newnewwaveview;

import android.graphics.Path;
import android.util.Log;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/6/21 0021 11:53
 * @description :
 */
public class Wave {

    Path path;

    protected int speed = 10;
    protected int waveHeight;
    protected float waveScale = 1.0f;

    public Wave() {
        path = new Path();
    }

    protected void initPath(int width, int height, boolean isFromUp, boolean isFromLeft) {
        int DP = UiUtils.dp2px(1);
        DP = DP < 1 ? 1 : DP;
        waveHeight = waveHeight == 0 ? height : waveHeight;

        int waveWidth = (int) (waveScale * width);
        int allWaveWidth = 2 * waveWidth;
        if (allWaveWidth < 2 * width) {
            allWaveWidth = (int) Math.ceil((float) width / (float) waveWidth);
        }

        path.reset();
        path.moveTo(isFromLeft ? 0 : width, 0);
        for (int x = DP; x <= allWaveWidth; x += DP) {
            int y = (waveHeight / 2) - ((int) (Math.sin(Math.PI * 2 * x / 360) * waveHeight / 2));
            path.lineTo(isFromLeft ? x : -x, -y);
            Log.d("TAG", "x:" + x + " y:" + y);
        }
//        path.lineTo(isFromLeft ? allWaveWidth : -allWaveWidth, isFromUp ? 0 : width);
//        path.lineTo(isFromLeft ? 0 : width, isFromUp ? 0 : width);
//        path.close();
    }
}
