package fi.tamk.tuni.paintapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private PaintView mPaintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPaintView = (PaintView) findViewById(R.id.paint_view);
    }

    public void colorClicked(View view) {

    }
}
