package fi.tamk.tuni.paintapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class StartActivity extends AppCompatActivity {

    View mRectangleView;
    int mDefaultColor;
    Button mButtonShowColors;

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

    public void startClicked(View v) {
        ImageButton b = findViewById(R.id.button_start);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // Functionality of show color button
    public void showColorPicker(View v) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                mRectangleView.setBackgroundColor(mDefaultColor);
            }
        });
        colorPicker.show();
    }


}
