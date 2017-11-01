package com.geb.controller;


import com.geb.util.ApplicationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    
    @RequestMapping("/{page}")
    public String index(@PathVariable String page) throws ApplicationException {
        if (page.equals("index")) {
            return "index";
        }
        return null;
    }
    
}
