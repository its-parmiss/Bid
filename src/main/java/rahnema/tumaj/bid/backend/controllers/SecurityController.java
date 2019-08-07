package rahnema.tumaj.bid.backend.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
public class SecurityController {
    public BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityController() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }
}
