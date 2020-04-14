package com.example.mlkit_barcode_ocr;


import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.mlkit_barcode_ocr.utility.GraphicOverlay;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextGraphicDecrypted extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.RED;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private String decrypted;
    private FirebaseVisionText.TextBlock text;

    TextGraphicDecrypted(GraphicOverlay overlay, FirebaseVisionText.TextBlock text, String decrypted) {
        super(overlay);

        this.decrypted = decrypted;
        this.text = text;
        //this.text = text;

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);
    }



    /** Draws the text block annotations for position, size, and raw value on the supplied canvas. */
    @Override
    public void draw(Canvas canvas) {
        if (decrypted == null) {
            throw new IllegalStateException("Attempting to draw a null text.");
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, rectPaint);

        // Renders the text at the bottom of the box.
        canvas.drawText(decrypted, rect.left, rect.bottom, textPaint);
    }
}