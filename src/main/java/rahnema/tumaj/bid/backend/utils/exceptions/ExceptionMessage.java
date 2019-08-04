package rahnema.tumaj.bid.backend.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionMessage {
    private String message;
    private int code;
}