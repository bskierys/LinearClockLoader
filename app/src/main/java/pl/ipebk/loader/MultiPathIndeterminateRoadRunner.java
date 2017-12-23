package pl.ipebk.loader;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.github.glomadrian.roadrunner.ProgressRoadRunner;
import com.github.glomadrian.roadrunner.RoadRunner;
import com.github.glomadrian.roadrunner.builder.RoadRunnerBuilder;
import com.github.glomadrian.roadrunner.painter.configuration.PathPainterConfiguration;
import com.github.glomadrian.roadrunner.painter.configuration.factory.PathPainterConfigurationFactory;
import com.github.glomadrian.roadrunner.painter.determinate.DeterminatePainter;
import com.github.glomadrian.roadrunner.painter.determinate.ProgressDeterminatePainter;
import com.github.glomadrian.roadrunner.painter.determinate.factory.DeterminatePainterFactory;
import com.github.glomadrian.roadrunner.path.PathContainer;
import com.github.glomadrian.roadrunner.utils.AssertUtils;
import com.github.glomadrian.roadrunner.utils.RangeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cimi.com.easeinterpolator.EaseQuadInOutInterpolator;

/**
 * TODO: Generic description. Replace with real one.
 */
public class MultiPathIndeterminateRoadRunner extends RoadRunner implements Animator.AnimatorListener {

    // Property to create an animation
    public static final String PROGRESS = "progress";
    private static final String TAG = "ProgressRoadRunner";
    private int originalWidth;
    private int originalHeight;
    private String initialPathData;
    private ProgressDeterminatePainter progressDeterminatePainter;

    private int min = 0;
    private int max = 100;
    private int progress = 0;

    private PathPainterConfiguration pathPainterConfiguration;
    private boolean firstDraw = true;

    private ObjectAnimator infiniteAnimator;
    private int currentPathIndex = 0;
    private int animationRepeatCounter = 0;
    private List<ProgressDeterminatePainter> progressPainters = new ArrayList<>();

    private MultiPathIndeterminateRoadRunner(MultiPathIndeterminateRoadRunner.Builder builder) {
        super(builder.context);

        initialPathData = builder.pathData;
        originalWidth = builder.originalWidth;
        originalHeight = builder.originalHeight;
        pathPainterConfiguration = PathPainterConfigurationFactory.makeConfiguration(builder, DeterminatePainter.PROGRESS);
    }

    public MultiPathIndeterminateRoadRunner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPath(attrs);
        initConfiguration(attrs);
    }

    public MultiPathIndeterminateRoadRunner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPath(attrs);
        initConfiguration(attrs);
    }

    /**
     * Added as hack
     */
    public void addNewPath(String pathData) {
        if (getHeight() == 0 || getWidth() == 0) {
            throw new IllegalStateException("view not visible yet: height || width == 0");
        }

        try {
            PathContainer container = buildPathData(getWidth(), getHeight(), pathData, originalWidth, originalHeight);
            progressPainters.add(initPathPainter(container));
        } catch (ParseException e) {
            Log.e(TAG, "Path parse exception:", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "Path data or original sizes are not initialized yet.");
        }
    }

    private int getNextProgressPainterIndex() {
        if (currentPathIndex + 1 >= progressPainters.size()) {
            currentPathIndex = 0;
        } else {
            currentPathIndex++;
        }
        return currentPathIndex;
    }

    public void start() {
        if (infiniteAnimator == null) {
            infiniteAnimator = ObjectAnimator.ofInt(this, ProgressRoadRunner.PROGRESS, 100);
            infiniteAnimator.setDuration(800);
            infiniteAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            infiniteAnimator.setRepeatCount(1);
            infiniteAnimator.addListener(this);
            infiniteAnimator.setInterpolator(new EaseQuadInOutInterpolator());
        }
        infiniteAnimator.start();
    }

    public void stop() {
        if (infiniteAnimator != null) {
            infiniteAnimator.end();
        }
    }

    public Paint getPaint() {
        return progressDeterminatePainter.getPaint();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int value) {
        if (value <= max || value >= min) {
            this.progress = value;
            float progress = RangeUtils.getFloatValueInRange(min, max, 0f, 1f, value);
            if (progressDeterminatePainter != null) {
                progressDeterminatePainter.setProgress(progress);
            }
        }
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override public void setColor(int color) {
        progressDeterminatePainter.setColor(color);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (progressPainters.size() != 0) {
            redrawNewPath(currentPathIndex);
            return;
        }

        try {
            PathContainer pathContainer = buildPathData(w, h, initialPathData, originalWidth, originalHeight);
            initPathPainter(pathContainer);
        } catch (ParseException e) {
            Log.e(TAG, "Path parse exception:", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "Path data or original sizes are not initialized yet.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (progressDeterminatePainter == null)
            return;

        if (!firstDraw) {
            progressDeterminatePainter.paintPath(canvas);
        } else {
            firstDraw = false;
        }
    }

    private void initPath(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, com.github.glomadrian.roadrunner.R.styleable.RoadRunner);
        initialPathData = attributes.getString(com.github.glomadrian.roadrunner.R.styleable.RoadRunner_path_data);
        originalWidth = attributes.getInteger(com.github.glomadrian.roadrunner.R.styleable.RoadRunner_path_original_width, 0);
        originalHeight = attributes.getInteger(com.github.glomadrian.roadrunner.R.styleable.RoadRunner_path_original_height, 0);

        AssertUtils.assertThis(initialPathData != null, "Path data must be defined", this.getClass());
        AssertUtils.assertThis(!initialPathData.isEmpty(), "Path data must be defined", this.getClass());
        AssertUtils.assertThis(!initialPathData.equals(""), "Path data must be defined", this.getClass());
        AssertUtils.assertThis(originalWidth > 0, "Original with of the path must be defined",
                this.getClass());
        AssertUtils.assertThis(originalHeight > 0, "Original height of the path must be defined",
                this.getClass());
    }

    private void initConfiguration(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, com.github.glomadrian.roadrunner.R.styleable.RoadRunner);
        min = attributes.getInteger(com.github.glomadrian.roadrunner.R.styleable.RoadRunner_min, min);
        max = attributes.getInteger(com.github.glomadrian.roadrunner.R.styleable.RoadRunner_max, max);
        pathPainterConfiguration =
                PathPainterConfigurationFactory.makeConfiguration(attributes, DeterminatePainter.PROGRESS);
        attributes.recycle();
    }

    private ProgressDeterminatePainter initPathPainter(PathContainer container) {
        return progressDeterminatePainter = (ProgressDeterminatePainter) DeterminatePainterFactory.makeIndeterminatePathPainter(
                DeterminatePainter.PROGRESS, container, this, pathPainterConfiguration);
    }

    @Override public void onAnimationStart(Animator animator) { }

    @Override public void onAnimationEnd(Animator animator) {
        redrawNewPath(getNextProgressPainterIndex());
        infiniteAnimator.setStartDelay(200);
        infiniteAnimator.start();
    }

    @Override public void onAnimationCancel(Animator animator) { }

    @Override public void onAnimationRepeat(Animator animator) { }

    private void redrawNewPath(int pathIndex) {
        progressDeterminatePainter = progressPainters.get(pathIndex);
    }

    public static class Builder extends RoadRunnerBuilder {

        public Builder(Context context) {
            super(context);
        }

        @Override public MultiPathIndeterminateRoadRunner build() {
            AssertUtils.assertThis(pathData != null, "Path data must be defined", this.getClass());
            AssertUtils.assertThis(!pathData.isEmpty(), "Path data must be defined", this.getClass());
            AssertUtils.assertThis(!pathData.equals(""), "Path data must be defined", this.getClass());
            AssertUtils.assertThis(originalWidth > 0, "Original with of the path must be defined",
                    this.getClass());
            AssertUtils.assertThis(originalHeight > 0, "Original height of the path must be defined",
                    this.getClass());

            return new MultiPathIndeterminateRoadRunner(this);
        }
    }

}
