package org.tensorflow.demo;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Size;
import android.util.TypedValue;
import com.homesecurity.firstone.C0000R;
import java.util.List;
import java.util.Vector;
import org.tensorflow.demo.OverlayView.DrawCallback;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "Mul:0";
    private static final int INPUT_SIZE = 299;
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";
    private static final Logger LOGGER = new Logger();
    private static final boolean MAINTAIN_ASPECT = true;
    private static final String MODEL_FILE = "file:///android_asset/graph.pb";
    private static final String OUTPUT_NAME = "final_result";
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10.0f;
    private BorderedText borderedText;
    private Classifier classifier;
    private boolean computing = false;
    private Bitmap cropCopyBitmap;
    private Matrix cropToFrameTransform;
    private Bitmap croppedBitmap = null;
    private Matrix frameToCropTransform;
    private long lastProcessingTimeMs;
    private int previewHeight = 0;
    private int previewWidth = 0;
    private ResultsView resultsView;
    private int[] rgbBytes = null;
    private Bitmap rgbFrameBitmap = null;
    private Integer sensorOrientation;
    private byte[][] yuvBytes;

    /* renamed from: org.tensorflow.demo.ClassifierActivity$2 */
    class C00092 implements Runnable {
        C00092() {
        }

        public void run() {
            long uptimeMillis = SystemClock.uptimeMillis();
            List recognizeImage = ClassifierActivity.this.classifier.recognizeImage(ClassifierActivity.this.croppedBitmap);
            ClassifierActivity.this.lastProcessingTimeMs = SystemClock.uptimeMillis() - uptimeMillis;
            ClassifierActivity.this.cropCopyBitmap = Bitmap.createBitmap(ClassifierActivity.this.croppedBitmap);
            ClassifierActivity.this.resultsView.setResults(recognizeImage);
            ClassifierActivity.this.requestRender();
            ClassifierActivity.this.computing = false;
        }
    }

    /* renamed from: org.tensorflow.demo.ClassifierActivity$1 */
    class C00141 implements DrawCallback {
        C00141() {
        }

        public void drawCallback(Canvas canvas) {
            ClassifierActivity.this.renderDebug(canvas);
        }
    }

    protected int getLayoutId() {
        return C0000R.layout.camera_connection_fragment;
    }

    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    public void onPreviewSizeChosen(Size size, int i) {
        this.borderedText = new BorderedText(TypedValue.applyDimension(1, TEXT_SIZE_DIP, getResources().getDisplayMetrics()));
        this.borderedText.setTypeface(Typeface.MONOSPACE);
        this.classifier = TensorFlowImageClassifier.create(getAssets(), MODEL_FILE, LABEL_FILE, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME);
        this.resultsView = (ResultsView) findViewById(C0000R.id.results);
        this.previewWidth = size.getWidth();
        this.previewHeight = size.getHeight();
        LOGGER.m5i("Sensor orientation: %d, Screen orientation: %d", Integer.valueOf(i), Integer.valueOf(getWindowManager().getDefaultDisplay().getRotation()));
        this.sensorOrientation = Integer.valueOf(i + size);
        LOGGER.m5i("Initializing at size %dx%d", Integer.valueOf(this.previewWidth), Integer.valueOf(this.previewHeight));
        this.rgbBytes = new int[(this.previewWidth * this.previewHeight)];
        this.rgbFrameBitmap = Bitmap.createBitmap(this.previewWidth, this.previewHeight, Config.ARGB_8888);
        this.croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888);
        this.frameToCropTransform = ImageUtils.getTransformationMatrix(this.previewWidth, this.previewHeight, INPUT_SIZE, INPUT_SIZE, this.sensorOrientation.intValue(), MAINTAIN_ASPECT);
        this.cropToFrameTransform = new Matrix();
        this.frameToCropTransform.invert(this.cropToFrameTransform);
        this.yuvBytes = new byte[3][];
        addCallback(new C00141());
    }

    public void onImageAvailable(ImageReader imageReader) {
        Throwable e;
        Throwable th;
        ClassifierActivity classifierActivity = this;
        Image acquireLatestImage;
        try {
            acquireLatestImage = imageReader.acquireLatestImage();
            if (acquireLatestImage != null) {
                try {
                    if (classifierActivity.computing) {
                        acquireLatestImage.close();
                        return;
                    }
                    classifierActivity.computing = MAINTAIN_ASPECT;
                    Trace.beginSection("imageAvailable");
                    Plane[] planes = acquireLatestImage.getPlanes();
                    fillBytes(planes, classifierActivity.yuvBytes);
                    ImageUtils.convertYUV420ToARGB8888(classifierActivity.yuvBytes[0], classifierActivity.yuvBytes[1], classifierActivity.yuvBytes[2], classifierActivity.previewWidth, classifierActivity.previewHeight, planes[0].getRowStride(), planes[1].getRowStride(), planes[1].getPixelStride(), classifierActivity.rgbBytes);
                    acquireLatestImage.close();
                    classifierActivity.rgbFrameBitmap.setPixels(classifierActivity.rgbBytes, 0, classifierActivity.previewWidth, 0, 0, classifierActivity.previewWidth, classifierActivity.previewHeight);
                    new Canvas(classifierActivity.croppedBitmap).drawBitmap(classifierActivity.rgbFrameBitmap, classifierActivity.frameToCropTransform, null);
                    runInBackground(new C00092());
                    Trace.endSection();
                } catch (Exception e2) {
                    e = e2;
                    th = e;
                    if (acquireLatestImage != null) {
                        acquireLatestImage.close();
                    }
                    LOGGER.m4e(th, "Exception!", new Object[0]);
                    Trace.endSection();
                }
            }
        } catch (Exception e3) {
            e = e3;
            acquireLatestImage = null;
            th = e;
            if (acquireLatestImage != null) {
                acquireLatestImage.close();
            }
            LOGGER.m4e(th, "Exception!", new Object[0]);
            Trace.endSection();
        }
    }

    public void onSetDebug(boolean z) {
        this.classifier.enableStatLogging(z);
    }

    private void renderDebug(Canvas canvas) {
        if (isDebug()) {
            Bitmap bitmap = this.cropCopyBitmap;
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postScale(2.0f, 2.0f);
                matrix.postTranslate(((float) canvas.getWidth()) - (((float) bitmap.getWidth()) * 2.0f), ((float) canvas.getHeight()) - (((float) bitmap.getHeight()) * 2.0f));
                canvas.drawBitmap(bitmap, matrix, new Paint());
                Vector vector = new Vector();
                if (this.classifier != null) {
                    for (Object add : this.classifier.getStatString().split("\n")) {
                        vector.add(add);
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Frame: ");
                stringBuilder.append(this.previewWidth);
                stringBuilder.append("x");
                stringBuilder.append(this.previewHeight);
                vector.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("Crop: ");
                stringBuilder.append(bitmap.getWidth());
                stringBuilder.append("x");
                stringBuilder.append(bitmap.getHeight());
                vector.add(stringBuilder.toString());
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("View: ");
                stringBuilder2.append(canvas.getWidth());
                stringBuilder2.append("x");
                stringBuilder2.append(canvas.getHeight());
                vector.add(stringBuilder2.toString());
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Rotation: ");
                stringBuilder2.append(this.sensorOrientation);
                vector.add(stringBuilder2.toString());
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Inference time: ");
                stringBuilder2.append(this.lastProcessingTimeMs);
                stringBuilder2.append("ms");
                vector.add(stringBuilder2.toString());
                this.borderedText.drawLines(canvas, TEXT_SIZE_DIP, (float) (canvas.getHeight() - 10), vector);
            }
        }
    }
}
