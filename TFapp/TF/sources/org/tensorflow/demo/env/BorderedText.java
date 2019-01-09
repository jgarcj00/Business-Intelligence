package org.tensorflow.demo.env;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import java.util.Iterator;
import java.util.Vector;

public class BorderedText {
    private final Paint exteriorPaint;
    private final Paint interiorPaint;
    private final float textSize;

    public BorderedText(float f) {
        this(-1, -16777216, f);
    }

    public BorderedText(int i, int i2, float f) {
        this.interiorPaint = new Paint();
        this.interiorPaint.setTextSize(f);
        this.interiorPaint.setColor(i);
        this.interiorPaint.setStyle(Style.FILL);
        this.interiorPaint.setAntiAlias(false);
        this.interiorPaint.setAlpha(255);
        this.exteriorPaint = new Paint();
        this.exteriorPaint.setTextSize(f);
        this.exteriorPaint.setColor(i2);
        this.exteriorPaint.setStyle(Style.FILL_AND_STROKE);
        this.exteriorPaint.setStrokeWidth(f / 1090519040);
        this.exteriorPaint.setAntiAlias(false);
        this.exteriorPaint.setAlpha(255);
        this.textSize = f;
    }

    public void setTypeface(Typeface typeface) {
        this.interiorPaint.setTypeface(typeface);
        this.exteriorPaint.setTypeface(typeface);
    }

    public void drawText(Canvas canvas, float f, float f2, String str) {
        canvas.drawText(str, f, f2, this.exteriorPaint);
        canvas.drawText(str, f, f2, this.interiorPaint);
    }

    public void drawLines(Canvas canvas, float f, float f2, Vector<String> vector) {
        Iterator it = vector.iterator();
        int i = 0;
        while (it.hasNext()) {
            drawText(canvas, f, f2 - (getTextSize() * ((float) ((vector.size() - i) - 1))), (String) it.next());
            i++;
        }
    }

    public void setInteriorColor(int i) {
        this.interiorPaint.setColor(i);
    }

    public void setExteriorColor(int i) {
        this.exteriorPaint.setColor(i);
    }

    public float getTextSize() {
        return this.textSize;
    }

    public void setAlpha(int i) {
        this.interiorPaint.setAlpha(i);
        this.exteriorPaint.setAlpha(i);
    }

    public void getTextBounds(String str, int i, int i2, Rect rect) {
        this.interiorPaint.getTextBounds(str, i, i2, rect);
    }

    public void setTextAlign(Align align) {
        this.interiorPaint.setTextAlign(align);
        this.exteriorPaint.setTextAlign(align);
    }
}
