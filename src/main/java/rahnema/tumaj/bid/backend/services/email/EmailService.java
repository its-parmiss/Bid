package rahnema.tumaj.bid.backend.services.email;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}
