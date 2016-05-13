package com.simplilearn.simpliweather.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.simplilearn.simpliweather.R;
import com.simplilearn.simpliweather.interfaces.WebWeatherListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.simplilearn.simpliweather.data.WeatherForecast.TOTAL_DAYS;


public class WebWeather implements Response.ErrorListener, Response.Listener<JSONObject> {
    final static String TAG = WebWeather.class.getSimpleName();
    final static String API_KEY = "7baeb17a0b9759b0954969ef46a4d3fd";
    final static String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    final static String WEATHER_URL = WEATHER_BASE_URL + "weather?";
    final static String FORECAST_URL = WEATHER_BASE_URL + "forecast/daily?cnt=" + TOTAL_DAYS;

    Map<String, Integer> mWeatherIcons;

    RequestQueue mRequestQueue;

    Context mContext;
    WebWeatherListener mWebWeatherListener;


    public WebWeather(Context context, WebWeatherListener webWeatherListener) {
        mContext = context;
        mWebWeatherListener = webWeatherListener;
        mRequestQueue = Volley.newRequestQueue(mContext);

        mWeatherIcons = new HashMap<String, Integer>();
        mWeatherIcons.put("01d", R.drawable.clear_sky_d);
        mWeatherIcons.put("01n", R.drawable.clear_sky_n);
        mWeatherIcons.put("02d", R.drawable.few_clouds_d);
        mWeatherIcons.put("02n", R.drawable.few_clouds_n);
        mWeatherIcons.put("03d", R.drawable.scattered_clouds);
        mWeatherIcons.put("03n", R.drawable.scattered_clouds);
        mWeatherIcons.put("04d", R.drawable.broken_clouds);
        mWeatherIcons.put("04n", R.drawable.broken_clouds);
        mWeatherIcons.put("09d", R.drawable.shower_rain);
        mWeatherIcons.put("09n", R.drawable.shower_rain);
        mWeatherIcons.put("10d", R.drawable.rain);
        mWeatherIcons.put("10n", R.drawable.rain);
        mWeatherIcons.put("11d", R.drawable.thunderstorm);
        mWeatherIcons.put("11n", R.drawable.thunderstorm);
        mWeatherIcons.put("13d", R.drawable.snow);
        mWeatherIcons.put("13n", R.drawable.snow);
        mWeatherIcons.put("50d", R.drawable.mist);
        mWeatherIcons.put("50n", R.drawable.mist);
    }

    public void getWeatherForLocation(double latitude, double longitude) {
        // Request Weather
        String reqURL = WEATHER_URL + "&lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                reqURL,
                "",
                this,
                this);

        // Adding request to request queue
        mRequestQueue.add(jsonObjReq);

        reqURL = FORECAST_URL + "&lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
        jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                reqURL,
                "",
                this,
                this);

        // Adding request to request queue
        mRequestQueue.add(jsonObjReq);
    }

    private String kelvinToCStr(double kelvin) {
        return String.valueOf((int)(kelvin - 273.15)) + "\u00b0c";
    }

    private String kelvinToFStr(double kelvin) {
        return String.valueOf((int)((kelvin - 273.15) * 1.8 + 32.0)) + "\u00b0f";
    }

    private void processWeather(JSONObject response) {
        WeatherInfo weatherInfo = new WeatherInfo();
        try {
            weatherInfo.Name = response.getString("name");

            JSONArray weatherListJSON = response.getJSONArray("weather");
            if(weatherListJSON.length() > 0) {
                JSONObject weatherItemJSON = weatherListJSON.getJSONObject(0);
                weatherInfo.Description = weatherItemJSON.getString("description");
                weatherInfo.Main = weatherItemJSON.getString("main");
                weatherInfo.IconId = mWeatherIcons.get(weatherItemJSON.getString("icon"));
            }

            JSONObject mainJSON = response.getJSONObject("main");

            weatherInfo.TempC = kelvinToCStr(mainJSON.getDouble("temp"));
            weatherInfo.TempF = kelvinToFStr(mainJSON.getDouble("temp"));

            mWebWeatherListener.onWeatherInfoReceived(weatherInfo);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "message: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void processForecast(JSONObject response) {
        WeatherForecast weatherForecast = new WeatherForecast();
        try {
            JSONObject cityJSON = response.getJSONObject("city");
            weatherForecast.City = cityJSON.getString("name");

            JSONArray listJSON = response.getJSONArray("list");
            for (int i = 0; i < listJSON.length(); i++) {
                JSONObject listItemJSON = listJSON.getJSONObject(i);
                JSONArray weatherListJSON = listItemJSON.getJSONArray("weather");
                JSONObject weatherItemJSON = weatherListJSON.getJSONObject(0);
                JSONObject tempJSON = listItemJSON.getJSONObject("temp");

                weatherForecast.Forecast[i] = new DailyForecast();
                // Display condition
                weatherForecast.Forecast[i].Description = weatherItemJSON.getString("description");
                weatherForecast.Forecast[i].Main  = weatherItemJSON.getString("main");
                weatherForecast.Forecast[i].IconId = mWeatherIcons.get(weatherItemJSON.getString("icon"));

                weatherForecast.Forecast[i].mMinTempC = kelvinToCStr(tempJSON.getDouble("min"));
                weatherForecast.Forecast[i].mMinTempF = kelvinToFStr(tempJSON.getDouble("min"));
                weatherForecast.Forecast[i].mMaxTempC = kelvinToCStr(tempJSON.getDouble("max"));
                weatherForecast.Forecast[i].mMaxTempF = kelvinToFStr(tempJSON.getDouble("max"));
            }
            mWebWeatherListener.onForecastReceived(weatherForecast);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "message: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());

        if(response.has("cnt")) {
            processForecast(response);
        } else {
            processWeather(response);
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
