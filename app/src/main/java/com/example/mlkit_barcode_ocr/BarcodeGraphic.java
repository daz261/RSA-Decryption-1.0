package com.example.mlkit_barcode_ocr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.example.mlkit_barcode_ocr.utility.GraphicOverlay;

/** Graphic instance for rendering Barcode position and content information in an overlay view. */
public class BarcodeGraphic extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.RED;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint rectPaint;
    private final Paint barcodePaint;
    private final FirebaseVisionBarcode barcode;
    String private_key;
    String encrypted;
    String decrypted;


    BarcodeGraphic(GraphicOverlay overlay, FirebaseVisionBarcode barcode) {
        super(overlay);

        this.barcode = barcode;

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        barcodePaint = new Paint();
        barcodePaint.setColor(TEXT_COLOR);
        barcodePaint.setTextSize(TEXT_SIZE);
    }

    /**
     * Draws the barcode block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        if (barcode == null) {
            throw new IllegalStateException("Attempting to draw a null barcode.");
        }

        // Draws the bounding box around the BarcodeBlock.
        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, rectPaint);

        //set up encrypted text
        encrypted = barcode.getRawValue();
        //decrypt text
        try {
            decrypted = RSA.decrypt(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Draws a label at the bottom of the barcode indicate the barcode value that was detected.

        if (decrypted != null) {
                canvas.drawText(decrypted, rect.left, rect.bottom, barcodePaint);
        } else {
                canvas.drawText(barcode.getRawValue(), rect.left, rect.bottom, barcodePaint);
        }

        // Renders the barcode at the bottom of the box.
       // canvas.drawText(barcode.getRawValue(), rect.left, rect.bottom, barcodePaint);
    }
}
