package rahnema.tumaj.bid.backend.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import rahnema.tumaj.bid.backend.services.UserDetailsServiceImpl;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

import java.util.ArrayList;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenUtil tokenUtil;
    private final UserDetailsService userDetailsService;


    public WebSocketConfig(TokenUtil tokenUtil, UserDetailsServiceImpl userDetailsService) {
        this.tokenUtil = tokenUtil;
        this.userDetailsService = userDetailsService;
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
            String userName = tokenUtil.getUsernameFromToken(jwtToken).orElseThrow(TokenNotFoundException::new);
            evaluateToken(headerAccessor, jwtToken, userName);
        }
    }

    private void evaluateToken(StompHeaderAccessor headerAccessor, String jwtToken, String userName) {
        if (isTokenValid(jwtToken, userName)) {
            Authentication u = new UsernamePasswordAuthenticationToken(userName, new ArrayList<>());
            headerAccessor.setUser(u);
        }
        else {
            System.out.println("token is not valid for user: " + userName);
            throw new TokenNotFoundException();
        }
    }

    private Boolean isTokenValid(String jwtToken, String userName) {
        return tokenUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(userName));
    }
}
