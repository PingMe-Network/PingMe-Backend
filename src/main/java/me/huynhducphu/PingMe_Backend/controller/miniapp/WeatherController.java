package me.huynhducphu.PingMe_Backend.controller.miniapp;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.weather.WeatherResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.ApiResponse;
import me.huynhducphu.PingMe_Backend.service.weather.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(weatherService.getWeather(lat, lon))
        );
    }

}
