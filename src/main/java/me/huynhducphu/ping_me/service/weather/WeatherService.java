package me.huynhducphu.ping_me.service.weather;

import me.huynhducphu.ping_me.dto.response.weather.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeather(double lat, double lon);
}
