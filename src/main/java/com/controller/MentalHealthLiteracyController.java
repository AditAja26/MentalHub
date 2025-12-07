package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/literacy")

public class MentalHealthLiteracyController{
    @GetMapping
    public String showLiteracyPage(Model model) {
        return "mentalHealthLiteracyModule/literacyPage";
    }
         
}