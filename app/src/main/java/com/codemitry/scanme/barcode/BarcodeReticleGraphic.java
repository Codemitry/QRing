package com.codemitry.scanme.barcode;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

import com.codemitry.scanme.R;

class BarcodeReticleGraphic extends BarcodeGraphicBase {

    private CameraReticleAnimator animator;
    private Paint ripplePaint;
    private int rippleSizeOffset;
    private int rippleStrokeWidth;
    private int rippleAlpha;

    private Resources resources;

    public BarcodeReticleGraphic(GraphicOverlay overlay, CameraReticleAnimator animator) {
        super(overlay);

        this.animator = animator;
        this.overlay = overlay;
        this.resources = overlay.getResources();
        this.ripplePaint = new Paint();
        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setColor(ContextCompat.getColor(context, R.color.reticle_ripple));
        rippleSizeOffset = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_size_offset);
        rippleStrokeWidth = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_stroke_width);
        rippleAlpha = ripplePaint.getAlpha();

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        ripplePaint.setAlpha((int) (rippleAlpha * animator.rippleAlphaScale));
        ripplePaint.setStrokeWidth((float) (rippleStrokeWidth * animator.rippleStrokeWidthScale));

        int offset = (int) (rippleSizeOffset * animator.rippleSizeScale);
        RectF rippleRect = new RectF(
                boxRect.left - offset,
                boxRect.top - offset,
                boxRect.right + offset,
                boxRect.bottom + offset
        );
        canvas.drawRoundRect(rippleRect, boxCornerRadius, boxCornerRadius, ripplePaint);
    }
}
