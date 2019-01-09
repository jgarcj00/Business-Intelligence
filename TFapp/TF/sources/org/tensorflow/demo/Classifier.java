package org.tensorflow.demo;

import android.graphics.Bitmap;
import android.graphics.RectF;
import java.util.List;

public interface Classifier {

    public static class Recognition {
        private final Float confidence;
        private final String id;
        private RectF location;
        private final String title;

        public Recognition(String str, String str2, Float f, RectF rectF) {
            this.id = str;
            this.title = str2;
            this.confidence = f;
            this.location = rectF;
        }

        public String getId() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public Float getConfidence() {
            return this.confidence;
        }

        public RectF getLocation() {
            return new RectF(this.location);
        }

        public void setLocation(RectF rectF) {
            this.location = rectF;
        }

        public String toString() {
            String str = "";
            if (this.id != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("[");
                stringBuilder.append(this.id);
                stringBuilder.append("] ");
                str = stringBuilder.toString();
            }
            if (this.title != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(this.title);
                stringBuilder.append(" ");
                str = stringBuilder.toString();
            }
            if (this.confidence != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(String.format("(%.1f%%) ", new Object[]{Float.valueOf(this.confidence.floatValue() * 100.0f)}));
                str = stringBuilder.toString();
            }
            if (this.location != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(this.location);
                stringBuilder.append(" ");
                str = stringBuilder.toString();
            }
            return str.trim();
        }
    }

    void close();

    void enableStatLogging(boolean z);

    String getStatString();

    List<Recognition> recognizeImage(Bitmap bitmap);
}
