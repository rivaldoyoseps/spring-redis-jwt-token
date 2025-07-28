package programmerzamannow.restful.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import programmerzamannow.restful.entity.User;

@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public String hello(User user){
        return "Hello " + user.getUsername();
    }
}
