package fi.tamk.tuni.paintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);
    }

    public void startClicked(View v) {
        ImageButton b = findViewById(R.id.button_start);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void showColorPicker(View v) {
        Button b = findViewById(R.id.show_colors);

    }


}
