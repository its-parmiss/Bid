package rahnema.tumaj.bid.backend.client;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.List;
import java.util.Map;

public class MyStompSessionHandler
        extends StompSessionHandlerAdapter {
    private String auctionId;

    public MyStompSessionHandler(String auctionId) {
        this.auctionId = auctionId;
    }

    private void showHeaders(StompHeaders headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            System.err.print("  " + e.getKey() + ": ");
            boolean first = true;
            for (String v : e.getValue()) {
                if (!first) System.err.print(", ");
                System.err.print(v);
                first = false;
            }
            System.err.println();
        }
    }


    @Override
    public void afterConnected(StompSession session,
                               StompHeaders connectedHeaders) {
        System.err.println("Connected! Headers:");
        showHeaders(connectedHeaders);

    }
}
