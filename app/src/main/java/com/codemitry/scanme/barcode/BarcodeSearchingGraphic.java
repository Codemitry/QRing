package com.codemitry.scanme.barcode;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.mlkit.vision.barcode.Barcode;

class BarcodeSearchingGraphic extends BarcodeGraphicBase {

    private PointF[] boxClickwiseCoordinates;

    private Point[] coordinateOffsetBits = {
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0),
            new Point(0, -1),
    };

    private PointF lastPathPoint = new PointF();

    private ValueAnimator searchingAnimation;

    private RectF barcodeRect;

    public BarcodeSearchingGraphic(GraphicOverlay overlay, ValueAnimator searchingAnimation, Barcode barcode) {
        super(overlay);

        this.searchingAnimation = searchingAnimation;
//        this.barcodeRect = new RectF(barcode.getBoundingBox());

        Rect notScaledRect = barcode.getBoundingBox();

        RectF scaledRect = new RectF(
                notScaledRect.left * overlay.mWidthScaleFactor,
                notScaledRect.top * overlay.mHeightScaleFactor,
                notScaledRect.right * overlay.mWidthScaleFactor,
                notScaledRect.bottom * overlay.mHeightScaleFactor);

        int paddingWidth = (int) scaledRect.width() / 4;
        int paddingHeight = (int) scaledRect.height() / 4;

        this.barcodeRect = new RectF(
                scaledRect.left - paddingWidth,
                scaledRect.top - paddingHeight,
                scaledRect.right + paddingWidth,
                scaledRect.bottom + paddingHeight
        );

        boxRect.left = barcodeRect.left;
        boxRect.right = barcodeRect.right;
        boxRect.top = barcodeRect.top;
        boxRect.bottom = barcodeRect.bottom;


        boxClickwiseCoordinates = new PointF[]{
                new PointF(boxRect.left, barcodeRect.top),
                new PointF(boxRect.right, barcodeRect.top),
                new PointF(boxRect.right, barcodeRect.bottom),
                new PointF(boxRect.left, barcodeRect.bottom),
        };
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float boxPerimeter = (barcodeRect.width() + barcodeRect.height()) * 2;

        Path path = new Path();

        float offsetLen = boxPerimeter * (float) searchingAnimation.getAnimatedValue() % boxPerimeter;
        int i = 0;
        while (i < 4) {
            double edgeLen = (i % 2 == 0) ? barcodeRect.width() : barcodeRect.height();
            if (offsetLen < edgeLen) {
                lastPathPoint.x = boxClickwiseCoordinates[i].x + coordinateOffsetBits[i].x * offsetLen;
                lastPathPoint.y = boxClickwiseCoordinates[i].y + coordinateOffsetBits[i].y * offsetLen;

                path.moveTo(lastPathPoint.x, lastPathPoint.y);
                break;
            }

            offsetLen -= edgeLen;
            i++;
        }

        float pathLen = boxPerimeter * 0.3f;
        for (int j = 0; j < 4; j++) {
            int index = (i + j) % 4;
            int nextIndex = (i + j + 1) % 4;

            float lineLen = Math.abs(boxClickwiseCoordinates[nextIndex].x - lastPathPoint.x) +
                    Math.abs(boxClickwiseCoordinates[nextIndex].y - lastPathPoint.y);
            if (lineLen > pathLen) {
                path.lineTo(
                        lastPathPoint.x + pathLen * coordinateOffsetBits[index].x,
                        lastPathPoint.y + pathLen * coordinateOffsetBits[index].y
                );
                break;
            }

            lastPathPoint.x = boxClickwiseCoordinates[nextIndex].x;
            lastPathPoint.y = boxClickwiseCoordinates[nextIndex].y;
            path.lineTo(lastPathPoint.x, lastPathPoint.y);
            pathLen -= lineLen;
        }
        canvas.drawPath(path, pathPaint);

    }
}
