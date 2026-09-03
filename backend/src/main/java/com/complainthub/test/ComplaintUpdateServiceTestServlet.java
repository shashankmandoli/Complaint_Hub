package com.complainthub.test;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintUpdate;
import com.complainthub.entity.User;
import com.complainthub.service.ComplaintUpdateService;
import com.complainthub.service.ComplaintUpdateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/complaint-update-service")
public class ComplaintUpdateServiceTestServlet extends HttpServlet {

    private final ComplaintUpdateService updateService =
            new ComplaintUpdateServiceImpl();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");

        String action = request.getParameter("action");

        try {

            if (action == null || action.isBlank()) {
                response.getWriter().println(
                        "Please provide an action."
                );
                return;
            }

            switch (action) {

                case "create":
                    createUpdate(request, response);
                    break;

                case "findById":
                    findById(request, response);
                    break;

                case "findAll":
                    findAll(response);
                    break;

                case "findByComplaint":
                    findByComplaint(request, response);
                    break;

                case "findByUser":
                    findByUser(request, response);
                    break;

                case "findVisibleByComplaint":
                    findVisibleByComplaint(request, response);
                    break;

                case "update":
                    updateUpdate(request, response);
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
        }
    }

    private void createUpdate(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long complaintId = parseId(
                request.getParameter("complaintId"),
                "Complaint Id"
        );

        long updatedById = parseId(
                request.getParameter("updatedById"),
                "Updated By Id"
        );

        String message =
                request.getParameter("message");

        boolean visibleToUser =
                parseBoolean(
                        request.getParameter("visibleToUser")
                );

        Complaint complaint = new Complaint();
        complaint.setId(complaintId);

        User updatedBy = new User();
        updatedBy.setId(updatedById);

        ComplaintUpdate complaintUpdate =
                new ComplaintUpdate();

        complaintUpdate.setComplaint(complaint);
        complaintUpdate.setUpdatedBy(updatedBy);
        complaintUpdate.setMessage(message);
        complaintUpdate.setVisibleToUser(visibleToUser);

        ComplaintUpdate created =
                updateService.createUpdate(
                        complaintUpdate
                );

        response.getWriter().println(
                "Complaint update created successfully."
        );

        printUpdate(created, response);
    }

    private void findById(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long id = parseId(
                request.getParameter("id"),
                "Complaint Update Id"
        );

        ComplaintUpdate update =
                updateService.getUpdateById(id);

        if (update == null) {
            response.getWriter().println(
                    "Complaint update not found."
            );
            return;
        }

        printUpdate(update, response);
    }

    private void findAll(
            HttpServletResponse response)
            throws IOException {

        List<ComplaintUpdate> updates =
                updateService.getAllUpdates();

        response.getWriter().println(
                "Total complaint updates: " +
                        updates.size()
        );

        for (ComplaintUpdate update : updates) {
            printUpdate(update, response);
        }
    }

    private void findByComplaint(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long complaintId = parseId(
                request.getParameter("complaintId"),
                "Complaint Id"
        );

        List<ComplaintUpdate> updates =
                updateService.getUpdatesByComplaint(
                        complaintId
                );

        response.getWriter().println(
                "Updates for Complaint ID " +
                        complaintId +
                        ": " +
                        updates.size()
        );

        for (ComplaintUpdate update : updates) {
            printUpdate(update, response);
        }
    }

    private void findByUser(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long userId = parseId(
                request.getParameter("userId"),
                "User Id"
        );

        List<ComplaintUpdate> updates =
                updateService.getUpdatesByUser(userId);

        response.getWriter().println(
                "Updates created by User ID " +
                        userId +
                        ": " +
                        updates.size()
        );

        for (ComplaintUpdate update : updates) {
            printUpdate(update, response);
        }
    }

    private void findVisibleByComplaint(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long complaintId = parseId(
                request.getParameter("complaintId"),
                "Complaint Id"
        );

        List<ComplaintUpdate> updates =
                updateService.getVisibleUpdatesByComplaint(
                        complaintId
                );

        response.getWriter().println(
                "Visible updates for Complaint ID " +
                        complaintId +
                        ": " +
                        updates.size()
        );

        for (ComplaintUpdate update : updates) {
            printUpdate(update, response);
        }
    }

    private void updateUpdate(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long id = parseId(
                request.getParameter("id"),
                "Complaint Update Id"
        );

        String message =
                request.getParameter("message");

        ComplaintUpdate existingUpdate =
                updateService.getUpdateById(id);

        if (existingUpdate == null) {
            response.getWriter().println(
                    "Complaint update not found."
            );
            return;
        }

        if (message != null) {
            existingUpdate.setMessage(message);
        }

        String visibleValue =
                request.getParameter("visibleToUser");

        if (visibleValue != null) {
            existingUpdate.setVisibleToUser(
                    parseBoolean(visibleValue)
            );
        }

        ComplaintUpdate updated =
                updateService.updateUpdate(
                        existingUpdate
                );

        response.getWriter().println(
                "Complaint update updated successfully."
        );

        printUpdate(updated, response);
    }

    private long parseId(
            String value,
            String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        try {
            return Long.parseLong(value);

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    fieldName +
                            " must be a valid number."
            );
        }
    }

    private boolean parseBoolean(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "visibleToUser is required."
            );
        }

        if (!value.equalsIgnoreCase("true")
                && !value.equalsIgnoreCase("false")) {

            throw new IllegalArgumentException(
                    "visibleToUser must be true or false."
            );
        }

        return Boolean.parseBoolean(value);
    }

    private void printUpdate(
            ComplaintUpdate update,
            HttpServletResponse response)
            throws IOException {

        response.getWriter().println(
                "-----------------------------"
        );

        response.getWriter().println(
                "Update ID: " +
                        update.getId()
        );

        response.getWriter().println(
                "Complaint ID: " +
                        (update.getComplaint() != null
                                ? update.getComplaint().getId()
                                : null)
        );

        response.getWriter().println(
                "Updated By User ID: " +
                        (update.getUpdatedBy() != null
                                ? update.getUpdatedBy().getId()
                                : null)
        );

        response.getWriter().println(
                "Message: " +
                        update.getMessage()
        );

        response.getWriter().println(
                "Visible To User: " +
                        update.getVisibleToUser()
        );

        response.getWriter().println(
                "Created At: " +
                        update.getCreatedAt()
        );
    }
}