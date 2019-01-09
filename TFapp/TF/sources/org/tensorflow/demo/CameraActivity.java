package org.tensorflow.demo;

import android.app.Activity;
import android.media.Image.Plane;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.KeyEvent;
import android.widget.Toast;
import com.homesecurity.firstone.C0000R;
import java.nio.ByteBuffer;
import org.tensorflow.demo.CameraConnectionFragment.ConnectionCallback;
import org.tensorflow.demo.OverlayView.DrawCallback;
import org.tensorflow.demo.env.Logger;

public abstract class CameraActivity extends Activity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_CAMERA = "android.permission.CAMERA";
    private static final String PERMISSION_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private boolean debug = false;
    private Handler handler;
    private HandlerThread handlerThread;

    /* renamed from: org.tensorflow.demo.CameraActivity$1 */
    class C00131 implements ConnectionCallback {
        C00131() {
        }

        public void onPreviewSizeChosen(Size size, int i) {
            CameraActivity.this.onPreviewSizeChosen(size, i);
        }
    }

    protected abstract Size getDesiredPreviewFrameSize();

    protected abstract int getLayoutId();

    protected abstract void onPreviewSizeChosen(Size size, int i);

    public void onSetDebug(boolean z) {
    }

    protected void onCreate(Bundle bundle) {
        bundle = LOGGER;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onCreate ");
        stringBuilder.append(this);
        bundle.m1d(stringBuilder.toString(), new Object[0]);
        super.onCreate(null);
        getWindow().addFlags(128);
        setContentView(C0000R.layout.activity_camera);
        if (hasPermission() != null) {
            setFragment();
        } else {
            requestPermission();
        }
    }

    public synchronized void onStart() {
        Logger logger = LOGGER;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onStart ");
        stringBuilder.append(this);
        logger.m1d(stringBuilder.toString(), new Object[0]);
        super.onStart();
    }

    public synchronized void onResume() {
        Logger logger = LOGGER;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onResume ");
        stringBuilder.append(this);
        logger.m1d(stringBuilder.toString(), new Object[0]);
        super.onResume();
        this.handlerThread = new HandlerThread("inference");
        this.handlerThread.start();
        this.handler = new Handler(this.handlerThread.getLooper());
    }

    public synchronized void onPause() {
        Logger logger = LOGGER;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onPause ");
        stringBuilder.append(this);
        logger.m1d(stringBuilder.toString(), new Object[0]);
        if (!isFinishing()) {
            LOGGER.m1d("Requesting finish", new Object[0]);
            finish();
        }
        this.handlerThread.quitSafely();
        try {
            this.handlerThread.join();
            this.handlerThread = null;
            this.handler = null;
        } catch (Throwable e) {
            LOGGER.m4e(e, "Exception!", new Object[0]);
        }
        super.onPause();
    }

    public synchronized void onStop() {
        Logger logger = LOGGER;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onStop ");
        stringBuilder.append(this);
        logger.m1d(stringBuilder.toString(), new Object[0]);
        super.onStop();
    }

    public synchronized void onDestroy() {
        Logger logger = LOGGER;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onDestroy ");
        stringBuilder.append(this);
        logger.m1d(stringBuilder.toString(), new Object[0]);
        super.onDestroy();
    }

    protected synchronized void runInBackground(Runnable runnable) {
        if (this.handler != null) {
            this.handler.post(runnable);
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            if (iArr.length > 0 && iArr[0] == 0 && iArr[1] == 0) {
                setFragment();
            } else {
                requestPermission();
            }
        }
    }

    private boolean hasPermission() {
        boolean z = true;
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        if (checkSelfPermission(PERMISSION_CAMERA) != 0 || checkSelfPermission(PERMISSION_STORAGE) != 0) {
            z = false;
        }
        return z;
    }

    private void requestPermission() {
        if (VERSION.SDK_INT >= 23) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) || shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(this, "Camera AND storage permission are required for this demo", 1).show();
            }
            requestPermissions(new String[]{PERMISSION_CAMERA, PERMISSION_STORAGE}, 1);
        }
    }

    protected void setFragment() {
        getFragmentManager().beginTransaction().replace(C0000R.id.container, CameraConnectionFragment.newInstance(new C00131(), this, getLayoutId(), getDesiredPreviewFrameSize())).commit();
    }

    protected void fillBytes(Plane[] planeArr, byte[][] bArr) {
        for (int i = 0; i < planeArr.length; i++) {
            ByteBuffer buffer = planeArr[i].getBuffer();
            if (bArr[i] == null) {
                LOGGER.m1d("Initializing buffer %d at size %d", Integer.valueOf(i), Integer.valueOf(buffer.capacity()));
                bArr[i] = new byte[buffer.capacity()];
            }
            buffer.get(bArr[i]);
        }
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void requestRender() {
        OverlayView overlayView = (OverlayView) findViewById(C0000R.id.debug_overlay);
        if (overlayView != null) {
            overlayView.postInvalidate();
        }
    }

    public void addCallback(DrawCallback drawCallback) {
        OverlayView overlayView = (OverlayView) findViewById(C0000R.id.debug_overlay);
        if (overlayView != null) {
            overlayView.addCallback(drawCallback);
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 25) {
            if (i != 24) {
                return super.onKeyDown(i, keyEvent);
            }
        }
        this.debug ^= 1;
        requestRender();
        onSetDebug(this.debug);
        return true;
    }
}
