package com.example.githubrepositoryviewer.configs.filters;

import com.example.githubrepositoryviewer.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

public class AcceptHeaderCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isAcceptHeaderInvalid(httpRequest)) {
            sendNotAcceptableResponse(httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAcceptHeaderInvalid(HttpServletRequest httpRequest) {
        String acceptHeader = httpRequest.getHeader("Accept");
        return !"application/json".equals(acceptHeader);
    }

    private void sendNotAcceptableResponse(HttpServletResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String customMessage = "The media type requested is not supported by this endpoint.";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), customMessage);
        httpResponse.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

}
