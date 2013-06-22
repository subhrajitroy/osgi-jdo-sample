package org.datanucleus.samples.jpa.osgi.web.controller;

import org.datanucleus.samples.jpa.osgi.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EntityController {

    @Autowired
    private EntityService entityService;


    @RequestMapping(value = "/create/{className}/{fieldName}", method = RequestMethod.GET)
    @ResponseBody
    public String createEntity(@PathVariable String className, @PathVariable String fieldName) {
        try {
            entityService.createEntity(className, fieldName);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Created " + className + " with field " + fieldName;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public String status() {
        return "jdo - ok";
    }

}
