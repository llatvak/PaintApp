package fi.tamk.tuni.paintapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.UUID;

/**
 * Class to hold UI actions and initialization of {@link PaintView}
 */
public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Custom paint view.
     */
    private PaintView mPaintView;
    /**
     * All tools as image buttons.
     */
    private ImageButton mCurrentPaint, mBrushButton, mEraseButton, mNewButton, mSaveButton, mUndoButton, mRedoButton;
    private float mXtraSmallBrush, mSmallBrush, mMediumBrush, mLargeBrush;
    /**
     * Custom request code for permission checking.
     */
    private static final int PERMISSION_REQUEST_CODE = 100;

    /**
     * Initialize {@link PaintView}, buttons and add click listeners.
     *
     * @param savedInstanceState current save instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        // Set background color of canvas
        mPaintView = (PaintView) findViewById(R.id.paint_view);
        mPaintView.setBackgroundColor(getIntent().getIntExtra("background_color",0));
        mPaintView.setCurrentBackgroundColor(getIntent().getIntExtra("background_color",0));

        // Retrieve color currently used by user from the color layout
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors1);
        mCurrentPaint = (ImageButton) paintLayout.getChildAt(0);
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

        // Init undo button
        mUndoButton = (ImageButton) findViewById(R.id.button_undo);
        mUndoButton.setOnClickListener(this);

        // Init redo button
        mRedoButton = (ImageButton) findViewById(R.id.button_redo);
        mRedoButton.setOnClickListener(this);
    }

    /**
     * Set paint color to pressed mode and set color.
     *
     * @param view current view element
     */
    public void colorClicked(View view) {
        mPaintView.setEraseMode(false);
        if(view != mCurrentPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            mPaintView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            mCurrentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            mCurrentPaint = (ImageButton) view;
        }
    }

    /**
     * On click functionality when UI elements are pressed.
     * <p>
     * Hold tools like brush, erase, create new file, save, undo and redo click listener
     * actions and call the correct methods from {@link PaintView}
     * </p>
     *
     * @param v current view element
     */
    @Override
    public void onClick(View v) {
        // Init brush choosing dialog and set layout
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setContentView(R.layout.brush_dialog_layout);
        // Init buttons
        ImageButton xtraSmallBtn = (ImageButton)brushDialog.findViewById(R.id.xtra_small_brush);
        ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
        ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
        ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
        switch(v.getId()) {
            case R.id.button_brush:
                // Open brush choosing dialog and update brush size from feedback.
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
                // Show erase tool dialog and update size from feedback.
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
                // Confirm action and clear file by calling PaintView method
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", (l, w) -> {
                    // Input ok
                    mPaintView.startNew();
                    l.dismiss();
                });
                newDialog.setNegativeButton("Cancel", (l, w) -> {
                    // If not ok just cancel
                    l.cancel();
                });
                newDialog.show();
                break;
            case R.id.button_save:
                // Check permission first if not already given and save to gallery if permission given.
                if (checkPermission()) {
                    saveFileToGallery();
                } else {
                    requestPermission();
                }
                break;
            case R.id.button_undo:
                // Undo button logic
                mPaintView.removeRecentPath();
                break;
            case R.id.button_redo:
                // Redo button logic
                mPaintView.redoRecentPath();
                break;
        }
    }

    /**
     * Request permission if it has not been declined before, otherwise give toast to remind user.
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(StartActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Save file to device's gallery from dialog feedback.
     */
    private void saveFileToGallery() {
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
    }

    /**
     * Check if permission is given by the user.
     *
     * @return boolean value based on if the user has given permission
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(StartActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Called when permission is given, check permission code.
     *
     * @param requestCode current request code
     * @param permissions current permissions in string array
     * @param grantResults current results granted in int array
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveFileToGallery();
            }
        }
    }
}
