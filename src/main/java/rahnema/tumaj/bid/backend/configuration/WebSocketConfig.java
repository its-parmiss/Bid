package rahnema.tumaj.bid.backend.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenUtil tokenUtil;


    public WebSocketConfig(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker( "/auction");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (headerAccessor != null)
                    extractUserFromToken(headerAccessor);
                return message;
            }
        });
    }

    private void extractUserFromToken(StompHeaderAccessor headerAccessor) {
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            String jwtToken = Objects.requireNonNull(headerAccessor.getFirstNativeHeader("Authorization")).substring(7);
            Authentication u = new UsernamePasswordAuthenticationToken(tokenUtil.getUsernameFromToken(jwtToken).orElseThrow(TokenNotFoundException::new), new ArrayList<>());
            headerAccessor.setUser(u);
        }
    }
}
