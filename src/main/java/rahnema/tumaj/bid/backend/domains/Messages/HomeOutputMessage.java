package rahnema.tumaj.bid.backend.domains.Messages;

import lombok.Data;

@Data
public class HomeOutputMessage {
    Boolean isFinished;
    Integer activeBidders;
}
