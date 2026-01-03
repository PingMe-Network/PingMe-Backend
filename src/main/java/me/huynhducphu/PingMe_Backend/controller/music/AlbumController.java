package me.huynhducphu.PingMe_Backend.controller.music;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.music.AlbumRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.AlbumResponse;
import me.huynhducphu.PingMe_Backend.service.music.AlbumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name = "Albums",
        description = "Quản lý album âm nhạc: tạo mới, cập nhật, tìm kiếm, xoá mềm, xoá cứng và khôi phục"
)
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    // ======================= GET ALL =======================
    @Operation(
            summary = "Lấy danh sách tất cả album",
            description = "API trả về toàn bộ album chưa bị xoá trong hệ thống"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách album thành công")
    })
    @GetMapping("/all")
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    // ======================= GET BY ID =======================
    @Operation(
            summary = "Lấy chi tiết album theo ID",
            description = "Truy xuất thông tin chi tiết của album thông qua ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy album"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy album")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbumById(
            @Parameter(description = "ID của album", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    // ======================= SEARCH =======================
    @Operation(
            summary = "Tìm kiếm album theo tiêu đề",
            description = "Tìm các album có tiêu đề chứa từ khoá (không phân biệt hoa thường)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công")
    })
    @GetMapping("/search")
    public ResponseEntity<List<AlbumResponse>> searchAlbums(
            @Parameter(description = "Từ khoá tìm kiếm theo tiêu đề", example = "Love")
            @RequestParam String title
    ) {
        return ResponseEntity.ok(
                albumService.getAlbumByTitleContainIgnoreCase(title)
        );
    }

    // ======================= CREATE =======================
    @Operation(
            summary = "Tạo mới album",
            description = "Tạo album mới kèm ảnh bìa (multipart/form-data)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo album thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<AlbumResponse> save(
            @Parameter(
                    description = "Thông tin album (JSON)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AlbumRequest.class)
                    )
            )
            @Valid
            @RequestPart("albumRequest") AlbumRequest albumRequest,

            @Parameter(
                    description = "Ảnh bìa album",
                    required = true
            )
            @RequestPart("albumCoverImg") MultipartFile albumCoverImg
    ) {
        return ResponseEntity.ok(
                albumService.save(albumRequest, albumCoverImg)
        );
    }

    // ======================= UPDATE =======================
    @Operation(
            summary = "Cập nhật album",
            description = "Cập nhật thông tin album và ảnh bìa"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy album")
    })
    @PutMapping(value = "/update/{albumId}", consumes = "multipart/form-data")
    public ResponseEntity<AlbumResponse> update(
            @Parameter(description = "ID album cần cập nhật", example = "1")
            @PathVariable Long albumId,

            @Valid
            @RequestPart("albumRequest") AlbumRequest albumRequestDto,

            @RequestPart("albumCoverImg") MultipartFile albumCoverImg
    ) {
        return ResponseEntity.ok(
                albumService.update(albumId, albumRequestDto, albumCoverImg)
        );
    }

    // ======================= SOFT DELETE =======================
    @Operation(
            summary = "Xoá mềm album",
            description = "Đánh dấu album là đã xoá (có thể khôi phục)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xoá mềm thành công")
    })
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDelete(
            @Parameter(description = "ID album", example = "1")
            @PathVariable Long id
    ) {
        albumService.softDelete(id);
        return ResponseEntity.ok().build();
    }

    // ======================= HARD DELETE =======================
    @Operation(
            summary = "Xoá cứng album",
            description = "Xoá vĩnh viễn album khỏi hệ thống"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xoá cứng thành công")
    })
    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDelete(
            @Parameter(description = "ID album", example = "1")
            @PathVariable Long id
    ) {
        albumService.hardDelete(id);
        return ResponseEntity.ok().build();
    }

    // ======================= RESTORE =======================
    @Operation(
            summary = "Khôi phục album",
            description = "Khôi phục album đã bị xoá mềm"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Khôi phục thành công")
    })
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restore(
            @Parameter(description = "ID album", example = "1")
            @PathVariable Long id
    ) {
        albumService.restore(id);
        return ResponseEntity.ok().build();
    }
}
