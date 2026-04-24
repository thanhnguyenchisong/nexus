package com.pulse.common.exception;

import com.pulse.common.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        String path = uriInfo != null ? uriInfo.getPath() : "unknown";

        // 404 — not found
        if (exception instanceof NotFoundException) {
            return Response.status(404)
                    .entity(ErrorResponse.of(404, "Not Found", "Resource not found", path))
                    .build();
        }

        // 400 — validation
        if (exception instanceof ConstraintViolationException cve) {
            String msg = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .findFirst()
                    .orElse("Validation failed");
            return Response.status(400)
                    .entity(ErrorResponse.of(400, "Bad Request", msg, path))
                    .build();
        }

        // 500 — unexpected
        log.error("Unhandled exception at [{}]: {}", path, exception.getMessage(), exception);
        return Response.status(500)
                .entity(ErrorResponse.of(500, "Internal Server Error",
                        "An unexpected error occurred", path))
                .build();
    }
}
