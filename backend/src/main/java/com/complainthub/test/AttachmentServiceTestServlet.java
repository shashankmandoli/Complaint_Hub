package com.complainthub.test;

import com.complainthub.entity.ComplaintAttachment;
import com.complainthub.entity.UserProfilePhoto;
import com.complainthub.service.ComplaintAttachmentService;
import com.complainthub.service.ComplaintAttachmentServiceImpl;
import com.complainthub.service.UserProfilePhotoService;
import com.complainthub.service.UserProfilePhotoServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/api/test/attachment-service")
public class AttachmentServiceTestServlet extends HttpServlet {

    private final UserProfilePhotoService profilePhotoService =
            new UserProfilePhotoServiceImpl();

    private final ComplaintAttachmentService attachmentService =
            new ComplaintAttachmentServiceImpl();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("text/plain");

        String action = request.getParameter("action");

        try {

            if (action == null) {
                response.getWriter().println(
                        "Action is required"
                );
                return;
            }

            switch (action) {

                // -------------------------
                // Profile photo
                // -------------------------

                case "profile-upload":
                    uploadProfilePhoto(response);
                    break;

                case "profile-replace":
                    replaceProfilePhoto(response);
                    break;

                case "profile-delete":
                    deleteProfilePhoto(request, response);
                    break;

                case "profile-find-user":
                    findProfilePhotoByUser(
                            request,
                            response
                    );
                    break;

                // -------------------------
                // Complaint attachment
                // -------------------------

                case "attachment-upload":
                    uploadAttachment(response);
                    break;

                case "attachment-delete":
                    deleteAttachment(
                            request,
                            response
                    );
                    break;

                case "attachment-cleanup":
                    cleanupAttachments(
                            request,
                            response
                    );
                    break;

                case "attachment-find-complaint":
                    findAttachmentsByComplaint(
                            request,
                            response
                    );
                    break;

                case "attachment-find-all":
                    findAllAttachments(response);
                    break;

                default:
                    response.getWriter().println(
                            "Unknown action: " + action
                    );
            }

        } catch (Exception e) {

            response.getWriter().println(
                    "ERROR: " + e.getMessage()
            );

            e.printStackTrace(
                    response.getWriter()
            );
        }
    }

    // =========================
    // PROFILE PHOTO
    // =========================

    private void uploadProfilePhoto(
            HttpServletResponse response
    ) throws IOException {

        byte[] testData =
                "Profile photo integration test"
                        .getBytes();

        UserProfilePhoto photo =
                profilePhotoService.uploadProfilePhoto(
                        2L,
                        testData,
                        "profile-test.jpg",
                        "image/jpeg"
                );

        response.getWriter().println(
                "=== PROFILE PHOTO UPLOAD ==="
        );

        printProfilePhoto(
                photo,
                response
        );

        verifyPhysicalFile(
                photo.getFilePath(),
                response
        );
    }

    private void replaceProfilePhoto(
            HttpServletResponse response
    ) throws IOException {

        UserProfilePhoto oldPhoto =
                profilePhotoService.getProfilePhotoByUser(
                        2L
                );

        if (oldPhoto == null) {

            response.getWriter().println(
                    "No existing profile photo found."
            );

            response.getWriter().println(
                    "Run ?action=profile-upload first."
            );

            return;
        }

        String oldPath =
                oldPhoto.getFilePath();

        byte[] newData =
                "Updated profile photo test"
                        .getBytes();

        UserProfilePhoto newPhoto =
                profilePhotoService.uploadProfilePhoto(
                        2L,
                        newData,
                        "profile-updated.png",
                        "image/png"
                );

        response.getWriter().println(
                "=== PROFILE PHOTO REPLACEMENT ==="
        );

        response.getWriter().println(
                "Old path: " + oldPath
        );

        response.getWriter().println(
                "New path: " + newPhoto.getFilePath()
        );

        response.getWriter().println();

        verifyPhysicalFile(
                newPhoto.getFilePath(),
                response
        );

        Path oldPhysicalPath =
                resolveUploadPath(oldPath);

        response.getWriter().println(
                "Old file exists: "
                        + Files.exists(oldPhysicalPath)
        );

        if (!Files.exists(oldPhysicalPath)) {

            response.getWriter().println(
                    "[PASS] Old physical file deleted"
            );

        } else {

            response.getWriter().println(
                    "[FAIL] Old physical file still exists"
            );
        }
    }

    private void deleteProfilePhoto(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id = Long.parseLong(
                request.getParameter("id")
        );

        UserProfilePhoto photo =
                profilePhotoService.getProfilePhotoById(
                        id
                );

        if (photo == null) {

            response.getWriter().println(
                    "Profile photo not found."
            );

            return;
        }

        String filePath =
                photo.getFilePath();

        boolean deleted =
                profilePhotoService.deleteProfilePhoto(
                        id
                );

        response.getWriter().println(
                "Deleted from database: "
                        + deleted
        );

        Path physicalPath =
                resolveUploadPath(filePath);

        response.getWriter().println(
                "Physical file exists after delete: "
                        + Files.exists(physicalPath)
        );
    }

    private void findProfilePhotoByUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long userId = Long.parseLong(
                request.getParameter("userId")
        );

        UserProfilePhoto photo =
                profilePhotoService.getProfilePhotoByUser(
                        userId
                );

        if (photo == null) {

            response.getWriter().println(
                    "Profile photo not found."
            );

            return;
        }

        printProfilePhoto(
                photo,
                response
        );
    }

    // =========================
    // COMPLAINT ATTACHMENTS
    // =========================

    private void uploadAttachment(
            HttpServletResponse response
    ) throws IOException {

        byte[] testData =
                "Complaint attachment integration test"
                        .getBytes();

        ComplaintAttachment attachment =
                attachmentService.uploadAttachment(
                        3L,
                        testData,
                        "complaint-photo.jpg",
                        "image/jpeg"
                );

        response.getWriter().println(
                "=== COMPLAINT ATTACHMENT UPLOAD ==="
        );

        printAttachment(
                attachment,
                response
        );

        verifyPhysicalFile(
                attachment.getFilePath(),
                response
        );
    }

    private void deleteAttachment(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id = Long.parseLong(
                request.getParameter("id")
        );

        ComplaintAttachment attachment =
                attachmentService.getAttachmentById(
                        id
                );

        if (attachment == null) {

            response.getWriter().println(
                    "Attachment not found."
            );

            return;
        }

        String filePath =
                attachment.getFilePath();

        boolean deleted =
                attachmentService.deleteAttachment(
                        id
                );

        response.getWriter().println(
                "Deleted from database: "
                        + deleted
        );

        Path physicalPath =
                resolveUploadPath(filePath);

        response.getWriter().println(
                "Physical file exists after delete: "
                        + Files.exists(physicalPath)
        );
    }

    private void cleanupAttachments(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long complaintId = Long.parseLong(
                request.getParameter(
                        "complaintId"
                )
        );

        List<ComplaintAttachment> attachments =
                attachmentService.getAttachmentsByComplaint(
                        complaintId
                );

        response.getWriter().println(
                "Attachments before cleanup: "
                        + attachments.size()
        );

        attachmentService.cleanupAttachments(
                complaintId
        );

        List<ComplaintAttachment> remaining =
                attachmentService.getAttachmentsByComplaint(
                        complaintId
                );

        response.getWriter().println(
                "Attachments after cleanup: "
                        + remaining.size()
        );

        if (remaining.isEmpty()) {

            response.getWriter().println(
                    "[PASS] Database metadata cleaned"
            );

        } else {

            response.getWriter().println(
                    "[FAIL] Database metadata remains"
            );
        }
    }

    private void findAttachmentsByComplaint(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long complaintId = Long.parseLong(
                request.getParameter(
                        "complaintId"
                )
        );

        List<ComplaintAttachment> attachments =
                attachmentService.getAttachmentsByComplaint(
                        complaintId
                );

        response.getWriter().println(
                "Attachments found: "
                        + attachments.size()
        );

        for (ComplaintAttachment attachment :
                attachments) {

            printAttachment(
                    attachment,
                    response
            );
        }
    }

    private void findAllAttachments(
            HttpServletResponse response
    ) throws IOException {

        List<ComplaintAttachment> attachments =
                attachmentService.getAllAttachments();

        response.getWriter().println(
                "Attachments found: "
                        + attachments.size()
        );

        for (ComplaintAttachment attachment :
                attachments) {

            printAttachment(
                    attachment,
                    response
            );
        }
    }

    // =========================
    // HELPERS
    // =========================

    private void verifyPhysicalFile(
            String filePath,
            HttpServletResponse response
    ) throws IOException {

        Path physicalPath =
                resolveUploadPath(filePath);

        response.getWriter().println(
                "Physical path: "
                        + physicalPath
        );

        response.getWriter().println(
                "Physical file exists: "
                        + Files.exists(physicalPath)
        );

        if (Files.exists(physicalPath)) {

            response.getWriter().println(
                    "[PASS] Physical file exists"
            );

        } else {

            response.getWriter().println(
                    "[FAIL] Physical file missing"
            );
        }
    }

    private Path resolveUploadPath(
            String filePath
    ) {
        String configuredPath =
                System.getProperty("complainthub.upload.root");

        if (configuredPath == null
                || configuredPath.isBlank()) {

            throw new IllegalStateException(
                    "Upload root is not configured"
            );
        }

        Path uploadRoot =
                Paths.get(configuredPath)
                        .toAbsolutePath()
                        .normalize();

        Path targetFile =
                uploadRoot
                        .resolve(filePath)
                        .normalize();

        if (!targetFile.startsWith(uploadRoot)) {
            throw new IllegalArgumentException(
                    "Invalid file path"
            );
        }

        return targetFile;
    }

    private void printProfilePhoto(
            UserProfilePhoto photo,
            HttpServletResponse response
    ) throws IOException {

        response.getWriter().println(
                "ID: " + photo.getId()
                        + " | User ID: "
                        + photo.getUser().getId()
                        + " | File: "
                        + photo.getFileName()
                        + " | Stored: "
                        + photo.getStoredFileName()
                        + " | Path: "
                        + photo.getFilePath()
                        + " | Type: "
                        + photo.getContentType()
                        + " | Size: "
                        + photo.getFileSize()
        );
    }

    private void printAttachment(
            ComplaintAttachment attachment,
            HttpServletResponse response
    ) throws IOException {

        response.getWriter().println(
                "ID: " + attachment.getId()
                        + " | Complaint ID: "
                        + attachment.getComplaint().getId()
                        + " | File: "
                        + attachment.getFileName()
                        + " | Stored: "
                        + attachment.getStoredFileName()
                        + " | Path: "
                        + attachment.getFilePath()
                        + " | Type: "
                        + attachment.getContentType()
                        + " | Size: "
                        + attachment.getFileSize()
        );
    }
}