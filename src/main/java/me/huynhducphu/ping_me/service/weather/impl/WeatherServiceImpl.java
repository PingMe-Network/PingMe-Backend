package me.huynhducphu.ping_me.service.weather.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.response.weather.WeatherResponse;
import me.huynhducphu.ping_me.service.weather.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.units}")
    private String units;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WeatherResponse getWeather(double lat, double lon) {

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .queryParam("units", units)
                .toUriString();

        return restTemplate.getForObject(url, WeatherResponse.class);
    }
}
