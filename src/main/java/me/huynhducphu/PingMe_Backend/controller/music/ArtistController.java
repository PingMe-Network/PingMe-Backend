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
import me.huynhducphu.PingMe_Backend.dto.request.music.ArtistRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.ArtistResponse;
import me.huynhducphu.PingMe_Backend.service.music.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name = "Artists",
        description = "Quản lý nghệ sĩ: tạo mới, cập nhật, tìm kiếm, xoá mềm, xoá cứng và khôi phục"
)
@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // ======================= GET ALL =======================
    @Operation(
            summary = "Lấy danh sách nghệ sĩ",
            description = "Lấy toàn bộ nghệ sĩ chưa bị xoá trong hệ thống"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách nghệ sĩ thành công")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ArtistResponse>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    // ======================= SEARCH =======================
    @Operation(
            summary = "Tìm kiếm nghệ sĩ theo tên",
            description = "Tìm nghệ sĩ theo tên (không phân biệt hoa thường)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ArtistResponse>> searchArtists(
            @Parameter(description = "Tên nghệ sĩ", example = "Taylor Swift")
            @RequestParam String name
    ) {
        return ResponseEntity.ok(artistService.searchArtists(name));
    }

    // ======================= GET BY ID =======================
    @Operation(
            summary = "Lấy chi tiết nghệ sĩ",
            description = "Lấy thông tin chi tiết nghệ sĩ theo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy nghệ sĩ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nghệ sĩ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getArtistById(
            @Parameter(description = "ID nghệ sĩ", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    // ======================= CREATE =======================
    @Operation(
            summary = "Tạo mới nghệ sĩ",
            description = "Tạo nghệ sĩ mới kèm ảnh đại diện (multipart/form-data)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo nghệ sĩ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<ArtistResponse> saveArtist(
            @Parameter(
                    description = "Thông tin nghệ sĩ (JSON)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ArtistRequest.class)
                    )
            )
            @Valid
            @RequestPart("artistRequest") ArtistRequest artistRequest,

            @Parameter(
                    description = "Ảnh đại diện nghệ sĩ",
                    required = true
            )
            @RequestPart("imgFile") MultipartFile imgFile
    ) {
        return ResponseEntity.ok(
                artistService.saveArtist(artistRequest, imgFile)
        );
    }

    // ======================= UPDATE =======================
    @Operation(
            summary = "Cập nhật nghệ sĩ",
            description = "Cập nhật thông tin nghệ sĩ, ảnh đại diện có thể thay đổi hoặc giữ nguyên"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nghệ sĩ")
    })
    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ArtistResponse> updateArtist(
            @Parameter(description = "ID nghệ sĩ", example = "1")
            @PathVariable Long id,

            @Valid
            @RequestPart("artistRequest") ArtistRequest artistRequest,

            @Parameter(description = "Ảnh đại diện mới (có thể bỏ trống)")
            @RequestPart(value = "imgFile", required = false) MultipartFile imgFile
    ) {
        return ResponseEntity.ok(
                artistService.updateArtist(id, artistRequest, imgFile)
        );
    }

    // ======================= SOFT DELETE =======================
    @Operation(
            summary = "Xoá mềm nghệ sĩ",
            description = "Đánh dấu nghệ sĩ là đã xoá (có thể khôi phục)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xoá mềm thành công")
    })
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDeleteArtist(
            @Parameter(description = "ID nghệ sĩ", example = "1")
            @PathVariable Long id
    ) {
        artistService.softDeleteArtist(id);
        return ResponseEntity.ok().build();
    }

    // ======================= HARD DELETE =======================
    @Operation(
            summary = "Xoá cứng nghệ sĩ",
            description = "Xoá vĩnh viễn nghệ sĩ khỏi hệ thống"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xoá cứng thành công")
    })
    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteArtist(
            @Parameter(description = "ID nghệ sĩ", example = "1")
            @PathVariable Long id
    ) {
        artistService.hardDeleteArtist(id);
        return ResponseEntity.ok().build();
    }

    // ======================= RESTORE =======================
    @Operation(
            summary = "Khôi phục nghệ sĩ",
            description = "Khôi phục nghệ sĩ đã bị xoá mềm"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Khôi phục thành công")
    })
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreArtist(
            @Parameter(description = "ID nghệ sĩ", example = "1")
            @PathVariable Long id
    ) {
        artistService.restoreArtist(id);
        return ResponseEntity.ok().build();
    }
}
