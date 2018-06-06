package in.xeno.mawesome;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import java.util.Map;
//import org.json.simple.JSONObject;

public class APICallJob extends Job {
    public static final String TAG="API";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {

        OkHttpClient client = new OkHttpClient();
        String result = "";
        PersistableBundleCompat bundle = params.getExtras();
        Map<String,String> map=new HashMap<>();

        try {
            Request request = new Request.Builder()
                    .url(bundle.getString("api",""))
                    .build();


            Response response = client.newCall(request).execute();
            result = response.body().string();
            Log.d("API Call Response",result);

            if(bundle.getString("apiType","").equals("weather")) {
                WeatherReport weatherReport = in.xeno.mawesome.JsonParser.getWeatherReport(result);
                EventBus.getDefault().post(weatherReport);
            }
            else if(bundle.getString("apiType","").equals("content"))
            {

                Content content = in.xeno.mawesome.JsonParser.getExactTemp(result);
                EventBus.getDefault().post(content);
            }
            else
            {

            }

        } catch (Exception e) {

        }
        return Result.SUCCESS;
    }
    public static void scheduleJob(String api,String apiType) {
        PersistableBundleCompat extras=new PersistableBundleCompat();
        extras.putString("api",api);
        extras.putString("apiType",apiType);

        new JobRequest.Builder(APICallJob.TAG).setExtras(extras)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }
}



