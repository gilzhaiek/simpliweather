package com.simplilearn.simpliweather.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplilearn.simpliweather.R;
import com.simplilearn.simpliweather.data.WeatherForecast;
import com.simplilearn.simpliweather.data.WeatherInfo;
import com.simplilearn.simpliweather.data.WebWeather;
import com.simplilearn.simpliweather.interfaces.WebWeatherListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WeatherFragment extends Fragment implements WebWeatherListener, SharedPreferences.OnSharedPreferenceChangeListener{
    static final int FORECAST_DAYS = 3;
    ImageView mCurrentTempImage;
    TextView mCurrentLocTempTextView;
    TextView mDay3TextView;

    ImageView[] mForecastIconIV = new ImageView[FORECAST_DAYS];
    TextView[] mForecastMaxTempTV = new TextView[FORECAST_DAYS];
    TextView[] mForecastMinTempTV = new TextView[FORECAST_DAYS];

    WebWeather mWebWeather;

    WeatherInfo mLastWeatherInfo;
    WeatherForecast mLastWeatherForecast;

    double mLastLatitude;
    double mLastLongitude;

    Context mContext;
    boolean mIsMetric = true;

    public WeatherFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        mWebWeather = new WebWeather(mContext, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View weatherView = inflater.inflate(
                R.layout.weather_view,
                container,
                false);

        PreferenceManager.getDefaultSharedPreferences(mContext).registerOnSharedPreferenceChangeListener(this);

        mCurrentTempImage = (ImageView)weatherView.findViewById(R.id.home_weather_image);
        mCurrentLocTempTextView = (TextView)weatherView.findViewById(R.id.home_current_loc_temp);
        mDay3TextView = (TextView)weatherView.findViewById(R.id.day_3);

        mForecastIconIV[0] = (ImageView)weatherView.findViewById(R.id.d1_icon);
        mForecastIconIV[1] = (ImageView)weatherView.findViewById(R.id.d2_icon);
        mForecastIconIV[2] = (ImageView)weatherView.findViewById(R.id.d3_icon);
        mForecastMaxTempTV[0] = (TextView)weatherView.findViewById(R.id.d1_max_temp);
        mForecastMaxTempTV[1] = (TextView)weatherView.findViewById(R.id.d2_max_temp);
        mForecastMaxTempTV[2] = (TextView)weatherView.findViewById(R.id.d3_max_temp);
        mForecastMinTempTV[0] = (TextView)weatherView.findViewById(R.id.d1_min_temp);
        mForecastMinTempTV[1] = (TextView)weatherView.findViewById(R.id.d2_min_temp);
        mForecastMinTempTV[2] = (TextView)weatherView.findViewById(R.id.d3_min_temp);

        updateWeather();

        return weatherView;
    }

    public void setLocationUpdateWeather(double latitude, double longitude) {
        mLastLatitude = latitude;
        mLastLongitude = longitude;

        updateWeather();
    }

    private String getDay3() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        return new SimpleDateFormat("EEEE").format(calendar.getTime());
    }

    private void updateWeather() {
        if(mWebWeather != null) {
            mWebWeather.getWeatherForLocation(mLastLatitude, mLastLongitude);
        }
    }

    private void updateWeatherView() {
        mDay3TextView.setText(getDay3());

        if(mLastWeatherInfo != null) {
            if(mIsMetric) {
                mCurrentLocTempTextView.setText(mLastWeatherInfo.Name + ", " + mLastWeatherInfo.TempC);
            } else {
                mCurrentLocTempTextView.setText(mLastWeatherInfo.Name + ", " + mLastWeatherInfo.TempF);
            }

            if(mLastWeatherInfo.IconId >= 0) {
                mCurrentTempImage.setImageResource(mLastWeatherInfo.IconId);
            }
        }

        if(mLastWeatherForecast != null) {
            for(int i = 0; i < mLastWeatherForecast.Forecast.length && i < FORECAST_DAYS; i++) {
                if(mIsMetric) {
                    mForecastMaxTempTV[i].setText(mLastWeatherForecast.Forecast[i].mMaxTempC);
                    mForecastMinTempTV[i].setText(mLastWeatherForecast.Forecast[i].mMinTempC);
                } else {
                    mForecastMaxTempTV[i].setText(mLastWeatherForecast.Forecast[i].mMaxTempF);
                    mForecastMinTempTV[i].setText(mLastWeatherForecast.Forecast[i].mMinTempF);
                }
                mForecastIconIV[i].setImageResource(mLastWeatherForecast.Forecast[i].IconId);
            }
        }
    }

    @Override
    public void onWeatherInfoReceived(WeatherInfo weatherInfo) {
        mLastWeatherInfo = weatherInfo;
        updateWeatherView();
    }

    @Override
    public void onForecastReceived(WeatherForecast weatherForecast) {
        mLastWeatherForecast = weatherForecast;
        updateWeatherView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("unit_key_preference")) {
            String val = sharedPreferences.getString(key, "C");
            if(val.equals("C")) {
                mIsMetric = true;
            } else {
                mIsMetric = false;
            }
            updateWeatherView();
        }
    }
}
