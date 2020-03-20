package fi.tamk.tuni.paintapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PaintView mPaintView;
    private ImageButton mCurrentPaint, mBrushButton, mEraseButton, mNewButton, mSaveButton;
    private float mXtraSmallBrush, mSmallBrush, mMediumBrush, mLargeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mPaintView = (PaintView) findViewById(R.id.paint_view);
        // Retrieve color currently used by user from the color layout
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors2);
        mCurrentPaint = (ImageButton) paintLayout.getChildAt(5);
        mCurrentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        // Init brush sizes
        mXtraSmallBrush = getResources().getInteger(R.integer.xtra_small_size);
        mSmallBrush = getResources().getInteger(R.integer.small_size);
        mMediumBrush = getResources().getInteger(R.integer.medium_size);
        mLargeBrush = getResources().getInteger(R.integer.large_size);
        // Init brush button
        mBrushButton = (ImageButton) findViewById(R.id.button_brush);
        mBrushButton.setOnClickListener(this);
        // Init erase button
        mEraseButton = (ImageButton) findViewById(R.id.button_erase);
        mEraseButton.setOnClickListener(this);
        // Init new draw button
        mNewButton = (ImageButton) findViewById(R.id.button_new_file);
        mNewButton.setOnClickListener(this);
        // Init save file button
        mSaveButton = (ImageButton) findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(this);
    }

    public void colorClicked(View view) {
        mPaintView.setEraseMode(false);
        mPaintView.setBrushSize(mPaintView.getLastBrushSize());
        if(view != mCurrentPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            mPaintView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            mCurrentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            mCurrentPaint = (ImageButton) view;
        }
    }

    @Override
    public void onClick(View v) {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setContentView(R.layout.brush_dialog_layout);
        ImageButton xtraSmallBtn = (ImageButton)brushDialog.findViewById(R.id.xtra_small_brush);
        ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
        ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
        ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
        switch(v.getId()) {
            case R.id.button_brush:
                brushDialog.show();
                mPaintView.setBrushSize(mPaintView.getLastBrushSize());
                xtraSmallBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(false);
                    mPaintView.setBrushSize(mXtraSmallBrush);
                    mPaintView.setLastBrushSize(mXtraSmallBrush);
                    brushDialog.dismiss();
                });
                smallBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(false);
                    mPaintView.setBrushSize(mSmallBrush);
                    mPaintView.setLastBrushSize(mSmallBrush);
                    brushDialog.dismiss();
                });
                mediumBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(false);
                    mPaintView.setBrushSize(mMediumBrush);
                    mPaintView.setLastBrushSize(mMediumBrush);
                    brushDialog.dismiss();
                });
                largeBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(false);
                    mPaintView.setBrushSize(mLargeBrush);
                    mPaintView.setLastBrushSize(mLargeBrush);
                    brushDialog.dismiss();
                });
                break;
            case R.id.button_erase:
                brushDialog.show();
                xtraSmallBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(true);
                    mPaintView.setBrushSize(mXtraSmallBrush);
                    brushDialog.dismiss();
                });
                smallBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(true);
                    mPaintView.setBrushSize(mSmallBrush);
                    brushDialog.dismiss();
                });
                mediumBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(true);
                    mPaintView.setBrushSize(mMediumBrush);
                    brushDialog.dismiss();
                });
                largeBtn.setOnClickListener((l) -> {
                    mPaintView.setEraseMode(true);
                    mPaintView.setBrushSize(mLargeBrush);
                    brushDialog.dismiss();
                });
                break;
            case R.id.button_new_file:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", (l, w) -> {
                    mPaintView.startNew();
                    l.dismiss();
                });
                newDialog.setNegativeButton("Cancel", (l, w) -> {
                    l.cancel();
                });
                newDialog.show();
                break;
            case R.id.button_save:
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Save drawing");
                saveDialog.setMessage("Save drawing to device Gallery?");
                saveDialog.setPositiveButton("Yes", (l, w) -> {
                    mPaintView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), mPaintView.getDrawingCache(),
                            UUID.randomUUID().toString() + ".png", "drawing");
                    if (imgSaved != null) {
                        Toast.makeText(getApplicationContext(), "Drawing saved to Gallery!", Toast.LENGTH_SHORT).show();
                        mPaintView.destroyDrawingCache();
                    } else {
                        Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
                    }
                });
                saveDialog.setNegativeButton("Cancel", (l, w) -> l.cancel());
                saveDialog.show();
                break;
        }
    }
}
