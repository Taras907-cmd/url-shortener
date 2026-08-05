package org.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {

    private static final Pattern CUSTOM_OFFSET_PATTERN =
            Pattern.compile("^(UTC|GMT)[+-]\\d{1,2}(:[0-5]\\d)?$", Pattern.CASE_INSENSITIVE);

    private static final List<String> AVAILABLE_IDS =
            Arrays.asList(TimeZone.getAvailableIDs());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String timezone = request.getParameter("timezone");
        if (timezone != null) {
            timezone = timezone.replace(' ', '+');
        }

        if (timezone != null && !isValidTimezone(timezone)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter writer = response.getWriter()) {
                writer.write("Invalid timezone");
            }
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isValidTimezone(String timezone) {
        if (CUSTOM_OFFSET_PATTERN.matcher(timezone).matches()) {
            return true;
        }
        return AVAILABLE_IDS.contains(timezone);
    }
}