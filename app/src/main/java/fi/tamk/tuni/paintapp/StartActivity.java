package fi.tamk.tuni.paintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
        mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
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

    }


}
