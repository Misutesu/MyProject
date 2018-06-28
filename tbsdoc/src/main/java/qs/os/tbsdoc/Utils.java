package qs.os.tbsdoc;

import android.text.TextUtils;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/5/16 0016 17:37
 * @description :
 */
public class Utils {

    public static String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }

        str = paramString.substring(i + 1);
        return str;
    }

    public static boolean isEquals(String str, String... strs) {
        boolean isSame = false;
        if (str != null) {
            for (String s : strs) {
                if (str.equals(s)) {
                    isSame = true;
                    break;
                }
            }
        }
        return isSame;
    }
}
