package me.huynhducphu.PingMe_Backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
