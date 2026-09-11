package com.complainthub.test;

import com.complainthub.entity.enums.UserRole;
import com.complainthub.util.AuthorizationException;
import com.complainthub.util.AuthorizationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/test/admin-only")
public class AuthorizationTestServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            AuthorizationUtil.requireRole(
                    request,
                    UserRole.ADMIN
            );

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    "Admin authorization successful."
            );

        } catch (AuthorizationException exception) {
            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.getWriter().write(
                    exception.getMessage()
            );
        }
    }
}