package com.complainthub.controller;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAssignment;
import com.complainthub.entity.User;
import com.complainthub.entity.enums.UserRole;
import com.complainthub.service.ComplaintAssignmentService;
import com.complainthub.service.ComplaintAssignmentServiceImpl;
import com.complainthub.util.AuthenticationConstants;
import com.complainthub.util.AuthorizationUtil;
import com.complainthub.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/admin/assignments/*")
public class ComplaintAssignmentController extends HttpServlet {

    private ComplaintAssignmentService complaintAssignmentService;

    @Override
    public void init() throws ServletException {
        complaintAssignmentService =
                new ComplaintAssignmentServiceImpl();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        AuthorizationUtil.requireRole(request, UserRole.ADMIN);

        try {
            long complaintId = Long.parseLong(request.getParameter("complaintId"));
            long agentId = Long.parseLong(request.getParameter("agentId"));
            long adminId = getLoggedInUserId(request);

            Complaint complaint = new Complaint();
            complaint.setId(complaintId);

            User agent = new User();
            agent.setId(agentId);

            User assignedBy = new User();
            assignedBy.setId(adminId);

            ComplaintAssignment assignment = new ComplaintAssignment();
            assignment.setComplaint(complaint);
            assignment.setAgent(agent);
            assignment.setAssignedBy(assignedBy);
            assignment.setActive(true);

            ComplaintAssignment createdAssignment = complaintAssignmentService.createAssignment(assignment);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("text/plain");

            response.getWriter().println(
                    "Complaint assigned successfully. Assignment ID: "
                            + createdAssignment.getId()
            );

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Complaint ID and agent ID must be valid numbers");

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        AuthorizationUtil.requireRole(request, UserRole.ADMIN);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                getAllAssignments(request, response);
                return;
            }

            if (pathInfo.startsWith("/complaint/")) {
                long complaintId = extractId(pathInfo, "/complaint/");
                getAssignmentsByComplaint(complaintId, response);
                return;
            }

            if (pathInfo.startsWith("/agent/")) {
                long agentId = extractId(pathInfo, "/agent/");
                getAssignmentsByAgent(agentId, response);
                return;
            }

            long assignmentId = extractId(pathInfo, "/");
            getAssignmentById(assignmentId, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID must be a valid number");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        AuthorizationUtil.requireRole(request, UserRole.ADMIN);
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Assignment ID is required");
            return;
        }

        try {
            long assignmentId = extractId(pathInfo, "/");
            ComplaintAssignment assignment = complaintAssignmentService.getAssignmentById(assignmentId);

            if (assignment == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Assignment not found");
                return;
            }

            String activeParameter = request.getParameter("active");
            if (activeParameter != null) {
                assignment.setActive(Boolean.parseBoolean(activeParameter));
            }

            String agentIdParameter = request.getParameter("agentId");
            if (agentIdParameter != null) {
                User agent = new User();
                agent.setId(Long.parseLong(agentIdParameter));
                assignment.setAgent(agent);
            }

            String complaintIdParameter = request.getParameter("complaintId");
            if (complaintIdParameter != null) {
                Complaint complaint = new Complaint();
                complaint.setId(Long.parseLong(complaintIdParameter));
                assignment.setComplaint(complaint);
            }

            ComplaintAssignment updatedAssignment = complaintAssignmentService.updateAssignment(assignment);

            response.setContentType("text/plain");
            response.getWriter().println(
                    "Assignment updated successfully. Assignment ID: "
                            + updatedAssignment.getId()
            );
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID must be a valid number");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        AuthorizationUtil.requireRole(request, UserRole.ADMIN);
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Assignment ID is required");
            return;
        }

        try {
            long assignmentId = extractId(pathInfo, "/");
            boolean deactivated = complaintAssignmentService.deactivateAssignment(assignmentId);

            if (!deactivated) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Assignment not found");
                return;
            }

            response.setContentType("text/plain");
            response.getWriter().println("Assignment deactivated successfully");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Assignment ID must be a valid number");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void getAllAssignments(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        List<ComplaintAssignment> assignments = complaintAssignmentService.getAllAssignments();
        response.setContentType("text/plain");

        if (assignments.isEmpty()) {
            response.getWriter().println("No assignments found");
            return;
        }

        for (ComplaintAssignment assignment : assignments) {
            writeAssignment(response, assignment);
        }
    }

    private void getAssignmentById(
            long assignmentId,
            HttpServletResponse response
    ) throws IOException {
        ComplaintAssignment assignment = complaintAssignmentService.getAssignmentById(assignmentId);

        if (assignment == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Assignment not found");
            return;
        }
        response.setContentType("text/plain");
        writeAssignment(response, assignment);
    }

    private void getAssignmentsByComplaint(
            long complaintId,
            HttpServletResponse response
    ) throws IOException {
        List<ComplaintAssignment> assignments = complaintAssignmentService.getAssignmentsByComplaint(complaintId);
        response.setContentType("text/plain");

        if (assignments.isEmpty()) {
            response.getWriter().println("No assignments found for this complaint");
            return;
        }

        for (ComplaintAssignment assignment : assignments) {
            writeAssignment(response, assignment);
        }
    }

    private void getAssignmentsByAgent(
            long agentId,
            HttpServletResponse response
    ) throws IOException {
        List<ComplaintAssignment> assignments = complaintAssignmentService.getAssignmentsByAgent(agentId);
        response.setContentType("text/plain");

        if (assignments.isEmpty()) {
            response.getWriter().println("No assignments found for this agent");
            return;
        }

        for (ComplaintAssignment assignment : assignments) {
            writeAssignment(response, assignment);
        }
    }

    private void writeAssignment(
            HttpServletResponse response,
            ComplaintAssignment assignment
    ) throws IOException {
        response.getWriter().println(
                "Assignment ID: " + assignment.getId()
                        + ", Complaint ID: "
                        + assignment.getComplaint().getId()
                        + ", Agent ID: "
                        + assignment.getAgent().getId()
                        + ", Assigned By: "
                        + assignment.getAssignedBy().getId()
                        + ", Active: "
                        + assignment.isActive()
        );
    }

    private long getLoggedInUserId(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        Object userId = session.getAttribute(AuthenticationConstants.USER_ID);

        if (userId == null) {throw new IllegalArgumentException("User ID is missing from session");
        }

        if (userId instanceof Long) {
            return (Long) userId;
        }
        return Long.parseLong(userId.toString());
    }

    private long extractId(
            String pathInfo,
            String prefix
    ) {
        String idValue = pathInfo.substring(prefix.length());

        if (idValue.isBlank()) {
            throw new IllegalArgumentException("ID is required");
        }
        return Long.parseLong(idValue);
    }
}