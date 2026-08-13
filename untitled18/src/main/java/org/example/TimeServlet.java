package org.example;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.IServletWebApplication;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet({"/time"})
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    private static final String TIMEZONE_COOKIE_NAME = "lastTimezone";
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        engine = new TemplateEngine();
        IServletWebApplication application =
                JakartaServletWebApplication.buildApplication(getServletContext());

        WebApplicationTemplateResolver
                resolver = new WebApplicationTemplateResolver(application);
        resolver.setPrefix("/WEB-INF/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        engine.setTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String timezoneParam = request.getParameter("timezone");
        String zoneLabel;

        if (timezoneParam == null || timezoneParam.isBlank()) {
            String cookieValue = getTimezoneFromCookie(request);
            zoneLabel = (cookieValue != null) ? cookieValue : "UTC";
        } else {
            zoneLabel = timezoneParam.replace(' ', '+');
            saveTimezoneToCookie(response, zoneLabel);
        }

        ZoneId zoneId = ZoneId.of(zoneLabel);
        String formattedTime = ZonedDateTime.now(zoneId).format(TIME_FORMATTER);

        Context context = new Context(request.getLocale());
        context.setVariable("time",formattedTime);
        context.setVariable("zone",zoneLabel);

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        try(PrintWriter writer = response.getWriter()) {
            engine.process("time",context,writer);

        }
        }
    private String getTimezoneFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return null;
        }
        for (Cookie cookie: cookies){
            if (TIMEZONE_COOKIE_NAME.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
    private void saveTimezoneToCookie(HttpServletResponse response, String zoneLabel) {
        Cookie cookie = new Cookie(TIMEZONE_COOKIE_NAME, zoneLabel);
        cookie.setPath("/");
        cookie.setMaxAge(10);
        response.addCookie(cookie);
    }
}
