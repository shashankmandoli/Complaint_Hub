package com.complainthub.test;

import com.complainthub.entity.Category;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.User;
import com.complainthub.entity.enums.ComplaintPriority;
import com.complainthub.entity.enums.ComplaintStatus;
import com.complainthub.service.ComplaintService;
import com.complainthub.service.ComplaintServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/complaint-service")
public class ComplaintServiceTestServlet extends HttpServlet {

    private final ComplaintService complaintService =
            new ComplaintServiceImpl();

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
                    createComplaint(request, response);
                    break;

                case "findById":
                    findById(request, response);
                    break;

                case "findAll":
                    findAll(response);
                    break;

                case "findByUser":
                    findByUser(request, response);
                    break;

                case "findByCategory":
                    findByCategory(request, response);
                    break;

                case "findByStatus":
                    findByStatus(request, response);
                    break;

                case "findByPriority":
                    findByPriority(request, response);
                    break;

                case "update":
                    updateComplaint(request, response);
                    break;

                case "updateStatus":
                    updateStatus(request, response);
                    break;

                case "updatePriority":
                    updatePriority(request, response);
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

    private void createComplaint(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String title = request.getParameter("title");
        String description = request.getParameter("description");

        long userId = parseId(
                request.getParameter("userId"),
                "User Id"
        );

        long categoryId = parseId(
                request.getParameter("categoryId"),
                "Category Id"
        );

        Complaint complaint = new Complaint();

        complaint.setTitle(title);
        complaint.setDescription(description);

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);

        complaint.setUser(user);
        complaint.setCategory(category);

        Complaint created =
                complaintService.createComplaint(complaint);

        response.getWriter().println(
                "Complaint created successfully."
        );

        printComplaint(created, response);
    }

    private void findById(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long id = parseId(
                request.getParameter("id"),
                "Complaint Id"
        );

        Complaint complaint =
                complaintService.getComplaintById(id);

        if (complaint == null) {
            response.getWriter().println(
                    "Complaint not found."
            );
            return;
        }

        printComplaint(complaint, response);
    }

    private void findAll(
            HttpServletResponse response)
            throws IOException {

        List<Complaint> complaints =
                complaintService.getAllComplaints();

        response.getWriter().println(
                "Total complaints: " + complaints.size()
        );

        for (Complaint complaint : complaints) {
            printComplaint(complaint, response);
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

        List<Complaint> complaints =
                complaintService.getComplaintsByUser(userId);

        response.getWriter().println(
                "Complaints for User ID " +
                        userId +
                        ": " +
                        complaints.size()
        );

        for (Complaint complaint : complaints) {
            printComplaint(complaint, response);
        }
    }

    private void findByCategory(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long categoryId = parseId(
                request.getParameter("categoryId"),
                "Category Id"
        );

        List<Complaint> complaints =
                complaintService.getComplaintsByCategory(categoryId);

        response.getWriter().println(
                "Complaints for Category ID " +
                        categoryId +
                        ": " +
                        complaints.size()
        );

        for (Complaint complaint : complaints) {
            printComplaint(complaint, response);
        }
    }

    private void findByStatus(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String statusValue =
                request.getParameter("status");

        ComplaintStatus status =
                ComplaintStatus.valueOf(
                        statusValue.trim().toUpperCase()
                );

        List<Complaint> complaints =
                complaintService.getComplaintsByStatus(status);

        response.getWriter().println(
                "Complaints with status " +
                        status +
                        ": " +
                        complaints.size()
        );

        for (Complaint complaint : complaints) {
            printComplaint(complaint, response);
        }
    }

    private void findByPriority(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String priorityValue =
                request.getParameter("priority");

        ComplaintPriority priority =
                ComplaintPriority.valueOf(
                        priorityValue.trim().toUpperCase()
                );

        List<Complaint> complaints =
                complaintService.getComplaintsByPriority(priority);

        response.getWriter().println(
                "Complaints with priority " +
                        priority +
                        ": " +
                        complaints.size()
        );

        for (Complaint complaint : complaints) {
            printComplaint(complaint, response);
        }
    }

    private void updateComplaint(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long id = parseId(
                request.getParameter("id"),
                "Complaint Id"
        );

        String title = request.getParameter("title");
        String description = request.getParameter("description");

        Complaint complaint =
                complaintService.getComplaintById(id);

        if (complaint == null) {
            response.getWriter().println(
                    "Complaint not found."
            );
            return;
        }

        if (title != null) {
            complaint.setTitle(title);
        }

        if (description != null) {
            complaint.setDescription(description);
        }

        Complaint updated =
                complaintService.updateComplaint(complaint);

        response.getWriter().println(
                "Complaint updated successfully."
        );

        printComplaint(updated, response);
    }

    private void updateStatus(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long complaintId = parseId(
                request.getParameter("id"),
                "Complaint Id"
        );

        String statusValue =
                request.getParameter("status");

        ComplaintStatus status =
                ComplaintStatus.valueOf(
                        statusValue.trim().toUpperCase()
                );

        Complaint updated =
                complaintService.updateStatus(
                        complaintId,
                        status
                );

        if (updated == null) {
            response.getWriter().println(
                    "Complaint not found."
            );
            return;
        }

        response.getWriter().println(
                "Complaint status updated successfully."
        );

        printComplaint(updated, response);
    }

    private void updatePriority(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        long complaintId = parseId(
                request.getParameter("id"),
                "Complaint Id"
        );

        String priorityValue =
                request.getParameter("priority");

        ComplaintPriority priority =
                ComplaintPriority.valueOf(
                        priorityValue.trim().toUpperCase()
                );

        Complaint updated =
                complaintService.updatePriority(
                        complaintId,
                        priority
                );

        if (updated == null) {
            response.getWriter().println(
                    "Complaint not found."
            );
            return;
        }

        response.getWriter().println(
                "Complaint priority updated successfully."
        );

        printComplaint(updated, response);
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
                    fieldName + " must be a valid number."
            );
        }
    }

    private void printComplaint(
            Complaint complaint,
            HttpServletResponse response)
            throws IOException {

        response.getWriter().println(
                "-----------------------------"
        );

        response.getWriter().println(
                "ID: " + complaint.getId()
        );

        response.getWriter().println(
                "Title: " + complaint.getTitle()
        );

        response.getWriter().println(
                "Description: " + complaint.getDescription()
        );

        response.getWriter().println(
                "Status: " + complaint.getStatus()
        );

        response.getWriter().println(
                "Priority: " + complaint.getPriority()
        );

        response.getWriter().println(
                "User ID: " +
                        (complaint.getUser() != null
                                ? complaint.getUser().getId()
                                : null)
        );

        response.getWriter().println(
                "Category ID: " +
                        (complaint.getCategory() != null
                                ? complaint.getCategory().getId()
                                : null)
        );

        response.getWriter().println(
                "Created At: " + complaint.getCreatedAt()
        );

        response.getWriter().println(
                "Updated At: " + complaint.getUpdatedAt()
        );
    }
}