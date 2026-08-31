package com.complainthub.controller;

import com.complainthub.dao.CategoryDao;
import com.complainthub.entity.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/category")
public class CategoryTestServlet extends HttpServlet {
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        resp.setContentType("text/plain");

        if(action == null){
            resp.getWriter().println("Please provide an action...");
        }

        switch (action) {
            case "save" -> saveCategory(resp);
            case "findById" -> findById(req, resp);
            case "findAll" -> findAll(resp);
            case "findAllActive" -> findAllActive(resp);
            case "update" -> updateCategory(req, resp);
            case "deactivate" -> deactivateCategory(req, resp);
            case "activate" -> activateCategory(req, resp);
            default -> resp.getWriter()
                    .println("Invalid action.");
        }
    }


    private void saveCategory(HttpServletResponse response)
            throws IOException {

        Category category = new Category();

        category.setName("Billing Issue");
        category.setDescription("Problems related to billing");
        category.setActive(true);

        categoryDao.save(category);

        response.getWriter()
                .println("Category saved successfully.");
    }


    private void findById(
            HttpServletRequest req,
            HttpServletResponse response
    ) throws IOException {

        String idParam = req.getParameter("id");

        if (idParam == null) {
            response.getWriter()
                    .println("Please provide a category ID.");

            return;
        }

        long id;

        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.getWriter()
                    .println("Invalid category ID.");

            return;
        }

        Category category = categoryDao.findById(id);

        if (category == null) {
            response.getWriter()
                    .println("Category not found.");

            return;
        }

        response.getWriter()
                .println("ID: " + category.getId());

        response.getWriter()
                .println("Name: " + category.getName());

        response.getWriter()
                .println("Description: " + category.getDescription());

        response.getWriter()
                .println("Active: " + category.isActive());
    }


    private void findAll(HttpServletResponse response)
            throws IOException {

        List<Category> categories = categoryDao.findAll();

        for (Category category : categories) {

            response.getWriter().println(
                    category.getId()
                            + " | "
                            + category.getName()
                            + " | "
                            + category.isActive()
            );
        }
    }


    private void findAllActive(HttpServletResponse response)
            throws IOException {

        List<Category> categories =
                categoryDao.findAllActive();

        for (Category category : categories) {

            response.getWriter().println(
                    category.getId()
                            + " | "
                            + category.getName()
                            + " | "
                            + category.isActive()
            );
        }
    }


    private void updateCategory(
            HttpServletRequest req,
            HttpServletResponse response
    ) throws IOException {

        String idParam = req.getParameter("id");

        if (idParam == null) {
            response.getWriter()
                    .println("Please provide a category ID.");

            return;
        }

        long id;

        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.getWriter()
                    .println("Invalid category ID.");

            return;
        }

        Category category = categoryDao.findById(id);

        if (category == null) {
            response.getWriter()
                    .println("Category not found.");

            return;
        }

        String name = req.getParameter("name");
        String description = req.getParameter("description");

        if (name != null && !name.isBlank()) {
            category.setName(name);
        }

        if (description != null && !description.isBlank()) {
            category.setDescription(description);
        }

        categoryDao.update(category);

        response.getWriter()
                .println("Category updated successfully.");
    }


    private void deactivateCategory(
            HttpServletRequest req,
            HttpServletResponse response
    ) throws IOException {

        String idParam = req.getParameter("id");

        if (idParam == null) {
            response.getWriter()
                    .println("Please provide a category ID.");

            return;
        }

        try {
            long id = Long.parseLong(idParam);

            Category category = categoryDao.findById(id);

            if (category == null) {
                response.getWriter()
                        .println("Category not found.");

                return;
            }

            categoryDao.deactivate(id);

            response.getWriter()
                    .println("Category deactivated successfully.");

        } catch (NumberFormatException e) {
            response.getWriter()
                    .println("Invalid category ID.");
        }
    }


    private void activateCategory(
            HttpServletRequest req,
            HttpServletResponse response
    ) throws IOException {

        String idParam = req.getParameter("id");

        if (idParam == null) {
            response.getWriter()
                    .println("Please provide a category ID.");

            return;
        }

        try {
            long id = Long.parseLong(idParam);

            Category category = categoryDao.findById(id);

            if (category == null) {
                response.getWriter()
                        .println("Category not found.");

                return;
            }

            categoryDao.activate(id);

            response.getWriter()
                    .println("Category activated successfully.");

        } catch (NumberFormatException e) {
            response.getWriter()
                    .println("Invalid category ID.");
        }
    }
}
