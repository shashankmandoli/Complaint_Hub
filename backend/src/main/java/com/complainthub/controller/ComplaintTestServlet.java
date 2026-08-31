package com.complainthub.controller;

import com.complainthub.config.HibernateUtil;
import com.complainthub.dao.ComplaintDao;
import com.complainthub.dao.ComplaintDaoImpl;
import com.complainthub.entity.Category;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.User;
import com.complainthub.entity.enums.ComplaintPriority;
import com.complainthub.entity.enums.ComplaintStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/complaint")
public class ComplaintTestServlet extends HttpServlet {

    private final ComplaintDao complaintDao =
            new ComplaintDaoImpl();

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
                        saveComplaint(req, resp);

                case "findById" ->
                        findById(req, resp);

                case "findAll" ->
                        findAll(resp);

                case "findByUserId" ->
                        findByUserId(req, resp);

                case "findByCategoryId" ->
                        findByCategoryId(req, resp);

                case "findByStatus" ->
                        findByStatus(req, resp);

                case "findByPriority" ->
                        findByPriority(req, resp);

                case "update" ->
                        updateComplaint(req, resp);

                case "updateStatus" ->
                        updateStatus(req, resp);

                case "updatePriority" ->
                        updatePriority(req, resp);

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
    // SAVE
    // =====================================================

    private void saveComplaint(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long userId = Long.parseLong(
                req.getParameter("userId")
        );

        long categoryId = Long.parseLong(
                req.getParameter("categoryId")
        );

        User user;
        Category category;

        try (Session session =
                     HibernateUtil
                             .getSessionFactory()
                             .openSession()) {

            user = session.get(User.class, userId);

            category = session.get(
                    Category.class,
                    categoryId
            );
        }

        if (user == null) {

            resp.getWriter().println(
                    "User not found."
            );

            return;
        }

        if (category == null) {

            resp.getWriter().println(
                    "Category not found."
            );

            return;
        }

        Complaint complaint = new Complaint();

        complaint.setTitle(
                req.getParameter("title")
        );

        complaint.setDescription(
                req.getParameter("description")
        );

        complaint.setUser(user);

        complaint.setCategory(category);

        complaint.setStatus(
                ComplaintStatus.valueOf(
                        req.getParameter("status")
                )
        );

        complaint.setPriority(
                ComplaintPriority.valueOf(
                        req.getParameter("priority")
                )
        );

        Complaint savedComplaint =
                complaintDao.save(complaint);

        resp.getWriter().println(
                "Complaint saved successfully."
        );

        resp.getWriter().println(
                "Complaint ID: "
                        + savedComplaint.getId()
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

        Complaint complaint =
                complaintDao.findById(id);

        if (complaint == null) {

            resp.getWriter().println(
                    "Complaint not found."
            );

            return;
        }

        printComplaint(resp, complaint);
    }


    // =====================================================
    // FIND ALL
    // =====================================================

    private void findAll(
            HttpServletResponse resp
    ) throws IOException {

        List<Complaint> complaints =
                complaintDao.findAll();

        if (complaints.isEmpty()) {

            resp.getWriter().println(
                    "No complaints found."
            );

            return;
        }

        for (Complaint complaint : complaints) {

            printComplaint(
                    resp,
                    complaint
            );

            resp.getWriter().println(
                    "--------------------------"
            );
        }
    }


    // =====================================================
    // FIND BY USER ID
    // =====================================================

    private void findByUserId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long userId = Long.parseLong(
                req.getParameter("userId")
        );

        List<Complaint> complaints =
                complaintDao.findByUserId(userId);

        printComplaintList(
                resp,
                complaints
        );
    }


    // =====================================================
    // FIND BY CATEGORY ID
    // =====================================================

    private void findByCategoryId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long categoryId = Long.parseLong(
                req.getParameter("categoryId")
        );

        List<Complaint> complaints =
                complaintDao.findByCategoryId(categoryId);

        printComplaintList(
                resp,
                complaints
        );
    }


    // =====================================================
    // FIND BY STATUS
    // =====================================================

    private void findByStatus(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        ComplaintStatus status =
                ComplaintStatus.valueOf(
                        req.getParameter("status")
                );

        List<Complaint> complaints =
                complaintDao.findByStatus(status);

        printComplaintList(
                resp,
                complaints
        );
    }


    // =====================================================
    // FIND BY PRIORITY
    // =====================================================

    private void findByPriority(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        ComplaintPriority priority =
                ComplaintPriority.valueOf(
                        req.getParameter("priority")
                );

        List<Complaint> complaints =
                complaintDao.findByPriority(priority);

        printComplaintList(
                resp,
                complaints
        );
    }


    // =====================================================
    // UPDATE COMPLAINT
    // =====================================================

    private void updateComplaint(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );

        Complaint complaint =
                complaintDao.findById(id);

        if (complaint == null) {

            resp.getWriter().println(
                    "Complaint not found."
            );

            return;
        }

        String title =
                req.getParameter("title");

        String description =
                req.getParameter("description");

        if (title != null && !title.isBlank()) {
            complaint.setTitle(title);
        }

        if (description != null &&
                !description.isBlank()) {

            complaint.setDescription(
                    description
            );
        }

        Complaint updatedComplaint =
                complaintDao.update(complaint);

        resp.getWriter().println(
                "Complaint updated successfully."
        );

        printComplaint(
                resp,
                updatedComplaint
        );
    }


    // =====================================================
    // UPDATE STATUS
    // =====================================================

    private void updateStatus(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );

        ComplaintStatus status =
                ComplaintStatus.valueOf(
                        req.getParameter("status")
                );

        Complaint complaint =
                complaintDao.updateStatus(
                        id,
                        status
                );

        if (complaint == null) {

            resp.getWriter().println(
                    "Complaint not found."
            );

            return;
        }

        resp.getWriter().println(
                "Complaint status updated successfully."
        );

        printComplaint(
                resp,
                complaint
        );
    }


    // =====================================================
    // UPDATE PRIORITY
    // =====================================================

    private void updatePriority(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );

        ComplaintPriority priority =
                ComplaintPriority.valueOf(
                        req.getParameter("priority")
                );

        Complaint complaint =
                complaintDao.updatePriority(
                        id,
                        priority
                );

        if (complaint == null) {

            resp.getWriter().println(
                    "Complaint not found."
            );

            return;
        }

        resp.getWriter().println(
                "Complaint priority updated successfully."
        );

        printComplaint(
                resp,
                complaint
        );
    }


    // =====================================================
    // PRINT SINGLE COMPLAINT
    // =====================================================

    private void printComplaint(
            HttpServletResponse resp,
            Complaint complaint
    ) throws IOException {

        resp.getWriter().println(
                "ID: " + complaint.getId()
        );

        resp.getWriter().println(
                "Title: " + complaint.getTitle()
        );

        resp.getWriter().println(
                "Description: "
                        + complaint.getDescription()
        );

        resp.getWriter().println(
                "Status: "
                        + complaint.getStatus()
        );

        resp.getWriter().println(
                "Priority: "
                        + complaint.getPriority()
        );

        resp.getWriter().println(
                "User ID: "
                        + complaint.getUser().getId()
        );

        resp.getWriter().println(
                "Category ID: "
                        + complaint.getCategory().getId()
        );

        resp.getWriter().println(
                "Created At: "
                        + complaint.getCreatedAt()
        );

        resp.getWriter().println(
                "Updated At: "
                        + complaint.getUpdatedAt()
        );
    }


    // =====================================================
    // PRINT COMPLAINT LIST
    // =====================================================

    private void printComplaintList(
            HttpServletResponse resp,
            List<Complaint> complaints
    ) throws IOException {

        if (complaints.isEmpty()) {

            resp.getWriter().println(
                    "No complaints found."
            );

            return;
        }

        for (Complaint complaint : complaints) {

            printComplaint(
                    resp,
                    complaint
            );

            resp.getWriter().println(
                    "--------------------------"
            );
        }
    }
}