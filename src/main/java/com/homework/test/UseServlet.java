package com.homework.test;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
@WebServlet(value = "/use")
public class UseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String id = session.getId();
        String msg = Database.getMap().get(id);
        resp.getWriter().write(msg);
        resp.getWriter().flush();
        resp.getWriter().close();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
