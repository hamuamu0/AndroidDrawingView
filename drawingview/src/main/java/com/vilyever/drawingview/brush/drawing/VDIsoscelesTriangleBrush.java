package com.vilyever.drawingview.brush.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.vilyever.drawingview.model.VDDrawingPath;
import com.vilyever.drawingview.model.VDDrawingPoint;

/**
 * VDIsoscelesTriangleBrush
 * AndroidDrawingView <com.vilyever.drawingview.brush>
 * Created by vilyever on 2015/10/21.
 * Feature:
 */
public class VDIsoscelesTriangleBrush extends VDShapeBrush {
    final VDIsoscelesTriangleBrush self = this;


    /* #Constructors */
    public VDIsoscelesTriangleBrush() {

    }

    public VDIsoscelesTriangleBrush(float size, int color) {
        this(size, color, FillType.Hollow);
    }

    public VDIsoscelesTriangleBrush(float size, int color, FillType fillType) {
        this(size, color, fillType, false);
    }

    public VDIsoscelesTriangleBrush(float size, int color, FillType fillType, boolean edgeRounded) {
        super(size, color, fillType, edgeRounded);
    }

    /* #Overrides */
    @Override
    public Paint getPaint() {
        Paint paint = super.getPaint();
        paint.setStrokeMiter(Integer.MAX_VALUE);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        return paint;
    }

    @Override
    public RectF drawPath(Canvas canvas, @NonNull VDDrawingPath drawingPath, DrawingPointerState state) {
        if (drawingPath.getPoints().size() > 1) {
            VDDrawingPoint beginPoint = drawingPath.getPoints().get(0);
            VDDrawingPoint lastPoint = drawingPath.getPoints().get(drawingPath.getPoints().size() - 1);

            RectF drawingRect = new RectF();
            drawingRect.left = Math.min(beginPoint.x, lastPoint.x);
            drawingRect.top = Math.min(beginPoint.y, lastPoint.y);
            drawingRect.right = Math.max(beginPoint.x, lastPoint.x);
            drawingRect.bottom = Math.max(beginPoint.y, lastPoint.y);

            if ((drawingRect.right - drawingRect.left) < self.getSize() * 2.0f
                    || (drawingRect.bottom - drawingRect.top) < self.getSize() * 2.0f) {
                return null;
            }

//            锐角距差计算，改用新算法
//            pathFrame = new RectF(drawingRect);
//
//            // 计算相似三角形比例
//            double x = pathFrame.right - pathFrame.left; // 底边
//            double h = pathFrame.bottom - pathFrame.top; // 高
//            double y = Math.sqrt((x / 2.0f) * (x / 2.0f) + h * h); // 斜边
//            double sin = (x / 2.0f) / y; // 顶角角度一半的sin值
//            double factor = (h + (self.getSize() / 2.0f) * (1 / sin + 1)) / h; // 相似比
//
//            pathFrame.left -= x * (factor - 1) / 2.0f;
//            pathFrame.top -= h * (factor - 1) - self.getSize() / 2.0f;
//            pathFrame.right += x * (factor - 1) / 2.0f;
//
//            pathFrame.bottom += self.getSize() / 2.0f;

            double w = self.getSize() / 2.0; // 内外间距
            double x = drawingRect.right - drawingRect.left; // 底边
            double h = drawingRect.bottom - drawingRect.top; // 高
            double a = Math.atan(x / 2.0 / h) * 2.0; // 顶角
            double b = (Math.PI - a) / 2.0; // 底角
            double dy = w / Math.sin(a / 2.0); // y差值
            double dx = w / Math.tan(b / 2.0); // x差值

            RectF outerRect = new RectF(drawingRect);
            outerRect.left -= dx;
            outerRect.top -= dy;
            outerRect.right += dx;
            outerRect.bottom += self.getSize() / 2.0f;

            RectF innerRect = new RectF(drawingRect);
            innerRect.left += dx;
            innerRect.top += dy;
            innerRect.right -= dx;
            innerRect.bottom -= self.getSize() / 2.0f;

            RectF pathFrame;
            if (!self.isEdgeRounded()) {
                pathFrame = new RectF(outerRect);
            }
            else {
                pathFrame = super.drawPath(canvas, drawingPath, state);
            }

            if (state == DrawingPointerState.ForceFinishFetchFrame) {
                return pathFrame;
            }
            else if (state == DrawingPointerState.FetchFrame || canvas == null) {
                return pathFrame;
            }

            Path path = new Path();
            path.moveTo(outerRect.left, outerRect.bottom);
            path.lineTo(outerRect.right, outerRect.bottom);
            path.lineTo((outerRect.left + outerRect.right) / 2.0f, outerRect.top);
            path.lineTo(outerRect.left, outerRect.bottom);

            if (self.getFillType() == FillType.Hollow) {
                path.lineTo(innerRect.left, innerRect.bottom);
                path.lineTo((innerRect.left + innerRect.right) / 2.0f, innerRect.top);
                path.lineTo(innerRect.right, innerRect.bottom);
                path.lineTo(innerRect.left, innerRect.bottom);

                path.lineTo(outerRect.left, outerRect.bottom);
            }

            if (state == DrawingPointerState.CalibrateToOrigin
                    || state == DrawingPointerState.ForceCalibrateToOrigin) {
                path.offset(-pathFrame.left, -pathFrame.top);
            }

            canvas.drawPath(path, self.getPaint());

            return pathFrame;
        }

        return null;
    }

    /* #Accessors */
     
    /* #Delegates */     
     
    /* #Private Methods */    
    
    /* #Public Methods */
    public static VDIsoscelesTriangleBrush defaultBrush() {
        return new VDIsoscelesTriangleBrush(5, Color.BLACK);
    }

    /* #Classes */

    /* #Interfaces */     
     
    /* #Annotations @interface */    
    
    /* #Enums */
}