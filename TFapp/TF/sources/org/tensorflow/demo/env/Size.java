package org.tensorflow.demo.env;

import android.graphics.Bitmap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Size implements Comparable<Size>, Serializable {
    public static final long serialVersionUID = 7689808733290872361L;
    public final int height;
    public final int width;

    public Size(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public Size(Bitmap bitmap) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    public static Size getRotatedSize(Size size, int i) {
        return i % 180 != 0 ? new Size(size.height, size.width) : size;
    }

    public static org.tensorflow.demo.env.Size parseFromString(java.lang.String r3) {
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
        r0 = android.text.TextUtils.isEmpty(r3);
        r1 = 0;
        if (r0 == 0) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        r3 = r3.trim();
        r0 = "x";
        r3 = r3.split(r0);
        r0 = r3.length;
        r2 = 2;
        if (r0 != r2) goto L_0x002b;
    L_0x0016:
        r0 = 0;
        r0 = r3[r0];	 Catch:{ NumberFormatException -> 0x002a }
        r0 = java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x002a }
        r2 = 1;	 Catch:{ NumberFormatException -> 0x002a }
        r3 = r3[r2];	 Catch:{ NumberFormatException -> 0x002a }
        r3 = java.lang.Integer.parseInt(r3);	 Catch:{ NumberFormatException -> 0x002a }
        r2 = new org.tensorflow.demo.env.Size;	 Catch:{ NumberFormatException -> 0x002a }
        r2.<init>(r0, r3);	 Catch:{ NumberFormatException -> 0x002a }
        return r2;
    L_0x002a:
        return r1;
    L_0x002b:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.demo.env.Size.parseFromString(java.lang.String):org.tensorflow.demo.env.Size");
    }

    public static List<Size> sizeStringToList(String str) {
        List<Size> arrayList = new ArrayList();
        if (str != null) {
            for (String parseFromString : str.split(",")) {
                Size parseFromString2 = parseFromString(parseFromString);
                if (parseFromString2 != null) {
                    arrayList.add(parseFromString2);
                }
            }
        }
        return arrayList;
    }

    public static String sizeListToString(List<Size> list) {
        String str = "";
        if (list != null && list.size() > 0) {
            str = ((Size) list.get(0)).toString();
            for (int i = 1; i < list.size(); i++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(",");
                stringBuilder.append(((Size) list.get(i)).toString());
                str = stringBuilder.toString();
            }
        }
        return str;
    }

    public final float aspectRatio() {
        return ((float) this.width) / ((float) this.height);
    }

    public int compareTo(Size size) {
        return (this.width * this.height) - (size.width * size.height);
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null || !(obj instanceof Size)) {
            return false;
        }
        Size size = (Size) obj;
        if (this.width == size.width && this.height == size.height) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return (this.width * 32713) + this.height;
    }

    public String toString() {
        return dimensionsAsString(this.width, this.height);
    }

    public static final String dimensionsAsString(int i, int i2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append("x");
        stringBuilder.append(i2);
        return stringBuilder.toString();
    }
}
