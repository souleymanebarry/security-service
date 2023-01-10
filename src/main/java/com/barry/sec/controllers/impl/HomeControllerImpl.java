package com.barry.sec.controllers.impl;

import com.barry.sec.controllers.HomeController;
import org.springframework.stereotype.Controller;

@Controller
public class HomeControllerImpl implements HomeController {
    @Override
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}
