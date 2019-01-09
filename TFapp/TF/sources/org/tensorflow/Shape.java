package org.tensorflow;

import java.util.Arrays;

public final class Shape {
    private long[] shape;

    public static Shape unknown() {
        return new Shape(null);
    }

    public static Shape scalar() {
        return new Shape(new long[0]);
    }

    public static Shape make(long j, long... jArr) {
        Object obj = new long[(jArr.length + 1)];
        obj[0] = j;
        System.arraycopy(jArr, 0, obj, 1, jArr.length);
        return new Shape(obj);
    }

    public int numDimensions() {
        return this.shape == null ? -1 : this.shape.length;
    }

    public long size(int i) {
        return this.shape[i];
    }

    public int hashCode() {
        return Arrays.hashCode(this.shape);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof Shape) && Arrays.equals(this.shape, ((Shape) obj).shape)) {
            return hasUnknownDimension() ^ 1;
        }
        return super.equals(obj);
    }

    public String toString() {
        if (this.shape == null) {
            return "<unknown>";
        }
        return Arrays.toString(this.shape).replace("-1", "?");
    }

    Shape(long[] jArr) {
        this.shape = jArr;
    }

    long[] asArray() {
        return this.shape;
    }

    private boolean hasUnknownDimension() {
        if (this.shape == null) {
            return true;
        }
        for (long j : this.shape) {
            if (j == -1) {
                return true;
            }
        }
        return false;
    }
}
