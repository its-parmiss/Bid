package rahnema.tumaj.bid.backend;

import com.google.gson.Gson;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class BidApplicationTest {

    @Autowired
    private MockMvc mvc;
    private Gson gson = new Gson();

    // Authenticate Method returns token
    String authenticate() throws Exception {
        String TOKEN_PREFIX = "Bearer ";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@bid.com");
        credentials.put("password", "Testp@ss");

        // Send request and expect to get 200 status code
        MvcResult requestResult =
                mvc.perform (
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType("application/json")
                                .content(gson.toJson(credentials))
                                .accept(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andReturn();

        // The body of the response in type of String
        String stringContent = requestResult
                .getResponse()
                .getContentAsString();

        // Convert the content into map
        Map contentMap = gson.fromJson(stringContent, Map.class);

        // Get token from map
        return TOKEN_PREFIX + contentMap.get("token");
    }

    @Test
    @Order(1)
    void register() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("firstName", "test name");
        map.put("email", "test@bid.com");
        map.put("password", "Testp@ss");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .content(gson.toJson(map))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(2)
    void createCategory() throws Exception {
        // Category creation needs password
        String CATEGORY_PASSWORD = "12345678";

        // Get the JWT Token
        String token = authenticate();

        Map<String, String> category = new HashMap<>();
        category.put("title", "my test category " + System.currentTimeMillis());

        mvc.perform(
                MockMvcRequestBuilders.post("/categories")
                .contentType("application/json")
                .header("Authorization", token)
                .param("password", CATEGORY_PASSWORD)
                .content(gson.toJson(category))
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void createAuction() throws Exception {

        // Get the JWT Token
        String token = authenticate();

        Map<String, Object> auction = new HashMap<>();
        auction.put("title", "Auction TEST");
        auction.put("description", "my test description");
        auction.put("Stringstrt", "2007-07-07 12:07:07");
        auction.put("lastBid", null);
        auction.put("activeBiddersLimit", "11");
        auction.put("expireDate", null);
        auction.put("categoryId", "2");
        auction.put("user", null);
        auction.put("imageUrls", new String[] {
                "http://testUrl.com/" + System.currentTimeMillis(),
                "https://my-url-test.ir/" + System.currentTimeMillis()
        });
        auction.put("basePrice", "15");

        mvc.perform(
                MockMvcRequestBuilders.post("/auctions")
                .contentType("application/json")
                .header("Authorization", token)
                .content(gson.toJson(auction))
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("[?($.basePrice == 15)]").exists());

    }

    @Test
    @Order(4)
    void getAllAuctions() throws Exception {

        // Get the JWT Token
        String token = authenticate();

        mvc.perform(
                MockMvcRequestBuilders.get("/auctions")
                        .contentType("application/json")
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list").exists());
    }

    @Test
    @Order(5)
    void getAllUsers() throws Exception {
        // Get the JWT Token
        String token = authenticate();

        mvc.perform(
                MockMvcRequestBuilders.get("/users")
                        .contentType("application/json")
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Order(6)
    void bookmarkAuction() throws Exception {

        // Get the JWT Token
        String token = authenticate();

        Map<String, Object> bookmark = new HashMap<>();
        bookmark.put("auctionId", "3");

        mvc.perform(
                MockMvcRequestBuilders.post("/auctions/bookmark")
                        .contentType("application/json")
                        .header("Authorization", token)
                        .content(gson.toJson(bookmark))
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());

    }

}
