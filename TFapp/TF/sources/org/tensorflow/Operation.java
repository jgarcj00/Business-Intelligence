package org.tensorflow;

public final class Operation {
    private final Graph graph;
    private final long unsafeNativeHandle;

    private static native int dtype(long j, long j2, int i);

    private static native int inputListLength(long j, String str);

    private static native String name(long j);

    private static native int numOutputs(long j);

    private static native int outputListLength(long j, String str);

    private static native long[] shape(long j, long j2, int i);

    private static native String type(long j);

    Operation(Graph graph, long j) {
        this.graph = graph;
        this.unsafeNativeHandle = j;
    }

    public String name() {
        Reference ref = this.graph.ref();
        try {
            String name = name(this.unsafeNativeHandle);
            return name;
        } finally {
            ref.close();
        }
    }

    public String type() {
        Reference ref = this.graph.ref();
        try {
            String type = type(this.unsafeNativeHandle);
            return type;
        } finally {
            ref.close();
        }
    }

    public int numOutputs() {
        Reference ref = this.graph.ref();
        try {
            int numOutputs = numOutputs(this.unsafeNativeHandle);
            return numOutputs;
        } finally {
            ref.close();
        }
    }

    public int outputListLength(String str) {
        Reference ref = this.graph.ref();
        try {
            str = outputListLength(this.unsafeNativeHandle, str);
            return str;
        } finally {
            ref.close();
        }
    }

    public Output<?>[] outputList(int i, int i2) {
        Output<?>[] outputArr = new Output[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            outputArr[i3] = output(i + i3);
        }
        return outputArr;
    }

    public <T> Output<T> output(int i) {
        return new Output(this, i);
    }

    public int hashCode() {
        return Long.valueOf(this.unsafeNativeHandle).hashCode();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Operation)) {
            return false;
        }
        Operation operation = (Operation) obj;
        if (this.graph != operation.graph) {
            return false;
        }
        Reference ref = this.graph.ref();
        try {
            if (this.unsafeNativeHandle != operation.unsafeNativeHandle) {
                z = false;
            }
            ref.close();
            return z;
        } catch (Throwable th) {
            ref.close();
        }
    }

    public String toString() {
        return String.format("<%s '%s'>", new Object[]{type(), name()});
    }

    public int inputListLength(String str) {
        Reference ref = this.graph.ref();
        try {
            str = inputListLength(this.unsafeNativeHandle, str);
            return str;
        } finally {
            ref.close();
        }
    }

    long getUnsafeNativeHandle() {
        return this.unsafeNativeHandle;
    }

    long[] shape(int i) {
        Reference ref = this.graph.ref();
        try {
            i = shape(ref.nativeHandle(), this.unsafeNativeHandle, i);
            return i;
        } finally {
            ref.close();
        }
    }

    DataType dtype(int i) {
        Reference ref = this.graph.ref();
        try {
            i = DataType.fromC(dtype(ref.nativeHandle(), this.unsafeNativeHandle, i));
            return i;
        } finally {
            ref.close();
        }
    }
}
