

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

public class ClientMain {


    final static String url = "http://localhost:8080/ws";


    public static void main(String []args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        WebSocketStompClient stompClient = configClient();
        String auctionId = in.readLine();
        String token = in.readLine();
        StompSession session = connectAndEnterAuction(stompClient, auctionId, token);
        waitForInput(in, auctionId, session);
    }

    private static StompSession connectAndEnterAuction(WebSocketStompClient stompClient, String auctionId, String token) throws InterruptedException, java.util.concurrent.ExecutionException {
        StompSession session = connectClient(stompClient, auctionId, token);
        subscribe(auctionId, session);
        session.send("/app/enter", new AuctionInputMessage(auctionId));
        return session;
    }

    private static void waitForInput(BufferedReader in, String auctionId, StompSession session) throws IOException {
        String input="";
        while (!input.equals("exit")) {
            input = in.readLine();
            if(input.equals("exitAuction")){
                session.send("/app/exit", new AuctionInputMessage(auctionId));
            }
            else if (input.equals("bid")){
                session.send("/app/bid", new AuctionInputMessage(auctionId, in.readLine()));
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
                System.err.println(payload.toString());
            }
        });
        session.subscribe("/user/auction/" + auctionId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return AuctionOutputMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers,
                                    Object payload) {
                System.err.println(payload.toString());
            }
        });
    }

    private static StompSession connectClient(WebSocketStompClient stompClient, String auctionId, String token) throws InterruptedException, java.util.concurrent.ExecutionException {
        StompSessionHandler sessionHandler = new MyStompSessionHandler(auctionId);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);
        stompClient.connect(url, new WebSocketHttpHeaders() ,connectHeaders, sessionHandler);
        return stompClient.connect(url, new WebSocketHttpHeaders() ,connectHeaders, sessionHandler)
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

