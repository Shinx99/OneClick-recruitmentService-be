package com.onceClick.recruitmentService.infrastructure.seeder;

import com.onceClick.recruitmentService.features.scanCv_Profile.ScanCvHandler;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ParsedResumeResponse;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ScanCvRequest;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class MinioSeeder implements ApplicationRunner {

    private final MinioClient minioClient;
    private final JdbcTemplate jdbcTemplate;
    private final ScanCvHandler scanCvHandler;

    @Value("${spring.cloud.aws.s3.bucket:recruitment-files}")
    private String bucket;

    // Mapping: fileName → candidate_id
    private static final List<String[]> SEED_FILES = List.of(
            new String[]{"01_NguyenVanAn.pdf",    "a1a1a1a1-0001-0001-0001-a1a1a1a1a1a1"},
            new String[]{"02_TranThiBich.pdf",     "a1a1a1a1-0002-0002-0002-a1a1a1a1a1a2"},
            new String[]{"03_LeMinhDuc.pdf",       "a1a1a1a1-0003-0003-0003-a1a1a1a1a1a3"},
            new String[]{"04_PhamThiHuong.pdf",    "a1a1a1a1-0004-0004-0004-a1a1a1a1a1a4"},
            new String[]{"05_HoangVanKhai.pdf",    "a1a1a1a1-0005-0005-0005-a1a1a1a1a1a5"},
            new String[]{"06_VoThiLan.pdf",        "a1a1a1a1-0006-0006-0006-a1a1a1a1a1a6"},
            new String[]{"07_DangQuocMinh.pdf",    "a1a1a1a1-0007-0007-0007-a1a1a1a1a1a7"},
            new String[]{"08_BuiThiNgoc.pdf",      "a1a1a1a1-0008-0008-0008-a1a1a1a1a1a8"},
            new String[]{"09_NguyenHoangPhuc.pdf", "a1a1a1a1-0009-0009-0009-a1a1a1a1a1a9"},
            new String[]{"10_TranVanQuang.pdf",    "a1a1a1a1-0010-0010-0010-a1a1a1a1a1a0"},
            new String[]{"11_LyThiSau.pdf",        "a1a1a1a1-0011-0011-0011-a1a1a1a1a1b1"},
            new String[]{"12_DinhVanTam.pdf",      "a1a1a1a1-0012-0012-0012-a1a1a1a1a1b2"},
            new String[]{"13_NgoThiUyen.pdf",      "a1a1a1a1-0013-0013-0013-a1a1a1a1a1b3"},
            new String[]{"14_TruongVanVinh.pdf",   "a1a1a1a1-0014-0014-0014-a1a1a1a1a1b4"},
            new String[]{"15_MaiThiXuan.pdf",      "a1a1a1a1-0015-0015-0015-a1a1a1a1a1b5"},
            new String[]{"16_CaoVanYen.pdf",       "a1a1a1a1-0016-0016-0016-a1a1a1a1a1b6"},
            new String[]{"17_LuuThiDung.pdf",      "a1a1a1a1-0017-0017-0017-a1a1a1a1a1b7"},
            new String[]{"18_HaVanBach.pdf",       "a1a1a1a1-0018-0018-0018-a1a1a1a1a1b8"},
            new String[]{"19_PhanThiCam.pdf",      "a1a1a1a1-0019-0019-0019-a1a1a1a1a1b9"},
            new String[]{"20_KieuVanDong.pdf",     "a1a1a1a1-0020-0020-0020-a1a1a1a1a1c0"}
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ensureBucketExists();

        int success = 0, skipped = 0, failed = 0;

        for (String[] entry : SEED_FILES) {
            String fileName    = entry[0];
            String candidateId = entry[1];

            // Idempotent: bỏ qua nếu đã có parsed_data
            if (resumeAlreadyParsed(candidateId)) {
                log.info("[MinioSeeder] Skipped (already parsed): {}", candidateId);
                skipped++;
                continue;
            }

            try (InputStream pdf = getClass()
                    .getResourceAsStream("/seed-files/" + fileName)) {

                if (pdf == null) {
                    log.warn("[MinioSeeder] File not found in resources: {}", fileName);
                    failed++;
                    continue;
                }

                // Wrap InputStream → MultipartFile để truyền vào ScanCvRequest
                // handle() sẽ tự: upload MinIO + extract text + AI parse + save DB
                MultipartFile mockFile = new InputStreamMultipartFile(
                        fileName,
                        "application/pdf",
                        pdf
                );

                ScanCvRequest req = new ScanCvRequest(mockFile);
                UUID candidateUUID = UUID.fromString(candidateId);

                ParsedResumeResponse response = scanCvHandler.handle(candidateUUID, req);

                if (response.isSuccess()) {
                    log.info("[MinioSeeder] Seeded resume for candidate: {}", candidateId);
                    success++;
                } else {
                    log.warn("[MinioSeeder] Warning for {}: {}", candidateId, response.getMessage());
                    failed++;
                }

                // Tránh rate limit AI (Gemini/OpenAI)
                Thread.sleep(1_500);

            } catch (Exception e) {
                log.error("[MinioSeeder] ❌ Error processing {}: {}", fileName, e.getMessage(), e);
                failed++;
            }
        }

        log.info("[MinioSeeder] Done — success: {}, ⏭ skipped: {}, failed: {}",
                success, skipped, failed);
    }

    // ── Helpers ──────────────────────────────────────────

    private boolean resumeAlreadyParsed(String candidateId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM resume WHERE candidate_id = ?::uuid AND parsed_data IS NOT NULL",
                Integer.class, candidateId
        );
        return count != null && count > 0;
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build()
        );
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("[MinioSeeder] Created bucket: {}", bucket);
        }
    }
}