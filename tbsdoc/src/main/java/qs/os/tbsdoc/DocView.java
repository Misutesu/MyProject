package qs.os.tbsdoc;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.ValueCallback;

import java.io.File;
import java.util.HashMap;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/5/16 0016 17:30
 * @description :
 */
public class DocView extends FrameLayout implements TbsReaderView.ReaderCallback, ValueCallback<String> {

    public interface OnDocListener {
        void onLoadSuccess();

        void onLoadError();

        void onUserBack();
    }

    private Context mContext;
    private TbsReaderView mTbsView;
    protected OnDocListener mOnDocListener;

    public DocView(Context context) {
        super(context);
        init(context);
    }

    public DocView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DocView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public void load(File file, File tempFile, OnDocListener onDocListener) {
        this.mOnDocListener = onDocListener;

        if (file == null || !file.exists() || tempFile == null) {
            if (mOnDocListener != null) {
                mOnDocListener.onLoadError();
            }
            return;
        }

        if (tempFile.exists() || (!tempFile.exists() && tempFile.mkdirs())) {
            mTbsView = new TbsReaderView(mContext, this);

            if (mTbsView.preOpen(Utils.getFileType(file.getAbsolutePath()), false)) {
                addView(mTbsView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                Bundle localBundle = new Bundle();
                localBundle.putString("filePath", file.getAbsolutePath());
                localBundle.putString("tempPath", tempFile.getAbsolutePath());
                mTbsView.openFile(localBundle);
                if (mOnDocListener != null) {
                    mOnDocListener.onLoadSuccess();
                }
                return;
            }
        }

        HashMap<String, String> extraParams = new HashMap<>();
        extraParams.put("style", "1");
        extraParams.put("topBarBgColor", "#d61517");

        if (QbSdk.openFileReader(mContext, file.getAbsolutePath(), extraParams, this) != -1) {
            if (mOnDocListener != null) {
                mOnDocListener.onLoadSuccess();
            }
        } else {
            if (mOnDocListener != null) {
                mOnDocListener.onLoadError();
            }
        }
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    public void onReceiveValue(String s) {
        if (Utils.isEquals(s, "openFileReader open in QB", "filepath error", "TbsReaderDialogClosed", "default browser:", "filepath error", "fileReaderClosed")) {
            if (mOnDocListener != null) {
                mOnDocListener.onUserBack();
            }
        }
    }
}
