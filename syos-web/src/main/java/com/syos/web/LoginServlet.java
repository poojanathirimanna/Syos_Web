package com.syos.web;

import com.syos.web.dao.UserDao;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        UserDao userDao = new UserDao();
        boolean ok = userDao.isValidUser(username, password);

        if (ok) {
            // Create session
            var session = req.getSession(true);
            session.setAttribute("username", username);

            // Redirect to protected home
            resp.sendRedirect("home");
        } else {
            // Invalid login → back to login page
            resp.sendRedirect("login.html");
        }
    }
}
