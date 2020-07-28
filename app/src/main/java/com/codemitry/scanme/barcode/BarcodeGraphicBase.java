package com.codemitry.scanme.barcode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

import com.codemitry.scanme.R;

class BarcodeGraphicBase extends GraphicOverlay.Graphic {

    RectF boxRect;
    int boxCornerRadius = 30;

    Paint scrimPaint = new Paint();

    {
        scrimPaint.setColor(ContextCompat.getColor(context, R.color.scrimColor));
    }

    Paint eraserPaint = new Paint();

    {
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    Paint boxPaint = new Paint();

    {
        boxPaint.setColor(ContextCompat.getColor(context, R.color.barcode_reticle_stroke));
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.barcode_reticle_stroke_width));
    }

    Paint pathPaint = new Paint();

    {
        pathPaint.setColor(Color.WHITE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(boxPaint.getStrokeWidth());
        pathPaint.setPathEffect(new CornerPathEffect(boxCornerRadius));
    }

    public BarcodeGraphicBase(GraphicOverlay overlay) {
        super(overlay);

        boxRect = new RectF(overlay.getLeft() + overlay.getWidth() / 8f,
                overlay.getTop() + overlay.getHeight() / 4f, overlay.getRight() - overlay.getWidth() / 8f, overlay.getBottom() - overlay.getHeight() / 4f);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), scrimPaint);

        eraserPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint);

        eraserPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint);

        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, boxPaint);
    }
}
