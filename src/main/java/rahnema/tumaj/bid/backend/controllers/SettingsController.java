package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.storage.StorageService;
import rahnema.tumaj.bid.backend.utils.TokenUtil;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;

import java.io.IOException;
import java.util.Map;

@RestController
public class SettingsController {

    private final UserService userService;
    private final TokenUtil tokenUtil;
    private final StorageService storageService;
    private final UserValidator userValidator;

    @Autowired
    public SettingsController(StorageService storageService,
                              UserService userService,
                              TokenUtil tokenUtil,
                              UserValidator userValidator) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
        this.storageService = storageService;
        this.userValidator = userValidator;
    }

    @PostMapping("/user/settings")
    public void changeAccountSettings(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> params) {

        String email = tokenUtil
                .getUsernameFromToken(token.split(" ")[1])
                .orElseThrow(TokenNotFoundException::new);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!validateUserFieldsFromParams(params)) {
            throw new IllegalUserInputException();
        } else {
            changeUserFieldsFromParams(params, user);
            userService.saveUser(user);
        }

    }

    private boolean validateUserFieldsFromParams(Map<String, String> params) {
        return
            userValidator.isUserEmailValid(params.get("email"),
                    ValidatorConstants.EMAIL) &&
            userValidator.isUserNameValid(params.get("first_name"),
                    params.get("last_name"),
                    ValidatorConstants.NAME);
    }

    @PostMapping("/user/settings/upload")
    public ResponseEntity<Resource> handleFileUpload(
            @RequestHeader("Authorization") String token,
            @RequestBody MultipartFile file) {

        String name = storageService.store(file, "profilePicture");
        org.springframework.core.io.Resource tempFile = storageService.loadAsResource(name, "profilePicture");
        String email = tokenUtil.getUsernameFromToken(token.split(" ")[1]).orElseThrow(TokenNotFoundException::new);
        User user = userService.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        try {
            user.setProfile_picture(tempFile.getURL().toString());
            userService.saveUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + tempFile.getFilename() + "\"").body(tempFile);
    }


    private void changeUserFieldsFromParams(Map<String, String> params, User user) {
        String newFirstName = params.get("first_name");
        String newLastName = params.get("last_name");
        String newEmail = params.get("email");

        setUpdatedUserFields(user, newFirstName, newLastName, newEmail);
    }

    private void setUpdatedUserFields(User user, String newFirstName, String newLastName, String newEmail) {
        user.setFirst_name(newFirstName);
        if(newLastName != null)
            user.setLast_name(newLastName);
        user.setEmail(newEmail);
    }

    @PostMapping("/user/settings/change-password")
    private void changeAccountPassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> params) {

        String email = tokenUtil
                .getUsernameFromToken(token.split(" ")[1])
                .orElseThrow(TokenNotFoundException::new);
        User user = userService.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

        String newPassword = params.get("password");
        user.setPassword(newPassword);

        userService.saveUser(user);
    }
}