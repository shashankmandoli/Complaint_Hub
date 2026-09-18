package com.complainthub.controller;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintUpdate;
import com.complainthub.entity.User;
import com.complainthub.service.ComplaintService;
import com.complainthub.service.ComplaintServiceImpl;
import com.complainthub.service.ComplaintUpdateService;
import com.complainthub.service.ComplaintUpdateServiceImpl;
import com.complainthub.util.AuthenticationConstants;
import com.complainthub.util.AuthorizationException;
import com.complainthub.util.AuthorizationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/complaint-updates/*")
public class ComplaintUpdateController extends HttpServlet {

    private final ComplaintUpdateService complaintUpdateService =
            new ComplaintUpdateServiceImpl();

    private final ComplaintService complaintService =
            new ComplaintServiceImpl();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        configureResponse(response);
        try {
            long authenticatedUserId = getAuthenticatedUserId(request);
            long complaintId = extractComplaintId(request);
            String message = request.getParameter("message");
            String visibleToUserParameter = request.getParameter("visibleToUser");
            boolean visibleToUser = parseVisibleToUser(visibleToUserParameter);

            User updatedBy = new User();

            updatedBy.setId(authenticatedUserId);

            Complaint complaint = new Complaint();

            complaint.setId(complaintId);

            ComplaintUpdate complaintUpdate = new ComplaintUpdate();

            complaintUpdate.setComplaint(complaint);
            complaintUpdate.setUpdatedBy(updatedBy);
            complaintUpdate.setMessage(message);
            complaintUpdate.setVisibleToUser(visibleToUser);

            ComplaintUpdate createdUpdate = complaintUpdateService.createUpdate(complaintUpdate);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().println("Complaint update created successfully.");

            printComplaintUpdate(createdUpdate, response);
        } catch (AuthorizationException exception) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to create complaint update.");
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        configureResponse(response);
        try {
            long authenticatedUserId = getAuthenticatedUserId(request);
            long complaintId = extractComplaintId(request);

            Complaint complaint = complaintService.getComplaintById(complaintId);

            if (complaint == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint not found.");
                return;
            }

//            HttpSession session =
//                    request.getSession(false);

            if (AuthorizationUtil.isAdmin(request)) {
                List<ComplaintUpdate> updates =
                        complaintUpdateService.getUpdatesByComplaint(complaintId);

                writeUpdates(updates, response);
                return;
            }

            AuthorizationUtil.requireOwnerOrAdmin(request, complaint.getUser().getId());
            List<ComplaintUpdate> updates =
                    complaintUpdateService
                            .getVisibleUpdatesByComplaint(
                                    complaintId
                            );

            writeUpdates(updates, response);
        } catch (AuthorizationException exception) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    exception.getMessage()
            );
        } catch (IllegalArgumentException exception) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve complaint updates."
            );
        }
    }

    private void writeUpdates(
            List<ComplaintUpdate> updates,
            HttpServletResponse response
    ) throws IOException {
        if (updates == null || updates.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("No complaint updates found.");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("Total updates: " + updates.size());
        response.getWriter().println();

        for (ComplaintUpdate update : updates) {
            printComplaintUpdate(update, response);
        }
    }

    private void printComplaintUpdate(
            ComplaintUpdate complaintUpdate,
            HttpServletResponse response
    ) throws IOException {

        response.getWriter().println(
                "--------------------------------------"
        );

        response.getWriter().println(
                "Update ID: "
                        + complaintUpdate.getId()
        );

        response.getWriter().println(
                "Complaint ID: "
                        + complaintUpdate.getComplaint().getId()
        );

        response.getWriter().println(
                "Updated By User ID: "
                        + complaintUpdate.getUpdatedBy().getId()
        );

        response.getWriter().println(
                "Message: "
                        + complaintUpdate.getMessage()
        );

        response.getWriter().println(
                "Visible To User: "
                        + complaintUpdate.getVisibleToUser()
        );

        response.getWriter().println(
                "Created At: "
                        + complaintUpdate.getCreatedAt()
        );

        response.getWriter().println(
                "--------------------------------------"
        );
    }

    private long getAuthenticatedUserId(
            HttpServletRequest request
    ) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            throw new IllegalArgumentException(
                    "Authenticated session is required."
            );
        }

        Object userId =
                session.getAttribute(
                        AuthenticationConstants.USER_ID
                );

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated User ID is missing."
            );
        }

        try {
            long parsedUserId =
                    Long.parseLong(userId.toString());

            if (parsedUserId <= 0) {
                throw new IllegalArgumentException(
                        "Authenticated User ID is invalid."
                );
            }

            return parsedUserId;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Authenticated User ID is invalid."
            );
        }
    }

    private long extractComplaintId(
            HttpServletRequest request
    ) {

        String pathInfo =
                request.getPathInfo();

        /*
         * Expected path:
         *
         * /complaint/42
         */
        if (pathInfo == null
                || pathInfo.isBlank()
                || pathInfo.equals("/")) {

            throw new IllegalArgumentException(
                    "Complaint update path is required."
            );
        }

        String[] pathParts =
                pathInfo.split("/");

        /*
         * pathParts[0] = ""
         * pathParts[1] = "complaint"
         * pathParts[2] = "42"
         */
        if (pathParts.length != 3
                || !"complaint".equalsIgnoreCase(pathParts[1])) {

            throw new IllegalArgumentException(
                    "Invalid complaint update URL."
            );
        }

        return parseId(
                pathParts[2],
                "Complaint ID"
        );
    }

    private long parseId(
            String value,
            String fieldName
    ) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        try {
            long id =
                    Long.parseLong(value);

            if (id <= 0) {
                throw new IllegalArgumentException(
                        fieldName
                                + " must be greater than zero."
                );
            }

            return id;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid number."
            );
        }
    }

    private boolean parseVisibleToUser(
            String value
    ) {

        /*
         * If the parameter is omitted, the update is
         * not visible to normal users by default.
         */
        if (value == null || value.isBlank()) {
            return false;
        }

        if ("true".equalsIgnoreCase(value)
                || "1".equals(value)) {

            return true;
        }

        if ("false".equalsIgnoreCase(value)
                || "0".equals(value)) {

            return false;
        }

        throw new IllegalArgumentException(
                "Visible To User must be true or false."
        );
    }

    private void configureResponse(
            HttpServletResponse response
    ) {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
    }
}