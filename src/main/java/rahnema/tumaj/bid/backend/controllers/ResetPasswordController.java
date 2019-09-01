package rahnema.tumaj.bid.backend.controllers;

import jdk.nashorn.internal.parser.Token;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rahnema.tumaj.bid.backend.services.forgotToken.ForgotTokenService;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

@Controller
public class ResetPasswordController {
    private final ForgotTokenService forgotTokenService;

    public ResetPasswordController(ForgotTokenService forgotTokenService) {
        this.forgotTokenService = forgotTokenService;
    }

    @GetMapping("/forgot")
    public String displayResetPassword(
            @RequestParam("token") String token,
            Model model) {
        model.addAttribute("token", token);
        if(forgotTokenService.findByForgotToken(token).isPresent())
            return "resetPassword";
        else
            throw new TokenNotFoundException();
    }
}