package pl.ipebk.loader;

import android.app.Application;

import com.github.bskierys.pine.Pine;

import timber.log.Timber;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();

        Pine pineWithReplace = new Pine.Builder()
                .addPackageReplacePattern(getPackageName(), "CLOLO")
                .grow();

        Timber.plant(pineWithReplace);
    }
}
