package me.huynhducphu.PingMe_Backend.controller.music;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDetailDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDto;
import me.huynhducphu.PingMe_Backend.service.music.PlaylistService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(
        name = "Playlists",
        description = "Quản lý playlist người dùng: tạo, cập nhật, xoá, thêm/xoá bài hát, sắp xếp và playlist công khai"
)
@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    // ======================= CREATE =======================
    @Operation(
            summary = "Tạo playlist mới",
            description = "Tạo playlist cho người dùng hiện tại"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo playlist thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PostMapping
    public ResponseEntity<PlaylistDto> createPlaylist(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin playlist",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaylistDto.class))
            )
            @RequestBody PlaylistDto dto
    ) {
        return ResponseEntity.ok(playlistService.createPlaylist(dto));
    }

    // ======================= GET USER PLAYLISTS =======================
    @Operation(
            summary = "Lấy danh sách playlist của người dùng",
            description = "Trả về toàn bộ playlist thuộc về người dùng hiện tại"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách playlist thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping
    public ResponseEntity<List<PlaylistDto>> getPlaylists() {
        return ResponseEntity.ok(playlistService.getPlaylistsByUser());
    }

    // ======================= GET DETAIL =======================
    @Operation(
            summary = "Lấy chi tiết playlist",
            description = "Lấy thông tin chi tiết playlist bao gồm danh sách bài hát"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy chi tiết playlist thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy playlist"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDetailDto> getDetail(
            @Parameter(description = "ID playlist", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(playlistService.getPlaylistDetail(id));
    }

    // ======================= DELETE PLAYLIST =======================
    @Operation(
            summary = "Xoá playlist",
            description = "Xoá playlist của người dùng hiện tại"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xoá playlist thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy playlist"),
            @ApiResponse(responseCode = "403", description = "Không có quyền xoá")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID playlist", example = "1")
            @PathVariable Long id
    ) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    // ======================= ADD SONG =======================
    @Operation(
            summary = "Thêm bài hát vào playlist",
            description = "Thêm bài hát vào playlist, trả về alreadyExists nếu bài hát đã tồn tại"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thêm bài hát thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy playlist hoặc bài hát")
    })
    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> addSong(
            @Parameter(description = "ID playlist", example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID bài hát", example = "12")
            @PathVariable Long songId
    ) {
        boolean added = playlistService.addSongToPlaylist(id, songId);
        return ResponseEntity.ok(Map.of("alreadyExists", !added));
    }

    // ======================= REMOVE SONG =======================
    @Operation(
            summary = "Xoá bài hát khỏi playlist",
            description = "Gỡ bài hát ra khỏi playlist"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xoá bài hát thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy playlist hoặc bài hát")
    })
    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Void> removeSong(
            @Parameter(description = "ID playlist", example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID bài hát", example = "12")
            @PathVariable Long songId
    ) {
        playlistService.removeSongFromPlaylist(id, songId);
        return ResponseEntity.noContent().build();
    }

    // ======================= REORDER SONGS =======================
    @Operation(
            summary = "Sắp xếp lại thứ tự bài hát trong playlist",
            description = "Cập nhật thứ tự bài hát theo danh sách orderedSongIds"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sắp xếp playlist thành công"),
            @ApiResponse(responseCode = "400", description = "Danh sách ID không hợp lệ")
    })
    @PatchMapping("/{id}/songs/reorder")
    public ResponseEntity<Void> reorder(
            @Parameter(description = "ID playlist", example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload sắp xếp: { \"orderedSongIds\": [1,2,3] }",
                    required = true
            )
            @RequestBody Map<String, Object> payload
    ) {
        @SuppressWarnings("unchecked")
        List<Integer> arr = (List<Integer>) payload.get("orderedSongIds");
        List<Long> ordered = arr.stream().map(Integer::longValue).toList();
        playlistService.reorderPlaylist(id, ordered);
        return ResponseEntity.ok().build();
    }

    // ======================= UPDATE PLAYLIST =======================
    @Operation(
            summary = "Cập nhật playlist",
            description = "Cập nhật tên, mô tả hoặc trạng thái public/private của playlist"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật playlist thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy playlist"),
            @ApiResponse(responseCode = "403", description = "Không có quyền cập nhật")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDto> update(
            @Parameter(description = "ID playlist", example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin playlist cập nhật",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaylistDto.class))
            )
            @RequestBody PlaylistDto dto
    ) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, dto));
    }

    // ======================= PUBLIC PLAYLISTS =======================
    @Operation(
            summary = "Lấy danh sách playlist công khai",
            description = "Lấy danh sách playlist public có phân trang"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách playlist công khai thành công")
    })
    @GetMapping("/public")
    public ResponseEntity<Page<PlaylistDto>> getPublicPlaylists(
            @Parameter(description = "Trang hiện tại (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số phần tử mỗi trang", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                playlistService.getPublicPlaylists(page, size)
        );
    }
}
