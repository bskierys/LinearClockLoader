package pl.ipebk.loader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.seek) SeekBar seekBar;
    @BindView(R.id.clock_loading) MultiPathIndeterminateRoadRunner clockLoading;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        ButterKnife.bind(this);

        seekBar.setOnSeekBarChangeListener(this);
    }

    @OnClick(R.id.runButton)
    public void runButtonClick() {
        clockLoading.start();
    }

    @OnClick(R.id.stopButton)
    public void stopButtonClick() {
        clockLoading.stop();
    }

    @OnClick(R.id.prepareButton) public void prepare() {
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (clockLoading != null) {
            stopButtonClick(); // Stop the animation first, if it is running
            clockLoading.setProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
