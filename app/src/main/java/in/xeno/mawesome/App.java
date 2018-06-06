package in.xeno.mawesome;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.JobManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new DemoJobCreator());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)

			.build();
        ImageLoader.getInstance().init(config);
    }
}



