package com.complainthub.controller;

import com.complainthub.entity.User;
import com.complainthub.service.AuthenticationService;
import com.complainthub.service.AuthenticationServiceImpl;
import com.complainthub.util.AuthenticationConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User authUser = authenticationService.authenticate(email, password);

            HttpSession session = req.getSession();
            session.setAttribute(AuthenticationConstants.USER_ID, authUser.getId());
            session.setAttribute(AuthenticationConstants.USER_ROLE, authUser.getRole());

            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().println("Login successful.");
        } catch (Exception e){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().println("Invalid email or password.");
        }
    }
}
