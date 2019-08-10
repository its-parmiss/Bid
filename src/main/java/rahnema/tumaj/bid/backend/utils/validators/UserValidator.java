package rahnema.tumaj.bid.backend.utils.validators;

public interface UserValidator {
    boolean isUserPasswordValid(String password, String validator);

    boolean isUserEmailValid(String email, String validator);

    boolean isUserNameValid(String firstName, String lastName, String validator);
}
