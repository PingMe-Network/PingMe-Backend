package me.huynhducphu.ping_me.controller.music;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.response.music.misc.FavoriteDto;
import me.huynhducphu.ping_me.service.music.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Favorites",
        description = "Quản lý danh sách bài hát yêu thích của người dùng hiện tại"
)
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // ======================= GET FAVORITES =======================
    @Operation(
            summary = "Lấy danh sách bài hát yêu thích",
            description = "Trả về danh sách các bài hát mà người dùng hiện tại đã đánh dấu yêu thích"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách yêu thích thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping
    public ResponseEntity<List<FavoriteDto>> getFavorites() {
        return ResponseEntity.ok(favoriteService.getFavorites());
    }

    // ======================= ADD FAVORITE =======================
    @Operation(
            summary = "Thêm bài hát vào danh sách yêu thích",
            description = "Đánh dấu một bài hát là yêu thích theo songId"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thêm yêu thích thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài hát"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PostMapping("/{songId}")
    public ResponseEntity<Void> addFav(
            @Parameter(description = "ID bài hát", example = "12")
            @PathVariable Long songId
    ) {
        favoriteService.addFavorite(songId);
        return ResponseEntity.ok().build();
    }

    // ======================= REMOVE FAVORITE =======================
    @Operation(
            summary = "Xoá bài hát khỏi danh sách yêu thích",
            description = "Bỏ đánh dấu yêu thích một bài hát theo songId"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xoá yêu thích thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài hát"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> removeFav(
            @Parameter(description = "ID bài hát", example = "12")
            @PathVariable Long songId
    ) {
        favoriteService.removeFavorite(songId);
        return ResponseEntity.noContent().build();
    }

    // ======================= CHECK FAVORITE =======================
    @Operation(
            summary = "Kiểm tra bài hát có nằm trong danh sách yêu thích hay không",
            description = "Trả về true nếu bài hát đã được yêu thích, ngược lại là false"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kiểm tra thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/is/{songId}")
    public ResponseEntity<Boolean> isFavorite(
            @Parameter(description = "ID bài hát", example = "12")
            @PathVariable Long songId
    ) {
        return ResponseEntity.ok(favoriteService.isFavorite(songId));
    }
}
