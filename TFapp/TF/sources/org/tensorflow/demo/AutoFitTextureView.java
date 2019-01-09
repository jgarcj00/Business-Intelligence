package org.tensorflow.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View.MeasureSpec;

public class AutoFitTextureView extends TextureView {
    private int ratioHeight;
    private int ratioWidth;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.ratioWidth = 0;
        this.ratioHeight = 0;
    }

    public void setAspectRatio(int i, int i2) {
        if (i >= 0) {
            if (i2 >= 0) {
                this.ratioWidth = i;
                this.ratioHeight = i2;
                requestLayout();
                return;
            }
        }
        throw new IllegalArgumentException("Size cannot be negative.");
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        i = MeasureSpec.getSize(i);
        i2 = MeasureSpec.getSize(i2);
        if (this.ratioWidth != 0) {
            if (this.ratioHeight != 0) {
                if (i < (this.ratioWidth * i2) / this.ratioHeight) {
                    setMeasuredDimension(i, (this.ratioHeight * i) / this.ratioWidth);
                    return;
                } else {
                    setMeasuredDimension((this.ratioWidth * i2) / this.ratioHeight, i2);
                    return;
                }
            }
        }
        setMeasuredDimension(i, i2);
    }
}
