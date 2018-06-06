package in.xeno.mawesome;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonParser {
    public static WeatherReport getWeatherReport(String result)
    {
        WeatherReport weatherobj= new WeatherReport();
        JsonObject resultobject=Util.toObject(result);
        JsonObject mainobject=Util.obj(resultobject,"main");
        int temp=(int)Util.numAsFloat(mainobject,"temp");
        int pressure=(int)Util.numAsFloat(mainobject,"pressure");
        int humidity=(int)Util.numAsFloat(mainobject,"humidity");
        int tempMin=(int)Util.numAsFloat(mainobject,"temp_min");
        int tempMax=(int)Util.numAsFloat(mainobject,"temp_max");
        String city=Util.str(resultobject,"name");
        JsonArray weather=Util.arr(resultobject,"weather");
        String desc=Util.str(weather.get(0).getAsJsonObject(),"description");

        weatherobj.setTemp(String.valueOf(temp));
        weatherobj.setHumidity(String.valueOf(humidity));
        weatherobj.setPressure(String.valueOf(pressure));
        weatherobj.setTempMax(String.valueOf(tempMax));
        weatherobj.setTempMin(String.valueOf(tempMin));
        weatherobj.setDescription(desc);
        weatherobj.setCity(city);


        return weatherobj;
    }
    public static Content getExactTemp(String result)
    {
        Content obj=new Content();
        JsonObject resultobject=Util.toObject(result);
        JsonObject mainobject=Util.obj(resultobject,"message");
        String imageURL=Util.str(mainobject,"content");
        obj.setContent(imageURL);
        return obj;

    }
}
