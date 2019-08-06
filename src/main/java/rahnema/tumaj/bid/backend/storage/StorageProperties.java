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
    private String profilePicLocation = "C:\\Users\\Asus\\Desktop\\uploads";
    private String auctionLocation="C:\\Users\\Asus\\Desktop\\uploads\\auctions";

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
