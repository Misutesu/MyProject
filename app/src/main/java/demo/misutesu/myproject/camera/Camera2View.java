package demo.misutesu.myproject.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

/**
 * @author : 伍加全(姓名) wu_developer@outlook.com(邮箱)
 * @date : 2018/6/22 0022 15:01
 * @description :
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2View extends TextureView implements TextureView.SurfaceTextureListener {

    private Context mContext;

    public Camera2View(Context context) {
        super(context);
        init(context);
    }

    public Camera2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Camera2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Camera2View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setSurfaceTextureListener(this);
    }

    private void initCameraInfo() {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        if (manager != null) {
            try {
                int cameraNum = manager.getCameraIdList().length;
                if (cameraNum > 0) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics("0");
                    StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (configurationMap != null) {
                        configurationMap.getOutputSizes(ImageFormat.JPEG);
                        configurationMap.getOutputSizes(TextureView.class);
                    }
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        manager.openCamera("0", new CameraDevice.StateCallback() {
                            @Override
                            public void onOpened(@NonNull CameraDevice cameraDevice) {
                                try {
                                    takePreview(cameraDevice);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                                cameraDevice.close();
                            }

                            @Override
                            public void onError(@NonNull CameraDevice cameraDevice, int i) {
                                cameraDevice.close();
                            }
                        }, null);
                    }
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void takePreview(CameraDevice cameraDevice) throws CameraAccessException {
        Surface surface = new Surface(getSurfaceTexture());
        cameraDevice.createCaptureSession(Arrays.asList(surface, ImageReader.newInstance(getWidth(), getHeight(),
                ImageFormat.JPEG, 2).getSurface()), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                try {
                    CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    builder.addTarget(new Surface(getSurfaceTexture()));
                    cameraCaptureSession.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                            super.onCaptureStarted(session, request, timestamp, frameNumber);
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    surface.release();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

            }
        }, null);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        initCameraInfo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
