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

    private static final int MAX_TIMEZONE_BOUND_FOR_LOCAL_DATE_TIME = 18;
    private static final int MIN_TIMEZONE_BOUND_FOR_LOCAL_DATE_TIME = -18;
    private static final int WHERE_HOUR_VALUE_IS_EXPECTED_TO_START_INDEX = 3;
    private static final String EXPECTED_PARAM_NAME = "timezone";
    private static final String INVALID_TIMEZONE_MESSAGE = "Invalid timezone";
    private static final String UNEXPECTED_ARGUMENT_MSG = "Unexpected argument -> bad request!";
    private static final String RESPONSE_WRITER_ERROR_MSG = "Response writer error!";

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        Enumeration<String> parameterNames = req.getParameterNames();
        while(parameterNames.hasMoreElements()){
            if(!EXPECTED_PARAM_NAME.equals(parameterNames.nextElement())){
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
            int hourOffset = Integer.parseInt(timeZone.replace("+","").substring(WHERE_HOUR_VALUE_IS_EXPECTED_TO_START_INDEX).trim());
            if (hourOffset < MIN_TIMEZONE_BOUND_FOR_LOCAL_DATE_TIME || hourOffset > MAX_TIMEZONE_BOUND_FOR_LOCAL_DATE_TIME) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
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
