package com.complainthub.controller;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAssignment;
import com.complainthub.entity.ComplaintUpdate;
import com.complainthub.entity.enums.ComplaintStatus;
import com.complainthub.entity.enums.UserRole;
import com.complainthub.service.ComplaintAssignmentService;
import com.complainthub.service.ComplaintAssignmentServiceImpl;
import com.complainthub.service.ComplaintService;
import com.complainthub.service.ComplaintServiceImpl;
import com.complainthub.service.ComplaintUpdateService;
import com.complainthub.service.ComplaintUpdateServiceImpl;
import com.complainthub.util.AuthenticationConstants;
import com.complainthub.util.AuthorizationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/agent/complaints/*")
public class AgentComplaintController extends HttpServlet {

    private ComplaintService complaintService;
    private ComplaintAssignmentService complaintAssignmentService;
    private ComplaintUpdateService complaintUpdateService;

    @Override
    public void init() throws ServletException {
        complaintService = new ComplaintServiceImpl();
        complaintAssignmentService = new ComplaintAssignmentServiceImpl();
        complaintUpdateService = new ComplaintUpdateServiceImpl();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            AuthorizationUtil.requireRole(
                    request,
                    UserRole.AGENT
            );

            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                getAssignedComplaints(request, response);
                return;
            }

            String[] pathParts = pathInfo.split("/");

            if (pathParts.length == 2) {
                long complaintId = parseId(pathParts[1], "Complaint Id");

                getAssignedComplaintById(
                        request,
                        response,
                        complaintId
                );
                return;
            }

            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Endpoint not found."
            );

        } catch (IllegalArgumentException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (SecurityException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );
        } catch (Exception e) {
            getServletContext().log(
                    "Failed to process agent GET request.",
                    e
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            AuthorizationUtil.requireRole(
                    request,
                    UserRole.AGENT
            );

            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                sendError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Complaint Id is required."
                );
                return;
            }

            String[] pathParts = pathInfo.split("/");

            /*
             * Expected endpoint:
             *
             * POST /api/agent/complaints/{complaintId}/updates
             */
            if (
                    pathParts.length == 3
                            && pathParts[2].equals("updates")
            ) {
                long complaintId = parseId(
                        pathParts[1],
                        "Complaint Id"
                );

                createComplaintUpdate(
                        request,
                        response,
                        complaintId
                );
                return;
            }

            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Endpoint not found."
            );

        } catch (IllegalArgumentException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (SecurityException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );
        } catch (Exception e) {
            getServletContext().log(
                    "Failed to process agent POST request.",
                    e
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred."
            );
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            AuthorizationUtil.requireRole(
                    request,
                    UserRole.AGENT
            );

            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                sendError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Complaint Id is required."
                );
                return;
            }

            String[] pathParts = pathInfo.split("/");

            /*
             * Expected endpoint:
             *
             * PUT /api/agent/complaints/{complaintId}/status
             */
            if (
                    pathParts.length == 3
                            && pathParts[2].equals("status")
            ) {
                long complaintId = parseId(
                        pathParts[1],
                        "Complaint Id"
                );

                updateComplaintStatus(
                        request,
                        response,
                        complaintId
                );
                return;
            }

            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Endpoint not found."
            );

        } catch (IllegalArgumentException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (SecurityException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );
        } catch (Exception e) {
            getServletContext().log(
                    "Failed to process agent PUT request.",
                    e
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred."
            );
        }
    }

    /*
     * GET /api/agent/complaints
     *
     * Returns complaints assigned to the logged-in agent.
     */
    private void getAssignedComplaints(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long agentId = getLoggedInUserId(request);

        List<ComplaintAssignment> assignments =
                complaintAssignmentService.getAssignmentsByAgent(agentId);

        StringBuilder result = new StringBuilder();

        result.append("Assigned complaints:\n");

        boolean foundActiveAssignment = false;

        for (ComplaintAssignment assignment : assignments) {

            if (!assignment.isActive()) {
                continue;
            }

            Complaint complaint = assignment.getComplaint();

            if (complaint == null) {
                continue;
            }

            foundActiveAssignment = true;

            result.append("\n");
            result.append("Complaint Id: ")
                    .append(complaint.getId())
                    .append("\n");

            result.append("Title: ")
                    .append(complaint.getTitle())
                    .append("\n");

            result.append("Status: ")
                    .append(complaint.getStatus())
                    .append("\n");

            result.append("Priority: ")
                    .append(complaint.getPriority())
                    .append("\n");

            result.append("Created At: ")
                    .append(complaint.getCreatedAt())
                    .append("\n");

            result.append("-------------------------");
        }

        if (!foundActiveAssignment) {
            result.append("No active complaints assigned to you.");
        }

        writeResponse(
                response,
                HttpServletResponse.SC_OK,
                result.toString()
        );
    }

    /*
     * GET /api/agent/complaints/{complaintId}
     *
     * Returns one complaint only if it is actively assigned
     * to the logged-in agent.
     */
    private void getAssignedComplaintById(
            HttpServletRequest request,
            HttpServletResponse response,
            long complaintId
    ) throws IOException {

        long agentId = getLoggedInUserId(request);

        ComplaintAssignment assignment =
                getActiveAssignmentForAgent(
                        complaintId,
                        agentId
                );

        if (assignment == null) {
            sendError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "This complaint is not actively assigned to you."
            );
            return;
        }

        Complaint complaint =
                complaintService.getComplaintById(complaintId);

        if (complaint == null) {
            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Complaint not found."
            );
            return;
        }

        List<ComplaintUpdate> updates =
                complaintUpdateService.getUpdatesByComplaint(
                        complaintId
                );

        StringBuilder result = new StringBuilder();

        result.append("Complaint Details\n");
        result.append("=================\n");
        result.append("Complaint Id: ")
                .append(complaint.getId())
                .append("\n");

        result.append("Title: ")
                .append(complaint.getTitle())
                .append("\n");

        result.append("Description: ")
                .append(complaint.getDescription())
                .append("\n");

        result.append("Status: ")
                .append(complaint.getStatus())
                .append("\n");

        result.append("Priority: ")
                .append(complaint.getPriority())
                .append("\n");

        result.append("Created At: ")
                .append(complaint.getCreatedAt())
                .append("\n");

        result.append("Updated At: ")
                .append(complaint.getUpdatedAt())
                .append("\n");

        result.append("\nComplaint Updates\n");
        result.append("=================\n");

        if (updates.isEmpty()) {
            result.append("No updates found.\n");
        } else {
            for (ComplaintUpdate update : updates) {
                result.append("\n");
                result.append("Update Id: ")
                        .append(update.getId())
                        .append("\n");

                result.append("Message: ")
                        .append(update.getMessage())
                        .append("\n");

                result.append("Updated By: ")
                        .append(update.getUpdatedBy().getId())
                        .append("\n");

                result.append("Visible To User: ")
                        .append(update.getVisibleToUser())
                        .append("\n");

                result.append("Created At: ")
                        .append(update.getCreatedAt())
                        .append("\n");

                result.append("-------------------------\n");
            }
        }

        writeResponse(
                response,
                HttpServletResponse.SC_OK,
                result.toString()
        );
    }

    /*
     * POST /api/agent/complaints/{complaintId}/updates
     *
     * Expected form parameter:
     *
     * message
     */
    private void createComplaintUpdate(
            HttpServletRequest request,
            HttpServletResponse response,
            long complaintId
    ) throws IOException {

        long agentId = getLoggedInUserId(request);

        ComplaintAssignment assignment =
                getActiveAssignmentForAgent(
                        complaintId,
                        agentId
                );

        if (assignment == null) {
            sendError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "You can add updates only to complaints assigned to you."
            );
            return;
        }

        String message = request.getParameter("message");

        if (message == null || message.isBlank()) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Update message is required."
            );
            return;
        }

        ComplaintUpdate complaintUpdate =
                new ComplaintUpdate();

        Complaint complaint = new Complaint();
        complaint.setId(complaintId);

        com.complainthub.entity.User agent =
                new com.complainthub.entity.User();

        agent.setId(agentId);

        complaintUpdate.setComplaint(complaint);
        complaintUpdate.setUpdatedBy(agent);
        complaintUpdate.setMessage(message.trim());

        /*
         * Agent-created updates are visible to the user.
         */
        complaintUpdate.setVisibleToUser(true);

        ComplaintUpdate savedUpdate =
                complaintUpdateService.createUpdate(
                        complaintUpdate
                );

        writeResponse(
                response,
                HttpServletResponse.SC_CREATED,
                "Complaint update created successfully. Update Id: "
                        + savedUpdate.getId()
        );
    }

    /*
     * PUT /api/agent/complaints/{complaintId}/status
     *
     * Expected form parameter:
     *
     * status
     */
    private void updateComplaintStatus(
            HttpServletRequest request,
            HttpServletResponse response,
            long complaintId
    ) throws IOException {

        long agentId = getLoggedInUserId(request);

        ComplaintAssignment assignment =
                getActiveAssignmentForAgent(
                        complaintId,
                        agentId
                );

        if (assignment == null) {
            sendError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "You can update only complaints assigned to you."
            );
            return;
        }

        String statusValue = request.getParameter("status");

//        System.out.println("Content-Type: "
//                + request.getContentType());
//
//        System.out.println("Status parameter: ["
//                + statusValue
//                + "]");
//
//        System.out.println("Request URI: "
//                + request.getRequestURI());
//
//        System.out.println("Path Info: "
//                + request.getPathInfo());

        if (statusValue == null || statusValue.isBlank()) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Status is required."
            );
            return;
        }

        ComplaintStatus newStatus;

        try {
            newStatus = ComplaintStatus.valueOf(
                    statusValue.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid complaint status."
            );
            return;
        }

        Complaint complaint =
                complaintService.getComplaintById(complaintId);

        if (complaint == null) {
            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Complaint not found."
            );
            return;
        }

        ComplaintStatus currentStatus =
                complaint.getStatus();

        if (!isAllowedStatusTransition(
                currentStatus,
                newStatus
        )) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "This status transition is not allowed."
            );
            return;
        }

        Complaint updatedComplaint =
                complaintService.updateStatus(
                        complaintId,
                        newStatus
                );

        writeResponse(
                response,
                HttpServletResponse.SC_OK,
                "Complaint status updated successfully. "
                        + "New status: "
                        + updatedComplaint.getStatus()
        );
    }

    /*
     * Checks whether the complaint is actively assigned
     * to the current agent.
     */
    private ComplaintAssignment getActiveAssignmentForAgent(
            long complaintId,
            long agentId
    ) {

        ComplaintAssignment activeAssignment =
                complaintAssignmentService
                        .getActiveAssignmentByComplaint(
                                complaintId
                        );

        if (activeAssignment == null) {
            return null;
        }

        if (activeAssignment.getAgent() == null) {
            return null;
        }

        if (
                activeAssignment.getAgent().getId()
                        != agentId
        ) {
            return null;
        }

        return activeAssignment;
    }

    /*
     * Current workflow:
     *
     * ASSIGNED -> IN_PROGRESS
     * IN_PROGRESS -> RESOLVED
     *
     * The agent cannot directly close a complaint.
     * CLOSED should be controlled by the admin or user-confirmation flow.
     */
    private boolean isAllowedStatusTransition(
            ComplaintStatus currentStatus,
            ComplaintStatus newStatus
    ) {

        if (currentStatus == null || newStatus == null) {
            return false;
        }

        if (currentStatus == ComplaintStatus.ASSIGNED) {
            return newStatus == ComplaintStatus.IN_PROGRESS;
        }

        if (currentStatus == ComplaintStatus.IN_PROGRESS) {
            return newStatus == ComplaintStatus.RESOLVED;
        }

        return false;
    }

    private long getLoggedInUserId(
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new SecurityException(
                    "You must be logged in."
            );
        }

        Object userIdAttribute =
                session.getAttribute(
                        AuthenticationConstants.USER_ID
                );

        if (userIdAttribute == null) {
            throw new SecurityException(
                    "You must be logged in."
            );
        }

        if (userIdAttribute instanceof Long) {
            return (Long) userIdAttribute;
        }

        if (userIdAttribute instanceof Integer) {
            return ((Integer) userIdAttribute).longValue();
        }

        if (userIdAttribute instanceof String) {
            try {
                return Long.parseLong(
                        (String) userIdAttribute
                );
            } catch (NumberFormatException e) {
                throw new SecurityException(
                        "Invalid logged-in user id."
                );
            }
        }

        throw new SecurityException(
                "Invalid logged-in user id."
        );
    }

    private long parseId(
            String value,
            String fieldName
    ) {

        try {
            long id = Long.parseLong(value);

            if (id <= 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be greater than zero."
                );
            }

            return id;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a valid number."
            );
        }
    }

    private void writeResponse(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(message);
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(message);
    }
}