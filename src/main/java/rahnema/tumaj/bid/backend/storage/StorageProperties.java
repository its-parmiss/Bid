package rahnema.tumaj.bid.backend.storage;

//
//import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String profilePicLocation = "/home/mohammad/rahnema/bid/src/main/resources/static/images";
    private String auctionLocation="/home/mohammad/rahnema/bid/src/main/resources/static/images";

    public String getAuctionLocation() {
        return auctionLocation;
    }

    public void setAuctionLocation(String auctionLocation) {
        this.auctionLocation = auctionLocation;
    }

    public String getProfilePicLocation() {
        return profilePicLocation;
    }

    public void setProfilePicLocation(String location) {
        this.profilePicLocation = location;
    }

}
