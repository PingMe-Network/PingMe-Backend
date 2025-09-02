package me.huynhducphu.PingMe_Backend.dto.response.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;

import java.util.List;

/**
 * Admin 9/2/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoryFriendshipResponse {

    private List<UserSummaryResponse> userSummaryResponses;
    private Long total;

}
