package in.xeno.mawesome;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
  private TextView weatherTextView;
  private TextView cityTextView;
  private TextView weatherDesc;
  private ImageView imageView;
  private ImageLoader imageLoader;
  private RelativeLayout swipe;
  private SwipeRefreshLayout swipeOnRefresh;


    LocationHelper locationHelper;
    ImageSceenshotSend imageSceenshotSend;

    public static final int REQUEST_LOCATION_PERMISSION=1;
    public static final int REQUEST_CHECK_SETTINGS = 1;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE=2;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        weatherTextView=findViewById(R.id.currentTemp);
        cityTextView=findViewById(R.id.city);
        weatherDesc=findViewById(R.id.weatherDescription);
        imageView=findViewById(R.id.image);
        swipe=findViewById(R.id.swipeAction);
        swipeOnRefresh=findViewById(R.id.swiperefresh);
        cityTextView=findViewById(R.id.city);
        final View rootView = getWindow().getDecorView().findViewById(R.id.swipeAction);
        imageSceenshotSend=new ImageSceenshotSend(MainActivity.this);

        swipe.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeTop() {


                imageSceenshotSend.getScreenShot(MainActivity.this,rootView);
            }

        });
        swipeOnRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG,"onRefresh called from SwipeRefreshLayout");
                        swipeOnRefresh.setRefreshing(false);

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.

                      WeatherReport weatherReport=new WeatherReport();
                        APICallJob.scheduleJob("https://mawesome.000webhostapp.com/read.php?temp="+weatherReport.getTemp(),"content");
                       // EventBus.getDefault().post(weatherReport);
                        Content content=new Content();
                        EventBus.getDefault().post(content);

                        }
                }
        );




         //imageLoader.displayImage("https://res.cloudinary.com/mawesomeweather/image/upload/v1527753988/mawesome1.jpg",imageView);


        FusedLocationProviderClient mFusedLocationClient;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationHelper=new LocationHelper(this);
        locationHelper.checkPermission();
        imageSceenshotSend.checkPermission();

        }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    locationHelper.changeSettings();


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length> 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }

            }// other 'case' lines to check for other permissions
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_CHECK_SETTINGS:{
                Log.d("turn on location",String.valueOf(resultCode));
                locationHelper.locationGetter();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        //mlatitude.setText(event.getMessage());
        APICallJob.scheduleJob("http://api.openweathermap.org/data/2.5/weather?lat="+event.getLatitude()+"&lon="+event.getLongitude()+"&units=metric&APPID=e2bf01c599a470fa873095e45b46facb","weather");

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WeatherReport weatherReport){
        //String weatherText="The current temperature is "+weatherReport.getTemp();
        String curTemp=weatherReport.getTemp()+(char)0x00B0+"C";
        weatherTextView.setText(curTemp);
        cityTextView.setText(weatherReport.getCity());
        weatherDesc.setText(weatherReport.getDescription());
        APICallJob.scheduleJob("https://mawesome.000webhostapp.com/read.php?temp="+weatherReport.getTemp(),"content");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Content content){
        imageLoader.displayImage(content.getContent(),imageView);
    }


    private ShareActionProvider mShareActionProvider;




    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
