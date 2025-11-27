package me.huynhducphu.PingMe_Backend.service.music.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.music.SongRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponseWithAllAlbum;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.AlbumSummaryDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.ArtistSummaryDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.model.constant.ArtistRole;
import me.huynhducphu.PingMe_Backend.model.music.*;
import me.huynhducphu.PingMe_Backend.repository.music.*;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.integration.S3Service;
import me.huynhducphu.PingMe_Backend.service.music.SongService;
import me.huynhducphu.PingMe_Backend.service.music.util.AudioUtil;
import me.huynhducphu.PingMe_Backend.repository.music.GenreRepository;
import me.huynhducphu.PingMe_Backend.repository.music.SongPlayHistoryRepository;
import me.huynhducphu.PingMe_Backend.repository.music.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final SongArtistRoleRepository songArtistRoleRepository;
    private final AudioUtil audioUtil;
    private final SongPlayHistoryRepository songPlayHistoryRepository;
    @Autowired
    @Qualifier("redisMessageStringTemplate")
    private RedisTemplate<String, String> redis;
    private final S3Service s3Service;
    private final CurrentUserProvider currentUserProvider;

    public List<SongResponseWithAllAlbum> getAllSongs() {
        List<Song> songs = songRepository.findAll();

        List<SongResponseWithAllAlbum> result = new ArrayList<>();
        for (Song song : songs) {
            SongResponseWithAllAlbum response = new SongResponseWithAllAlbum();

            // --- Map các trường cơ bản ---
            response.setId(song.getId());
            response.setTitle(song.getTitle());
            response.setDuration(song.getDuration());
            response.setPlayCount(song.getPlayCount());
            response.setSongUrl(song.getSongUrl());
            response.setCoverImageUrl(song.getImgUrl());

            // --- Xử lý Artist ---
            List<SongArtistRole> roles = song.getArtistRoles();

            // Main Artist
            Optional<Artist> mainArtistOpt = roles.stream()
                    .filter(r -> r.getRole() == ArtistRole.MAIN_ARTIST)
                    .map(SongArtistRole::getArtist)
                    .findFirst();
            mainArtistOpt.ifPresent(a -> response.setMainArtist(
                    new ArtistSummaryDto(a.getId(), a.getName(), a.getImgUrl())
            ));

            // Featured Artists
            List<ArtistSummaryDto> featuredList = roles.stream()
                    .filter(r -> r.getRole() == ArtistRole.FEATURED_ARTIST)
                    .map(r -> {
                        Artist a = r.getArtist();
                        return new ArtistSummaryDto(a.getId(), a.getName(), a.getImgUrl());
                    })
                    .collect(Collectors.toList());
            response.setFeaturedArtists(featuredList);

            // --- Xử lý Genres ---
            List<GenreDto> genreDtos = song.getGenres().stream()
                    .map(g -> new GenreDto(g.getId(), g.getName()))
                    .collect(Collectors.toList());
            response.setGenres(genreDtos);

            // --- Xử lý Albums ---
            if (song.getAlbums() != null && !song.getAlbums().isEmpty()) {
                List<AlbumSummaryDto> albumSummaries = song.getAlbums().stream()
                        .map(a -> new AlbumSummaryDto(a.getId(), a.getTitle(), a.getPlayCount()))
                        .collect(Collectors.toList());
                response.setAlbums(albumSummaries);
            }

            result.add(response);
        }

        return result;
    }


    @Override
    public SongResponse getSongById(Long id) {
        Song song = songRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài hát với id: " + id));

        return mapToSongResponse(song, song.getAlbums() != null && !song.getAlbums().isEmpty()
                ? song.getAlbums().iterator().next()
                : null);
    }

    @Override
    public List<SongResponse> getSongByTitle(String title) {
        //Lấy danh sách bài hát từ DB theo title (case-insensitive)
        List<Song> songs = songRepository.findSongsByTitleContainingIgnoreCase(title);

        return flattenSongsWithAlbums(songs);
    }

    @Override
    public List<SongResponse> getSongByGenre(Long id) { // Hoặc nhận thẳng Long genreId
        if (id == null) {
            throw new RuntimeException("Genre ID không được trống");
        }

        // Gọi hàm vừa viết - Chỉ tốn đúng 1 query
        List<Song> songs = songRepository.findSongsByGenreId(id);

        return flattenSongsWithAlbums(songs);
    }

    // Cho phép truyền số lượng bài muốn lấy
    public List<SongResponse> getTopPlayedSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Song> topSongs = songRepository.findSongsByPlayCount(pageable);
        return flattenSongsWithAlbums(topSongs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // Rollback nếu lỗi S3 hoặc DB
    public List<SongResponse> save(
            SongRequest dto, MultipartFile musicFile, MultipartFile imgFile
    ) {

        // 1. Validate file đầu vào (Bắt buộc phải có)
        if (musicFile == null || musicFile.isEmpty()) {
            throw new RuntimeException("Vui lòng tải lên file nhạc");
        }
        if (imgFile == null || imgFile.isEmpty()) {
            throw new RuntimeException("Vui lòng tải lên ảnh bìa");
        }

        // 2. Khởi tạo Song Entity
        var song = new Song();
        song.setTitle(dto.getTitle());

        int calculatedDuration = audioUtil.getDurationFromMusicFile(musicFile);
        if (calculatedDuration <= 0) {
            throw new RuntimeException("File nhạc lỗi hoặc không xác định được độ dài");
        }
        song.setDuration(calculatedDuration);

        song.setPlayCount(0L); // Mặc định 0 view

        // 3. Upload File Nhạc lên S3
        String audioFileName = generateFileName(musicFile);
        String songUrl = s3Service.uploadFile(
                musicFile,
                "music/song", // Folder trên S3
                audioFileName,
                true, // Lấy URL về
                MAX_AUDIO_SIZE
        );
        song.setSongUrl(songUrl);

        // 4. Upload File Ảnh lên S3
        String imageFileName = generateFileName(imgFile);
        String imgUrl = s3Service.uploadFile(
                imgFile,
                "music/img", // Folder trên S3
                imageFileName,
                true,
                MAX_COVER_SIZE
        );
        song.setImgUrl(imgUrl);

        // 5. Xử lý Genre (Thể loại)
        if (dto.getGenreIds() != null && dto.getGenreIds().length > 0) {
            // Lưu ý: DTO tên là Names nhưng kiểu Long[] nên t hiểu là IDs
            var genreIds = Arrays.asList(dto.getGenreIds());
            var genres = new HashSet<>(genreRepository.findAllById(genreIds));
            song.setGenres(genres);
        }

        // 6. Lưu tạm Song để có ID (quan trọng cho bước ArtistRole)
        var savedSong = songRepository.save(song);

        // 7. Xử lý Artist (Main & Featured)
        List<SongArtistRole> artistRoles = new ArrayList<>();

        // 7a. Main Artist
        var mainArtist = artistRepository.findById(dto.getMainArtistId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ chính"));

        var mainRole = new SongArtistRole();
        mainRole.setSong(savedSong);
        mainRole.setArtist(mainArtist);
        mainRole.setRole(ArtistRole.MAIN_ARTIST);
        artistRoles.add(mainRole);

        // 7b. Featured Artists
        if (dto.getFeaturedArtistIds() != null) {
            for (Long featId : dto.getFeaturedArtistIds()) {
                var featArtist = artistRepository.findById(featId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ phụ ID: " + featId));

                var featRole = new SongArtistRole();
                featRole.setSong(savedSong);
                featRole.setArtist(featArtist);
                featRole.setRole(ArtistRole.FEATURED_ARTIST);
                artistRoles.add(featRole);
            }
        }

        // Lưu Batch roles
        songArtistRoleRepository.saveAll(artistRoles);
        savedSong.setArtistRoles(artistRoles); // Update lại object để map response

        // 8. Xử lý Album (CẬP NHẬT METADATA)
        if (dto.getAlbumId() != null && dto.getAlbumId().length > 0) {
            var albumIds = Arrays.asList(dto.getAlbumId());
            var albums = new HashSet<>(albumRepository.findAllById(albumIds));

            // Lấy danh sách Featured Artists của bài hát hiện tại ra trước
            List<Artist> allArtistsInSong = artistRoles.stream()
                    .map(SongArtistRole::getArtist)
                    .toList();

            for (var album : albums) {
                // A. Thêm bài hát vào Album
                album.getSongs().add(savedSong);

                // B. Cập nhật Genre cho Album (Tự động merge, ko sợ trùng vì dùng Set)
                if (savedSong.getGenres() != null) {
                    album.getGenres().addAll(savedSong.getGenres());
                }

                // C. Cập nhật Featured Artist cho Album (Tự động merge)
                if (!allArtistsInSong.isEmpty()) {
                    // Lưu ý: Phải đảm bảo list featuredArtists trong Album đã được khởi tạo (new HashSet)
                    // Nếu chưa thì check null: if(album.getFeaturedArtists() == null) album.setFeaturedArtists(new HashSet<>());
                    album.getFeaturedArtists().addAll(allArtistsInSong);
                }

                // D. Lưu Album -> Hibernate sẽ update cả bảng album_song, album_genre, album_artist
                albumRepository.save(album);
            }

            // Set ngược lại cho song (chỉ để hiển thị)
            savedSong.setAlbums(albums);
        }

        // 9. Map sang Response và trả về
        List<Song> songs = new ArrayList<>();
        songs.add(savedSong);
        return flattenSongsWithAlbums(songs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SongResponse> update(Long id, SongRequest dto, MultipartFile musicFile, MultipartFile imgFile) {
        // 1. Tìm bài hát
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài hát với ID: " + id));

        // 2. Update Title
        song.setTitle(dto.getTitle());

        // 3. Xử lý Audio File (Nếu có file mới gửi lên)
        if (musicFile != null && !musicFile.isEmpty()) {
            // A. Xóa file cũ
            try {
                if (song.getSongUrl() != null) s3Service.deleteFileByUrl(song.getSongUrl());
            } catch (Exception e) { /* Log warning */ }

            // B. Upload file mới
            String audioName = generateFileName(musicFile);
            String newUrl = s3Service.uploadFile(musicFile, "music/song", audioName, true, MAX_AUDIO_SIZE);
            song.setSongUrl(newUrl);

            // C. Tính lại duration
            int newDuration = audioUtil.getDurationFromMusicFile(musicFile);
            if (newDuration > 0) song.setDuration(newDuration);
        }

        // 4. Xử lý Image File (Nếu có file mới gửi lên)
        if (imgFile != null && !imgFile.isEmpty()) {
            // A. Xóa ảnh cũ
            try {
                if (song.getImgUrl() != null) s3Service.deleteFileByUrl(song.getImgUrl());
            } catch (Exception e) { /* Log warning */ }

            // B. Upload ảnh mới
            String imgName = generateFileName(imgFile);
            String newImgUrl = s3Service.uploadFile(imgFile, "music/img", imgName, true, MAX_COVER_SIZE);
            song.setImgUrl(newImgUrl);
        }

        // 5. Update Genres (Cơ chế: Xóa hết cũ -> Thêm mới)
        if (dto.getGenreIds() != null) {
            song.getGenres().clear(); // Xóa set hiện tại
            if (dto.getGenreIds().length > 0) {
                var genreIds = Arrays.asList(dto.getGenreIds());
                var newGenres = new HashSet<>(genreRepository.findAllById(genreIds));
                song.getGenres().addAll(newGenres);
            }
        }

        // 6. Update Artist (Cơ chế: Xóa hết Role cũ -> Tạo Role mới)
        // Lưu ý: Phải xóa thủ công trong DB vì logic update list này khá phức tạp
        songArtistRoleRepository.deleteAll(song.getArtistRoles());
        song.getArtistRoles().clear();

        List<SongArtistRole> newRoles = new ArrayList<>();

        // 6a. Main Artist
        var mainArtist = artistRepository.findById(dto.getMainArtistId())
                .orElseThrow(() -> new RuntimeException("Main Artist not found"));
        newRoles.add(new SongArtistRole(null, song, mainArtist, ArtistRole.MAIN_ARTIST));

        // 6b. Featured Artists
        if (dto.getFeaturedArtistIds() != null) {
            for (Long featId : dto.getFeaturedArtistIds()) {
                var featArtist = artistRepository.findById(featId)
                        .orElseThrow(() -> new RuntimeException("Featured Artist not found"));
                newRoles.add(new SongArtistRole(null, song, featArtist, ArtistRole.FEATURED_ARTIST));
            }
        }
        songArtistRoleRepository.saveAll(newRoles);
        song.setArtistRoles(newRoles);

        // 7. Update Albums (Logic phức tạp nhất)
        // Bước A: Gỡ bài hát khỏi TẤT CẢ album cũ trước
        if (song.getAlbums() != null && !song.getAlbums().isEmpty()) {
            for (Album oldAlbum : song.getAlbums()) {
                oldAlbum.getSongs().remove(song);
                albumRepository.save(oldAlbum);
            }
            song.getAlbums().clear();
        }

        // Bước B: Thêm vào các album mới được chọn
        if (dto.getAlbumId() != null && dto.getAlbumId().length > 0) {
            var newAlbumIds = Arrays.asList(dto.getAlbumId());
            var newAlbums = new HashSet<>(albumRepository.findAllById(newAlbumIds));

            // Lấy danh sách Artist để merge vào Album
            List<Artist> allArtists = newRoles.stream().map(SongArtistRole::getArtist).toList();

            for (Album album : newAlbums) {
                // Thêm song
                album.getSongs().add(song);

                // Merge Metadata (Genre + Artist) vào Album
                if (song.getGenres() != null) {
                    album.getGenres().addAll(song.getGenres());
                }
                if (!allArtists.isEmpty()) {
                    if (album.getFeaturedArtists() == null) album.setFeaturedArtists(new HashSet<>());
                    album.getFeaturedArtists().addAll(allArtists);
                    // Remove owner khỏi featured
                    if (album.getAlbumOwner() != null) album.getFeaturedArtists().remove(album.getAlbumOwner());
                }
                albumRepository.save(album);
            }
            song.setAlbums(newAlbums);
        }

        // 8. Save & Return
        Song updatedSong = songRepository.save(song);

        List<Song> songs = new ArrayList<>();
        songs.add(updatedSong);
        return flattenSongsWithAlbums(songs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài hát với ID: " + id));

        // Đánh dấu là đã xóa
        song.setDeleted(true);

        // Lưu lại trạng thái mới
        songRepository.save(song);

        // Tùy chọn: Nếu muốn user không tìm thấy bài hát này trong Album nữa,
        // bạn có thể remove nó khỏi album (logic giống hard delete) hoặc giữ nguyên.
        // Nếu giữ nguyên thì user vào Album vẫn thấy bài hát (trừ khi Album cũng lọc song deleted).
    }

    @Override
    @Transactional
    public void restore(Long id) {

        Song song = songRepository.findSoftDeletedSong(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài hát đã xóa mềm"));

        song.setDeleted(false);
        songRepository.save(song);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void hardDelete(Long id) {
        // 1. Tìm bài hát (Nếu ko thấy thì báo lỗi)
        Song song = songRepository.findByIdIgnoringDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài hát với ID: " + id));

        // 2. Gỡ bài hát ra khỏi các Album (Quan trọng)
        // Vì Album là bên sở hữu quan hệ (Owner), ta nên cập nhật từ phía Album
        if (song.getAlbums() != null && !song.getAlbums().isEmpty()) {
            for (Album album : song.getAlbums()) {
                // Xóa song khỏi set songs của album
                album.getSongs().remove(song);
                // Lưu lại album để cập nhật bảng trung gian album_song
                albumRepository.save(album);
            }
        }

        // 3. Xóa file trên S3 (Dọn rác)
        // Bọc trong try-catch để lỡ S3 lỗi thì vẫn cho phép xóa DB (tùy nghiệp vụ, ở đây t để strict)
        try {
            if (song.getSongUrl() != null) {
                s3Service.deleteFileByUrl(song.getSongUrl());
            }
            if (song.getImgUrl() != null) {
                s3Service.deleteFileByUrl(song.getImgUrl());
            }
        } catch (Exception e) {
            // Tùy chọn: Throw lỗi để rollback DB nếu muốn bắt buộc xóa S3 thành công
            throw new RuntimeException("Lỗi khi xóa file trên S3: " + e.getMessage());
        }

        // 4. Xóa bài hát trong DB
        // Hibernate sẽ tự động xóa các dòng trong bảng con song_artist_role (do CascadeType.ALL)
        // và bảng trung gian song_genre.
        songRepository.delete(song);
    }

    // --- Helper Methods ---

    private String generateFileName(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        return UUID.randomUUID() + ext;
    }

    private List<SongResponse> flattenSongsWithAlbums(List<Song> songs) {
        List<SongResponse> result = new ArrayList<>();
        for (Song song : songs) {
            if (song.getAlbums() != null && !song.getAlbums().isEmpty()) {
                for (Album album : song.getAlbums()) {
                    result.add(mapToSongResponse(song, album));
                }
            } else {
                // Nếu song không có album, vẫn trả về song với album = null
                result.add(mapToSongResponse(song, null));
            }
        }
        return result;
    }

    @Transactional
    @Override
    public void increasePlayCount(Long songId) {
        var userId = currentUserProvider.get().getId();
        String redisKey = "play:" + userId + ":" + songId;

        // Nếu trong 30s đã nghe → không tăng tiếp
        Boolean alreadyPlayed = redis.hasKey(redisKey);
        if (Boolean.TRUE.equals(alreadyPlayed)) return;

        // Tăng playCount
        songRepository.incrementPlayCount(songId, userId);

        // Lấy song để log lịch sử
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));

        // Lưu lịch sử nghe
        songPlayHistoryRepository.save(
                SongPlayHistory.builder()
                        .song(song)
                        .userId(userId)
                        .playedAt(LocalDateTime.now())
                        .build()
        );
        // Set key Redis sống 30s → debounce
        redis.opsForValue().set(redisKey, "1", Duration.ofSeconds(30));
    }

    private SongResponse mapToSongResponse(Song song, Album album) {
        SongResponse response = new SongResponse();

        // --- Map các trường cơ bản ---
        response.setId(song.getId());
        response.setTitle(song.getTitle());
        response.setDuration(song.getDuration());
        response.setPlayCount(song.getPlayCount());
        response.setSongUrl(song.getSongUrl());
        response.setCoverImageUrl(song.getImgUrl());

        // --- Xử lý Artist ---
        List<SongArtistRole> roles = song.getArtistRoles();

        // Main Artist
        Optional<Artist> mainArtistOpt = roles.stream()
                .filter(r -> r.getRole() == ArtistRole.MAIN_ARTIST)
                .map(SongArtistRole::getArtist)
                .findFirst();
        mainArtistOpt.ifPresent(a -> response.setMainArtist(
                new ArtistSummaryDto(a.getId(), a.getName(), a.getImgUrl())
        ));

        // Featured Artists
        List<ArtistSummaryDto> featuredList = roles.stream()
                .filter(r -> r.getRole() == ArtistRole.FEATURED_ARTIST)
                .map(r -> {
                    Artist a = r.getArtist();
                    return new ArtistSummaryDto(a.getId(), a.getName(), a.getImgUrl());
                })
                .collect(Collectors.toList());
        response.setFeaturedArtists(featuredList);

        // --- Xử lý Genres ---
        List<GenreDto> genreDtos = song.getGenres().stream()
                .map(g -> new GenreDto(g.getId(), g.getName()))
                .collect(Collectors.toList());
        response.setGenres(genreDtos);

        // --- Xử lý Album ---
        if (album != null) {
            response.setAlbum(new AlbumSummaryDto(album.getId(), album.getTitle(), album.getPlayCount()));
        }

        return response;
    }
}
