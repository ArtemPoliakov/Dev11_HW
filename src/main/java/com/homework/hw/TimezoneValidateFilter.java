package com.homework.hw;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

@WebFilter(servletNames = "TimeServlet")
public class TimezoneValidateFilter extends HttpFilter {
    private static final Logger logger = LogManager.getLogger(TimezoneValidateFilter.class);
    private static final String EXPECTED_PARAM_NAME = "timezone";
    private static final String INVALID_TIMEZONE_MESSAGE = "Invalid timezone";
    private static final String UNEXPECTED_ARGUMENT_MSG = "Unexpected argument -> bad request!";
    private static final String RESPONSE_WRITER_ERROR_MSG = "Response writer error!";
    private static final String TIME_OFFSET_PARAM_VALIDATION_REGEX = "UTC[+\\- ](1[0-8]|\\d)";

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            if (!EXPECTED_PARAM_NAME.equals(parameterNames.nextElement())) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                try {
                    res.getWriter().write(UNEXPECTED_ARGUMENT_MSG);
                    res.getWriter().flush();
                    res.getWriter().close();
                } catch (IOException e) {
                    logger.error(RESPONSE_WRITER_ERROR_MSG);
                }
                return;
            }
        }

        String timeZone = req.getParameter(EXPECTED_PARAM_NAME);
        if (Objects.isNull(timeZone)) {
            try {
                chain.doFilter(req, res);
            } catch (IOException | ServletException e) {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }

        try {
            if (!timeZone.trim().matches(TIME_OFFSET_PARAM_VALIDATION_REGEX)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                res.getWriter().write(INVALID_TIMEZONE_MESSAGE);

                res.getWriter().flush();
                res.getWriter().close();
            } catch (IOException ioe) {
                logger.error(RESPONSE_WRITER_ERROR_MSG);
            }
            return;
        }

        try {
            chain.doFilter(req, res);
        } catch (IOException | ServletException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
