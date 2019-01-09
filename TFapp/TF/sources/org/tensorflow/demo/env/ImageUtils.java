package org.tensorflow.demo.env;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.media.Image.Plane;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import junit.framework.Assert;

public class ImageUtils {
    private static final Logger LOGGER = new Logger();
    static final int kMaxChannelValue = 262143;

    public static int getYUVByteSize(int i, int i2) {
        return (i * i2) + ((((i + 1) / 2) * ((i2 + 1) / 2)) * 2);
    }

    public static void saveBitmap(Bitmap bitmap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        stringBuilder.append(File.separator);
        stringBuilder.append("tensorflow");
        LOGGER.m5i("Saving %dx%d bitmap to %s.", Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight()), stringBuilder.toString());
        File file = new File(r0);
        if (!file.mkdirs()) {
            LOGGER.m5i("Make dir failed", new Object[0]);
        }
        File file2 = new File(file, "preview.png");
        if (file2.exists()) {
            file2.delete();
        }
        try {
            OutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(CompressFormat.PNG, 99, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Bitmap bitmap2) {
            LOGGER.m4e(bitmap2, "Exception!", new Object[0]);
        }
    }

    public static int[] convertImageToBitmap(Image image, int[] iArr, byte[][] bArr) {
        if (bArr == null || bArr.length != 3) {
            bArr = new byte[3][];
        }
        Plane[] planes = image.getPlanes();
        fillBytes(planes, bArr);
        convertYUV420ToARGB8888(bArr[0], bArr[1], bArr[2], image.getWidth(), image.getHeight(), planes[0].getRowStride(), planes[1].getRowStride(), planes[1].getPixelStride(), iArr);
        return iArr;
    }

    public static void convertYUV420ToARGB8888(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        int i6 = i2;
        int i7 = 0;
        int i8 = 0;
        while (i7 < i6) {
            int i9 = i3 * i7;
            int i10 = (i7 >> 1) * i4;
            int i11 = i8;
            int i12 = 0;
            i8 = i;
            while (i12 < i8) {
                int i13 = i11 + 1;
                int i14 = ((i12 >> 1) * i5) + i10;
                iArr[i11] = YUV2RGB(convertByteToInt(bArr, i9 + i12), convertByteToInt(bArr2, i14), convertByteToInt(bArr3, i14));
                i12++;
                i11 = i13;
                i6 = i2;
            }
            byte[] bArr4 = bArr;
            byte[] bArr5 = bArr2;
            byte[] bArr6 = bArr3;
            i7++;
            i8 = i11;
            i6 = i2;
        }
    }

    private static int convertByteToInt(byte[] bArr, int i) {
        return bArr[i] & 255;
    }

    private static int YUV2RGB(int i, int i2, int i3) {
        i -= 16;
        i2 -= 128;
        i3 -= 128;
        if (i < 0) {
            i = 0;
        }
        int i4 = 1192 * i;
        i = (1634 * i3) + i4;
        i3 = (i4 - (833 * i3)) - (400 * i2);
        return (((((Math.min(kMaxChannelValue, Math.max(0, i)) >> 10) & 255) << 16) | -16777216) | (((Math.min(kMaxChannelValue, Math.max(0, i3)) >> 10) & 255) << 8)) | ((Math.min(kMaxChannelValue, Math.max(0, i4 + (2066 * i2))) >> 10) & 255);
    }

    private static void fillBytes(Plane[] planeArr, byte[][] bArr) {
        int i = 0;
        while (i < planeArr.length) {
            ByteBuffer buffer = planeArr[i].getBuffer();
            if (bArr[i] == null || bArr[i].length != buffer.capacity()) {
                bArr[i] = new byte[buffer.capacity()];
            }
            buffer.get(bArr[i]);
            i++;
        }
    }

    public static void cropAndRescaleBitmap(Bitmap bitmap, Bitmap bitmap2, int i) {
        Assert.assertEquals(bitmap2.getWidth(), bitmap2.getHeight());
        float min = (float) Math.min(bitmap.getWidth(), bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.preTranslate(-Math.max(0.0f, (((float) bitmap.getWidth()) - min) / 2.0f), -Math.max(0.0f, (((float) bitmap.getHeight()) - min) / 2.0f));
        float height = ((float) bitmap2.getHeight()) / min;
        matrix.postScale(height, height);
        if (i != 0) {
            matrix.postTranslate(((float) (-bitmap2.getWidth())) / 2.0f, ((float) (-bitmap2.getHeight())) / 2.0f);
            matrix.postRotate((float) i);
            matrix.postTranslate(((float) bitmap2.getWidth()) / 1073741824, ((float) bitmap2.getHeight()) / 2.0f);
        }
        new Canvas(bitmap2).drawBitmap(bitmap, matrix, null);
    }

    public static Matrix getTransformationMatrix(int i, int i2, int i3, int i4, int i5, boolean z) {
        Matrix matrix = new Matrix();
        if (i5 != 0) {
            matrix.postTranslate(((float) (-i)) / 2.0f, ((float) (-i2)) / 2.0f);
            matrix.postRotate((float) i5);
        }
        Object obj = (Math.abs(i5) + 90) % 180 == 0 ? 1 : null;
        int i6 = obj != null ? i2 : i;
        if (obj == null) {
            i = i2;
        }
        if (!(i6 == i3 && i == i4)) {
            i2 = ((float) i3) / ((float) i6);
            float f = ((float) i4) / ((float) i);
            if (z) {
                i = Math.max(i2, f);
                matrix.postScale(i, i);
            } else {
                matrix.postScale(i2, f);
            }
        }
        if (i5 != 0) {
            matrix.postTranslate(((float) i3) / 1073741824, ((float) i4) / 1073741824);
        }
        return matrix;
    }
}
