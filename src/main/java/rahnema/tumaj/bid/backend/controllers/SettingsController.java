package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.TokenUtil;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;

import java.util.Map;

@RestController
public class SettingsController {

    private final UserService userService;
    private final TokenUtil tokenUtil;

    @Autowired
    public SettingsController(UserService userService,
                              TokenUtil tokenUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping("/user/settings")
    public void changeAccountSettings(@RequestHeader("Authorization") String token,
                                      @RequestParam Map<String, String> params) {

        String email = tokenUtil.getUsernameFromToken(token).orElseThrow(TokenNotFoundException::new);
        User user = userService.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

        extractUserFieldsFromParams(params, user);

        userService.saveUser(user);

    }

    private void extractUserFieldsFromParams(@RequestParam Map<String, String> params, User user) {
        String newFirstName = params.get("first_name");
        String newLastName = params.get("last_name");
        String newEmail = params.get("email");

        setUpdatedUserFields(user, newFirstName, newLastName, newEmail);
    }

    private void setUpdatedUserFields(User user, String newFirstName, String newLastName, String newEmail) {
        user.setFirst_name(newFirstName);
        user.setLast_name(newLastName);
        user.setEmail(newEmail);
    }
}
