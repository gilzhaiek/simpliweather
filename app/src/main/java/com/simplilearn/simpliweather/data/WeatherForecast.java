package com.simplilearn.simpliweather.data;

import com.simplilearn.simpliweather.data.DailyForecast;

public class WeatherForecast {
    public final static int TOTAL_DAYS = 5;
    public String City;
    public DailyForecast Forecast[] = new DailyForecast[5];
}
