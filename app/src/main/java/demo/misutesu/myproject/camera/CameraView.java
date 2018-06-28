package demo.misutesu.myproject.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/6/22 0022 14:34
 * @description :
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraView.class.getName();

    private Context mContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraView(Context context) {
        super(context);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    private void initCameraInfo() {
        int cameraNum = Camera.getNumberOfCameras();
        if (cameraNum > 0) {
            try {
                mCamera = Camera.open(0);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                mCamera.setDisplayOrientation(90);
                Camera.Parameters params = mCamera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
                mCamera.setParameters(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCameraInfo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
