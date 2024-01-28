package com.homework.hw;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@WebServlet(urlPatterns = "/time", name = "TimeServlet")
public class TimeServlet extends HttpServlet {
    public static final String TIME_ZONE_SESSION_PARAM_NAME = "timeZone";
    public static final String TIME_ZONE_QUERY_PARAM_NAME = "timezone";
    public static final String DEFAULT_OFFSET = "UTC+0";
    private TemplateEngine templateEngine;
    private static final Logger logger = LogManager.getLogger(TimeServlet.class);
    public static final int TIME_WITHOUT_MILLIS_LENGTH = 19;
    private static final String RESPONSE_WRITER_ERROR_MSG = "Response writer error!";
    private static final String RELATIVE_PATH_TO_TEMPLATES = "/templates/";

    @Override
    public void init(){
        templateEngine = new TemplateEngine();
        FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setPrefix(getServletContext().getRealPath(RELATIVE_PATH_TO_TEMPLATES));
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setTemplateMode("HTML5");
        fileTemplateResolver.setCacheable(false);
        fileTemplateResolver.setOrder(templateEngine.getTemplateResolvers().size());

        templateEngine.addTemplateResolver(fileTemplateResolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String finalTimeOffsetToParse = DEFAULT_OFFSET;
        String timeZone = req.getParameter(TIME_ZONE_QUERY_PARAM_NAME);
        if(Objects.nonNull(timeZone)){
            req.getSession().setAttribute(TIME_ZONE_SESSION_PARAM_NAME, timeZone);
        }
        String sessionTimezoneAttribute = (String) req.getSession().getAttribute(TIME_ZONE_SESSION_PARAM_NAME);
        if(Objects.nonNull(sessionTimezoneAttribute)){
            finalTimeOffsetToParse = sessionTimezoneAttribute;
        }
        String time = parseTime(finalTimeOffsetToParse);
        try(PrintWriter writer = resp.getWriter()) {
            Context context = new Context();
            context.setVariable("time", time);
            templateEngine.process("time_template", context, writer);
        } catch (IOException e) {
            logger.error(RESPONSE_WRITER_ERROR_MSG);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private static String parseTime(String finalTimeOffsetToParse) {
        String parsedTimeZone = finalTimeOffsetToParse.replace("+","");
        int timeZoneInt = Integer.parseInt(parsedTimeZone.substring(3).trim());
        String time = LocalDateTime.now(ZoneOffset.ofHours(timeZoneInt)).toString();
        time = time.replace("T", " ").substring(0, TIME_WITHOUT_MILLIS_LENGTH)
                + " " + finalTimeOffsetToParse;
        return time;
    }
}