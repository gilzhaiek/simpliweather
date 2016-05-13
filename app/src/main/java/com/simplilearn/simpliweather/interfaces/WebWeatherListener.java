package com.simplilearn.simpliweather.interfaces;

import com.simplilearn.simpliweather.data.WeatherForecast;
import com.simplilearn.simpliweather.data.WeatherInfo;

public interface WebWeatherListener {
    void onWeatherInfoReceived(WeatherInfo weatherInfo);
    void onForecastReceived(WeatherForecast weatherForecast);
}
