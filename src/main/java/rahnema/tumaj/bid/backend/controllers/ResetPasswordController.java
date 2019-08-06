package rahnema.tumaj.bid.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {
    @GetMapping("/forgot")
    public String displayResetPassword (
            @RequestParam("token") String token,
            Model model) {
        model.addAttribute("token", token);
        return "resetPassword";
    }
}
