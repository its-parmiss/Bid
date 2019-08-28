

package rahnema.tumaj.bid.backend.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.ThreadLocalRandom;

import java.lang.reflect.Type;

import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import rahnema.tumaj.bid.backend.client.ClientMessage;
import rahnema.tumaj.bid.backend.client.ServerMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionInputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.AuctionOutputMessage;
import rahnema.tumaj.bid.backend.domains.Messages.HomeOutputMessage;

public class ClientMain {


    final static String url = "http://localhost:8080/ws";


    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        WebSocketStompClient stompClient = configClient();
        String token = in.readLine();
        StompSession session = connect(stompClient, token);
        waitForInput(in, session);
    }

    private static StompSession connect(WebSocketStompClient stompClient, String token) throws InterruptedException, java.util.concurrent.ExecutionException {
        StompSession session = connectClient(stompClient, token);
        return session;
    }

    private static void waitForInput(BufferedReader in, StompSession session) throws IOException {
        String input = "";
        while (!input.equals("exit")) {
            input = in.readLine();
            if (input.equals("exitAuction")) {
                String id = in.readLine();
                System.out.println("id = " +  id);
                session.send("/app/exit", new AuctionInputMessage(id));
            } else if (input.equals("bid")) {

                String id = in.readLine();
                System.out.println("id = " +  id);
                session.send("/app/bid", new AuctionInputMessage(id, in.readLine()));
            }
            else if (input.equals("home")){
                String id = in.readLine();
                System.out.println("id = " +  id);
                System.out.println("session.isConnected() = " + session.isConnected());
                session.subscribe("/home/auction/" + id , new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return HomeOutputMessage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println("payload = " + payload.toString());
                    }
                    
                    
                });
            }
            else if (input.equals("enter")){
                String id = in.readLine();
                System.out.println("id = " +  id);
                session.send("/app/enter", new AuctionInputMessage(id));
                subscribe(id,session);
            }
        }
    }

    private static void subscribe(String auctionId, StompSession session) {
        session.subscribe("/auction/" + auctionId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return AuctionOutputMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers,
                                    Object payload) {
                if (!((AuctionOutputMessage) payload).getMessageType().equals("newBid"))
                    System.err.println(payload.toString());
            }
        });

    }

    private static StompSession connectClient(WebSocketStompClient stompClient, String token) throws InterruptedException, java.util.concurrent.ExecutionException {
        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);
        stompClient.connect(url, new WebSocketHttpHeaders(), connectHeaders, sessionHandler);
        return stompClient.connect(url, new WebSocketHttpHeaders(), connectHeaders, sessionHandler)
                .get();
    }

    private static WebSocketStompClient configClient() {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }
}

