package pl.ipebk.loader;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.glomadrian.roadrunner.DeterminateRoadRunner;
import com.github.glomadrian.roadrunner.IndeterminateRoadRunner;

public class MainActivity extends AppCompatActivity {

    private static final int DOWNLOAD_TIME = 5000;
    private ValueAnimator progressAnimator;
    private IndeterminateRoadRunner clockLoading;
    private Button clickButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockLoading = findViewById(R.id.clock_loading);
        //clickButton = findViewById(R.id.btn_start);
        /*clickButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                clockLoading.start();
            }
        });*/

        /*progressAnimator = ValueAnimator.ofInt(0, 100).setDuration(DOWNLOAD_TIME);
        progressAnimator.setStartDelay(1200);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                clockLoading.setValue(value);
            }
        });*/
    }
}
