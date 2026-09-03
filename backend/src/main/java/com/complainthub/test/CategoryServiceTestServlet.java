package com.complainthub.test;

import com.complainthub.entity.Category;
import com.complainthub.service.CategoryService;
import com.complainthub.service.CategoryServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/category-service")
public class CategoryServiceTestServlet extends HttpServlet {

    private final CategoryService categoryService =
            new CategoryServiceImpl();


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {

            response.getWriter().println(
                    "Action parameter is required."
            );

            return;
        }

        try {

            switch (action) {

                case "create":
                    createCategory(request, response);
                    break;

                case "findById":
                    findCategoryById(request, response);
                    break;

                case "findAll":
                    findAllCategories(response);
                    break;

                case "findActive":
                    findActiveCategories(response);
                    break;

                case "update":
                    updateCategory(request, response);
                    break;

                case "activate":
                    activateCategory(request, response);
                    break;

                case "deactivate":
                    deactivateCategory(request, response);
                    break;

                default:
                    response.getWriter().println(
                            "Invalid action."
                    );
            }

        } catch (IllegalArgumentException e) {

            response.getWriter().println(
                    "Validation Error: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            response.getWriter().println(
                    "Error: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // CREATE
    // =====================================================

    private void createCategory(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String name = request.getParameter("name");

        String description =
                request.getParameter("description");

        Category category =
                new Category();

        category.setName(name);
        category.setDescription(description);

        Category savedCategory =
                categoryService.createCategory(category);

        response.getWriter().println(
                "Category created successfully."
        );

        response.getWriter().println(
                "ID: " + savedCategory.getId()
        );

        response.getWriter().println(
                "Name: " + savedCategory.getName()
        );

        response.getWriter().println(
                "Description: "
                        + savedCategory.getDescription()
        );

        response.getWriter().println(
                "Active: " + savedCategory.isActive()
        );
    }


    // =====================================================
    // FIND BY ID
    // =====================================================

    private void findCategoryById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Long id = parseId(
                request.getParameter("id")
        );

        Category category =
                categoryService.getCategoryById(id);

        if (category == null) {

            response.getWriter().println(
                    "Category not found."
            );

            return;
        }

        printCategory(category, response);
    }


    // =====================================================
    // FIND ALL
    // =====================================================

    private void findAllCategories(
            HttpServletResponse response
    ) throws IOException {

        List<Category> categories =
                categoryService.getAllCategories();

        if (categories.isEmpty()) {

            response.getWriter().println(
                    "No categories found."
            );

            return;
        }

        for (Category category : categories) {

            printCategory(category, response);

            response.getWriter().println(
                    "-------------------------"
            );
        }
    }


    // =====================================================
    // FIND ACTIVE
    // =====================================================

    private void findActiveCategories(
            HttpServletResponse response
    ) throws IOException {

        List<Category> categories =
                categoryService.getActiveCategories();

        if (categories.isEmpty()) {

            response.getWriter().println(
                    "No active categories found."
            );

            return;
        }

        for (Category category : categories) {

            printCategory(category, response);

            response.getWriter().println(
                    "-------------------------"
            );
        }
    }


    // =====================================================
    // UPDATE
    // =====================================================

    private void updateCategory(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Long id = parseId(
                request.getParameter("id")
        );

        Category existingCategory =
                categoryService.getCategoryById(id);

        if (existingCategory == null) {

            response.getWriter().println(
                    "Category not found."
            );

            return;
        }

        String name =
                request.getParameter("name");

        String description =
                request.getParameter("description");

        if (name != null) {
            existingCategory.setName(name);
        }

        if (description != null) {
            existingCategory.setDescription(description);
        }

        Category updatedCategory =
                categoryService.updateCategory(
                        existingCategory
                );

        response.getWriter().println(
                "Category updated successfully."
        );

        printCategory(
                updatedCategory,
                response
        );
    }


    // =====================================================
    // ACTIVATE
    // =====================================================

    private void activateCategory(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Long id = parseId(
                request.getParameter("id")
        );

        boolean activated =
                categoryService.activateCategory(id);

        if (activated) {

            response.getWriter().println(
                    "Category activated successfully."
            );

        } else {

            response.getWriter().println(
                    "Category not found."
            );
        }
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    private void deactivateCategory(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Long id = parseId(
                request.getParameter("id")
        );

        boolean deactivated =
                categoryService.deactivateCategory(id);

        if (deactivated) {

            response.getWriter().println(
                    "Category deactivated successfully."
            );

        } else {

            response.getWriter().println(
                    "Category not found."
            );
        }
    }


    // =====================================================
    // HELPER METHODS
    // =====================================================

    private Long parseId(String value) {

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Category ID is required."
            );
        }

        try {

            return Long.parseLong(value);

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Category ID must be a valid number."
            );
        }
    }


    private void printCategory(
            Category category,
            HttpServletResponse response
    ) throws IOException {

        response.getWriter().println(
                "ID: " + category.getId()
        );

        response.getWriter().println(
                "Name: " + category.getName()
        );

        response.getWriter().println(
                "Description: "
                        + category.getDescription()
        );

        response.getWriter().println(
                "Active: " + category.isActive()
        );
    }
}