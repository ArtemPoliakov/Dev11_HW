package com.homework.test;

import jakarta.servlet.ServletException;

import java.io.IOException;

public class GetBookCommand extends FrontCommand {
    @Override
    public void process() throws ServletException, IOException {
        response.setStatus(200);
        response.getWriter().write("No books go home");
        response.getWriter().flush();
        response.getWriter().close();
    }
}
