package org.tensorflow;

import java.util.Iterator;

public final class Graph implements AutoCloseable {
    private long nativeHandle;
    private final Object nativeHandleLock;
    private int refcount;

    private static final class OperationIterator implements Iterator<Operation> {
        private final Graph graph;
        private Operation operation = null;
        private int position = null;

        OperationIterator(Graph graph) {
            this.graph = graph;
            advance();
        }

        private final void advance() {
            Reference ref = this.graph.ref();
            this.operation = null;
            try {
                long[] access$400 = Graph.nextOperation(ref.nativeHandle(), this.position);
                if (!(access$400 == null || access$400[0] == 0)) {
                    this.operation = new Operation(this.graph, access$400[0]);
                    this.position = (int) access$400[1];
                }
                ref.close();
            } catch (Throwable th) {
                ref.close();
            }
        }

        public boolean hasNext() {
            return this.operation != null;
        }

        public Operation next() {
            Operation operation = this.operation;
            advance();
            return operation;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() is unsupported.");
        }
    }

    class Reference implements AutoCloseable {
        private boolean active;

        private Reference() {
            synchronized (Graph.this.nativeHandleLock) {
                this.active = Graph.this.nativeHandle != 0;
                if (this.active) {
                    this.active = true;
                    Graph.this.refcount = Graph.this.refcount + 1;
                } else {
                    throw new IllegalStateException("close() has been called on the Graph");
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void close() {
            /*
            r2 = this;
            r0 = org.tensorflow.Graph.this;
            r0 = r0.nativeHandleLock;
            monitor-enter(r0);
            r1 = r2.active;	 Catch:{ all -> 0x0023 }
            if (r1 != 0) goto L_0x000d;
        L_0x000b:
            monitor-exit(r0);	 Catch:{ all -> 0x0023 }
            return;
        L_0x000d:
            r1 = 0;
            r2.active = r1;	 Catch:{ all -> 0x0023 }
            r1 = org.tensorflow.Graph.this;	 Catch:{ all -> 0x0023 }
            r1 = org.tensorflow.Graph.access$206(r1);	 Catch:{ all -> 0x0023 }
            if (r1 != 0) goto L_0x0021;
        L_0x0018:
            r1 = org.tensorflow.Graph.this;	 Catch:{ all -> 0x0023 }
            r1 = r1.nativeHandleLock;	 Catch:{ all -> 0x0023 }
            r1.notifyAll();	 Catch:{ all -> 0x0023 }
        L_0x0021:
            monitor-exit(r0);	 Catch:{ all -> 0x0023 }
            return;
        L_0x0023:
            r1 = move-exception;
            monitor-exit(r0);	 Catch:{ all -> 0x0023 }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.Graph.Reference.close():void");
        }

        public long nativeHandle() {
            long access$100;
            synchronized (Graph.this.nativeHandleLock) {
                access$100 = this.active ? Graph.this.nativeHandle : 0;
            }
            return access$100;
        }
    }

    private static native long allocate();

    private static native void delete(long j);

    private static native void importGraphDef(long j, byte[] bArr, String str) throws IllegalArgumentException;

    private static native long[] nextOperation(long j, int i);

    private static native long operation(long j, String str);

    private static native byte[] toGraphDef(long j);

    static /* synthetic */ int access$206(Graph graph) {
        int i = graph.refcount - 1;
        graph.refcount = i;
        return i;
    }

    public Graph() {
        this.nativeHandleLock = new Object();
        this.refcount = 0;
        this.nativeHandle = allocate();
    }

    Graph(long j) {
        this.nativeHandleLock = new Object();
        this.refcount = 0;
        this.nativeHandle = j;
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/32863545.run(Unknown Source)
*/
        /*
        r5 = this;
        r0 = r5.nativeHandleLock;
        monitor-enter(r0);
        r1 = r5.nativeHandle;	 Catch:{ all -> 0x0029 }
        r3 = 0;	 Catch:{ all -> 0x0029 }
        r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ all -> 0x0029 }
        if (r1 != 0) goto L_0x000d;	 Catch:{ all -> 0x0029 }
    L_0x000b:
        monitor-exit(r0);	 Catch:{ all -> 0x0029 }
        return;	 Catch:{ all -> 0x0029 }
    L_0x000d:
        r1 = r5.refcount;	 Catch:{ all -> 0x0029 }
        if (r1 <= 0) goto L_0x0020;
    L_0x0011:
        r1 = r5.nativeHandleLock;	 Catch:{ InterruptedException -> 0x0017 }
        r1.wait();	 Catch:{ InterruptedException -> 0x0017 }
        goto L_0x000d;
    L_0x0017:
        r1 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0029 }
        r1.interrupt();	 Catch:{ all -> 0x0029 }
        monitor-exit(r0);	 Catch:{ all -> 0x0029 }
        return;	 Catch:{ all -> 0x0029 }
    L_0x0020:
        r1 = r5.nativeHandle;	 Catch:{ all -> 0x0029 }
        delete(r1);	 Catch:{ all -> 0x0029 }
        r5.nativeHandle = r3;	 Catch:{ all -> 0x0029 }
        monitor-exit(r0);	 Catch:{ all -> 0x0029 }
        return;	 Catch:{ all -> 0x0029 }
    L_0x0029:
        r1 = move-exception;	 Catch:{ all -> 0x0029 }
        monitor-exit(r0);	 Catch:{ all -> 0x0029 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.Graph.close():void");
    }

    public Operation operation(String str) {
        synchronized (this.nativeHandleLock) {
            long operation = operation(this.nativeHandle, str);
            if (operation == 0) {
                return null;
            }
            str = new Operation(this, operation);
            return str;
        }
    }

    public Iterator<Operation> operations() {
        return new OperationIterator(this);
    }

    public OperationBuilder opBuilder(String str, String str2) {
        return new OperationBuilder(this, str, str2);
    }

    public void importGraphDef(byte[] bArr) throws IllegalArgumentException {
        importGraphDef(bArr, "");
    }

    public void importGraphDef(byte[] bArr, String str) throws IllegalArgumentException {
        if (bArr != null) {
            if (str != null) {
                synchronized (this.nativeHandleLock) {
                    importGraphDef(this.nativeHandle, bArr, str);
                }
                return;
            }
        }
        throw new IllegalArgumentException("graphDef and prefix cannot be null");
    }

    public byte[] toGraphDef() {
        byte[] toGraphDef;
        synchronized (this.nativeHandleLock) {
            toGraphDef = toGraphDef(this.nativeHandle);
        }
        return toGraphDef;
    }

    Reference ref() {
        return new Reference();
    }

    static {
        TensorFlow.init();
    }
}
