package org.tensorflow;

import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.HashMap;

public final class Tensor<T> implements AutoCloseable {
    private static HashMap<Class<?>, DataType> classDataTypes = new HashMap();
    private DataType dtype;
    private long nativeHandle;
    private long[] shapeCopy = null;

    private static native long allocate(int i, long[] jArr, long j);

    private static native long allocateNonScalarBytes(long[] jArr, Object[] objArr);

    private static native long allocateScalarBytes(byte[] bArr);

    private static native ByteBuffer buffer(long j);

    private static native void delete(long j);

    private static native int dtype(long j);

    private static native void readNDArray(long j, Object obj);

    private static native boolean scalarBoolean(long j);

    private static native byte[] scalarBytes(long j);

    private static native double scalarDouble(long j);

    private static native float scalarFloat(long j);

    private static native int scalarInt(long j);

    private static native long scalarLong(long j);

    private static native void setValue(long j, Object obj);

    private static native long[] shape(long j);

    public static <T> Tensor<T> create(Object obj, Class<T> cls) {
        DataType fromClass = DataType.fromClass(cls);
        if (objectCompatWithType(obj, fromClass)) {
            return create(obj, fromClass);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DataType of object does not match T (expected ");
        stringBuilder.append(fromClass);
        stringBuilder.append(", got ");
        stringBuilder.append(dataTypeOf(obj));
        stringBuilder.append(")");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static Tensor<?> create(Object obj) {
        return create(obj, dataTypeOf(obj));
    }

    private static Tensor<?> create(Object obj, DataType dataType) {
        Tensor<?> tensor = new Tensor(dataType);
        tensor.shapeCopy = new long[numDimensions(obj, dataType)];
        fillShape(obj, 0, tensor.shapeCopy);
        if (tensor.dtype != DataType.STRING) {
            tensor.nativeHandle = allocate(tensor.dtype.m0c(), tensor.shapeCopy, (long) (elemByteSize(tensor.dtype) * numElements(tensor.shapeCopy)));
            setValue(tensor.nativeHandle, obj);
        } else if (tensor.shapeCopy.length != null) {
            tensor.nativeHandle = allocateNonScalarBytes(tensor.shapeCopy, (Object[]) obj);
        } else {
            tensor.nativeHandle = allocateScalarBytes((byte[]) obj);
        }
        return tensor;
    }

    public static Tensor<Integer> create(long[] jArr, IntBuffer intBuffer) {
        jArr = allocateForBuffer(DataType.INT32, jArr, intBuffer.remaining());
        jArr.buffer().asIntBuffer().put(intBuffer);
        return jArr;
    }

    public static Tensor<Float> create(long[] jArr, FloatBuffer floatBuffer) {
        jArr = allocateForBuffer(DataType.FLOAT, jArr, floatBuffer.remaining());
        jArr.buffer().asFloatBuffer().put(floatBuffer);
        return jArr;
    }

    public static Tensor<Double> create(long[] jArr, DoubleBuffer doubleBuffer) {
        jArr = allocateForBuffer(DataType.DOUBLE, jArr, doubleBuffer.remaining());
        jArr.buffer().asDoubleBuffer().put(doubleBuffer);
        return jArr;
    }

    public static Tensor<Long> create(long[] jArr, LongBuffer longBuffer) {
        jArr = allocateForBuffer(DataType.INT64, jArr, longBuffer.remaining());
        jArr.buffer().asLongBuffer().put(longBuffer);
        return jArr;
    }

    public static <T> Tensor<T> create(Class<T> cls, long[] jArr, ByteBuffer byteBuffer) {
        return create(DataType.fromClass(cls), jArr, byteBuffer);
    }

    private static Tensor<?> create(DataType dataType, long[] jArr, ByteBuffer byteBuffer) {
        int remaining;
        if (dataType != DataType.STRING) {
            int elemByteSize = elemByteSize(dataType);
            if (byteBuffer.remaining() % elemByteSize != 0) {
                throw new IllegalArgumentException(String.format("ByteBuffer with %d bytes is not compatible with a %s Tensor (%d bytes/element)", new Object[]{Integer.valueOf(byteBuffer.remaining()), dataType.toString(), Integer.valueOf(elemByteSize)}));
            }
            remaining = byteBuffer.remaining() / elemByteSize;
        } else {
            remaining = byteBuffer.remaining();
        }
        dataType = allocateForBuffer(dataType, jArr, remaining);
        dataType.buffer().put(byteBuffer);
        return dataType;
    }

    public <U> Tensor<U> expect(Class<U> cls) {
        cls = DataType.fromClass(cls);
        if (cls.equals(this.dtype)) {
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot cast from tensor of ");
        stringBuilder.append(this.dtype);
        stringBuilder.append(" to tensor of ");
        stringBuilder.append(cls);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private static <T> Tensor<T> allocateForBuffer(DataType dataType, long[] jArr, int i) {
        int numElements = numElements(jArr);
        if (dataType != DataType.STRING) {
            if (i != numElements) {
                throw incompatibleBuffer(i, jArr);
            }
            i = elemByteSize(dataType) * numElements;
        }
        Tensor<T> tensor = new Tensor(dataType);
        tensor.shapeCopy = Arrays.copyOf(jArr, jArr.length);
        tensor.nativeHandle = allocate(tensor.dtype.m0c(), tensor.shapeCopy, (long) i);
        return tensor;
    }

    public void close() {
        if (this.nativeHandle != 0) {
            delete(this.nativeHandle);
            this.nativeHandle = 0;
        }
    }

    public DataType dataType() {
        return this.dtype;
    }

    public int numDimensions() {
        return this.shapeCopy.length;
    }

    public int numBytes() {
        return buffer().remaining();
    }

    public int numElements() {
        return numElements(this.shapeCopy);
    }

    public long[] shape() {
        return this.shapeCopy;
    }

    public float floatValue() {
        return scalarFloat(this.nativeHandle);
    }

    public double doubleValue() {
        return scalarDouble(this.nativeHandle);
    }

    public int intValue() {
        return scalarInt(this.nativeHandle);
    }

    public long longValue() {
        return scalarLong(this.nativeHandle);
    }

    public boolean booleanValue() {
        return scalarBoolean(this.nativeHandle);
    }

    public byte[] bytesValue() {
        return scalarBytes(this.nativeHandle);
    }

    public <U> U copyTo(U u) {
        throwExceptionIfTypeIsIncompatible(u);
        readNDArray(this.nativeHandle, u);
        return u;
    }

    public void writeTo(IntBuffer intBuffer) {
        if (this.dtype != DataType.INT32) {
            throw incompatibleBuffer((Buffer) intBuffer, this.dtype);
        }
        intBuffer.put(buffer().asIntBuffer());
    }

    public void writeTo(FloatBuffer floatBuffer) {
        if (this.dtype != DataType.FLOAT) {
            throw incompatibleBuffer((Buffer) floatBuffer, this.dtype);
        }
        floatBuffer.put(buffer().asFloatBuffer());
    }

    public void writeTo(DoubleBuffer doubleBuffer) {
        if (this.dtype != DataType.DOUBLE) {
            throw incompatibleBuffer((Buffer) doubleBuffer, this.dtype);
        }
        doubleBuffer.put(buffer().asDoubleBuffer());
    }

    public void writeTo(LongBuffer longBuffer) {
        if (this.dtype != DataType.INT64) {
            throw incompatibleBuffer((Buffer) longBuffer, this.dtype);
        }
        longBuffer.put(buffer().asLongBuffer());
    }

    public void writeTo(ByteBuffer byteBuffer) {
        byteBuffer.put(buffer());
    }

    public String toString() {
        return String.format("%s tensor with shape %s", new Object[]{this.dtype.toString(), Arrays.toString(shape())});
    }

    static Tensor<?> fromHandle(long j) {
        Tensor<?> tensor = new Tensor(DataType.fromC(dtype(j)));
        tensor.shapeCopy = shape(j);
        tensor.nativeHandle = j;
        return tensor;
    }

    long getNativeHandle() {
        return this.nativeHandle;
    }

    private Tensor(DataType dataType) {
        this.dtype = dataType;
    }

    private ByteBuffer buffer() {
        return buffer(this.nativeHandle).order(ByteOrder.nativeOrder());
    }

    private static IllegalArgumentException incompatibleBuffer(Buffer buffer, DataType dataType) {
        return new IllegalArgumentException(String.format("cannot use %s with Tensor of type %s", new Object[]{buffer.getClass().getName(), dataType}));
    }

    private static IllegalArgumentException incompatibleBuffer(int i, long[] jArr) {
        return new IllegalArgumentException(String.format("buffer with %d elements is not compatible with a Tensor with shape %s", new Object[]{Integer.valueOf(i), Arrays.toString(jArr)}));
    }

    private static int numElements(long[] jArr) {
        int i = 1;
        for (long j : jArr) {
            i *= (int) j;
        }
        return i;
    }

    private static int elemByteSize(DataType dataType) {
        switch (dataType) {
            case FLOAT:
            case INT32:
                return 4;
            case DOUBLE:
            case INT64:
                return 8;
            case BOOL:
            case UINT8:
                return 1;
            case STRING:
                throw new IllegalArgumentException("STRING tensors do not have a fixed element size");
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("DataType ");
                stringBuilder.append(dataType);
                stringBuilder.append(" is not supported yet");
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static void throwExceptionIfNotByteOfByteArrays(Object obj) {
        if (obj.getClass().getName().equals("[[B") == null) {
            throw new IllegalArgumentException("object cannot be converted to a Tensor as it includes an array with null elements");
        }
    }

    static {
        classDataTypes.put(Integer.TYPE, DataType.INT32);
        classDataTypes.put(Integer.class, DataType.INT32);
        classDataTypes.put(Long.TYPE, DataType.INT64);
        classDataTypes.put(Long.class, DataType.INT64);
        classDataTypes.put(Float.TYPE, DataType.FLOAT);
        classDataTypes.put(Float.class, DataType.FLOAT);
        classDataTypes.put(Double.TYPE, DataType.DOUBLE);
        classDataTypes.put(Double.class, DataType.DOUBLE);
        classDataTypes.put(Byte.TYPE, DataType.STRING);
        classDataTypes.put(Byte.class, DataType.STRING);
        classDataTypes.put(Boolean.TYPE, DataType.BOOL);
        classDataTypes.put(Boolean.class, DataType.BOOL);
        TensorFlow.init();
    }

    private static Class<?> baseObjType(Object obj) {
        obj = obj.getClass();
        while (obj.isArray()) {
            obj = obj.getComponentType();
        }
        return obj;
    }

    private static DataType dataTypeOf(Object obj) {
        return dataTypeFromClass(baseObjType(obj));
    }

    private static DataType dataTypeFromClass(Class<?> cls) {
        DataType dataType = (DataType) classDataTypes.get(cls);
        if (dataType != null) {
            return dataType;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("cannot create Tensors of type ");
        stringBuilder.append(cls.getName());
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private static int numDimensions(Object obj, DataType dataType) {
        obj = numArrayDimensions(obj);
        return (dataType != DataType.STRING || obj <= null) ? obj : obj - 1;
    }

    private static int numArrayDimensions(Object obj) {
        obj = obj.getClass();
        int i = 0;
        while (obj.isArray()) {
            obj = obj.getComponentType();
            i++;
        }
        return i;
    }

    private static void fillShape(Object obj, int i, long[] jArr) {
        if (jArr != null) {
            if (i != jArr.length) {
                int length = Array.getLength(obj);
                if (length == 0) {
                    throw new IllegalArgumentException("cannot create Tensors with a 0 dimension");
                }
                int i2 = 0;
                if (jArr[i] == 0) {
                    jArr[i] = (long) length;
                } else if (jArr[i] != ((long) length)) {
                    throw new IllegalArgumentException(String.format("mismatched lengths (%d and %d) in dimension %d", new Object[]{Long.valueOf(jArr[i]), Integer.valueOf(length), Integer.valueOf(i)}));
                }
                while (i2 < length) {
                    fillShape(Array.get(obj, i2), i + 1, jArr);
                    i2++;
                }
            }
        }
    }

    private static boolean objectCompatWithType(Object obj, DataType dataType) {
        Class baseObjType = baseObjType(obj);
        DataType dataTypeFromClass = dataTypeFromClass(baseObjType);
        obj = numDimensions(obj, dataTypeFromClass);
        if (!baseObjType.isPrimitive() && baseObjType != String.class && obj != null) {
            throw new IllegalArgumentException("cannot create non-scalar Tensors from arrays of boxed values");
        } else if (dataTypeFromClass.equals(dataType) != null) {
            return true;
        } else {
            if (dataTypeFromClass == DataType.STRING && dataType == DataType.UINT8) {
                return true;
            }
            return null;
        }
    }

    private void throwExceptionIfTypeIsIncompatible(Object obj) {
        int numDimensions = numDimensions();
        if (numDimensions(obj, this.dtype) != numDimensions) {
            throw new IllegalArgumentException(String.format("cannot copy Tensor with %d dimensions into an object with %d", new Object[]{Integer.valueOf(numDimensions), Integer.valueOf(numDimensions(obj, this.dtype))}));
        } else if (objectCompatWithType(obj, this.dtype)) {
            long[] jArr = new long[numDimensions];
            fillShape(obj, 0, jArr);
            for (obj = null; obj < jArr.length; obj++) {
                if (jArr[obj] != shape()[obj]) {
                    throw new IllegalArgumentException(String.format("cannot copy Tensor with shape %s into object with shape %s", new Object[]{Arrays.toString(shape()), Arrays.toString(jArr)}));
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("cannot copy Tensor with DataType %s into an object of type %s", new Object[]{this.dtype.toString(), obj.getClass().getName()}));
        }
    }
}
