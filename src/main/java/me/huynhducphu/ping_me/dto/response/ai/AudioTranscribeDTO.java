package me.huynhducphu.ping_me.dto.response.ai;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Le Tran Gia Huy
 * @created 03/03/2026 - 3:27 PM
 * @project PingMe-Backend
 * @package me.huynhducphu.ping_me.dto.response.ai
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AudioTranscribeDTO {
    String content;
}
