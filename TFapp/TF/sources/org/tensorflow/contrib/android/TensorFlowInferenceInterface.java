package org.tensorflow.contrib.android;

import android.os.Build.VERSION;
import android.os.Trace;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.Session;
import org.tensorflow.Session.Runner;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.Tensors;
import org.tensorflow.types.UInt8;

public class TensorFlowInferenceInterface {
    private static final String ASSET_FILE_PREFIX = "file:///android_asset/";
    private static final String TAG = "TensorFlowInferenceInterface";
    private List<String> feedNames = new ArrayList();
    private List<Tensor<?>> feedTensors = new ArrayList();
    private List<String> fetchNames = new ArrayList();
    private List<Tensor<?>> fetchTensors = new ArrayList();
    /* renamed from: g */
    private final Graph f0g;
    private final String modelName;
    private RunStats runStats;
    private Runner runner;
    private final Session sess;

    private static class TensorId {
        String name;
        int outputIndex;

        private TensorId() {
        }

        public static org.tensorflow.contrib.android.TensorFlowInferenceInterface.TensorId parse(java.lang.String r4) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/32863545.run(Unknown Source)
*/
            /*
            r0 = new org.tensorflow.contrib.android.TensorFlowInferenceInterface$TensorId;
            r0.<init>();
            r1 = 58;
            r1 = r4.lastIndexOf(r1);
            r2 = 0;
            if (r1 >= 0) goto L_0x0013;
        L_0x000e:
            r0.outputIndex = r2;
            r0.name = r4;
            return r0;
        L_0x0013:
            r3 = r1 + 1;
            r3 = r4.substring(r3);	 Catch:{ NumberFormatException -> 0x0026 }
            r3 = java.lang.Integer.parseInt(r3);	 Catch:{ NumberFormatException -> 0x0026 }
            r0.outputIndex = r3;	 Catch:{ NumberFormatException -> 0x0026 }
            r1 = r4.substring(r2, r1);	 Catch:{ NumberFormatException -> 0x0026 }
            r0.name = r1;	 Catch:{ NumberFormatException -> 0x0026 }
            goto L_0x002a;
        L_0x0026:
            r0.outputIndex = r2;
            r0.name = r4;
        L_0x002a:
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.contrib.android.TensorFlowInferenceInterface.TensorId.parse(java.lang.String):org.tensorflow.contrib.android.TensorFlowInferenceInterface$TensorId");
        }
    }

    public TensorFlowInferenceInterface(android.content.res.AssetManager r5, java.lang.String r6) {
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
        r4 = this;
        r4.<init>();
        r0 = new java.util.ArrayList;
        r0.<init>();
        r4.feedNames = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r4.feedTensors = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r4.fetchNames = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r4.fetchTensors = r0;
        r4.prepareNativeRuntime();
        r4.modelName = r6;
        r0 = new org.tensorflow.Graph;
        r0.<init>();
        r4.f0g = r0;
        r0 = new org.tensorflow.Session;
        r1 = r4.f0g;
        r0.<init>(r1);
        r4.sess = r0;
        r0 = r4.sess;
        r0 = r0.runner();
        r4.runner = r0;
        r0 = "file:///android_asset/";
        r0 = r6.startsWith(r0);
        if (r0 == 0) goto L_0x0050;
    L_0x0044:
        r1 = "file:///android_asset/";	 Catch:{ IOException -> 0x004e }
        r1 = r6.split(r1);	 Catch:{ IOException -> 0x004e }
        r2 = 1;	 Catch:{ IOException -> 0x004e }
        r1 = r1[r2];	 Catch:{ IOException -> 0x004e }
        goto L_0x0051;	 Catch:{ IOException -> 0x004e }
    L_0x004e:
        r5 = move-exception;	 Catch:{ IOException -> 0x004e }
        goto L_0x0056;	 Catch:{ IOException -> 0x004e }
    L_0x0050:
        r1 = r6;	 Catch:{ IOException -> 0x004e }
    L_0x0051:
        r5 = r5.open(r1);	 Catch:{ IOException -> 0x004e }
        goto L_0x007a;
    L_0x0056:
        if (r0 == 0) goto L_0x0074;
    L_0x0058:
        r0 = new java.lang.RuntimeException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Failed to load model from '";
        r1.append(r2);
        r1.append(r6);
        r6 = "'";
        r1.append(r6);
        r6 = r1.toString();
        r0.<init>(r6, r5);
        throw r0;
    L_0x0074:
        r0 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0106 }
        r0.<init>(r6);	 Catch:{ IOException -> 0x0106 }
        r5 = r0;
    L_0x007a:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ IOException -> 0x00e9 }
        r1 = 18;	 Catch:{ IOException -> 0x00e9 }
        if (r0 < r1) goto L_0x008a;	 Catch:{ IOException -> 0x00e9 }
    L_0x0080:
        r0 = "initializeTensorFlow";	 Catch:{ IOException -> 0x00e9 }
        android.os.Trace.beginSection(r0);	 Catch:{ IOException -> 0x00e9 }
        r0 = "readGraphDef";	 Catch:{ IOException -> 0x00e9 }
        android.os.Trace.beginSection(r0);	 Catch:{ IOException -> 0x00e9 }
    L_0x008a:
        r0 = r5.available();	 Catch:{ IOException -> 0x00e9 }
        r0 = new byte[r0];	 Catch:{ IOException -> 0x00e9 }
        r2 = r5.read(r0);	 Catch:{ IOException -> 0x00e9 }
        r3 = r0.length;	 Catch:{ IOException -> 0x00e9 }
        if (r2 == r3) goto L_0x00b7;	 Catch:{ IOException -> 0x00e9 }
    L_0x0097:
        r5 = new java.io.IOException;	 Catch:{ IOException -> 0x00e9 }
        r1 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x00e9 }
        r1.<init>();	 Catch:{ IOException -> 0x00e9 }
        r3 = "read error: read only ";	 Catch:{ IOException -> 0x00e9 }
        r1.append(r3);	 Catch:{ IOException -> 0x00e9 }
        r1.append(r2);	 Catch:{ IOException -> 0x00e9 }
        r2 = " of the graph, expected to read ";	 Catch:{ IOException -> 0x00e9 }
        r1.append(r2);	 Catch:{ IOException -> 0x00e9 }
        r0 = r0.length;	 Catch:{ IOException -> 0x00e9 }
        r1.append(r0);	 Catch:{ IOException -> 0x00e9 }
        r0 = r1.toString();	 Catch:{ IOException -> 0x00e9 }
        r5.<init>(r0);	 Catch:{ IOException -> 0x00e9 }
        throw r5;	 Catch:{ IOException -> 0x00e9 }
    L_0x00b7:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ IOException -> 0x00e9 }
        if (r2 < r1) goto L_0x00be;	 Catch:{ IOException -> 0x00e9 }
    L_0x00bb:
        android.os.Trace.endSection();	 Catch:{ IOException -> 0x00e9 }
    L_0x00be:
        r2 = r4.f0g;	 Catch:{ IOException -> 0x00e9 }
        r4.loadGraph(r0, r2);	 Catch:{ IOException -> 0x00e9 }
        r5.close();	 Catch:{ IOException -> 0x00e9 }
        r5 = "TensorFlowInferenceInterface";	 Catch:{ IOException -> 0x00e9 }
        r0 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x00e9 }
        r0.<init>();	 Catch:{ IOException -> 0x00e9 }
        r2 = "Successfully loaded model from '";	 Catch:{ IOException -> 0x00e9 }
        r0.append(r2);	 Catch:{ IOException -> 0x00e9 }
        r0.append(r6);	 Catch:{ IOException -> 0x00e9 }
        r2 = "'";	 Catch:{ IOException -> 0x00e9 }
        r0.append(r2);	 Catch:{ IOException -> 0x00e9 }
        r0 = r0.toString();	 Catch:{ IOException -> 0x00e9 }
        android.util.Log.i(r5, r0);	 Catch:{ IOException -> 0x00e9 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ IOException -> 0x00e9 }
        if (r5 < r1) goto L_0x00e8;	 Catch:{ IOException -> 0x00e9 }
    L_0x00e5:
        android.os.Trace.endSection();	 Catch:{ IOException -> 0x00e9 }
    L_0x00e8:
        return;
    L_0x00e9:
        r5 = move-exception;
        r0 = new java.lang.RuntimeException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Failed to load model from '";
        r1.append(r2);
        r1.append(r6);
        r6 = "'";
        r1.append(r6);
        r6 = r1.toString();
        r0.<init>(r6, r5);
        throw r0;
    L_0x0106:
        r0 = new java.lang.RuntimeException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Failed to load model from '";
        r1.append(r2);
        r1.append(r6);
        r6 = "'";
        r1.append(r6);
        r6 = r1.toString();
        r0.<init>(r6, r5);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.contrib.android.TensorFlowInferenceInterface.<init>(android.content.res.AssetManager, java.lang.String):void");
    }

    public TensorFlowInferenceInterface(InputStream inputStream) {
        prepareNativeRuntime();
        this.modelName = "";
        this.f0g = new Graph();
        this.sess = new Session(this.f0g);
        this.runner = this.sess.runner();
        try {
            if (VERSION.SDK_INT >= 18) {
                Trace.beginSection("initializeTensorFlow");
                Trace.beginSection("readGraphDef");
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(inputStream.available() > 16384 ? inputStream.available() : 16384);
            byte[] bArr = new byte[16384];
            while (true) {
                int read = inputStream.read(bArr, 0, bArr.length);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            inputStream = byteArrayOutputStream.toByteArray();
            if (VERSION.SDK_INT >= 18) {
                Trace.endSection();
            }
            loadGraph(inputStream, this.f0g);
            Log.i(TAG, "Successfully loaded model from the input stream");
            if (VERSION.SDK_INT >= 18) {
                Trace.endSection();
            }
        } catch (InputStream inputStream2) {
            throw new RuntimeException("Failed to load model from the input stream", inputStream2);
        }
    }

    public TensorFlowInferenceInterface(Graph graph) {
        prepareNativeRuntime();
        this.modelName = "";
        this.f0g = graph;
        this.sess = new Session(graph);
        this.runner = this.sess.runner();
    }

    public void run(String[] strArr) {
        run(strArr, false);
    }

    public void run(String[] strArr, boolean z) {
        closeFetches();
        for (String str : strArr) {
            this.fetchNames.add(str);
            TensorId parse = TensorId.parse(str);
            this.runner.fetch(parse.name, parse.outputIndex);
        }
        if (z) {
            try {
                strArr = this.runner.setOptions(RunStats.runOptions()).runAndFetchMetadata();
                this.fetchTensors = strArr.outputs;
                if (!this.runStats) {
                    this.runStats = new RunStats();
                }
                this.runStats.add(strArr.metadata);
            } catch (String[] strArr2) {
                z = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to run TensorFlow inference with inputs:[");
                stringBuilder.append(TextUtils.join(", ", this.feedNames));
                stringBuilder.append("], outputs:[");
                stringBuilder.append(TextUtils.join(", ", this.fetchNames));
                stringBuilder.append("]");
                Log.e(z, stringBuilder.toString());
                throw strArr2;
            } catch (Throwable th) {
                closeFeeds();
                this.runner = this.sess.runner();
            }
        } else {
            this.fetchTensors = this.runner.run();
        }
        closeFeeds();
        this.runner = this.sess.runner();
    }

    public Graph graph() {
        return this.f0g;
    }

    public Operation graphOperation(String str) {
        Operation operation = this.f0g.operation(str);
        if (operation != null) {
            return operation;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Node '");
        stringBuilder.append(str);
        stringBuilder.append("' does not exist in model '");
        stringBuilder.append(this.modelName);
        stringBuilder.append("'");
        throw new RuntimeException(stringBuilder.toString());
    }

    public String getStatString() {
        return this.runStats == null ? "" : this.runStats.summary();
    }

    public void close() {
        closeFeeds();
        closeFetches();
        this.sess.close();
        this.f0g.close();
        if (this.runStats != null) {
            this.runStats.close();
        }
        this.runStats = null;
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public void feed(String str, boolean[] zArr, long... jArr) {
        byte[] bArr = new byte[zArr.length];
        for (int i = 0; i < zArr.length; i++) {
            bArr[i] = zArr[i];
        }
        addFeed(str, Tensor.create(Boolean.class, jArr, ByteBuffer.wrap(bArr)));
    }

    public void feed(String str, float[] fArr, long... jArr) {
        addFeed(str, Tensor.create(jArr, FloatBuffer.wrap(fArr)));
    }

    public void feed(String str, int[] iArr, long... jArr) {
        addFeed(str, Tensor.create(jArr, IntBuffer.wrap(iArr)));
    }

    public void feed(String str, long[] jArr, long... jArr2) {
        addFeed(str, Tensor.create(jArr2, LongBuffer.wrap(jArr)));
    }

    public void feed(String str, double[] dArr, long... jArr) {
        addFeed(str, Tensor.create(jArr, DoubleBuffer.wrap(dArr)));
    }

    public void feed(String str, byte[] bArr, long... jArr) {
        addFeed(str, Tensor.create(UInt8.class, jArr, ByteBuffer.wrap(bArr)));
    }

    public void feedString(String str, byte[] bArr) {
        addFeed(str, Tensors.create(bArr));
    }

    public void feedString(String str, byte[][] bArr) {
        addFeed(str, Tensors.create(bArr));
    }

    public void feed(String str, FloatBuffer floatBuffer, long... jArr) {
        addFeed(str, Tensor.create(jArr, floatBuffer));
    }

    public void feed(String str, IntBuffer intBuffer, long... jArr) {
        addFeed(str, Tensor.create(jArr, intBuffer));
    }

    public void feed(String str, LongBuffer longBuffer, long... jArr) {
        addFeed(str, Tensor.create(jArr, longBuffer));
    }

    public void feed(String str, DoubleBuffer doubleBuffer, long... jArr) {
        addFeed(str, Tensor.create(jArr, doubleBuffer));
    }

    public void feed(String str, ByteBuffer byteBuffer, long... jArr) {
        addFeed(str, Tensor.create(UInt8.class, jArr, byteBuffer));
    }

    public void fetch(String str, float[] fArr) {
        fetch(str, FloatBuffer.wrap(fArr));
    }

    public void fetch(String str, int[] iArr) {
        fetch(str, IntBuffer.wrap(iArr));
    }

    public void fetch(String str, long[] jArr) {
        fetch(str, LongBuffer.wrap(jArr));
    }

    public void fetch(String str, double[] dArr) {
        fetch(str, DoubleBuffer.wrap(dArr));
    }

    public void fetch(String str, byte[] bArr) {
        fetch(str, ByteBuffer.wrap(bArr));
    }

    public void fetch(String str, FloatBuffer floatBuffer) {
        getTensor(str).writeTo(floatBuffer);
    }

    public void fetch(String str, IntBuffer intBuffer) {
        getTensor(str).writeTo(intBuffer);
    }

    public void fetch(String str, LongBuffer longBuffer) {
        getTensor(str).writeTo(longBuffer);
    }

    public void fetch(String str, DoubleBuffer doubleBuffer) {
        getTensor(str).writeTo(doubleBuffer);
    }

    public void fetch(String str, ByteBuffer byteBuffer) {
        getTensor(str).writeTo(byteBuffer);
    }

    private void prepareNativeRuntime() {
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
        r2 = this;
        r0 = "TensorFlowInferenceInterface";
        r1 = "Checking to see if TensorFlow native methods are already loaded";
        android.util.Log.i(r0, r1);
        r0 = new org.tensorflow.contrib.android.RunStats;	 Catch:{ UnsatisfiedLinkError -> 0x0014 }
        r0.<init>();	 Catch:{ UnsatisfiedLinkError -> 0x0014 }
        r0 = "TensorFlowInferenceInterface";	 Catch:{ UnsatisfiedLinkError -> 0x0014 }
        r1 = "TensorFlow native methods already loaded";	 Catch:{ UnsatisfiedLinkError -> 0x0014 }
        android.util.Log.i(r0, r1);	 Catch:{ UnsatisfiedLinkError -> 0x0014 }
        goto L_0x0027;
    L_0x0014:
        r0 = "TensorFlowInferenceInterface";
        r1 = "TensorFlow native methods not found, attempting to load via tensorflow_inference";
        android.util.Log.i(r0, r1);
        r0 = "tensorflow_inference";	 Catch:{ UnsatisfiedLinkError -> 0x0028 }
        java.lang.System.loadLibrary(r0);	 Catch:{ UnsatisfiedLinkError -> 0x0028 }
        r0 = "TensorFlowInferenceInterface";	 Catch:{ UnsatisfiedLinkError -> 0x0028 }
        r1 = "Successfully loaded TensorFlow native methods (RunStats error may be ignored)";	 Catch:{ UnsatisfiedLinkError -> 0x0028 }
        android.util.Log.i(r0, r1);	 Catch:{ UnsatisfiedLinkError -> 0x0028 }
    L_0x0027:
        return;
    L_0x0028:
        r0 = new java.lang.RuntimeException;
        r1 = "Native TF methods not found; check that the correct native libraries are present in the APK.";
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.contrib.android.TensorFlowInferenceInterface.prepareNativeRuntime():void");
    }

    private void loadGraph(byte[] bArr, Graph graph) throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        if (VERSION.SDK_INT >= 18) {
            Trace.beginSection("importGraphDef");
        }
        try {
            graph.importGraphDef(bArr);
            if (VERSION.SDK_INT >= 18) {
                Trace.endSection();
            }
            bArr = System.currentTimeMillis();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Model load took ");
            stringBuilder.append(bArr - currentTimeMillis);
            stringBuilder.append("ms, TensorFlow version: ");
            stringBuilder.append(TensorFlow.version());
            Log.i(str, stringBuilder.toString());
        } catch (byte[] bArr2) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Not a valid TensorFlow Graph serialization: ");
            stringBuilder2.append(bArr2.getMessage());
            throw new IOException(stringBuilder2.toString());
        }
    }

    private void addFeed(String str, Tensor<?> tensor) {
        TensorId parse = TensorId.parse(str);
        this.runner.feed(parse.name, parse.outputIndex, tensor);
        this.feedNames.add(str);
        this.feedTensors.add(tensor);
    }

    private Tensor<?> getTensor(String str) {
        int i = 0;
        for (String equals : this.fetchNames) {
            if (equals.equals(str)) {
                return (Tensor) this.fetchTensors.get(i);
            }
            i++;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Node '");
        stringBuilder.append(str);
        stringBuilder.append("' was not provided to run(), so it cannot be read");
        throw new RuntimeException(stringBuilder.toString());
    }

    private void closeFeeds() {
        for (Tensor close : this.feedTensors) {
            close.close();
        }
        this.feedTensors.clear();
        this.feedNames.clear();
    }

    private void closeFetches() {
        for (Tensor close : this.fetchTensors) {
            close.close();
        }
        this.fetchTensors.clear();
        this.fetchNames.clear();
    }
}
