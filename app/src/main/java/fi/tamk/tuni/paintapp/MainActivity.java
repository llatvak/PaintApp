package fi.tamk.tuni.paintapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PaintView mPaintView;
    private ImageButton mCurrentPaint, mBrushButton;
    private float mSmallBrush, mMediumBrush, mLargeBrush;

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
        mSmallBrush = getResources().getInteger(R.integer.small_size);
        mMediumBrush = getResources().getInteger(R.integer.medium_size);
        mLargeBrush = getResources().getInteger(R.integer.large_size);
        // Init brush button
        mBrushButton = (ImageButton) findViewById(R.id.button_brush);
        mBrushButton.setOnClickListener(this);
    }

    public void colorClicked(View view) {
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

    }
}
