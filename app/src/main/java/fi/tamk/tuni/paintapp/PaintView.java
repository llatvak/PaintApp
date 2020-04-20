package fi.tamk.tuni.paintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;


/**
 * Custom view that holds paint's tool functions and canvas attributes.
 * <p>
 * Holds attributes and lists for paths needed for drawing to canvas.
 * Initializes paint attributes and holds functionality for tools.
 * Handles the touch input and draws lines based on that.
 * </p>
 *
 * @author Lauri Latva-Kyyny
 * @version 1.0
 */
public class PaintView extends View {

    private Path mPath;
    private Paint mDrawPaint, mCanvasPaint;
    private int mPaintColor = Color.parseColor("#000000");
    private Canvas mDrawCanvas;
    private Bitmap mCanvasBitmap;
    private float mBrushSize, mLastBrushSize;
    private boolean mEraseMode = false;
    private int backgroundColor;
    /**
     * Current paths drawn, only queried when undo is pressed.
     */
    private ArrayList<DrawPath> paths = new ArrayList<>();
    /**
     * Undone paths, only queried when redo pressed, reset when new line is drawn.
     */
    private ArrayList<DrawPath> undo = new ArrayList<>();
    float mX = 0;
    float mY = 0;
    /**
     * Determine when user pressed redo or undo button to draw paths from list.
     */
    boolean redoOrUndoPressed = false;
    private int previousColor;
    private float prevBrushSize;

    /**
     * Constructor that initializes paint view.
     *
     * @param context current app context
     * @param attrs current attribute set
     */
    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializePaintView();
    }

    /**
     * Initializes all required attributes needed for drawing and sets default values.
     */
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
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    /**
     * When size is changed create bitmap and save it to attribute and create new canvas.
     *
     * @param width new width
     * @param height new height
     * @param oldWidth old width
     * @param oldHeight old height
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    /**
     * Called when path is drawn to screen from touch input.
     * <p>
     * Determines if user has pressed redo or undo, if so queries list and draws paths based
     * on that.
     * Adds attributes for drawn paths to custom {@link DrawPath} class.
     * If redo or undo is not pressed draws paths one at a time, so performance is kept
     * steady with no lag, even there are lot of paths.
     * </p>
     *
     * @param canvas current canvas to draw into
     */
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

    /**
     * Handles touch input and draws paths based on the feedback.
     *
     * @param event touch input event from pressing screen
     * @return returns boolean to end input
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Clear undo list when new path drawn and reset current path.
                undo.clear();
                mPath.reset();
                // Check if erase mode is not on or paint color is set
                if(mPaintColor != 0 && !mEraseMode) {
                    previousColor = mPaintColor;
                    String hexColor = String.format("#%06X", (0xFFFFFF & previousColor));
                    setColor(hexColor);
                } else {
                    mDrawPaint.setColor(mPaintColor);
                }
                // Move to position and reset canvas
                mPath.moveTo(touchX, touchY);
                mX = touchX;
                mY = touchY;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // Calculate smooth path from moving and reset canvas
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
                // End drawing line, add to drawn paths list and rest canvas
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

    /**
     * Change color of the brush.
     *
     * @param newColor new color to replace old one
     */
    public void setColor(String newColor) {
        this.mPaintColor = Color.parseColor(newColor);
        if(mPaintColor != 0 && !mEraseMode) {
            previousColor = mPaintColor;
            mDrawPaint.setColor(mPaintColor);
            prevBrushSize = mBrushSize;
            mDrawPaint.setStrokeWidth(prevBrushSize);
        } else {
            mDrawPaint.setColor(mPaintColor);
        }
        invalidate();
    }

    /**
     * Set brush size to new one.
     *
     * @param newSize new brush size to replace old one
     */
    public void setBrushSize(float newSize) {
        this.mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    /**
     * Sets the last brush size.
     *
     * @param lastSize last brush size to be set
     */
    public void setLastBrushSize(float lastSize) {
        this.mLastBrushSize = lastSize;
    }

    /**
     * Get last brush size.
     *
     * @return last brush size to get
     */
    public float getLastBrushSize() {
        return mLastBrushSize;
    }

    /**
     * Sets erase mode to set to clear paths to default color.
     *
     * @param isErase is erasemode on or not
     */
    public void setEraseMode(boolean isErase) {
        mEraseMode = isErase;
        if(mEraseMode) {
            String hexColor = String.format("#%06X", (0xFFFFFF & backgroundColor));
            setColor(hexColor);
        } else {
            mPaintColor = previousColor;
            mDrawPaint.setColor(mPaintColor);
        }
    }

    /**
     * Clear canvas and paths when reset button clicked.
     */
    public void startNew() {
        paths.clear();
        undo.clear();
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    /**
     * Undo the most recent path from current paths list and reset canvas.
     */
    public void removeRecentPath() {
        if(paths.size() > 0) {
            undo.add(paths.remove(paths.size() - 1));
            redoOrUndoPressed = true;
            invalidate();
        }
    }

    /**
     * If undone path list is not empty redo paths and add them to current paths list.
     */
    public void redoRecentPath() {
        if(undo.size() > 0) {
            paths.add(undo.remove(undo.size() - 1));
            redoOrUndoPressed = true;
            invalidate();
        }
    }

    /**
     * Sets current background color.
     *
     * @param color current background color to set
     */
    public void setCurrentBackgroundColor(int color) {
        backgroundColor = color;
    }
}
