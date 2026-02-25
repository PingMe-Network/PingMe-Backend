package org.ping_me.service.weather.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.ping_me.client.WeatherClient;
import org.ping_me.dto.response.weather.WeatherResponse;
import org.ping_me.service.weather.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WeatherServiceImpl implements WeatherService {

    WeatherClient weatherClient;

    @Value("${weather.api.key}")
    @NonFinal
    String apiKey;

    @Value("${weather.api.units}")
    @NonFinal
    String units;

    @Override
    public WeatherResponse getWeather(double lat, double lon) {
        return weatherClient.getWeather(lat, lon, apiKey, units);
    }
}
