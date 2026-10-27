package com.student;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/MarksServlet")
public class MarksServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("studentName");

        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Student name cannot be empty."
                );
            }

            name = name.trim();

            if (name.length() > 100) {
                throw new IllegalArgumentException(
                    "Student name is too long."
                );
            }

            int mark1 = parseMark(request.getParameter("mark1"));
            int mark2 = parseMark(request.getParameter("mark2"));
            int mark3 = parseMark(request.getParameter("mark3"));

            int total = mark1 + mark2 + mark3;
            double average = total / 3.0;

            String grade;

            if (average >= 90) {
                grade = "A";
            } else if (average >= 75) {
                grade = "B";
            } else if (average >= 60) {
                grade = "C";
            } else if (average >= 40) {
                grade = "D";
            } else {
                grade = "Fail";
            }

            String resultUrl = "result.html"
                + "?name=" + encode(name)
                + "&total=" + total
                + "&average=" + String.format(
                    java.util.Locale.US, "%.2f", average
                )
                + "&grade=" + encode(grade);

            response.sendRedirect(resultUrl);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().println(
                "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                + "<title>Invalid Input</title></head><body>"
                + "<h2>Invalid Input</h2><p>"
                + escapeHtml(e.getMessage())
                + "</p><a href='index.html'>Go Back</a>"
                + "</body></html>"
            );
        }
    }

    private int parseMark(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "All three subject marks are required."
            );
        }

        try {
            int mark = Integer.parseInt(value.trim());

            if (mark < 0 || mark > 100) {
                throw new IllegalArgumentException(
                    "Marks must be between 0 and 100."
                );
            }

            return mark;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Marks must be whole numbers between 0 and 100."
            );
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}