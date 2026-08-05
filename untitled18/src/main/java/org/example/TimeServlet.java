package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet({"/time"})
public class TimeServlet extends HttpServlet {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String timezoneParam = request.getParameter("timezone");
        String zoneLabel;
        if (timezoneParam == null || timezoneParam.isBlank()) {
            zoneLabel = "UTC";
        } else {
            zoneLabel = timezoneParam.replace(' ', '+');
        }
        ZoneId zoneId = ZoneId.of(zoneLabel);
        String formattedTime = ZonedDateTime.now(zoneId).format(TIME_FORMATTER);

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<html>");
            writer.println("<head><title>Current time</title></head>");
            writer.println("<body>");
            writer.println("<h1>" + formattedTime + " " + zoneLabel + "</h1>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
