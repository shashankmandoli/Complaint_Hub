package com.complainthub.test;

import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAssignment;
import com.complainthub.entity.User;
import com.complainthub.service.ComplaintAssignmentService;
import com.complainthub.service.ComplaintAssignmentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/complaint-assignment-service")
public class ComplaintAssignmentServiceTestServlet
        extends HttpServlet {

    private final ComplaintAssignmentService assignmentService =
            new ComplaintAssignmentServiceImpl();

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
                    createAssignment(request, response);
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

                case "findByAgent":
                    findByAgent(request, response);
                    break;

                case "findActiveByComplaint":
                    findActiveByComplaint(request, response);
                    break;

                case "update":
                    updateAssignment(request, response);
                    break;

                case "deactivate":
                    deactivateAssignment(request, response);
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

    private void createAssignment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long complaintId = parseId(
                request.getParameter("complaintId"),
                "Complaint Id"
        );

        long agentId = parseId(
                request.getParameter("agentId"),
                "Agent Id"
        );

        long assignedById = parseId(
                request.getParameter("assignedById"),
                "Assigned By Id"
        );

        Complaint complaint = new Complaint();
        complaint.setId(complaintId);

        User agent = new User();
        agent.setId(agentId);

        User assignedBy = new User();
        assignedBy.setId(assignedById);

        ComplaintAssignment assignment =
                new ComplaintAssignment();

        assignment.setComplaint(complaint);
        assignment.setAgent(agent);
        assignment.setAssignedBy(assignedBy);

        ComplaintAssignment created =
                assignmentService.createAssignment(
                        assignment
                );

        response.getWriter().println(
                "Complaint assignment created successfully."
        );

        printAssignment(created, response);
    }

    private void findById(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long id = parseId(
                request.getParameter("id"),
                "Assignment Id"
        );

        ComplaintAssignment assignment =
                assignmentService.getAssignmentById(id);

        if (assignment == null) {
            response.getWriter().println(
                    "Assignment not found."
            );
            return;
        }

        printAssignment(assignment, response);
    }

    private void findAll(
            HttpServletResponse response)
            throws IOException {

        List<ComplaintAssignment> assignments =
                assignmentService.getAllAssignments();

        response.getWriter().println(
                "Total assignments: " +
                        assignments.size()
        );

        for (ComplaintAssignment assignment : assignments) {
            printAssignment(assignment, response);
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

        List<ComplaintAssignment> assignments =
                assignmentService.getAssignmentsByComplaint(
                        complaintId
                );

        response.getWriter().println(
                "Assignments for Complaint ID " +
                        complaintId +
                        ": " +
                        assignments.size()
        );

        for (ComplaintAssignment assignment : assignments) {
            printAssignment(assignment, response);
        }
    }

    private void findByAgent(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long agentId = parseId(
                request.getParameter("agentId"),
                "Agent Id"
        );

        List<ComplaintAssignment> assignments =
                assignmentService.getAssignmentsByAgent(
                        agentId
                );

        response.getWriter().println(
                "Assignments for Agent ID " +
                        agentId +
                        ": " +
                        assignments.size()
        );

        for (ComplaintAssignment assignment : assignments) {
            printAssignment(assignment, response);
        }
    }

    private void findActiveByComplaint(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long complaintId = parseId(
                request.getParameter("complaintId"),
                "Complaint Id"
        );

        ComplaintAssignment assignment =
                assignmentService.getActiveAssignmentByComplaint(
                        complaintId
                );

        if (assignment == null) {
            response.getWriter().println(
                    "No active assignment found for complaint."
            );
            return;
        }

        printAssignment(assignment, response);
    }

    private void updateAssignment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long assignmentId = parseId(
                request.getParameter("id"),
                "Assignment Id"
        );

        long complaintId = parseId(
                request.getParameter("complaintId"),
                "Complaint Id"
        );

        long agentId = parseId(
                request.getParameter("agentId"),
                "Agent Id"
        );

        long assignedById = parseId(
                request.getParameter("assignedById"),
                "Assigned By Id"
        );

        Complaint complaint = new Complaint();
        complaint.setId(complaintId);

        User agent = new User();
        agent.setId(agentId);

        User assignedBy = new User();
        assignedBy.setId(assignedById);

        ComplaintAssignment assignment =
                new ComplaintAssignment();

        assignment.setId(assignmentId);
        assignment.setComplaint(complaint);
        assignment.setAgent(agent);
        assignment.setAssignedBy(assignedBy);

        String activeValue =
                request.getParameter("active");

        if (activeValue != null) {
            assignment.setActive(
                    Boolean.parseBoolean(activeValue)
            );
        } else {
            assignment.setActive(true);
        }

        ComplaintAssignment updated =
                assignmentService.updateAssignment(
                        assignment
                );

        response.getWriter().println(
                "Complaint assignment updated successfully."
        );

        printAssignment(updated, response);
    }

    private void deactivateAssignment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long assignmentId = parseId(
                request.getParameter("id"),
                "Assignment Id"
        );

        boolean result =
                assignmentService.deactivateAssignment(
                        assignmentId
                );

        if (!result) {
            response.getWriter().println(
                    "Assignment not found."
            );
            return;
        }

        response.getWriter().println(
                "Complaint assignment deactivated successfully."
        );

        ComplaintAssignment assignment =
                assignmentService.getAssignmentById(
                        assignmentId
                );

        if (assignment != null) {
            printAssignment(assignment, response);
        }
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

    private void printAssignment(
            ComplaintAssignment assignment,
            HttpServletResponse response)
            throws IOException {

        response.getWriter().println(
                "-----------------------------"
        );

        response.getWriter().println(
                "Assignment ID: " +
                        assignment.getId()
        );

        response.getWriter().println(
                "Complaint ID: " +
                        (assignment.getComplaint() != null
                                ? assignment.getComplaint().getId()
                                : null)
        );

        response.getWriter().println(
                "Agent ID: " +
                        (assignment.getAgent() != null
                                ? assignment.getAgent().getId()
                                : null)
        );

        response.getWriter().println(
                "Assigned By ID: " +
                        (assignment.getAssignedBy() != null
                                ? assignment.getAssignedBy().getId()
                                : null)
        );

        response.getWriter().println(
                "Active: " +
                        assignment.isActive()
        );

        response.getWriter().println(
                "Created At: " +
                        assignment.getCreatedAt()
        );

        response.getWriter().println(
                "Updated At: " +
                        assignment.getUpdatedAt()
        );
    }
}