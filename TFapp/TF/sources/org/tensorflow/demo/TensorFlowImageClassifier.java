package org.tensorflow.demo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.demo.Classifier.Recognition;

public class TensorFlowImageClassifier implements Classifier {
    private static final int MAX_RESULTS = 1;
    private static final String TAG = "TensorFlowImageClassifier";
    private static final float THRESHOLD = 0.85f;
    private float[] floatValues;
    private int imageMean;
    private float imageStd;
    private TensorFlowInferenceInterface inferenceInterface;
    private String inputName;
    private int inputSize;
    private int[] intValues;
    private Vector<String> labels = new Vector();
    private boolean logStats = false;
    private String outputName;
    private String[] outputNames;
    private float[] outputs;

    /* renamed from: org.tensorflow.demo.TensorFlowImageClassifier$1 */
    class C00111 implements Comparator<Recognition> {
        C00111() {
        }

        public int compare(Recognition recognition, Recognition recognition2) {
            return Float.compare(recognition2.getConfidence().floatValue(), recognition.getConfidence().floatValue());
        }
    }

    private TensorFlowImageClassifier() {
    }

    public static Classifier create(AssetManager assetManager, String str, String str2, int i, int i2, float f, String str3, String str4) {
        Classifier tensorFlowImageClassifier = new TensorFlowImageClassifier();
        tensorFlowImageClassifier.inputName = str3;
        tensorFlowImageClassifier.outputName = str4;
        str2 = str2.split("file:///android_asset/")[1];
        String str5 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Reading labels from: ");
        stringBuilder.append(str2);
        Log.i(str5, stringBuilder.toString());
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(str2)));
            while (true) {
                str2 = bufferedReader.readLine();
                if (str2 != null) {
                    tensorFlowImageClassifier.labels.add(str2);
                } else {
                    bufferedReader.close();
                    tensorFlowImageClassifier.inferenceInterface = new TensorFlowInferenceInterface(assetManager, str);
                    assetManager = (int) tensorFlowImageClassifier.inferenceInterface.graphOperation(str4).output(0).shape().size(1);
                    str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Read ");
                    stringBuilder2.append(tensorFlowImageClassifier.labels.size());
                    stringBuilder2.append(" labels, output layer size is ");
                    stringBuilder2.append(assetManager);
                    Log.i(str2, stringBuilder2.toString());
                    tensorFlowImageClassifier.inputSize = i;
                    tensorFlowImageClassifier.imageMean = i2;
                    tensorFlowImageClassifier.imageStd = f;
                    tensorFlowImageClassifier.outputNames = new String[]{str4};
                    i *= i;
                    tensorFlowImageClassifier.intValues = new int[i];
                    tensorFlowImageClassifier.floatValues = new float[(i * 3)];
                    tensorFlowImageClassifier.outputs = new float[assetManager];
                    return tensorFlowImageClassifier;
                }
            }
        } catch (AssetManager assetManager2) {
            throw new RuntimeException("Problem reading label file!", assetManager2);
        }
    }

    public List<Recognition> recognizeImage(Bitmap bitmap) {
        int i;
        Trace.beginSection("recognizeImage");
        Trace.beginSection("preprocessBitmap");
        bitmap.getPixels(this.intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        bitmap = null;
        for (int i2 = 0; i2 < this.intValues.length; i2++) {
            i = this.intValues[i2];
            int i3 = i2 * 3;
            this.floatValues[i3 + 0] = ((float) (((i >> 16) & 255) - this.imageMean)) / this.imageStd;
            this.floatValues[i3 + 1] = ((float) (((i >> 8) & 255) - this.imageMean)) / this.imageStd;
            this.floatValues[i3 + 2] = ((float) ((i & 255) - this.imageMean)) / this.imageStd;
        }
        Trace.endSection();
        Trace.beginSection("feed");
        this.inferenceInterface.feed(this.inputName, this.floatValues, 1, (long) this.inputSize, (long) this.inputSize, 3);
        Trace.endSection();
        Trace.beginSection("run");
        this.inferenceInterface.run(this.outputNames, this.logStats);
        Trace.endSection();
        Trace.beginSection("fetch");
        this.inferenceInterface.fetch(this.outputName, this.outputs);
        Trace.endSection();
        PriorityQueue priorityQueue = new PriorityQueue(3, new C00111());
        i = 0;
        while (i < this.outputs.length) {
            if (this.outputs[i] > THRESHOLD) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(i);
                priorityQueue.add(new Recognition(stringBuilder.toString(), this.labels.size() > i ? (String) this.labels.get(i) : "unknown", Float.valueOf(this.outputs[i]), null));
                SystemClock.sleep(1000);
            }
            i++;
        }
        List arrayList = new ArrayList();
        int min = Math.min(priorityQueue.size(), 1);
        while (bitmap < min) {
            arrayList.add(priorityQueue.poll());
            bitmap++;
        }
        Trace.endSection();
        return arrayList;
    }

    public void enableStatLogging(boolean z) {
        this.logStats = z;
    }

    public String getStatString() {
        return this.inferenceInterface.getStatString();
    }

    public void close() {
        this.inferenceInterface.close();
    }
}
