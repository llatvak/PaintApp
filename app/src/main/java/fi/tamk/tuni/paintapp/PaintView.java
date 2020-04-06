package fi.tamk.tuni.paintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class PaintView extends View {

    private Path mPath;
    private Paint mDrawPaint, mCanvasPaint;
    private int mPaintColor = Color.parseColor("#000000");
    private Canvas mDrawCanvas;
    private Bitmap mCanvasBitmap;
    private float mBrushSize, mLastBrushSize;
    private boolean mEraseMode = false;
    private MaskFilter mBlur;
    private boolean mBlurMode = false;
    private int backgroundColor = Color.WHITE;
    private ArrayList<DrawPath> paths = new ArrayList<>();
    private ArrayList<DrawPath> undo = new ArrayList<>();
    float mX = 0;
    float mY = 0;
    boolean redoOrUndoPressed = false;
    private int previousColor;
    private float mPreviousBrushSize;

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //setLayerType(LAYER_TYPE_SOFTWARE, null);
        initializePaintView();
    }

    public void initializePaintView() {
        mPath = new Path();
        mDrawPaint = new Paint();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mBrushSize = getResources().getInteger(R.integer.small_size);
        mLastBrushSize = mBrushSize;
        mBlur = new BlurMaskFilter(mBrushSize, BlurMaskFilter.Blur.NORMAL);
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!redoOrUndoPressed) {
            canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
            canvas.drawPath(mPath, mDrawPaint);
        }

        if(redoOrUndoPressed) {
            canvas.save();
            mDrawCanvas.drawColor(backgroundColor);
            for (DrawPath dp : paths) {
                mDrawPaint.setColor(dp.color);
                mDrawPaint.setStrokeWidth(dp.strokeWidth);
                mDrawCanvas.drawPath(dp.path, mDrawPaint);
            }
            canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
            canvas.restore();
            redoOrUndoPressed = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                undo.clear();
                mPath.reset();

                    if(mPaintColor != 0 && !mEraseMode) {
                        previousColor = mPaintColor;
                        mPreviousBrushSize = mBrushSize;
                        System.out.println("Ennen " + mPaintColor);
                        String hexColor = String.format("#%06X", (0xFFFFFF & previousColor));
                        setColor(hexColor);
                        mBrushSize = mPreviousBrushSize;
                        System.out.println("Jälkeen " + mPaintColor);
                    } else {
                        mDrawPaint.setColor(mPaintColor);
                        mDrawPaint.setStrokeWidth(mBrushSize);
                    }

                mPath.moveTo(touchX, touchY);
                mX = touchX;
                mY = touchY;
                invalidate();

                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(touchX - mX);
                float dy = Math.abs(touchY - mY);
                if (dx >= 4 || dy >= 4) {
                    mPath.quadTo(mX, mY, (touchX + mX) / 2, (touchY + mY) / 2);
                    mX = touchX;
                    mY = touchY;
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX, mY);
                mDrawCanvas.drawPath(mPath, mDrawPaint);
                DrawPath d = new DrawPath(mPaintColor, (int)mBrushSize, mPath);
                paths.add(d);
                mPath = new Path();
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    public void setColor(String newColor) {
        this.mPaintColor = Color.parseColor(newColor);
        if(mPaintColor != 0 && !mEraseMode) {
            previousColor = mPaintColor;
            mPreviousBrushSize = mBrushSize;
            mDrawPaint.setColor(mPaintColor);
            mDrawPaint.setStrokeWidth(mBrushSize);
        } else {
            mDrawPaint.setColor(mPaintColor);
            mDrawPaint.setStrokeWidth(mBrushSize);
        }
        invalidate();
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        this.mBrushSize = pixelAmount;
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    public void setLastBrushSize(float lastSize) {
        this.mLastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return mLastBrushSize;
    }

    public void setEraseMode(boolean isErase) {
        mEraseMode = isErase;
        if(mEraseMode) {
            if(mPaintColor != Color.WHITE) {
                setColor("#FFFFFF");
            }
        } else {
            System.out.println("PREV ERASE " + previousColor);
            System.out.println("CURR ERASE " + mPaintColor);
            mPaintColor = previousColor;
            mDrawPaint.setColor(mPaintColor);
        }
    }

    public void startNew() {
        paths.clear();
        undo.clear();
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void removeRecentPath() {
        if(paths.size() > 0) {
            undo.add(paths.remove(paths.size() - 1));
            redoOrUndoPressed = true;
            invalidate();
        }
    }

    public void redoRecentPath() {
        if(undo.size() > 0) {
            paths.add(undo.remove(undo.size() - 1));
            redoOrUndoPressed = true;
            invalidate();
        }
    }
}
