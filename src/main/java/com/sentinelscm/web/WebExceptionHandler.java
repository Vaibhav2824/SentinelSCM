package com.sentinelscm.web;

import com.sentinelscm.service.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/** Page-level error rendering for the Thymeleaf controllers (the API has its own JSON advice). */
@ControllerAdvice(basePackages = "com.sentinelscm.web")
public class WebExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView notFound(NotFoundException e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 404);
        mav.addObject("message", e.getMessage());
        return mav;
    }
}
