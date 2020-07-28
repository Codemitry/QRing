package com.codemitry.scanme.barcode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.mlkit.vision.barcode.Barcode;


public class BarcodeFoundGraphic extends BarcodeGraphicBase {
    private int RECT_COLOR = Color.RED;
    private float strokeWidth = 4.0f;
    private Paint rectPaint;
    private Rect rect;
    private com.codemitry.scanme.barcode.GraphicOverlay graphicOverlay;

    public BarcodeFoundGraphic(GraphicOverlay graphicOverlay, Barcode barcode) {
        super(graphicOverlay);
        this.graphicOverlay = graphicOverlay;
        this.rect = barcode.getBoundingBox();
        rectPaint = new Paint();
        rectPaint.setColor(RECT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(strokeWidth);
        postInvalidate();
    }


    @Override
    public void draw(Canvas canvas) {
        RectF rectF = new RectF(rect);
        rectF.left = translateX(rectF.left);
        rectF.right = translateX(rectF.right);
        rectF.top = translateY(rectF.top);
        rectF.bottom = translateY(rectF.bottom);
        canvas.drawRect(rectF, rectPaint);
    }
}