package qs.os.tbsdoc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

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

        void onOtherOpen();

        void onLoadError();

        void onUserBack();
    }

    private Context mContext;
    private WebView mWebView;
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

    public void load(String html) {
        mWebView = new WebView(mContext);
        addView(mWebView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.loadData(html, "text/html; charset=UTF-8", null);
    }

    public void load(File file, File tempFile, OnDocListener onDocListener) {
        this.mOnDocListener = onDocListener;
        if (file == null || !file.exists() || tempFile == null) {
            if (mOnDocListener != null) {
                mOnDocListener.onLoadError();
            }
            return;
        }
        if (DocUtils.isIsUseTbs()) {
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
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
        } else {
            loadBySystem(file);
        }
    }

    public void stop() {
        removeAllViews();
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.stopLoading();
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }
        if (mTbsView != null) {
            mTbsView.onStop();
            mTbsView = null;
        }
        mContext = null;
        mOnDocListener = null;
        QbSdk.setTbsListener(null);
    }

    private void loadBySystem(File file) {
        String fileType = Utils.getFileType(file.getAbsolutePath());
        Intent intent = new Intent();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(mContext, "qs.os.tbsdoc.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        for (String[] format : FormatUtils.FORMATS) {
            if (fileType.equals(format[0])) {
                intent.setDataAndType(uri, format[1]);
                break;
            }
        }
        if (intent.getData() == null) {
            intent.setDataAndType(uri, "*/*");
        }
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                intent.setDataAndType(uri, "*/*");
                mContext.startActivity(intent);
                Toast.makeText(mContext, R.string.no_soft_can_open_doc, Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (mOnDocListener != null) {
            mOnDocListener.onOtherOpen();
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
