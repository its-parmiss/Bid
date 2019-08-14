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
    private String profilePicLocation = "/home/amirali/Public/bid-backend-service/src/main/resources/static/profilePictures";
    private String auctionLocation="/home/amirali/Public/bid-backend-service/src/main/resources/static/auctionImages";
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
