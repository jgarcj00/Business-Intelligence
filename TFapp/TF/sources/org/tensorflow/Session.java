package org.tensorflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Session implements AutoCloseable {
    private final Graph graph;
    private final Reference graphRef;
    private long nativeHandle;
    private final Object nativeHandleLock;
    private int numActiveRuns;

    public static final class Run {
        public byte[] metadata;
        public List<Tensor<?>> outputs;
    }

    public final class Runner {
        private ArrayList<Tensor<?>> inputTensors = new ArrayList();
        private ArrayList<Output<?>> inputs = new ArrayList();
        private ArrayList<Output<?>> outputs = new ArrayList();
        private byte[] runOptions = null;
        private ArrayList<Operation> targets = new ArrayList();

        private class Reference implements AutoCloseable {
            public Reference() {
                synchronized (Session.this.nativeHandleLock) {
                    if (Session.this.nativeHandle == 0) {
                        throw new IllegalStateException("run() cannot be called on the Session after close()");
                    }
                    Session.access$304(Session.this);
                }
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void close() {
                /*
                r5 = this;
                r0 = org.tensorflow.Session.Runner.this;
                r0 = org.tensorflow.Session.this;
                r0 = r0.nativeHandleLock;
                monitor-enter(r0);
                r1 = org.tensorflow.Session.Runner.this;	 Catch:{ all -> 0x0030 }
                r1 = org.tensorflow.Session.this;	 Catch:{ all -> 0x0030 }
                r1 = r1.nativeHandle;	 Catch:{ all -> 0x0030 }
                r3 = 0;
                r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
                if (r1 != 0) goto L_0x0019;
            L_0x0017:
                monitor-exit(r0);	 Catch:{ all -> 0x0030 }
                return;
            L_0x0019:
                r1 = org.tensorflow.Session.Runner.this;	 Catch:{ all -> 0x0030 }
                r1 = org.tensorflow.Session.this;	 Catch:{ all -> 0x0030 }
                r1 = org.tensorflow.Session.access$306(r1);	 Catch:{ all -> 0x0030 }
                if (r1 != 0) goto L_0x002e;
            L_0x0023:
                r1 = org.tensorflow.Session.Runner.this;	 Catch:{ all -> 0x0030 }
                r1 = org.tensorflow.Session.this;	 Catch:{ all -> 0x0030 }
                r1 = r1.nativeHandleLock;	 Catch:{ all -> 0x0030 }
                r1.notifyAll();	 Catch:{ all -> 0x0030 }
            L_0x002e:
                monitor-exit(r0);	 Catch:{ all -> 0x0030 }
                return;
            L_0x0030:
                r1 = move-exception;
                monitor-exit(r0);	 Catch:{ all -> 0x0030 }
                throw r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.Session.Runner.Reference.close():void");
            }
        }

        public Runner feed(String str, Tensor<?> tensor) {
            return feed(parseOutput(str), (Tensor) tensor);
        }

        public Runner feed(String str, int i, Tensor<?> tensor) {
            str = operationByName(str);
            if (str != null) {
                this.inputs.add(str.output(i));
                this.inputTensors.add(tensor);
            }
            return this;
        }

        public Runner feed(Output<?> output, Tensor<?> tensor) {
            this.inputs.add(output);
            this.inputTensors.add(tensor);
            return this;
        }

        public Runner fetch(String str) {
            return fetch(parseOutput(str));
        }

        public Runner fetch(String str, int i) {
            str = operationByName(str);
            if (str != null) {
                this.outputs.add(str.output(i));
            }
            return this;
        }

        public Runner fetch(Output<?> output) {
            this.outputs.add(output);
            return this;
        }

        public Runner addTarget(String str) {
            str = operationByName(str);
            if (str != null) {
                this.targets.add(str);
            }
            return this;
        }

        public Runner addTarget(Operation operation) {
            this.targets.add(operation);
            return this;
        }

        public Runner setOptions(byte[] bArr) {
            this.runOptions = bArr;
            return this;
        }

        public List<Tensor<?>> run() {
            return runHelper(false).outputs;
        }

        public Run runAndFetchMetadata() {
            return runHelper(true);
        }

        private Run runHelper(boolean z) {
            long[] jArr = new long[this.inputTensors.size()];
            long[] jArr2 = new long[this.inputs.size()];
            int[] iArr = new int[this.inputs.size()];
            long[] jArr3 = new long[this.outputs.size()];
            int[] iArr2 = new int[this.outputs.size()];
            long[] jArr4 = new long[this.targets.size()];
            long[] jArr5 = new long[this.outputs.size()];
            Iterator it = this.inputTensors.iterator();
            int i = 0;
            int i2 = 0;
            while (it.hasNext()) {
                int i3 = i2 + 1;
                jArr[i2] = ((Tensor) it.next()).getNativeHandle();
                i2 = i3;
            }
            it = r1.inputs.iterator();
            i2 = 0;
            while (it.hasNext()) {
                Output output = (Output) it.next();
                jArr2[i2] = output.op().getUnsafeNativeHandle();
                iArr[i2] = output.index();
                i2++;
            }
            it = r1.outputs.iterator();
            i2 = 0;
            while (it.hasNext()) {
                output = (Output) it.next();
                jArr3[i2] = output.op().getUnsafeNativeHandle();
                iArr2[i2] = output.index();
                i2++;
            }
            it = r1.targets.iterator();
            i2 = 0;
            while (it.hasNext()) {
                i3 = i2 + 1;
                jArr4[i2] = ((Operation) it.next()).getUnsafeNativeHandle();
                i2 = i3;
            }
            Reference reference = new Reference();
            try {
                byte[] access$100 = Session.run(Session.this.nativeHandle, r1.runOptions, jArr, jArr2, iArr, jArr3, iArr2, jArr4, z, jArr5);
                reference.close();
                List<Tensor> arrayList = new ArrayList();
                int length = jArr5.length;
                while (i < length) {
                    try {
                        arrayList.add(Tensor.fromHandle(jArr5[i]));
                        i++;
                    } catch (Exception e) {
                        Exception exception = e;
                        for (Tensor close : arrayList) {
                            close.close();
                        }
                        arrayList.clear();
                        throw exception;
                    }
                }
                Run run = new Run();
                run.outputs = arrayList;
                run.metadata = access$100;
                return run;
            } catch (Throwable th) {
                Throwable th2 = th;
                reference.close();
            }
        }

        private Operation operationByName(String str) {
            Operation operation = Session.this.graph.operation(str);
            if (operation != null) {
                return operation;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No Operation named [");
            stringBuilder.append(str);
            stringBuilder.append("] in the Graph");
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        private org.tensorflow.Output<?> parseOutput(java.lang.String r5) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/32863545.run(Unknown Source)
*/
            /*
            r4 = this;
            r0 = 58;
            r0 = r5.lastIndexOf(r0);
            r1 = 0;
            r2 = -1;
            if (r0 == r2) goto L_0x0035;
        L_0x000a:
            r2 = r5.length();
            r2 = r2 + -1;
            if (r0 != r2) goto L_0x0013;
        L_0x0012:
            goto L_0x0035;
        L_0x0013:
            r2 = r5.substring(r1, r0);	 Catch:{ NumberFormatException -> 0x002b }
            r0 = r0 + 1;	 Catch:{ NumberFormatException -> 0x002b }
            r0 = r5.substring(r0);	 Catch:{ NumberFormatException -> 0x002b }
            r0 = java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x002b }
            r3 = new org.tensorflow.Output;	 Catch:{ NumberFormatException -> 0x002b }
            r2 = r4.operationByName(r2);	 Catch:{ NumberFormatException -> 0x002b }
            r3.<init>(r2, r0);	 Catch:{ NumberFormatException -> 0x002b }
            return r3;
        L_0x002b:
            r0 = new org.tensorflow.Output;
            r5 = r4.operationByName(r5);
            r0.<init>(r5, r1);
            return r0;
        L_0x0035:
            r0 = new org.tensorflow.Output;
            r5 = r4.operationByName(r5);
            r0.<init>(r5, r1);
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.Session.Runner.parseOutput(java.lang.String):org.tensorflow.Output<?>");
        }
    }

    private static native long allocate(long j);

    private static native long allocate2(long j, String str, byte[] bArr);

    private static native void delete(long j);

    private static native byte[] run(long j, byte[] bArr, long[] jArr, long[] jArr2, int[] iArr, long[] jArr3, int[] iArr2, long[] jArr4, boolean z, long[] jArr5);

    static /* synthetic */ int access$304(Session session) {
        int i = session.numActiveRuns + 1;
        session.numActiveRuns = i;
        return i;
    }

    static /* synthetic */ int access$306(Session session) {
        int i = session.numActiveRuns - 1;
        session.numActiveRuns = i;
        return i;
    }

    public Session(Graph graph) {
        this(graph, null);
    }

    public Session(Graph graph, byte[] bArr) {
        long allocate;
        this.nativeHandleLock = new Object();
        this.graph = graph;
        Reference ref = graph.ref();
        if (bArr == null) {
            try {
                allocate = allocate(ref.nativeHandle());
            } catch (Throwable th) {
                ref.close();
            }
        } else {
            allocate = allocate2(ref.nativeHandle(), null, bArr);
        }
        this.nativeHandle = allocate;
        this.graphRef = graph.ref();
        ref.close();
    }

    Session(Graph graph, long j) {
        this.nativeHandleLock = new Object();
        this.graph = graph;
        this.nativeHandle = j;
        this.graphRef = graph.ref();
    }

    public void close() {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/32863545.run(Unknown Source)
*/
        /*
        r5 = this;
        r0 = r5.graphRef;
        r0.close();
        r0 = r5.nativeHandleLock;
        monitor-enter(r0);
        r1 = r5.nativeHandle;	 Catch:{ all -> 0x002e }
        r3 = 0;	 Catch:{ all -> 0x002e }
        r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ all -> 0x002e }
        if (r1 != 0) goto L_0x0012;	 Catch:{ all -> 0x002e }
    L_0x0010:
        monitor-exit(r0);	 Catch:{ all -> 0x002e }
        return;	 Catch:{ all -> 0x002e }
    L_0x0012:
        r1 = r5.numActiveRuns;	 Catch:{ all -> 0x002e }
        if (r1 <= 0) goto L_0x0025;
    L_0x0016:
        r1 = r5.nativeHandleLock;	 Catch:{ InterruptedException -> 0x001c }
        r1.wait();	 Catch:{ InterruptedException -> 0x001c }
        goto L_0x0012;
    L_0x001c:
        r1 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x002e }
        r1.interrupt();	 Catch:{ all -> 0x002e }
        monitor-exit(r0);	 Catch:{ all -> 0x002e }
        return;	 Catch:{ all -> 0x002e }
    L_0x0025:
        r1 = r5.nativeHandle;	 Catch:{ all -> 0x002e }
        delete(r1);	 Catch:{ all -> 0x002e }
        r5.nativeHandle = r3;	 Catch:{ all -> 0x002e }
        monitor-exit(r0);	 Catch:{ all -> 0x002e }
        return;	 Catch:{ all -> 0x002e }
    L_0x002e:
        r1 = move-exception;	 Catch:{ all -> 0x002e }
        monitor-exit(r0);	 Catch:{ all -> 0x002e }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.Session.close():void");
    }

    public Runner runner() {
        return new Runner();
    }
}
