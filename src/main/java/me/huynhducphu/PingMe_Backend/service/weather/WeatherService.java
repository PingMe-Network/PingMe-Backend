package me.huynhducphu.PingMe_Backend.service.weather;

import me.huynhducphu.PingMe_Backend.dto.request.miniapp.weather.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeather(double lat, double lon);
}
