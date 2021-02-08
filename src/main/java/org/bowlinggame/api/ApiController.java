package org.bowlinggame.api;

/* Main class to handle APIs */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/")
    public String test() {
        return "Hello";
    }
}
