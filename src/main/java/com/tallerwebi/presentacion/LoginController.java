package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @RequestMapping("/vistaLogin")
    public ModelAndView mostrarLogin() {
        return new ModelAndView("log-in");
    }

}
