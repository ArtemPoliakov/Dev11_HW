package com.homework.test;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(value = "/login")
public class LoginServlet extends HttpServlet {
    private static final String MSG_TEMPLATE = "Your login = %s; Your id = %s";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        String userId = req.getParameter("id");
        String userName = req.getParameter("login");
        Database.getMap().put(sessionId, String.format(MSG_TEMPLATE, userName, userId));
        resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        resp.sendRedirect("use");
    }
}
