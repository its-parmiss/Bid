package rahnema.tumaj.bid.backend.services.email;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendSimpleEmail(SimpleMailMessage mail);
}
