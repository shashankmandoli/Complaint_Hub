package com.complainthub.test;

import com.complainthub.service.LocalFileStorageService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/api/test/file-storage")
public class FileStorageTestServlet extends HttpServlet {

    private final LocalFileStorageService fileStorageService =
            new LocalFileStorageService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("text/plain");

        StringBuilder result = new StringBuilder();

        result.append("=== FILE STORAGE TEST ===\n\n");

        int passed = 0;
        int failed = 0;

        // --------------------------------------------------
        // TEST 1: Store file
        // --------------------------------------------------

        String storedPath = null;

        try {

            byte[] testData = "Complaint Hub file storage test".getBytes();

            storedPath = fileStorageService.store(
                    testData,
                    "test-photo.jpg",
                    "image/jpeg",
                    "profile"
            );

            result.append("[PASS] File stored successfully\n");
            passed++;

        } catch (Exception e) {

            result.append("[FAIL] File storage failed: ")
                    .append(e.getMessage())
                    .append("\n");

            failed++;
        }

        // --------------------------------------------------
        // TEST 2: Returned path
        // --------------------------------------------------

        if (storedPath != null) {

            if (storedPath.startsWith("profile/")
                    && !Paths.get(storedPath).isAbsolute()) {

                result.append("[PASS] Returned path is relative\n");
                passed++;

            } else {

                result.append("[FAIL] Returned path is not relative\n");
                failed++;
            }

        } else {

            result.append("[FAIL] Cannot test returned path\n");
            failed++;
        }

        // --------------------------------------------------
        // TEST 3: Unique filename
        // --------------------------------------------------

        if (storedPath != null) {

            if (!storedPath.endsWith("test-photo.jpg")
                    && !storedPath.contains("test-photo")) {

                result.append("[PASS] Stored filename is server-generated\n");
                passed++;

            } else {

                result.append("[FAIL] Original filename was used\n");
                failed++;
            }

        } else {

            result.append("[FAIL] Cannot test stored filename\n");
            failed++;
        }

        // --------------------------------------------------
        // TEST 4: Physical file exists
        // --------------------------------------------------

        Path physicalFile = null;

        if (storedPath != null) {

            physicalFile = Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize()
                    .resolve(storedPath)
                    .normalize();

            if (Files.exists(physicalFile)) {

                result.append("[PASS] Physical file exists\n");
                passed++;

            } else {

                result.append("[FAIL] Physical file does not exist\n");
                failed++;
            }

        } else {

            result.append("[FAIL] Cannot check physical file\n");
            failed++;
        }

        // --------------------------------------------------
        // TEST 5: Delete file
        // --------------------------------------------------

        if (storedPath != null) {

            try {

                boolean deleted =
                        fileStorageService.delete(storedPath);

                if (deleted) {

                    result.append("[PASS] File deleted successfully\n");
                    passed++;

                } else {

                    result.append("[FAIL] File was not deleted\n");
                    failed++;
                }

            } catch (Exception e) {

                result.append("[FAIL] File deletion failed: ")
                        .append(e.getMessage())
                        .append("\n");

                failed++;
            }

        } else {

            result.append("[FAIL] Cannot test deletion\n");
            failed++;
        }

        // --------------------------------------------------
        // TEST 6: Verify file no longer exists
        // --------------------------------------------------

        if (physicalFile != null) {

            if (!Files.exists(physicalFile)) {

                result.append("[PASS] Deleted file no longer exists\n");
                passed++;

            } else {

                result.append("[FAIL] Deleted file still exists\n");
                failed++;
            }

        } else {

            result.append("[FAIL] Cannot verify deletion\n");
            failed++;
        }

        // --------------------------------------------------
        // VALIDATION TESTS
        // --------------------------------------------------

        result.append("\n=== VALIDATION TESTS ===\n\n");

        // Empty file
        try {

            fileStorageService.store(
                    new byte[0],
                    "test.jpg",
                    "image/jpeg",
                    "profile"
            );

            result.append("[FAIL] Empty file was accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {

            result.append("[PASS] Empty file rejected\n");
            passed++;
        }

        // Missing filename
        try {

            fileStorageService.store(
                    "test".getBytes(),
                    null,
                    "image/jpeg",
                    "profile"
            );

            result.append("[FAIL] Missing filename was accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {

            result.append("[PASS] Missing filename rejected\n");
            passed++;
        }

        // Missing content type
        try {

            fileStorageService.store(
                    "test".getBytes(),
                    "test.jpg",
                    null,
                    "profile"
            );

            result.append("[FAIL] Missing content type was accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {

            result.append("[PASS] Missing content type rejected\n");
            passed++;
        }

        // Missing storage directory
        try {

            fileStorageService.store(
                    "test".getBytes(),
                    "test.jpg",
                    "image/jpeg",
                    null
            );

            result.append("[FAIL] Missing storage directory was accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {

            result.append("[PASS] Missing storage directory rejected\n");
            passed++;
        }

        // Path traversal in storage directory
        try {

            fileStorageService.store(
                    "test".getBytes(),
                    "test.jpg",
                    "image/jpeg",
                    "../outside"
            );

            result.append("[FAIL] Path traversal was accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {

            result.append("[PASS] Path traversal rejected\n");
            passed++;
        }

        // Path traversal in delete
        try {

            fileStorageService.delete("../outside/test.jpg");

            result.append("[FAIL] Invalid delete path was accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {

            result.append("[PASS] Invalid delete path rejected\n");
            passed++;
        }

        // --------------------------------------------------
        // FINAL RESULT
        // --------------------------------------------------

        result.append("\n=== RESULT ===\n\n");

        result.append("Passed: ")
                .append(passed)
                .append("\n");

        result.append("Failed: ")
                .append(failed)
                .append("\n\n");

        if (failed == 0) {

            result.append("ALL FILE STORAGE TESTS PASSED");

        } else {

            result.append("SOME FILE STORAGE TESTS FAILED");
        }

        response.getWriter().println(result);
    }
}