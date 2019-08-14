package rahnema.tumaj.bid.backend.utils.validators;

import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;

@Component
public class UserValidatorImpl implements UserValidator {

    public boolean isUserPasswordValid(String password, String validator) {
        return password.matches(validator);
    }

    public boolean isUserEmailValid(String email, String validator) {
        return email.matches(validator);
    }

    public boolean isUserNameValid(String firstName, String lastName, String validator) {
        return firstName.matches(validator) &&
            (
                lastName == null ||
                lastName.equals("") ||
                lastName.matches(validator)
            );
    }

}