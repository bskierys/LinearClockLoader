package pl.ipebk.loader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.clock_loading) MultiPathIndeterminateRoadRunner clockLoading;
    @BindView(R.id.txt_loading_prompt) TextView loadingPrompt;
    @BindView(R.id.borders) ImageView borderView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override public void onStart() {
        super.onStart();
        run();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(800, 600);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        borderView.setLayoutParams(params);
    }

    private void run() {
        clockLoading.postDelayed(new Runnable() {
            @Override public void run() {
                loadingPrompt.setVisibility(View.VISIBLE);
                prepare();
                loadingPrompt.setVisibility(View.INVISIBLE);
                clockLoading.start();
            }
        }, 1000);
    }

    private void prepare() {
        List<String> paths = new ArrayList<>();
        paths.add(getResources().getString(R.string.clock_3_path));
        paths.add(getResources().getString(R.string.clock_4_path));
        paths.add(getResources().getString(R.string.clock_6_path));
        paths.add(getResources().getString(R.string.clock_8_path));
        paths.add(getResources().getString(R.string.clock_9_path));
        paths.add(getResources().getString(R.string.clock_10_path));
        paths.add(getResources().getString(R.string.clock_12_path));
        paths.add(getResources().getString(R.string.clock_2_path));

        for (int i = 0; i < paths.size(); i++) {
            clockLoading.addNewPath(paths.get(i));
        }
    }
}
