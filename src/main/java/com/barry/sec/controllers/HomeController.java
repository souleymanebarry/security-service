package com.barry.sec.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/")
public interface HomeController {
    @GetMapping(value = "${swagger-ui.redirect:}")
    @Operation(description = "Redirect to swagger-ui.html")
    String redirectToSwaggerUi();
}
