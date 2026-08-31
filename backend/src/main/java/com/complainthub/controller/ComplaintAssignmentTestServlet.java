package com.complainthub.controller;

import com.complainthub.config.HibernateUtil;
import com.complainthub.dao.ComplaintAssignmentDao;
import com.complainthub.dao.ComplaintAssignmentDaoImpl;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintAssignment;
import com.complainthub.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/assignment")
public class ComplaintAssignmentTestServlet extends HttpServlet {

    private final ComplaintAssignmentDao assignmentDao =
            new ComplaintAssignmentDaoImpl();


    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        resp.setContentType("text/plain");

        String action = req.getParameter("action");

        if (action == null) {
            resp.getWriter().println(
                    "Please provide an action."
            );
            return;
        }

        try {

            switch (action) {

                case "save" ->
                        saveAssignment(req, resp);

                case "findById" ->
                        findById(req, resp);

                case "findAll" ->
                        findAll(resp);

                case "findByComplaintId" ->
                        findByComplaintId(req, resp);

                case "findByAgentId" ->
                        findByAgentId(req, resp);

                case "findActiveByComplaintId" ->
                        findActiveByComplaintId(req, resp);

                case "update" ->
                        updateAssignment(req, resp);

                default ->
                        resp.getWriter().println(
                                "Invalid action."
                        );
            }

        } catch (Exception e) {

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.getWriter().println(
                    "Error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // SAVE ASSIGNMENT
    // =====================================================

    private void saveAssignment(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long complaintId = Long.parseLong(
                req.getParameter("complaintId")
        );

        long agentId = Long.parseLong(
                req.getParameter("agentId")
        );

        long assignedById = Long.parseLong(
                req.getParameter("assignedById")
        );

        Complaint complaint;
        User agent;
        User assignedBy;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            complaint = session.get(
                    Complaint.class,
                    complaintId
            );

            agent = session.get(
                    User.class,
                    agentId
            );

            assignedBy = session.get(
                    User.class,
                    assignedById
            );
        }

        if (complaint == null) {

            resp.getWriter().println(
                    "Complaint not found."
            );

            return;
        }

        if (agent == null) {

            resp.getWriter().println(
                    "Agent not found."
            );

            return;
        }

        if (assignedBy == null) {

            resp.getWriter().println(
                    "AssignedBy user not found."
            );

            return;
        }

        ComplaintAssignment assignment =
                new ComplaintAssignment();

        assignment.setComplaint(complaint);
        assignment.setAgent(agent);
        assignment.setAssignedBy(assignedBy);

        assignment.setActive(true);

        ComplaintAssignment savedAssignment =
                assignmentDao.save(assignment);

        resp.getWriter().println(
                "Complaint assignment saved successfully."
        );

        resp.getWriter().println(
                "Assignment ID: "
                        + savedAssignment.getId()
        );
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    private void findById(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );

        ComplaintAssignment assignment =
                assignmentDao.findById(id);

        if (assignment == null) {

            resp.getWriter().println(
                    "Assignment not found."
            );

            return;
        }

        printAssignment(resp, assignment);
    }


    // =====================================================
    // FIND ALL
    // =====================================================

    private void findAll(
            HttpServletResponse resp
    ) throws IOException {

        List<ComplaintAssignment> assignments =
                assignmentDao.findAll();

        printAssignmentList(
                resp,
                assignments
        );
    }


    // =====================================================
    // FIND BY COMPLAINT ID
    // =====================================================

    private void findByComplaintId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long complaintId = Long.parseLong(
                req.getParameter("complaintId")
        );

        List<ComplaintAssignment> assignments =
                assignmentDao.findByComplaintId(
                        complaintId
                );

        printAssignmentList(
                resp,
                assignments
        );
    }


    // =====================================================
    // FIND BY AGENT ID
    // =====================================================

    private void findByAgentId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long agentId = Long.parseLong(
                req.getParameter("agentId")
        );

        List<ComplaintAssignment> assignments =
                assignmentDao.findByAgentId(
                        agentId
                );

        printAssignmentList(
                resp,
                assignments
        );
    }


    // =====================================================
    // FIND ACTIVE ASSIGNMENT BY COMPLAINT ID
    // =====================================================

    private void findActiveByComplaintId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long complaintId = Long.parseLong(
                req.getParameter("complaintId")
        );

        ComplaintAssignment assignment =
                assignmentDao
                        .findActiveAssignmentByComplaintId(
                                complaintId
                        );

        if (assignment == null) {

            resp.getWriter().println(
                    "No active assignment found."
            );

            return;
        }

        printAssignment(resp, assignment);
    }


    // =====================================================
    // UPDATE ASSIGNMENT
    // =====================================================

    private void updateAssignment(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );

        ComplaintAssignment assignment =
                assignmentDao.findById(id);

        if (assignment == null) {

            resp.getWriter().println(
                    "Assignment not found."
            );

            return;
        }

        String activeParam =
                req.getParameter("active");

        if (activeParam != null) {

            assignment.setActive(
                    Boolean.parseBoolean(activeParam)
            );
        }

        ComplaintAssignment updatedAssignment =
                assignmentDao.update(assignment);

        resp.getWriter().println(
                "Complaint assignment updated successfully."
        );

        printAssignment(
                resp,
                updatedAssignment
        );
    }


    // =====================================================
    // PRINT SINGLE ASSIGNMENT
    // =====================================================

    private void printAssignment(
            HttpServletResponse resp,
            ComplaintAssignment assignment
    ) throws IOException {

        resp.getWriter().println(
                "Assignment ID: "
                        + assignment.getId()
        );

        resp.getWriter().println(
                "Complaint ID: "
                        + assignment.getComplaint().getId()
        );

        resp.getWriter().println(
                "Agent ID: "
                        + assignment.getAgent().getId()
        );

        resp.getWriter().println(
                "Assigned By ID: "
                        + assignment.getAssignedBy().getId()
        );

        resp.getWriter().println(
                "Active: "
                        + assignment.isActive()
        );

        resp.getWriter().println(
                "Created At: "
                        + assignment.getCreatedAt()
        );

        resp.getWriter().println(
                "Updated At: "
                        + assignment.getUpdatedAt()
        );
    }


    // =====================================================
    // PRINT ASSIGNMENT LIST
    // =====================================================

    private void printAssignmentList(
            HttpServletResponse resp,
            List<ComplaintAssignment> assignments
    ) throws IOException {

        if (assignments.isEmpty()) {

            resp.getWriter().println(
                    "No assignments found."
            );

            return;
        }

        for (ComplaintAssignment assignment : assignments) {

            printAssignment(
                    resp,
                    assignment
            );

            resp.getWriter().println(
                    "----------------------------"
            );
        }
    }
}