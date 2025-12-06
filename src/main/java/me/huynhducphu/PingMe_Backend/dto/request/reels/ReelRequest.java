package me.huynhducphu.PingMe_Backend.dto.request.reels;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReelRequest {

    @Size(max = 200, message = "Caption không quá 200 ký tự")
    private String caption;

    // hashtags string, e.g. "#fun,#travel" or "#fun #travel"
    private String hashtags;
}
