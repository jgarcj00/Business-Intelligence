package org.tensorflow.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.List;
import org.tensorflow.demo.Classifier.Recognition;

public class RecognitionScoreView extends View implements ResultsView {
    private static final float TEXT_SIZE_DIP = 24.0f;
    private final Paint bgPaint;
    private final Paint fgPaint = new Paint();
    private List<Recognition> results;
    private final float textSizePx = TypedValue.applyDimension(1, TEXT_SIZE_DIP, getResources().getDisplayMetrics());

    public RecognitionScoreView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.fgPaint.setTextSize(this.textSizePx);
        this.bgPaint = new Paint();
        this.bgPaint.setColor(-868055564);
    }

    public void setResults(List<Recognition> list) {
        this.results = list;
        postInvalidate();
    }

    public void onDraw(Canvas canvas) {
        int textSize = (int) (this.fgPaint.getTextSize() * 1.5f);
        canvas.drawPaint(this.bgPaint);
        if (this.results != null) {
            for (Recognition recognition : this.results) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(recognition.getTitle());
                stringBuilder.append(": ");
                stringBuilder.append(recognition.getConfidence());
                float f = (float) textSize;
                canvas.drawText(stringBuilder.toString(), 10.0f, f, this.fgPaint);
                textSize = (int) (f + (this.fgPaint.getTextSize() * 1.5f));
            }
        }
    }
}
