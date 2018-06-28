package demo.misutesu.myproject.newnewwaveview;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/6/21 0021 15:04
 * @description :
 */
public class UiUtils {
    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
