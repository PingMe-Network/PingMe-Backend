package org.ping_me.client;

import org.ping_me.dto.response.weather.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "weather-api",
        url = "${weather.api.base-url}"
)
public interface WeatherClient {

    @GetMapping
    WeatherResponse getWeather(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam("appid") String apiKey,
            @RequestParam("units") String units
    );
}
