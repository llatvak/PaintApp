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
import android.graphics.PorterDuffXfermode;
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
    boolean something = false;

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
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
        if(!something) {
            canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
            canvas.drawPath(mPath, mDrawPaint);
        }
        /*
        canvas.save();
        mDrawCanvas.drawColor(backgroundColor);
        for (DrawPath dp : paths) {
            mDrawPaint.setColor(dp.color);
            mDrawPaint.setStrokeWidth(dp.strokeWidth);
            //mDrawCanvas.drawPath(dp.path, mDrawPaint);
        }
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.restore();
        */
        if(something) {
            System.out.println(paths);
            canvas.save();
            mDrawCanvas.drawColor(backgroundColor);
            for (DrawPath dp : paths) {
                mDrawPaint.setColor(dp.color);
                mDrawPaint.setStrokeWidth(dp.strokeWidth);
                mDrawCanvas.drawPath(dp.path, mDrawPaint);
            }
            canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
            canvas.restore();
            something = false;
        }
        //System.out.println(paths);
        // invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //mPath = new Path();
                //DrawPath d = new DrawPath(mPaintColor, 10, mPath);
                //paths.add(d);
                mPath.reset();
                mPath.moveTo(touchX, touchY);
                mX = touchX;
                mY = touchY;
                break;
            case MotionEvent.ACTION_MOVE:
                //mPath.lineTo(touchX, touchY);
                //mX = touchX;
                //mY = touchY;
                float dx = Math.abs(touchX - mX);
                float dy = Math.abs(touchY - mY);
                if (dx >= 4 || dy >= 4) {
                    mPath.quadTo(mX, mY, (touchX + mX) / 2, (touchY + mY) / 2);
                    mX = touchX;
                    mY = touchY;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(something) {
                    System.out.println("SOMETHING TRUE");
                    System.out.println(mPaintColor);
                    System.out.println(mBrushSize);
                    mPath.lineTo(mX, mY);
                    mDrawCanvas.drawPath(mPath, mDrawPaint);
                    //mPath.reset();
                    //mPath = new Path();
                } else {
                    System.out.println("SOMETHING FALSE");
                    System.out.println(mPaintColor);
                    System.out.println(mBrushSize);
                    mPath.lineTo(mX, mY);
                    mDrawCanvas.drawPath(mPath, mDrawPaint);
                    //DrawPath d = new DrawPath(mPaintColor, (int)mBrushSize, mPath);
                    DrawPath d = new DrawPath(mPaintColor, (int)mBrushSize, mPath);
                    paths.add(d);
                    //mPath.reset();
                    mPath = new Path();
                }
                //mPath.lineTo(mX, mY);
                //mDrawCanvas.drawPath(mPath, mDrawPaint);
                //mPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        this.mPaintColor = Color.parseColor(newColor);
        mDrawPaint.setColor(mPaintColor);
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
            mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            mDrawPaint.setXfermode(null);
        }
    }

    public void startNew() {
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void removeRecentPath() {
        if(paths.size() > 0) {
            undo.add(paths.remove(paths.size() - 1));
            something = true;
            invalidate();
        }
    }
}
