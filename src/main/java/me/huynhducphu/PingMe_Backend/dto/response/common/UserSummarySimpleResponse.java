package me.huynhducphu.PingMe_Backend.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.FriendshipStatus;
import me.huynhducphu.PingMe_Backend.model.constant.UserStatus;

/**
 * @author Le Tran Gia Huy
 * @created 09/12/2025 - 1:27 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.dto.response.common
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummarySimpleResponse {
    private Long id;
    private String name;
    private String avatarUrl;
}
