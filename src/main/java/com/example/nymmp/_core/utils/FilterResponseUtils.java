package com.example.nymmp._core.utils;

import com.example.nymmp._core.exception.Exception401;
import com.example.nymmp._core.exception.Exception403;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FilterResponseUtils {
    public FilterResponseUtils() {
    }

    public static void unAuthorized(HttpServletResponse resp, Exception401 e) throws IOException {
        resp.setStatus(e.status().value());
        resp.setContentType("application/json; charset=utf-8");
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(e.body());
        resp.getWriter().println(responseBody);
    }

    public static void forbidden(HttpServletResponse resp, Exception403 e) throws IOException {
        resp.setStatus(e.status().value());
        resp.setContentType("application/json; charset=utf-8");
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(e.body());
        resp.getWriter().println(responseBody);
    }
}
