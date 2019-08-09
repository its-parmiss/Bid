package rahnema.tumaj.bid.backend.utils;


import java.io.Serializable;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenUtil extends Serializable {

    /**
     * retrieve username from given token
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token);

    /**
     * retrieve expiration date from token
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token);

    /**
     * generate token for user
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails);

    /**
     * validate token
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails);
}