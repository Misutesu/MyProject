package qs.os.tbsdoc;

import android.content.Context;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.io.File;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/5/16 0016 17:37
 * @description :
 */
public class DocUtils {

    private static boolean isInstallEnd;
    private static boolean isUseTbs;

    public static void init(Context context) {
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
            }

            @Override
            public void onInstallFinish(int i) {
            }

            @Override
            public void onDownloadProgress(int i) {
            }
        });
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean b) {
                isUseTbs = b;
                isInstallEnd = true;
            }
        });
    }

    public static boolean isIsInstallEnd() {
        return isInstallEnd;
    }

    public static boolean isIsUseTbs() {
        return isUseTbs;
    }

    public static File getDocTempDir(Context context) {
        return new File(context.getExternalCacheDir(), "tbsTemp");
    }
}
