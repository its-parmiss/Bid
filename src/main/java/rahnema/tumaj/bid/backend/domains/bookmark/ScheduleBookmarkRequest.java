package rahnema.tumaj.bid.backend.domains.bookmark;

import lombok.Data;

@Data
public class ScheduleBookmarkRequest {
    Long auctionId;
    String sessionId;
}
