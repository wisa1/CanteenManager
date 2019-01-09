package com.example.canteenchecker.canteenmanager.proxy;

import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.core.Rating;
import com.example.canteenchecker.canteenmanager.core.ReviewData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class ServiceProxy {

    // ADMIN --> https://canteencheckeradmin.azurewebsites.net/
    // Test user: S23423432/S23423432
    // SWAGGER --> https://canteenchecker.azurewebsites.net/swagger/ui/index
    private static final String SERVICE_BASE_URL = "https://canteenchecker.azurewebsites.net/";
    private static final long ARTIFICIAL_DELAY = 100;

    private final Proxy proxy = new Retrofit.Builder()
            .baseUrl(SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Proxy.class);

    private void causeDelay() {
        try {
            Thread.sleep(ARTIFICIAL_DELAY);
        } catch (InterruptedException ignored) {
        }
    }

    public String authenticate(String userName, String password) throws IOException {
        causeDelay(); // for testing only
        return proxy.postLogin(new ProxyLogin(userName, password)).execute().body();
    }

    public Canteen getCanteen(String auth) throws IOException {
        causeDelay(); //TEST
        ProxyCanteen canteen = proxy.getCanteen(auth).execute().body();
        if (canteen == null) {
            return null;
        }
        return canteen.toCanteen();
    }

    public Void updateCanteen(String auth, Canteen canteen) throws IOException {
        causeDelay();
        ProxyCanteen pc = new ProxyCanteen();
        pc.canteenId = Integer.parseInt(canteen.getId());
        pc.name = canteen.getName();
        pc.address = canteen.getLocation();
        pc.averageRating = canteen.getAverageRating();
        pc.averageWaitingTime = canteen.getAverageWaitingTime();
        pc.meal = canteen.getSetMeal();
        pc.mealPrice = canteen.getSetMealPrice();
        pc.phone = canteen.getPhoneNumber();
        pc.website = canteen.getWebsite();

        proxy.updateCanteen(auth, pc).execute();
        return null;
    }

    public ReviewData getReviewData(String auth, String canteenId) throws IOException {
        causeDelay(); // for testing only

        ProxyReviewData reviewData = proxy.getReviewData(auth, canteenId).execute().body();
        return reviewData != null ? reviewData.toReviewData() : null;
    }

    public Collection<Rating> getAllRatings(String canteenId) throws IOException {
        causeDelay();
        ProxyReviewData reviewData = proxy.getAllRatings(canteenId).execute().body();

        Collection<Rating> ratings = new ArrayList<>();

        if(reviewData != null && reviewData.ratings != null){
            for(ProxyRating x : reviewData.ratings){
                ratings.add(x.toRating());
            }
        }
        return ratings;
    }

    public Void deleteRating(String auth, String canteenId) throws IOException {
        proxy.deleteRating(auth,canteenId).execute();
        return null;
    }

    private interface Proxy {

        @POST("/Admin/Login")
        Call<String> postLogin(@Body ProxyLogin login);

        @GET("/Admin/Canteen")
        Call<ProxyCanteen> getCanteen(@Header("Authorization") String auth);

        @PUT("/Admin/Canteen")
        Call<Void> updateCanteen(@Header("Authorization") String auth, @Body ProxyCanteen canteen);

        @GET("/Public/Canteen/{id}/Rating?nrOfRatings=0")
        Call<ProxyReviewData> getReviewData(@Header("Authorization") String auth,
                                            @Path("id") String canteenId);

        @GET("/Public/Canteen/{id}/Rating")
        Call<ProxyReviewData> getAllRatings(@Path("id") String canteenId);

        @DELETE("/Admin/Canteen/Rating/{id}")
        Call<Void> deleteRating (@Header("Authorization") String auth, @Path("id") String canteenId);

    }

    private static class ProxyCanteen {

        int canteenId;
        String name;
        String meal;
        float mealPrice;
        String website;
        String phone;
        String address;
        float averageRating;
        int averageWaitingTime;

        Canteen toCanteen() {
            return new Canteen(String.valueOf(canteenId), name, phone, website, meal, mealPrice, averageRating, address, averageWaitingTime);
        }

    }
    private static class ProxyRating {

        int ratingId;
        String username;
        String remark;
        int ratingPoints;
        long timestamp;

        private Rating toRating(){
            return new Rating(ratingId, username, remark, ratingPoints, timestamp);
        }

    }

    private static class ProxyReviewData {

        float average;
        //int count;
        int totalCount;
        ProxyRating[] ratings;
        int[] countsPerGrade;

        private int getRatingsForGrade(int grade) {
            grade--;
            return countsPerGrade != null && grade >= 0 && grade < countsPerGrade.length ?
                    countsPerGrade[grade] : 0;
        }

        ReviewData toReviewData() {
            return new ReviewData(average, totalCount, getRatingsForGrade(1),
                    getRatingsForGrade(2), getRatingsForGrade(3), getRatingsForGrade(4),
                    getRatingsForGrade(5));
        }

    }
    private static class ProxyLogin {

        final String username;
        final String password;

        ProxyLogin(String userName, String password) {
            this.username = userName;
            this.password = password;
        }

    }

}
