package rahnema.tumaj.bid.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

@Controller
public class ResetPasswordController {

    private final RestTemplate restTemplate;

    public ResetPasswordController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/forgot")
    public String displayResetPassword(
            @RequestParam("token") String token,
            Model model) {
        model.addAttribute("token", token);
        if(tokenIsPresentAndValid(token))
            return "resetPassword";
        else
            throw new TokenNotFoundException();
    }

    private boolean tokenIsPresentAndValid(String token) {
        try {
            boolean isTokenPresent = restTemplate.postForObject("http://localhost:8701/isTokenValid", token, Boolean.class);
            return isTokenPresent;
        } catch (HttpClientErrorException ex) {
            throw new TokenNotFoundException();
        }
    }

    @GetMapping("/passwordChanged")
    public String displayPasswordChanged(
            @RequestParam("token") String token) {
        return "errors/accountConfirmed";
    }
}