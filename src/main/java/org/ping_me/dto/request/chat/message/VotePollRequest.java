package org.ping_me.dto.request.chat.message;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Admin 4/23/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VotePollRequest {

    @NotNull(message = "Danh sách lựa chọn không được để trống")
    private List<String> optionIds;
}
