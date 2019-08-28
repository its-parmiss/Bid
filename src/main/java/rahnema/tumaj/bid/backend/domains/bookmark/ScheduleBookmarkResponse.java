package rahnema.tumaj.bid.backend.domains.bookmark;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleBookmarkResponse {

    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;

    public ScheduleBookmarkResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
