package com.complainthub.test;

import com.complainthub.util.FileValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/test/file-validation")
public class FileValidationTestServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("text/plain");

        StringBuilder result = new StringBuilder();

        int passed = 0;
        int failed = 0;

        result.append("=== FILE VALIDATION TEST ===\n\n");

        // Valid JPEG
        try {
            FileValidationUtil.validateImage(
                    "test image".getBytes(),
                    "photo.jpg",
                    "image/jpeg"
            );

            result.append("[PASS] Valid JPEG accepted\n");
            passed++;

        } catch (Exception e) {
            result.append("[FAIL] Valid JPEG rejected: ")
                    .append(e.getMessage())
                    .append("\n");
            failed++;
        }

        // Valid PNG
        try {
            FileValidationUtil.validateImage(
                    "test image".getBytes(),
                    "photo.png",
                    "image/png"
            );

            result.append("[PASS] Valid PNG accepted\n");
            passed++;

        } catch (Exception e) {
            result.append("[FAIL] Valid PNG rejected: ")
                    .append(e.getMessage())
                    .append("\n");
            failed++;
        }

        // Valid WebP
        try {
            FileValidationUtil.validateImage(
                    "test image".getBytes(),
                    "photo.webp",
                    "image/webp"
            );

            result.append("[PASS] Valid WebP accepted\n");
            passed++;

        } catch (Exception e) {
            result.append("[FAIL] Valid WebP rejected: ")
                    .append(e.getMessage())
                    .append("\n");
            failed++;
        }

        // Empty file
        try {
            FileValidationUtil.validateImage(
                    new byte[0],
                    "photo.jpg",
                    "image/jpeg"
            );

            result.append("[FAIL] Empty file accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {
            result.append("[PASS] Empty file rejected\n");
            passed++;
        }

        // Unsupported MIME type
        try {
            FileValidationUtil.validateImage(
                    "test image".getBytes(),
                    "photo.jpg",
                    "application/pdf"
            );

            result.append("[FAIL] Unsupported MIME type accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {
            result.append("[PASS] Unsupported MIME type rejected\n");
            passed++;
        }

        // Unsupported extension
        try {
            FileValidationUtil.validateImage(
                    "test image".getBytes(),
                    "photo.pdf",
                    "image/jpeg"
            );

            result.append("[FAIL] Unsupported extension accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {
            result.append("[PASS] Unsupported extension rejected\n");
            passed++;
        }

        // Missing filename
        try {
            FileValidationUtil.validateImage(
                    "test image".getBytes(),
                    null,
                    "image/jpeg"
            );

            result.append("[FAIL] Missing filename accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {
            result.append("[PASS] Missing filename rejected\n");
            passed++;
        }

        // Missing content type
        try {
            FileValidationUtil.validateImage(
                    "test image".getBytes(),
                    "photo.jpg",
                    null
            );

            result.append("[FAIL] Missing content type accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {
            result.append("[PASS] Missing content type rejected\n");
            passed++;
        }

        // Oversized file
        try {
            FileValidationUtil.validateImage(
                    new byte[(int) FileValidationUtil.getMaxImageSize() + 1],
                    "large.jpg",
                    "image/jpeg"
            );

            result.append("[FAIL] Oversized file accepted\n");
            failed++;

        } catch (IllegalArgumentException e) {
            result.append("[PASS] Oversized file rejected\n");
            passed++;
        }

        result.append("\n=== RESULT ===\n\n");
        result.append("Passed: ").append(passed).append("\n");
        result.append("Failed: ").append(failed).append("\n\n");

        if (failed == 0) {
            result.append("ALL FILE VALIDATION TESTS PASSED");
        } else {
            result.append("SOME FILE VALIDATION TESTS FAILED");
        }

        response.getWriter().println(result);
    }
}