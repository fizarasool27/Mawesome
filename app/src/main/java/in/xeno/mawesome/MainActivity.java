package in.xeno.mawesome;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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
import android.view.Window;
import android.view.WindowManager;
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
  public String mlat;
  public String mlon;
  public static final int REQUEST_PERMISSION=1;


    LocationHelper locationHelper;
    ImageSceenshotSend imageSceenshotSend;
    static LocationManager locationManager;

    public static final int REQUEST_LOCATION_PERMISSION=1;
    public static final int REQUEST_CHECK_SETTINGS = 1;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE=2;
    //public static final int REQUEST_LOCATION=1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       // locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        weatherTextView=(TextView)findViewById(R.id.currentTemp);
        cityTextView=(TextView)findViewById(R.id.city);
        weatherDesc=(TextView)findViewById(R.id.weatherDescription);
        imageView=(ImageView)findViewById(R.id.image);
        swipe=(RelativeLayout)findViewById(R.id.swipeAction);
        swipeOnRefresh=findViewById(R.id.swiperefresh);
        cityTextView=(TextView)findViewById(R.id.city);
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
                        locationHelper.locationGetter();
                        WeatherReport weatherReport=new WeatherReport();
                        MessageEvent event=new MessageEvent();
                        String mlat=event.getLatitude();
                        String mlon=event.getLongitude();

                        APICallJob.scheduleJob("http://api.openweathermap.org/data/2.5/weather?lat="+mlat+"&lon="+mlon+"&units=metric&APPID=e2bf01c599a470fa873095e45b46facb","weather");
                        APICallJob.scheduleJob("https://mawesome.000webhostapp.com/read.php?temp="+weatherReport.getTemp()+"&lat="+event.getLatitude()+"&long="+event.getLongitude(),"content");
                        Content content=new Content();
                        EventBus.getDefault().post(content);
                        }
                }
        );

        FusedLocationProviderClient mFusedLocationClient;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationHelper=new LocationHelper(this);
        locationHelper.checkPermission();
        imageSceenshotSend.checkPermission();
        //locationHelper.getCurrentLocation();
        locationHelper.locationGetter();

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
       // locationHelper.getCurrentLocation();
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        //mlatitude.setText(event.getMessage());
        mlat=event.getLatitude();
        mlon=event.getLongitude();
        APICallJob.scheduleJob("http://api.openweathermap.org/data/2.5/weather?lat="+mlat+"&lon="+mlon+"&units=metric&APPID=e2bf01c599a470fa873095e45b46facb","weather");

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WeatherReport weatherReport){
        //String weatherText="The current temperature is "+weatherReport.getTemp();
        //MessageEvent event=new MessageEvent();
        String curTemp=weatherReport.getTemp()+(char)0x00B0+"C";
        weatherTextView.setText(curTemp);
        cityTextView.setText(weatherReport.getCity());
        weatherDesc.setText(weatherReport.getDescription());
        APICallJob.scheduleJob("https://mawesome.000webhostapp.com/read.php?temp="+weatherReport.getTemp()+"&lat="+mlat+"&long="+mlon,"content");
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
