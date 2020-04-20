package fi.tamk.tuni.paintapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Main activity to start application, holds preview functionality.
 */
public class StartActivity extends AppCompatActivity {

    /**
     * Custom preview view to see background color on canvas.
     */
    private View mRectangleView;
    /**
     * Default background color for canvas.
     */
    private int mDefaultColor;
    /**
     * Button to show color picker dialog.
     */
    private Button mButtonShowColors;

    /**
     * Initialize default values and views.
     *
     * @param savedInstanceState current saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);
        // Get rectangle template view to change background color to chosen/default
        mRectangleView = findViewById(R.id.myRectangleView);
        mDefaultColor = Color.WHITE;
        // Init show color button
        mButtonShowColors = findViewById(R.id.show_colors);
        mButtonShowColors.setOnClickListener(this::showColorPicker) ;
    }

    /**
     * When new drawing is started send chosen background color to be set.
     *
     * @param v current view element
     */
    public void startClicked(View v) {
        ImageButton b = findViewById(R.id.button_start);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("background_color", mDefaultColor);
        startActivity(i);
    }

    /**
     * Color picker dialog checking.
     *
     * @param v current view element
     */
    // Functionality of show color button
    public void showColorPicker(View v) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // Check which color is chosen and set it to background color of the custom view.
                mDefaultColor = color;
                mRectangleView.setBackgroundColor(mDefaultColor);
            }
        });
        colorPicker.show();
    }
}
