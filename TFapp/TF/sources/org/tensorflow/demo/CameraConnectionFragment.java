package org.tensorflow.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.homesecurity.firstone.C0000R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.tensorflow.demo.env.Logger;

public class CameraConnectionFragment extends Fragment {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String FRAGMENT_DIALOG = "dialog";
    private static final Logger LOGGER = new Logger();
    private static final int MINIMUM_PREVIEW_SIZE = 320;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private final ConnectionCallback cameraConnectionCallback;
    private CameraDevice cameraDevice;
    private String cameraId;
    private final Semaphore cameraOpenCloseLock = new Semaphore(1);
    private final CaptureCallback captureCallback = new C00064();
    private CameraCaptureSession captureSession;
    private final OnImageAvailableListener imageListener;
    private final Size inputSize;
    private final int layout;
    private ImageReader previewReader;
    private CaptureRequest previewRequest;
    private Builder previewRequestBuilder;
    private Size previewSize;
    private Integer sensorOrientation;
    private final StateCallback stateCallback = new C00042();
    private final SurfaceTextureListener surfaceTextureListener = new C00031();
    private AutoFitTextureView textureView;

    /* renamed from: org.tensorflow.demo.CameraConnectionFragment$1 */
    class C00031 implements SurfaceTextureListener {
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        C00031() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            CameraConnectionFragment.this.openCamera(i, i2);
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            CameraConnectionFragment.this.configureTransform(i, i2);
        }
    }

    /* renamed from: org.tensorflow.demo.CameraConnectionFragment$2 */
    class C00042 extends StateCallback {
        C00042() {
        }

        public void onOpened(CameraDevice cameraDevice) {
            CameraConnectionFragment.this.cameraOpenCloseLock.release();
            CameraConnectionFragment.this.cameraDevice = cameraDevice;
            CameraConnectionFragment.this.createCameraPreviewSession();
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            CameraConnectionFragment.this.cameraOpenCloseLock.release();
            cameraDevice.close();
            CameraConnectionFragment.this.cameraDevice = null;
        }

        public void onError(CameraDevice cameraDevice, int i) {
            CameraConnectionFragment.this.cameraOpenCloseLock.release();
            cameraDevice.close();
            CameraConnectionFragment.this.cameraDevice = 0;
            cameraDevice = CameraConnectionFragment.this.getActivity();
            if (cameraDevice != null) {
                cameraDevice.finish();
            }
        }
    }

    /* renamed from: org.tensorflow.demo.CameraConnectionFragment$4 */
    class C00064 extends CaptureCallback {
        public void onCaptureCompleted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
        }

        public void onCaptureProgressed(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, CaptureResult captureResult) {
        }

        C00064() {
        }
    }

    /* renamed from: org.tensorflow.demo.CameraConnectionFragment$5 */
    class C00075 extends CameraCaptureSession.StateCallback {
        C00075() {
        }

        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            if (CameraConnectionFragment.this.cameraDevice != null) {
                CameraConnectionFragment.this.captureSession = cameraCaptureSession;
                try {
                    CameraConnectionFragment.this.previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, Integer.valueOf(4));
                    CameraConnectionFragment.this.previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(2));
                    CameraConnectionFragment.this.previewRequest = CameraConnectionFragment.this.previewRequestBuilder.build();
                    CameraConnectionFragment.this.captureSession.setRepeatingRequest(CameraConnectionFragment.this.previewRequest, CameraConnectionFragment.this.captureCallback, CameraConnectionFragment.this.backgroundHandler);
                } catch (CameraCaptureSession cameraCaptureSession2) {
                    CameraConnectionFragment.LOGGER.m4e(cameraCaptureSession2, "Exception!", new Object[0]);
                }
            }
        }

        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            CameraConnectionFragment.this.showToast("Failed");
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size size, Size size2) {
            return Long.signum((((long) size.getWidth()) * ((long) size.getHeight())) - (((long) size2.getWidth()) * ((long) size2.getHeight())));
        }
    }

    public interface ConnectionCallback {
        void onPreviewSizeChosen(Size size, int i);
    }

    public static class ErrorDialog extends DialogFragment {
        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String str) {
            ErrorDialog errorDialog = new ErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, str);
            errorDialog.setArguments(bundle);
            return errorDialog;
        }

        public Dialog onCreateDialog(Bundle bundle) {
            bundle = getActivity();
            return new AlertDialog.Builder(bundle).setMessage(getArguments().getString(ARG_MESSAGE)).setPositiveButton(17039370, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    bundle.finish();
                }
            }).create();
        }
    }

    static {
        ORIENTATIONS.append(0, 90);
        ORIENTATIONS.append(1, 0);
        ORIENTATIONS.append(2, 270);
        ORIENTATIONS.append(3, 180);
    }

    private CameraConnectionFragment(ConnectionCallback connectionCallback, OnImageAvailableListener onImageAvailableListener, int i, Size size) {
        this.cameraConnectionCallback = connectionCallback;
        this.imageListener = onImageAvailableListener;
        this.layout = i;
        this.inputSize = size;
    }

    private void showToast(final String str) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, str, 0).show();
                }
            });
        }
    }

    private static Size chooseOptimalSize(Size[] sizeArr, int i, int i2) {
        int max = Math.max(Math.min(i, i2), MINIMUM_PREVIEW_SIZE);
        Size size = new Size(i, i2);
        i = new ArrayList();
        i2 = new ArrayList();
        int length = sizeArr.length;
        int i3 = 0;
        int i4 = i3;
        while (i3 < length) {
            Size size2 = sizeArr[i3];
            if (size2.equals(size)) {
                i4 = 1;
            }
            if (size2.getHeight() < max || size2.getWidth() < max) {
                i2.add(size2);
            } else {
                i.add(size2);
            }
            i3++;
        }
        Logger logger = LOGGER;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Desired size: ");
        stringBuilder.append(size);
        stringBuilder.append(", min size: ");
        stringBuilder.append(max);
        stringBuilder.append("x");
        stringBuilder.append(max);
        logger.m5i(stringBuilder.toString(), new Object[0]);
        Logger logger2 = LOGGER;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Valid preview sizes: [");
        stringBuilder2.append(TextUtils.join(", ", i));
        stringBuilder2.append("]");
        logger2.m5i(stringBuilder2.toString(), new Object[0]);
        logger2 = LOGGER;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Rejected preview sizes: [");
        stringBuilder2.append(TextUtils.join(", ", i2));
        stringBuilder2.append("]");
        logger2.m5i(stringBuilder2.toString(), new Object[0]);
        if (i4 != 0) {
            LOGGER.m5i("Exact size match found.", new Object[0]);
            return size;
        } else if (i.size() > 0) {
            Size size3 = (Size) Collections.min(i, new CompareSizesByArea());
            i = LOGGER;
            i2 = new StringBuilder();
            i2.append("Chosen size: ");
            i2.append(size3.getWidth());
            i2.append("x");
            i2.append(size3.getHeight());
            i.m5i(i2.toString(), new Object[0]);
            return size3;
        } else {
            LOGGER.m3e("Couldn't find any suitable preview size", new Object[0]);
            return sizeArr[0];
        }
    }

    public static CameraConnectionFragment newInstance(ConnectionCallback connectionCallback, OnImageAvailableListener onImageAvailableListener, int i, Size size) {
        return new CameraConnectionFragment(connectionCallback, onImageAvailableListener, i, size);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(this.layout, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        this.textureView = (AutoFitTextureView) view.findViewById(C0000R.id.texture);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (this.textureView.isAvailable()) {
            openCamera(this.textureView.getWidth(), this.textureView.getHeight());
        } else {
            this.textureView.setSurfaceTextureListener(this.surfaceTextureListener);
        }
    }

    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void setUpCameraOutputs(int r9, int r10) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/32863545.run(Unknown Source)
*/
        /*
        r8 = this;
        r9 = r8.getActivity();
        r10 = "camera";
        r9 = r9.getSystemService(r10);
        r9 = (android.hardware.camera2.CameraManager) r9;
        r10 = 0;
        r0 = r9.getCameraIdList();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r1 = r0.length;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r2 = r10;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x0013:
        if (r2 >= r1) goto L_0x00cc;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x0015:
        r3 = r0[r2];	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = r9.getCameraCharacteristics(r3);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = android.hardware.camera2.CameraCharacteristics.LENS_FACING;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r4.get(r5);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = (java.lang.Integer) r5;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        if (r5 == 0) goto L_0x002c;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x0025:
        r5 = r5.intValue();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        if (r5 != 0) goto L_0x002c;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x002b:
        goto L_0x00a0;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x002c:
        r5 = android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r4.get(r5);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = (android.hardware.camera2.params.StreamConfigurationMap) r5;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        if (r5 != 0) goto L_0x0037;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x0036:
        goto L_0x00a0;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x0037:
        r6 = 35;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = r5.getOutputSizes(r6);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = java.util.Arrays.asList(r6);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r7 = new org.tensorflow.demo.CameraConnectionFragment$CompareSizesByArea;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r7.<init>();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = java.util.Collections.max(r6, r7);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = (android.util.Size) r6;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = r4.get(r6);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = (java.lang.Integer) r4;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r8.sensorOrientation = r4;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = android.graphics.SurfaceTexture.class;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = r5.getOutputSizes(r4);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r8.inputSize;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r5.getWidth();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = r8.inputSize;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = r6.getHeight();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = chooseOptimalSize(r4, r5, r6);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r8.previewSize = r4;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = r8.getResources();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = r4.getConfiguration();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4 = r4.orientation;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = 2;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        if (r4 != r5) goto L_0x008d;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x007b:
        r4 = r8.textureView;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r8.previewSize;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r5.getWidth();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = r8.previewSize;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = r6.getHeight();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4.setAspectRatio(r5, r6);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        goto L_0x009e;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x008d:
        r4 = r8.textureView;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r8.previewSize;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r5 = r5.getHeight();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = r8.previewSize;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r6 = r6.getWidth();	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
        r4.setAspectRatio(r5, r6);	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x009e:
        r8.cameraId = r3;	 Catch:{ CameraAccessException -> 0x00c2, NullPointerException -> 0x00a4 }
    L_0x00a0:
        r2 = r2 + 1;
        goto L_0x0013;
    L_0x00a4:
        r9 = 2131165188; // 0x7f070004 float:1.7944586E38 double:1.052935505E-314;
        r10 = r8.getString(r9);
        r10 = org.tensorflow.demo.CameraConnectionFragment.ErrorDialog.newInstance(r10);
        r0 = r8.getChildFragmentManager();
        r1 = "dialog";
        r10.show(r0, r1);
        r10 = new java.lang.RuntimeException;
        r9 = r8.getString(r9);
        r10.<init>(r9);
        throw r10;
    L_0x00c2:
        r9 = move-exception;
        r0 = LOGGER;
        r1 = "Exception!";
        r10 = new java.lang.Object[r10];
        r0.m4e(r9, r1, r10);
    L_0x00cc:
        r9 = r8.cameraConnectionCallback;
        r10 = r8.previewSize;
        r0 = r8.sensorOrientation;
        r0 = r0.intValue();
        r9.onPreviewSizeChosen(r10, r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.demo.CameraConnectionFragment.setUpCameraOutputs(int, int):void");
    }

    private void openCamera(int i, int i2) {
        setUpCameraOutputs(i, i2);
        configureTransform(i, i2);
        CameraManager cameraManager = (CameraManager) getActivity().getSystemService("camera");
        try {
            if (this.cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS) == 0) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            cameraManager.openCamera(this.cameraId, this.stateCallback, this.backgroundHandler);
        } catch (int i3) {
            LOGGER.m4e(i3, "Exception!", new Object[0]);
        } catch (int i32) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", i32);
        }
    }

    private void closeCamera() {
        try {
            this.cameraOpenCloseLock.acquire();
            if (this.captureSession != null) {
                this.captureSession.close();
                this.captureSession = null;
            }
            if (this.cameraDevice != null) {
                this.cameraDevice.close();
                this.cameraDevice = null;
            }
            if (this.previewReader != null) {
                this.previewReader.close();
                this.previewReader = null;
            }
            this.cameraOpenCloseLock.release();
        } catch (Throwable e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } catch (Throwable th) {
            this.cameraOpenCloseLock.release();
        }
    }

    private void startBackgroundThread() {
        this.backgroundThread = new HandlerThread("ImageListener");
        this.backgroundThread.start();
        this.backgroundHandler = new Handler(this.backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        this.backgroundThread.quitSafely();
        try {
            this.backgroundThread.join();
            this.backgroundThread = null;
            this.backgroundHandler = null;
        } catch (Throwable e) {
            LOGGER.m4e(e, "Exception!", new Object[0]);
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = this.textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
            Surface surface = new Surface(surfaceTexture);
            this.previewRequestBuilder = this.cameraDevice.createCaptureRequest(1);
            this.previewRequestBuilder.addTarget(surface);
            Logger logger = LOGGER;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Opening camera preview: ");
            stringBuilder.append(this.previewSize.getWidth());
            stringBuilder.append("x");
            stringBuilder.append(this.previewSize.getHeight());
            logger.m5i(stringBuilder.toString(), new Object[0]);
            this.previewReader = ImageReader.newInstance(this.previewSize.getWidth(), this.previewSize.getHeight(), 35, 2);
            this.previewReader.setOnImageAvailableListener(this.imageListener, this.backgroundHandler);
            this.previewRequestBuilder.addTarget(this.previewReader.getSurface());
            this.cameraDevice.createCaptureSession(Arrays.asList(new Surface[]{surface, this.previewReader.getSurface()}), new C00075(), null);
        } catch (Throwable e) {
            LOGGER.m4e(e, "Exception!", new Object[0]);
        }
    }

    private void configureTransform(int i, int i2) {
        Activity activity = getActivity();
        if (!(this.textureView == null || this.previewSize == null)) {
            if (activity != null) {
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                Matrix matrix = new Matrix();
                i = (float) i;
                i2 = (float) i2;
                RectF rectF = new RectF(0.0f, 0.0f, i, i2);
                RectF rectF2 = new RectF(0.0f, 0.0f, (float) this.previewSize.getHeight(), (float) this.previewSize.getWidth());
                float centerX = rectF.centerX();
                float centerY = rectF.centerY();
                if (1 != rotation) {
                    if (3 != rotation) {
                        if (2 == rotation) {
                            matrix.postRotate(1127481344, centerX, centerY);
                        }
                        this.textureView.setTransform(matrix);
                    }
                }
                rectF2.offset(centerX - rectF2.centerX(), centerY - rectF2.centerY());
                matrix.setRectToRect(rectF, rectF2, ScaleToFit.FILL);
                i = Math.max(i2 / ((float) this.previewSize.getHeight()), i / ((float) this.previewSize.getWidth()));
                matrix.postScale(i, i, centerX, centerY);
                matrix.postRotate((float) (90 * (rotation - 2)), centerX, centerY);
                this.textureView.setTransform(matrix);
            }
        }
    }
}
