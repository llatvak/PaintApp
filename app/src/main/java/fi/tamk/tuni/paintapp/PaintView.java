package fi.tamk.tuni.paintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PaintView extends View {

    private Path mPath;
    private Paint mDrawPaint, mCanvasPaint;
    private int mPaintColor = 0xFF660000;
    private Canvas mDrawCanvas;
    private Bitmap mCanvasBitmap;

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializePaintView();
    }

    public void initializePaintView() {
        mPath = new Path();
        mDrawPaint = new Paint();
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(20);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mPath, mDrawPaint);
    }
}
