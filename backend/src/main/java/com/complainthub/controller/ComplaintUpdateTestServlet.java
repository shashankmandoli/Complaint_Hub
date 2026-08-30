package com.complainthub.controller;

import com.complainthub.config.HibernateUtil;
import com.complainthub.dao.ComplaintUpdateDao;
import com.complainthub.dao.ComplaintUpdateDaoImpl;
import com.complainthub.entity.Complaint;
import com.complainthub.entity.ComplaintUpdate;
import com.complainthub.entity.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/complaint-update")
public class ComplaintUpdateTestServlet extends HttpServlet {

    private final ComplaintUpdateDao complaintUpdateDao =
            new ComplaintUpdateDaoImpl();


    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        resp.setContentType("text/plain");

        String action =
                req.getParameter("action");


        if (action == null) {

            resp.getWriter().println(
                    "Please provide an action."
            );

            return;
        }


        try {

            switch (action) {

                case "save" ->
                        saveComplaintUpdate(req, resp);

                case "findById" ->
                        findById(req, resp);

                case "findAll" ->
                        findAll(resp);

                case "findByComplaintId" ->
                        findByComplaintId(req, resp);

                case "findByUserId" ->
                        findByUserId(req, resp);

                case "findVisibleByComplaintId" ->
                        findVisibleByComplaintId(req, resp);

                case "update" ->
                        updateComplaintUpdate(req, resp);

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
    // SAVE COMPLAINT UPDATE
    // =====================================================

    private void saveComplaintUpdate(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long complaintId = Long.parseLong(
                req.getParameter("complaintId")
        );

        long updatedById = Long.parseLong(
                req.getParameter("updatedById")
        );

        String message =
                req.getParameter("message");

        String visibleParam =
                req.getParameter("visibleToUser");


        if (message == null ||
                message.isBlank()) {

            resp.getWriter().println(
                    "Message is required."
            );

            return;
        }


        if (visibleParam == null) {

            resp.getWriter().println(
                    "visibleToUser is required."
            );

            return;
        }


        boolean visibleToUser =
                Boolean.parseBoolean(visibleParam);


        Complaint complaint;
        User updatedBy;


        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            complaint = session.get(
                    Complaint.class,
                    complaintId
            );

            updatedBy = session.get(
                    User.class,
                    updatedById
            );
        }


        if (complaint == null) {

            resp.getWriter().println(
                    "Complaint not found."
            );

            return;
        }


        if (updatedBy == null) {

            resp.getWriter().println(
                    "UpdatedBy user not found."
            );

            return;
        }


        ComplaintUpdate complaintUpdate =
                new ComplaintUpdate();


        complaintUpdate.setComplaint(
                complaint
        );

        complaintUpdate.setUpdatedBy(
                updatedBy
        );

        complaintUpdate.setMessage(
                message
        );

        complaintUpdate.setVisibleToUser(
                visibleToUser
        );


        ComplaintUpdate savedUpdate =
                complaintUpdateDao.save(
                        complaintUpdate
                );


        resp.getWriter().println(
                "Complaint update saved successfully."
        );

        resp.getWriter().println(
                "Update ID: "
                        + savedUpdate.getId()
        );
    }


    // =====================================================
    // FIND UPDATE BY ID
    // =====================================================

    private void findById(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );


        ComplaintUpdate complaintUpdate =
                complaintUpdateDao.findById(id);


        if (complaintUpdate == null) {

            resp.getWriter().println(
                    "Complaint update not found."
            );

            return;
        }


        printComplaintUpdate(
                resp,
                complaintUpdate
        );
    }


    // =====================================================
    // FIND ALL UPDATES
    // =====================================================

    private void findAll(
            HttpServletResponse resp
    ) throws IOException {

        List<ComplaintUpdate> updates =
                complaintUpdateDao.findAll();


        if (updates.isEmpty()) {

            resp.getWriter().println(
                    "No complaint updates found."
            );

            return;
        }


        printComplaintUpdateList(
                resp,
                updates
        );
    }


    // =====================================================
    // FIND UPDATES BY COMPLAINT ID
    // =====================================================

    private void findByComplaintId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long complaintId = Long.parseLong(
                req.getParameter("complaintId")
        );


        List<ComplaintUpdate> updates =
                complaintUpdateDao.findByComplaintId(
                        complaintId
                );


        if (updates.isEmpty()) {

            resp.getWriter().println(
                    "No updates found for this complaint."
            );

            return;
        }


        printComplaintUpdateList(
                resp,
                updates
        );
    }


    // =====================================================
    // FIND UPDATES BY USER ID
    // =====================================================

    private void findByUserId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long userId = Long.parseLong(
                req.getParameter("userId")
        );


        List<ComplaintUpdate> updates =
                complaintUpdateDao.findByUserId(
                        userId
                );


        if (updates.isEmpty()) {

            resp.getWriter().println(
                    "No updates found for this user."
            );

            return;
        }


        printComplaintUpdateList(
                resp,
                updates
        );
    }


    // =====================================================
    // FIND VISIBLE UPDATES BY COMPLAINT ID
    // =====================================================

    private void findVisibleByComplaintId(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long complaintId = Long.parseLong(
                req.getParameter("complaintId")
        );


        List<ComplaintUpdate> updates =
                complaintUpdateDao
                        .findVisibleByComplaintId(
                                complaintId
                        );


        if (updates.isEmpty()) {

            resp.getWriter().println(
                    "No visible updates found."
            );

            return;
        }


        printComplaintUpdateList(
                resp,
                updates
        );
    }


    // =====================================================
    // UPDATE COMPLAINT UPDATE
    // =====================================================

    private void updateComplaintUpdate(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );


        ComplaintUpdate complaintUpdate =
                complaintUpdateDao.findById(id);


        if (complaintUpdate == null) {

            resp.getWriter().println(
                    "Complaint update not found."
            );

            return;
        }


        String message =
                req.getParameter("message");

        String visibleParam =
                req.getParameter("visibleToUser");


        if (message != null &&
                !message.isBlank()) {

            complaintUpdate.setMessage(
                    message
            );
        }


        if (visibleParam != null &&
                !visibleParam.isBlank()) {

            complaintUpdate.setVisibleToUser(
                    Boolean.parseBoolean(
                            visibleParam
                    )
            );
        }


        ComplaintUpdate updatedComplaintUpdate =
                complaintUpdateDao.update(
                        complaintUpdate
                );


        resp.getWriter().println(
                "Complaint update updated successfully."
        );


        printComplaintUpdate(
                resp,
                updatedComplaintUpdate
        );
    }


    // =====================================================
    // PRINT SINGLE COMPLAINT UPDATE
    // =====================================================

    private void printComplaintUpdate(
            HttpServletResponse resp,
            ComplaintUpdate complaintUpdate
    ) throws IOException {

        resp.getWriter().println(
                "ID: "
                        + complaintUpdate.getId()
        );

        resp.getWriter().println(
                "Complaint ID: "
                        + complaintUpdate
                        .getComplaint()
                        .getId()
        );

        resp.getWriter().println(
                "Updated By ID: "
                        + complaintUpdate
                        .getUpdatedBy()
                        .getId()
        );

        resp.getWriter().println(
                "Updated By Name: "
                        + complaintUpdate
                        .getUpdatedBy()
                        .getName()
        );

        resp.getWriter().println(
                "Message: "
                        + complaintUpdate.getMessage()
        );

        resp.getWriter().println(
                "Visible To User: "
                        + complaintUpdate.getVisibleToUser()
        );

        resp.getWriter().println(
                "Created At: "
                        + complaintUpdate.getCreatedAt()
        );
    }


    // =====================================================
    // PRINT COMPLAINT UPDATE LIST
    // =====================================================

    private void printComplaintUpdateList(
            HttpServletResponse resp,
            List<ComplaintUpdate> updates
    ) throws IOException {

        for (ComplaintUpdate complaintUpdate : updates) {

            printComplaintUpdate(
                    resp,
                    complaintUpdate
            );

            resp.getWriter().println(
                    "--------------------------------"
            );
        }
    }
}