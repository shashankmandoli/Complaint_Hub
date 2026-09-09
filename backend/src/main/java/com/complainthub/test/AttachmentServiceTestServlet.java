package com.complainthub.test;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAttachment;
import com.complainthub.entity.User;
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
            switch (action) {

                // Profile photo
                case "profile-create":
                    createProfilePhoto(request, response);
                    break;

                case "profile-find-id":
                    findProfilePhotoById(request, response);
                    break;

                case "profile-find-user":
                    findProfilePhotoByUser(request, response);
                    break;

                case "profile-find-all":
                    findAllProfilePhotos(response);
                    break;

                case "profile-update":
                    updateProfilePhoto(request, response);
                    break;

                case "profile-delete":
                    deleteProfilePhoto(request, response);
                    break;

                // Complaint attachments
                case "attachment-create":
                    createAttachment(request, response);
                    break;

                case "attachment-find-id":
                    findAttachmentById(request, response);
                    break;

                case "attachment-find-complaint":
                    findAttachmentsByComplaint(request, response);
                    break;

                case "attachment-find-all":
                    findAllAttachments(response);
                    break;

                case "attachment-delete":
                    deleteAttachment(request, response);
                    break;

                case "attachment-cleanup":
                    cleanupAttachments(request, response);
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

            e.printStackTrace(response.getWriter());
        }
    }

    // =========================
    // PROFILE PHOTO
    // =========================

    private void createProfilePhoto(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long userId = Long.parseLong(
                request.getParameter("userId")
        );

        User user = new User();
        user.setId(userId);

        UserProfilePhoto photo = new UserProfilePhoto();

        photo.setUser(user);
        photo.setFileName("profile.jpg");
        photo.setStoredFileName(
                "mock-profile-" + userId + ".jpg"
        );
        photo.setFilePath(
                "/uploads/profile/mock-profile-"
                        + userId
                        + ".jpg"
        );
        photo.setContentType("image/jpeg");
        photo.setFileSize(204800);

        UserProfilePhoto saved =
                profilePhotoService.createProfilePhoto(photo);

        response.getWriter().println(
                "Profile photo created successfully."
        );

        response.getWriter().println(
                "ID: " + saved.getId()
        );
    }

    private void findProfilePhotoById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id = Long.parseLong(
                request.getParameter("id")
        );

        UserProfilePhoto photo =
                profilePhotoService.getProfilePhotoById(id);

        if (photo == null) {
            response.getWriter().println(
                    "Profile photo not found."
            );
            return;
        }

        printProfilePhoto(photo, response);
    }

    private void findProfilePhotoByUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long userId = Long.parseLong(
                request.getParameter("userId")
        );

        UserProfilePhoto photo =
                profilePhotoService.getProfilePhotoByUser(userId);

        if (photo == null) {
            response.getWriter().println(
                    "Profile photo not found."
            );
            return;
        }

        printProfilePhoto(photo, response);
    }

    private void findAllProfilePhotos(
            HttpServletResponse response
    ) throws IOException {

        List<UserProfilePhoto> photos =
                profilePhotoService.getAllProfilePhotos();

        response.getWriter().println(
                "Profile photos found: "
                        + photos.size()
        );

        for (UserProfilePhoto photo : photos) {
            printProfilePhoto(photo, response);
        }
    }

    private void updateProfilePhoto(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id = Long.parseLong(
                request.getParameter("id")
        );

        UserProfilePhoto photo =
                profilePhotoService.getProfilePhotoById(id);

        if (photo == null) {
            response.getWriter().println(
                    "Profile photo not found."
            );
            return;
        }

        photo.setFileName("updated-profile.png");
        photo.setStoredFileName("mock-profile-updated.png");
        photo.setFilePath(
                "/uploads/profile/mock-profile-updated.png"
        );
        photo.setContentType("image/png");
        photo.setFileSize(307200);

        UserProfilePhoto updated =
                profilePhotoService.updateProfilePhoto(photo);

        response.getWriter().println(
                "Profile photo updated successfully."
        );

        response.getWriter().println(
                "ID: " + updated.getId()
        );
    }

    private void deleteProfilePhoto(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id = Long.parseLong(
                request.getParameter("id")
        );

        boolean deleted =
                profilePhotoService.deleteProfilePhoto(id);

        response.getWriter().println(
                "Deleted: " + deleted
        );
    }

    // =========================
    // COMPLAINT ATTACHMENTS
    // =========================

    private void createAttachment(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long complaintId = Long.parseLong(
                request.getParameter("complaintId")
        );

        Complaint complaint = new Complaint();
        complaint.setId(complaintId);

        ComplaintAttachment attachment =
                new ComplaintAttachment();

        attachment.setComplaint(complaint);
        attachment.setFileName("damage-front.jpg");
        attachment.setStoredFileName(
                "mock-complaint-"
                        + complaintId
                        + "-front.jpg"
        );
        attachment.setFilePath(
                "/uploads/complaints/"
                        + complaintId
                        + "/mock-front.jpg"
        );
        attachment.setContentType("image/jpeg");
        attachment.setFileSize(512000);

        ComplaintAttachment saved =
                attachmentService.createAttachment(
                        attachment
                );

        response.getWriter().println(
                "Complaint attachment created successfully."
        );

        response.getWriter().println(
                "ID: " + saved.getId()
        );
    }

    private void findAttachmentById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id = Long.parseLong(
                request.getParameter("id")
        );

        ComplaintAttachment attachment =
                attachmentService.getAttachmentById(id);

        if (attachment == null) {
            response.getWriter().println(
                    "Attachment not found."
            );
            return;
        }

        printAttachment(attachment, response);
    }

    private void findAttachmentsByComplaint(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long complaintId = Long.parseLong(
                request.getParameter("complaintId")
        );

        List<ComplaintAttachment> attachments =
                attachmentService.getAttachmentsByComplaint(
                        complaintId
                );

        response.getWriter().println(
                "Attachments found: "
                        + attachments.size()
        );

        for (ComplaintAttachment attachment : attachments) {
            printAttachment(attachment, response);
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

        for (ComplaintAttachment attachment : attachments) {
            printAttachment(attachment, response);
        }
    }

    private void deleteAttachment(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id = Long.parseLong(
                request.getParameter("id")
        );

        boolean deleted =
                attachmentService.deleteAttachment(id);

        response.getWriter().println(
                "Deleted: " + deleted
        );
    }

    private void cleanupAttachments(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long complaintId = Long.parseLong(
                request.getParameter("complaintId")
        );

        attachmentService.cleanupAttachments(
                complaintId
        );

        response.getWriter().println(
                "All attachments cleaned up for complaint "
                        + complaintId
        );
    }

    // =========================
    // PRINT HELPERS
    // =========================

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
                        + " | Type: "
                        + attachment.getContentType()
                        + " | Size: "
                        + attachment.getFileSize()
        );
    }
}