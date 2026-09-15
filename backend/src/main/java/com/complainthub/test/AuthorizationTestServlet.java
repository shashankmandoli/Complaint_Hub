package com.complainthub.test;

import com.complainthub.entity.enums.UserRole;
import com.complainthub.util.AuthorizationException;
import com.complainthub.util.AuthorizationUtil;
import com.complainthub.util.AuthenticationConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/test/authorization")
public class AuthorizationTestServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String testType = request.getParameter("test");

        if (testType == null || testType.isBlank()) {
            sendBadRequest(
                    response,
                    "Missing test parameter."
            );
            return;
        }

        try {
            switch (testType) {

                case "session":
                    testSession(request, response);
                    break;

                case "admin":
                    testAdmin(request, response);
                    break;

                case "user":
                    testUser(request, response);
                    break;

                case "owner-or-admin":
                    testOwnerOrAdmin(request, response);
                    break;

                case "require-admin":
                    testRequireAdmin(request, response);
                    break;

                default:
                    sendBadRequest(
                            response,
                            "Unknown test type."
                    );
            }

        } catch (AuthorizationException exception) {
            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.getWriter().write(
                    exception.getMessage()
            );
        }
    }

    private void testSession(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            sendUnauthorized(
                    response,
                    "No authenticated session found."
            );
            return;
        }

        Object userId =
                session.getAttribute(
                        AuthenticationConstants.USER_ID
                );

        Object userRole =
                session.getAttribute(
                        AuthenticationConstants.USER_ROLE
                );

        if (userId == null || userRole == null) {
            sendUnauthorized(
                    response,
                    "Session is missing authentication attributes."
            );
            return;
        }

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "Session authentication successful.\n"
                        + "User ID: " + userId + "\n"
                        + "User Role: " + userRole
        );
    }

    private void testAdmin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        boolean admin =
                AuthorizationUtil.isAdmin(request);

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "isAdmin result: " + admin
        );
    }

    private void testUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String userIdParameter =
                request.getParameter("userId");

        if (userIdParameter == null
                || userIdParameter.isBlank()) {

            sendBadRequest(
                    response,
                    "Missing userId parameter."
            );
            return;
        }

        long userId;

        try {
            userId = Long.parseLong(userIdParameter);

        } catch (NumberFormatException exception) {
            sendBadRequest(
                    response,
                    "userId must be a valid number."
            );
            return;
        }

        boolean owner =
                AuthorizationUtil.isUser(
                        request,
                        userId
                );

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "isUser result: " + owner
        );
    }

    private void testOwnerOrAdmin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String ownerIdParameter =
                request.getParameter("ownerId");

        if (ownerIdParameter == null
                || ownerIdParameter.isBlank()) {

            sendBadRequest(
                    response,
                    "Missing ownerId parameter."
            );
            return;
        }

        long ownerId;

        try {
            ownerId = Long.parseLong(ownerIdParameter);

        } catch (NumberFormatException exception) {
            sendBadRequest(
                    response,
                    "ownerId must be a valid number."
            );
            return;
        }

        AuthorizationUtil.requireOwnerOrAdmin(
                request,
                ownerId
        );

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "Owner or admin authorization successful."
        );
    }

    private void testRequireAdmin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        AuthorizationUtil.requireRole(
                request,
                UserRole.ADMIN
        );

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "Required ADMIN role confirmed."
        );
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.getWriter().write(message);
    }

    private void sendBadRequest(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
        );

        response.getWriter().write(message);
    }
}