package com.example.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class RoutingCircle extends View {
    private Paint paint;
    private Path path;

    public RoutingCircle(Context context) {
        super(context);
        path = new Path();
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    public Paint getPaint() {
        return paint;
    }

    public Path getPath() {
        return path;
    }
}
