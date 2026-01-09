package me.huynhducphu.ping_me.controller.music;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.request.music.GenreRequest;
import me.huynhducphu.ping_me.dto.response.music.GenreResponse;
import me.huynhducphu.ping_me.service.music.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Genres",
        description = "Quản lý thể loại âm nhạc: tạo mới, cập nhật, xoá mềm, xoá cứng và khôi phục"
)
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    // ======================= GET ALL =======================
    @Operation(
            summary = "Lấy danh sách thể loại",
            description = "Lấy toàn bộ thể loại âm nhạc chưa bị xoá"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thể loại thành công")
    })
    @GetMapping("/all")
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    // ======================= GET BY ID =======================
    @Operation(
            summary = "Lấy chi tiết thể loại",
            description = "Lấy thông tin thể loại theo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy thể loại"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thể loại")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenreById(
            @Parameter(description = "ID thể loại", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    // ======================= CREATE =======================
    @Operation(
            summary = "Tạo mới thể loại",
            description = "Tạo thể loại âm nhạc mới"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thể loại thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<GenreResponse> saveGenre(
            @Parameter(
                    description = "Thông tin thể loại",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = GenreRequest.class)
                    )
            )
            @Valid
            @RequestPart("genreRequest") GenreRequest genreRequest
    ) {
        return ResponseEntity.ok(genreService.createGenre(genreRequest));
    }

    // ======================= UPDATE =======================
    @Operation(
            summary = "Cập nhật thể loại",
            description = "Cập nhật thông tin thể loại theo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thể loại")
    })
    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<GenreResponse> updateGenre(
            @Parameter(description = "ID thể loại", example = "1")
            @PathVariable Long id,

            @Valid
            @RequestPart("genreRequest") GenreRequest request
    ) {
        return ResponseEntity.ok(genreService.updateGenre(id, request));
    }

    // ======================= SOFT DELETE =======================
    @Operation(
            summary = "Xoá mềm thể loại",
            description = "Đánh dấu thể loại là đã xoá (có thể khôi phục)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xoá mềm thành công")
    })
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDeleteGenre(
            @Parameter(description = "ID thể loại", example = "1")
            @PathVariable Long id
    ) {
        genreService.softDeleteGenre(id);
        return ResponseEntity.ok().build();
    }

    // ======================= RESTORE =======================
    @Operation(
            summary = "Khôi phục thể loại",
            description = "Khôi phục thể loại đã bị xoá mềm"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Khôi phục thành công")
    })
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreGenre(
            @Parameter(description = "ID thể loại", example = "1")
            @PathVariable Long id
    ) {
        genreService.restoreGenre(id);
        return ResponseEntity.ok().build();
    }

    // ======================= HARD DELETE =======================
    @Operation(
            summary = "Xoá cứng thể loại",
            description = "Xoá vĩnh viễn thể loại khỏi hệ thống"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xoá cứng thành công")
    })
    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteGenre(
            @Parameter(description = "ID thể loại", example = "1")
            @PathVariable Long id
    ) {
        genreService.hardDeleteGenre(id);
        return ResponseEntity.ok().build();
    }
}
