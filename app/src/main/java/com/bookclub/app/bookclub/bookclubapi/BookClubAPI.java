/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bookclub.app.bookclub.bookclubapi;

import okhttp3.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

/**
 *
 * @author mosma
 */
public class BookClubAPI {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
//    private String serverUrl = "http://book-mertdonmezyurek297604.codeanyapp.com:8000/bookclub_server/";
    private String serverUrl = "http://192.168.43.56:8000/bookclub_server/";
    private static String cookie = null;
    private static int id;

    public BookClubAPI() {

    }

    private String get(String url) {

        try {
            Request request = null;

            if (this.cookie == null) {
                request = new Request.Builder()
                        .url(this.serverUrl + url)
                        .get()
                        .build();
            } else {
                request = new Request.Builder()
                        .url(this.serverUrl + url)
                        .get()
                        .addHeader("Cookie", this.cookie)
                        .build();
            }
//            System.out.println("Cookie:\n" + this.cookie);

            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (IOException ex) {
            Logger.getLogger(BookClubAPI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getURL(String url, String cookie) {

        try {
            Request request = null;

            if (cookie == null) {
                request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("Cookie", cookie)
                        .build();
            }
//            System.out.println("Cookie:\n" + this.cookie);
            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (IOException ex) {
            Logger.getLogger(BookClubAPI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String post(String url, String json) {
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, json);

        try {
            Request request = null;
            if (this.cookie == null) {
                request = new Request.Builder()
                        .url(this.serverUrl + url)
                        .post(body)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(this.serverUrl + url)
                        .post(body)
                        .addHeader("Cookie", this.cookie)
                        .build();
            }

            Response response = client.newCall(request).execute();

            if (this.cookie == null) {
                this.cookie = response.headers().get("Set-Cookie");
            }

//            System.out.println("---\npost():\n" + response.headers().get("Set-Cookie").toString() + "\n---");
            return response.body().string();

//            Request firstRequest = new Request.Builder()
//                    .url(this.serverUrl + url)
//                    .post(body)
//                    .build();
//            Response firstResponse = client.newCall(firstRequest).execute();
//
//            Request.Builder requestBuilder = new Request.Builder()
//                    .url(this.serverUrl + url)
//                    .post(body);
//
//            Headers headers = firstResponse.headers();
//
//            requestBuilder = requestBuilder.addHeader("Cookie", headers.get("Set-Cookie"));
//
//            Request secondRequest = requestBuilder.get().build();
//            Response secondResponse = client.newCall(secondRequest).execute();
//            return secondResponse.body().string();
        } catch (IOException ex) {
            Logger.getLogger(BookClubAPI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    //////////////////////////////
    //    USERCONTROLLER        //
    //////////////////////////////
    public ArrayList<Object> login(String username, String password) {

        ArrayList<Object> arr = new ArrayList<>();
        String json = "{"
                + "\"username\": \"" + username + "\","
                + "\"password\": \"" + password + "\""
                + "}";

        try {
            JSONObject reader = new JSONObject(this.post("login/", json));
//            System.out.println("---\nlogin():\n" + reader.toString() + "\n---");
            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            arr.add("error");
            arr.add("failed to login to server. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> signup(
            String username,
            String password,
            String mail,
            String name,
            String surname,
            String country,
            String phoneNumber,
            Date dateOfBirth,
            double d_lat,
            double d_lon
    ) {

        //Temp variables
        String lon = "" + d_lon;
        String lat = "" + d_lat;
        String onlineState = "0";
        String profilePicture = "default.jpg";
        ////////////

        ArrayList<Object> arr = new ArrayList<>();
        String json = "{"
                + "\"username\": \"" + username + "\","
                + "\"password\": \"" + password + "\","
                + "\"mail\": \"" + mail + "\","
                + "\"name\": \"" + name + " " + surname + "\","
                + "\"country\": \"" + country + "\","
                + "\"phoneNumber\": \"" + phoneNumber + "\","
                + "\"dateOfBirth\": \"" + (new SimpleDateFormat("yyyy-MM-dd")).format(dateOfBirth) + "\","
                + "\"long\": \"" + lon + "\","
                + "\"lat\": \"" + lat + "\","
                + "\"onlineState\": \"" + onlineState + "\","
                + "\"profilePicture\": \"" + profilePicture + "\""
                + "}";

        System.out.println(json);

        try {
            JSONObject reader = new JSONObject(this.post("signup/", json));
            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> forgotPassword(String username, String email) {

        if (username == null && email == null) {
            throw new Error("Username and email cannot be null at the same time");
        }

        ArrayList<Object> arr = new ArrayList<>();
        String json = "{"
                + "\"username\": \"" + username + "\","
                + "\"mail\": \"" + email + "\""
                + "}";

        try {
            JSONObject reader = new JSONObject(this.post("forgotPassword/", json));
            String status = reader.getString("status");
            String message = reader.getString("message");
            String newPass = reader.getString("password");

            arr.add(status);
            arr.add(message);
            arr.add(newPass);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> signout() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("signout/"));
            String status = reader.getString("status");
            String message = reader.getString("message");

            this.cookie = null;
            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> getSession() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("getSession/"));
            JSONObject user_info_json = reader.optJSONObject("session_id");

            User user_info = new User(
                    user_info_json.getInt("id"),
                    user_info_json.getString("username"),
                    user_info_json.getString("password"),
                    user_info_json.getString("mail"),
                    user_info_json.getString("name"),
                    user_info_json.getString("country"),
                    user_info_json.getString("phoneNumber"),
                    user_info_json.getString("profilePicture"),
                    user_info_json.getBoolean("onlineState"),
                    user_info_json.getString("dateOfBirth"),
                    user_info_json.getDouble("long"),
                    user_info_json.getDouble("lat")
            );

            arr.add("success");
            arr.add("session returned");
            arr.add(user_info);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> actionOnMatch(int match_id, boolean isConfirmed) {
        String state = isConfirmed ? "confirmed" : "rejected";

        String json = "{"
                + "\"match_id\": \"" + match_id + "\","
                + "\"state\": \"" + state + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("actionOnMatch/", json));

            String status = reader.getString("status");
            String message = reader.getString("message");

            // Returns only status and message
            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> actionOnSuggestion(int suggestion_id, boolean isConfirmed) {
        String state = isConfirmed ? "confirmed" : "rejected";

        String json = "{"
                + "\"suggestion_id\": \"" + suggestion_id + "\","
                + "\"state\": \"" + state + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("actionOnSuggestion/", json));

            String status = reader.getString("status");
            String message = reader.getString("message");

            // Returns only status and message
            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> seeOtherUserProfile(String username) {

        String url = "seeOtherUserProfile/";
        ArrayList<Object> arr = new ArrayList<>();
        String json = "{"
                + "\"username\": \"" + username + "\""
                + "}";

        try {
            JSONObject reader = new JSONObject(this.post(url, json));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject user_info_json = reader.getJSONObject("user_info");

            User user_info = new User(
                    user_info_json.getInt("id"),
                    user_info_json.getString("username"),
                    user_info_json.getString("password"),
                    user_info_json.getString("mail"),
                    user_info_json.getString("name"),
                    user_info_json.getString("country"),
                    user_info_json.getString("phoneNumber"),
                    user_info_json.getString("profilePicture"),
                    user_info_json.getBoolean("onlineState"),
                    user_info_json.getString("dateOfBirth"),
                    user_info_json.getDouble("long"),
                    user_info_json.getDouble("lat")
            );

            arr.add(status);
            arr.add(message);
            arr.add(user_info);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> matchListIndex() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("matchListIndex/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray matchJSONArr = reader.optJSONArray("matchlistIndex");

            ArrayList<Object> matchArray = null;

            if (matchJSONArr != null) {
                matchArray = new ArrayList<>();

                for (int i = 0; i < matchJSONArr.length(); i++) {
                    ArrayList<Object> match = new ArrayList<>();

                    ArrayList<Object> matchlist_info = new ArrayList<>();

                    int matchID = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getInt("id");
                    int user_id = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getInt("user_id");
                    int matched_user_id = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getInt("matched_user");
                    int match_score = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getInt("match_score");
                    int giving_book_id = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getInt("giving_book");
                    int wanted_book_id = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getInt("wanted_book");
                    String state = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getString("state");
                    String matchDate = matchJSONArr.getJSONObject(i).getJSONObject("matchlist_info").getString("match_date");

                    matchlist_info.add(matchID);
                    matchlist_info.add(user_id);
                    matchlist_info.add(matched_user_id);
                    matchlist_info.add(match_score);
                    matchlist_info.add(giving_book_id);
                    matchlist_info.add(wanted_book_id);
                    matchlist_info.add(state);
                    matchlist_info.add(matchDate);

                    Book givingBook = new Book(
                            matchJSONArr.getJSONObject(i).getJSONObject("givingBook_info").getInt("id"),
                            matchJSONArr.getJSONObject(i).getJSONObject("givingBook_info").getString("title"),
                            matchJSONArr.getJSONObject(i).getJSONObject("givingBook_info").getString("authorName"),
                            matchJSONArr.getJSONObject(i).getJSONObject("givingBook_info").getString("isbn"),
                            matchJSONArr.getJSONObject(i).getJSONObject("givingBook_info").getString("publisher"),
                            matchJSONArr.getJSONObject(i).getJSONObject("givingBook_info").getString("publishDate"),
                            matchJSONArr.getJSONObject(i).getJSONObject("givingBook_info").getString("bookPhoto")
                    );

                    Book wantedBook = new Book(
                            matchJSONArr.getJSONObject(i).getJSONObject("wantedBook_info").getInt("id"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wantedBook_info").getString("title"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wantedBook_info").getString("authorName"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wantedBook_info").getString("isbn"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wantedBook_info").getString("publisher"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wantedBook_info").getString("publishDate"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wantedBook_info").getString("bookPhoto")
                    );

                    match.add(matchlist_info);
                    match.add(givingBook);
                    match.add(wantedBook);

                    matchArray.add(match);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(matchArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    // result variable needs to be handled
    public ArrayList<Object> suggestionListIndex() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("suggestionListIndex/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray suggestJSONArr = reader.optJSONArray("suggestionList");

            ArrayList<Object> suggestArray = null;

            if (suggestJSONArr != null) {
                suggestArray = new ArrayList<>();

                for (int i = 0; i < suggestJSONArr.length(); i++) {
                    ArrayList<Object> suggest = new ArrayList<>();

                    ArrayList<Object> suggest_info = new ArrayList<>();

                    int suggestID = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getInt("id");
                    int user_id = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getInt("user_id");
                    int suggested_user_id = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getInt("suggested_user");
                    int recommendation_score = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getInt("recommendation_score");
                    int giving_book_id = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getInt("giving_book");
                    int wanted_book_id = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getInt("wanted_book");
                    String state = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getString("state");
                    String suggestion_date = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getString("suggestion_date");
                    int suggested_book_id = suggestJSONArr.getJSONObject(i).getJSONObject("suggest_info").getInt("suggested_book_id");

                    suggest_info.add(suggestID);
                    suggest_info.add(user_id);
                    suggest_info.add(suggested_user_id);
                    suggest_info.add(recommendation_score);
                    suggest_info.add(giving_book_id);
                    suggest_info.add(wanted_book_id);
                    suggest_info.add(state);
                    suggest_info.add(suggestion_date);
                    suggest_info.add(suggested_book_id);

                    Book giving_book = new Book(
                            suggestJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getInt("id"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("title"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("authorName"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("isbn"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publisher"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publishDate"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("bookPhoto")
                    );

                    Book wanted_book = new Book(
                            suggestJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getInt("id"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("title"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("authorName"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("isbn"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("publisher"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("publishDate"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("bookPhoto")
                    );

                    Book suggested_book = new Book(
                            suggestJSONArr.getJSONObject(i).getJSONObject("suggested_book_info").getInt("id"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("suggested_book_info").getString("title"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("suggested_book_info").getString("authorName"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("suggested_book_info").getString("isbn"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("suggested_book_info").getString("publisher"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("suggested_book_info").getString("publishDate"),
                            suggestJSONArr.getJSONObject(i).getJSONObject("suggested_book_info").getString("bookPhoto")
                    );

                    suggest.add(suggest_info);
                    suggest.add(giving_book);
                    suggest.add(wanted_book);
                    suggest.add(suggested_book);

                    suggestArray.add(suggest);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(suggestArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> mainMenuIndex() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("mainMenuIndex/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray tradeJSONArr = reader.optJSONArray("mainMenuIndex");

            ArrayList<Object> tradeArray = null;

            if (tradeJSONArr != null) {
                tradeArray = new ArrayList<>();

                for (int i = 0; i < tradeJSONArr.length(); i++) {
                    ArrayList<Object> trade = new ArrayList<>();

                    int tradeID = tradeJSONArr.getJSONObject(i).getJSONObject("trade_info").getInt("id");
                    User user = new User(
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getInt("id"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("username"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("password"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("mail"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("name"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("country"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("phoneNumber"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("profilePicture"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getBoolean("onlineState"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("dateOfBirth"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getDouble("long"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getDouble("lat")
                    );
                    Book book = new Book(
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getInt("id"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("title"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("authorName"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("isbn"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publisher"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publishDate"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("bookPhoto")
                    );

                    trade.add(tradeID);
                    trade.add(user);
                    trade.add(book);
                    tradeArray.add(trade);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(tradeArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> searchIndex(String input) {

        String json = "{"
                + "\"search_query\": \"" + input + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("searchIndex/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray tradeJSONArr = reader.optJSONArray("searchIndex");

            ArrayList<Object> tradeArray = null;

            if (tradeJSONArr != null) {
                tradeArray = new ArrayList<>();

                for (int i = 0; i < tradeJSONArr.length(); i++) {
                    ArrayList<Object> trade = new ArrayList<>();

                    int tradeID = tradeJSONArr.getJSONObject(i).getJSONObject("trade_info").getInt("id");
                    User user = new User(
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getInt("id"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("username"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("password"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("mail"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("name"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("country"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("phoneNumber"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("profilePicture"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getBoolean("onlineState"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getString("dateOfBirth"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getDouble("long"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("user_info").getDouble("lat")
                    );
                    Book book = new Book(
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getInt("id"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("title"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("authorName"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("isbn"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publisher"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publishDate"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("bookPhoto")
                    );

                    trade.add(tradeID);
                    trade.add(user);
                    trade.add(book);
                    tradeArray.add(trade);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(tradeArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> getUserProfile(int user_id) {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("getUserProfile/"));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject user_info = reader.optJSONObject("user_info");

            User user = null;

            if (user_info != null) {
                user = new User(
                        user_info.getInt("id"),
                        user_info.getString("username"),
                        user_info.getString("password"),
                        user_info.getString("mail"),
                        user_info.getString("name"),
                        user_info.getString("country"),
                        user_info.getString("phoneNumber"),
                        user_info.getString("profilePicture"),
                        user_info.getBoolean("onlineState"),
                        user_info.getString("dateOfBirth"),
                        user_info.getDouble("long"),
                        user_info.getDouble("lat")
                );
            }

            arr.add(status);
            arr.add(message);
            arr.add(user);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> rate(int user_id, int rating, int match_id, int suggestion_id) {

        // rating should be out of 5
        String json = "{"
                + "\"user_id\": \"" + user_id + "\","
                + "\"rating\": \"" + rating + "\","
                + "\"match_id\": " + (match_id == 0 ? "null" : "\"" + match_id + "\"") + "," // can be null
                + "\"suggestion_id\": " + (suggestion_id == 0 ? "null" : "\"" + suggestion_id + "\"") + "" // can be null
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("rate/", json));

            String status = reader.getString("status");
            String message = reader.getString("message");

            // Returns only status and message
            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> getBook(int book_id) {
        String json = "{"
                + "\"book_id\": \"" + book_id + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("getBook/", json));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject book_info = reader.optJSONObject("book_info");

            Book book = null;

            if (book_info != null) {
                book = new Book(
                        book_info.getInt("id"),
                        book_info.getString("title"),
                        book_info.getString("authorName"),
                        book_info.getString("isbn"),
                        book_info.getString("publisher"),
                        book_info.getString("publishDate"),
                        book_info.getString("bookPhoto")
                );
            }

            arr.add(status);
            arr.add(message);
            arr.add(book);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> getUserProfileID(int user_id) {

        String json = "{"
                + "\"user_id\": \"" + user_id + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("getUserProfileID/", json));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject user_info = reader.optJSONObject("user_info");

            User user = null;

            if (user_info != null) {
                user = new User(
                        user_info.getInt("id"),
                        user_info.getString("username"),
                        user_info.getString("password"),
                        user_info.getString("mail"),
                        user_info.getString("name"),
                        user_info.getString("country"),
                        user_info.getString("phoneNumber"),
                        user_info.getString("profilePicture"),
                        user_info.getBoolean("onlineState"),
                        user_info.getString("dateOfBirth"),
                        user_info.getDouble("long"),
                        user_info.getDouble("lat")
                );
            }

            arr.add(status);
            arr.add(message);
            arr.add(user);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> confirmTrade(int chat_id, boolean isConfirmed) {
        
        String state = isConfirmed ? "confirmed" : "rejected";
        
        String json = "{"
                + "\"chat_id\": \"" + chat_id + "\","
                + "\"state\": \"" + state + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("confirmTrade/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> searchBook(String search_book) {

        String json = "{"
                + "\"search_book\": \"" + search_book + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("searchBook/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray tradeJSONArr = reader.optJSONArray("books_index");

            ArrayList<Object> tradeArray = null;

            if (tradeJSONArr != null) {
                tradeArray = new ArrayList<>();

                for (int i = 0; i < tradeJSONArr.length(); i++) {

                    Book book = new Book(
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getInt("id"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("title"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("authorName"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("isbn"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publisher"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publishDate"),
                            tradeJSONArr.getJSONObject(i).getJSONObject("book_info").getString("bookPhoto")
                    );

                    tradeArray.add(book);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(tradeArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> userRating(int user_id) {
        ArrayList<Object> arr = new ArrayList<>();

        String json = "{"
                + "\"id\": \"" + user_id + "\""
                + "}";

        try {
            JSONObject reader = new JSONObject(this.post("userRating/", json));
            System.out.println("Reader : " + reader);
            String status = reader.getString("status");
            String message = reader.getString("message");
            double rate = reader.optDouble("final_rate");

            arr.add(status);
            arr.add(message);
            arr.add(rate);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    //////////////////////////////
    //    USERCONTROLLER ENDS   //
    //////////////////////////////
    // ------------------------------------- //
    /////////////////////////////////////
    //       WISHLISTCONTROLLER        //
    /////////////////////////////////////
    public ArrayList<Object> wishlist_index() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("wishlist/index/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray chatJSONArr = reader.optJSONArray("wishlist");

            ArrayList<Object> matchArray = null;

            if (chatJSONArr != null) {
                matchArray = new ArrayList<>();

                for (int i = 0; i < chatJSONArr.length(); i++) {
                    ArrayList<Object> match = new ArrayList<>();

                    ArrayList<Object> matchlist_info = new ArrayList<>();

                    int matchID = chatJSONArr.getJSONObject(i).getJSONObject("wishlist_info").getInt("id");
                    int user_id = chatJSONArr.getJSONObject(i).getJSONObject("wishlist_info").getInt("user_id");
                    int book_id = chatJSONArr.getJSONObject(i).getJSONObject("wishlist_info").getInt("book_id");
                    int order = chatJSONArr.getJSONObject(i).getJSONObject("wishlist_info").getInt("order");

                    matchlist_info.add(matchID);
                    matchlist_info.add(user_id);
                    matchlist_info.add(book_id);
                    matchlist_info.add(order);

                    Book book_info = new Book(
                            chatJSONArr.getJSONObject(i).getJSONObject("book_info").getInt("id"),
                            chatJSONArr.getJSONObject(i).getJSONObject("book_info").getString("title"),
                            chatJSONArr.getJSONObject(i).getJSONObject("book_info").getString("authorName"),
                            chatJSONArr.getJSONObject(i).getJSONObject("book_info").getString("isbn"),
                            chatJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publisher"),
                            chatJSONArr.getJSONObject(i).getJSONObject("book_info").getString("publishDate"),
                            chatJSONArr.getJSONObject(i).getJSONObject("book_info").getString("bookPhoto")
                    );

                    // Inception level 1
                    match.add(matchlist_info);
                    match.add(book_info);

                    // Inception level 2
                    matchArray.add(match);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(matchArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> wishlist_add(int bookID) {
        String json = "{"
                + "\"book_id\": \"" + bookID + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("wishlist/add/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            //there are only success messages
            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> wishlist_delete(int wishlist_id) {
        String json = "{"
                + "\"wishlist_id\": \"" + wishlist_id + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("wishlist/delete/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            //there are only success messages
            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> wishlist_drag(int wishlist_id, boolean isGoingUp) {
        String json = "{"
                + "\"wishlist_id\": \"" + wishlist_id + "\","
                + "\"action\": \"" + (isGoingUp ? "up" : "down") + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("wishlist/drag/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    /////////////////////////////////////
    //     WISHLISTCONTROLLER ENDS     //
    /////////////////////////////////////
    // ------------------------------------- //
    /////////////////////////////////////
    //    ACCOUNTSETTINGSCONTROLLER    //
    /////////////////////////////////////
    public ArrayList<Object> accountSettings_index() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("accountSettings/index/"));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject account_settings = reader.optJSONObject("account_settings");

            ArrayList<Object> options = null;

            if (account_settings != null) {
                options = new ArrayList<>();
                options.add(account_settings.getInt("user_id"));
                options.add(account_settings.getBoolean("userAvailability"));
                options.add(account_settings.getBoolean("userMessagable"));
                options.add(account_settings.getBoolean("lastSeen"));
            }

            arr.add(status);
            arr.add(message);
            arr.add(options);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_reset() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("accountSettings/reset/"));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject account_settings = reader.optJSONObject("account_settings");

            ArrayList<Object> options = null;

            if (account_settings != null) {
                options = new ArrayList<>();
                options.add(account_settings.getInt("user_id"));
                options.add(account_settings.getBoolean("userAvailability"));
                options.add(account_settings.getBoolean("userMessagable"));
                options.add(account_settings.getBoolean("lastSeen"));
            }

            arr.add(status);
            arr.add(message);
            arr.add(options);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_changeAvailability() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("accountSettings/changeAvailability/"));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject account_settings = reader.optJSONObject("account_settings");

            ArrayList<Object> options = null;

            if (account_settings != null) {
                options = new ArrayList<>();
                options.add(account_settings.getInt("user_id"));
                options.add(account_settings.getBoolean("userAvailability"));
                options.add(account_settings.getBoolean("userMessagable"));
                options.add(account_settings.getBoolean("lastSeen"));
            }

            arr.add(status);
            arr.add(message);
            arr.add(options);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_changeMessagable() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("accountSettings/changeMessagable/"));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject account_settings = reader.optJSONObject("account_settings");

            ArrayList<Object> options = null;

            if (account_settings != null) {
                options = new ArrayList<>();
                options.add(account_settings.getInt("user_id"));
                options.add(account_settings.getBoolean("userAvailability"));
                options.add(account_settings.getBoolean("userMessagable"));
                options.add(account_settings.getBoolean("lastSeen"));
            }

            arr.add(status);
            arr.add(message);
            arr.add(options);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_lastSeen() {

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("accountSettings/lastSeen/"));

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject account_settings = reader.optJSONObject("account_settings");

            ArrayList<Object> options = null;

            if (account_settings != null) {
                options = new ArrayList<>();
                options.add(account_settings.getInt("user_id"));
                options.add(account_settings.getBoolean("userAvailability"));
                options.add(account_settings.getBoolean("userMessagable"));
                options.add(account_settings.getBoolean("lastSeen"));
            }

            arr.add(status);
            arr.add(message);
            arr.add(options);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_changePicture(String profile_picture) {

        String json = "{"
                + "\"profile_picture\": \"" + profile_picture + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("accountSettings/changePicture/", json));
            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONObject account_settings = reader.optJSONObject("account_settings");

            ArrayList<Object> options = null;

            if (account_settings != null) {
                options = new ArrayList<>();
                options.add(account_settings.getInt("user_id"));
                options.add(account_settings.getBoolean("userAvailability"));
                options.add(account_settings.getBoolean("userMessagable"));
                options.add(account_settings.getBoolean("lastSeen"));
            }

            arr.add(status);
            arr.add(message);
            arr.add(options);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_changePhoneNumber(String phone_number) {
        String json = "{"
                + "\"phone_number\": \"" + phone_number + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("accountSettings/changePhoneNumber/", json));
            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_changeMail(String mail) {
        String json = "{"
                + "\"mail\": \"" + mail + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("accountSettings/changeMail/", json));
            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_changeLocation(double lat, double lon, String country) {
        String json = "{"
                + "\"lat\": \"" + lat + "\","
                + "\"long\": \"" + lon + "\","
                + "\"country\": \"" + country + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("accountSettings/changeLocation/", json));
            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_onlineState() {
        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.get("accountSettings/onlineState/"));

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> accountSettings_changePassword(String password) {
        String json = "{"
                + "\"password\": \"" + password + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(this.post("accountSettings/changePassword/", json));
            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    /////////////////////////////////////////
    //    ACCOUNTSETTINGSCONTROLLER ENDS   //
    /////////////////////////////////////////
    // ------------------------------------- //
    /////////////////////////////////////
    //      TRADELISTCONTROLLER        //
    /////////////////////////////////////
    public ArrayList<Object> tradelist_index() {
        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("tradelist/index/");
            JSONObject reader = new JSONObject(a);
            System.out.println(reader);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray JSONArr = reader.optJSONArray("tradelist");

            ArrayList<Object> tradeArray = null;

            if (JSONArr != null) {
                tradeArray = new ArrayList<>();

                for (int i = 0; i < JSONArr.length(); i++) {
                    ArrayList<Object> trade = new ArrayList<>();

                    ArrayList<Object> tradelist_info = new ArrayList<>();

                    int tradeID = JSONArr.getJSONObject(i).getJSONObject("tradelist_info").getInt("id");
                    int user_id = JSONArr.getJSONObject(i).getJSONObject("tradelist_info").getInt("user_id");
                    int giving_book_id = JSONArr.getJSONObject(i).getJSONObject("tradelist_info").getInt("givingBook_id");

                    tradelist_info.add(tradeID);
                    tradelist_info.add(user_id);
                    tradelist_info.add(giving_book_id);

                    Book givingBook = new Book(
                            JSONArr.getJSONObject(i).getJSONObject("giving_book_info").getInt("id"),
                            JSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("title"),
                            JSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("authorName"),
                            JSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("isbn"),
                            JSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publisher"),
                            JSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publishDate"),
                            JSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("bookPhoto")
                    );

                    trade.add(tradelist_info);
                    trade.add(givingBook);

                    tradeArray.add(trade);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(tradeArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> tradelist_delete(int tradelist_id) {
        String json = "{"
                + "\"tradelist_id\": \"" + tradelist_id + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("tradelist/delete/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> tradelist_add(int user_id, int givingBook_id) {
        String json = "{"
                + "\"user_id\": \"" + user_id + "\","
                + "\"givingBook_id\": \"" + givingBook_id + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("tradelist/add/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    /////////////////////////////////////
    //    TRADELISTCONTROLLER ENDS     //
    /////////////////////////////////////
    // ------------------------------------- //
    /////////////////////////////////////
    //        HISTORYCONTROLLER        //
    /////////////////////////////////////

    public ArrayList<Object> history_index_suggestion() {
        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("history/index/suggestion/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray matchJSONArr = reader.optJSONArray("history");

            ArrayList<Object> matchArray = null;

            if (matchJSONArr != null) {
                matchArray = new ArrayList<>();

                for (int i = 0; i < matchJSONArr.length(); i++) {
                    ArrayList<Object> match = new ArrayList<>();

                    ///////////////////////////////////////////////
                    ArrayList<Object> suggestion_history_info = new ArrayList<>();

                    int match_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_history_info").optInt("match_id"); // can be null
                    int user_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_history_info").getInt("user_id");
                    int suggestion_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_history_info").optInt("suggestion_id"); // can be null
                    String state = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_history_info").getString("state");
                    String dateOfAction = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_history_info").getString("dateOfAction");

                    suggestion_history_info.add(match_id);
                    suggestion_history_info.add(user_id);
                    suggestion_history_info.add(suggestion_id);
                    suggestion_history_info.add(state);
                    suggestion_history_info.add(dateOfAction);

                    ////////////////////////////////////////////////////
                    ArrayList<Object> suggestion_info = new ArrayList<>();

                    int suggestion_info_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getInt("id");
                    int suggestion_user_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getInt("user_id");
                    int suggested_user_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getInt("suggested_user");
                    int recommendation_score = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getInt("recommendation_score");
                    int giving_book_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getInt("giving_book");
                    int wanted_book_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getInt("wanted_book");
                    String suggestion_state = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getString("state");
                    String suggestion_date = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getString("suggestion_date");
                    int suggested_book_id = matchJSONArr.getJSONObject(i).getJSONObject("suggestion_info").getInt("suggested_book_id");

                    suggestion_info.add(suggestion_info_id);
                    suggestion_info.add(suggestion_user_id);
                    suggestion_info.add(suggested_user_id);
                    suggestion_info.add(recommendation_score);
                    suggestion_info.add(giving_book_id);
                    suggestion_info.add(wanted_book_id);
                    suggestion_info.add(suggestion_state);
                    suggestion_info.add(suggestion_date);
                    suggestion_info.add(suggested_book_id);

                    ////////////////////////////////////////////////
                    Book giving_book = new Book(
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getInt("id"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("title"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("authorName"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("isbn"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publisher"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publishDate"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("bookPhoto")
                    );

                    Book wanted_book = new Book(
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getInt("id"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("title"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("authorName"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("isbn"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("publisher"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("publishDate"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("bookPhoto")
                    );

                    match.add(suggestion_history_info);
                    match.add(suggestion_info);
                    match.add(giving_book);
                    match.add(wanted_book);

                    matchArray.add(match);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(matchArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }
    
    public ArrayList<Object> history_index_match() {
        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("history/index/match/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray matchJSONArr = reader.optJSONArray("history");

            ArrayList<Object> matchArray = null;

            if (matchJSONArr != null) {
                matchArray = new ArrayList<>();

                for (int i = 0; i < matchJSONArr.length(); i++) {
                    ArrayList<Object> match = new ArrayList<>();

                    ///////////////////////////////////////////////
                    ArrayList<Object> match_history_info = new ArrayList<>();

                    int match_id = matchJSONArr.getJSONObject(i).getJSONObject("match_history_info").optInt("match_id"); // can be null
                    int user_id = matchJSONArr.getJSONObject(i).getJSONObject("match_history_info").getInt("user_id");
                    int suggestion_id = matchJSONArr.getJSONObject(i).getJSONObject("match_history_info").optInt("suggestion_id"); // can be null
                    String state = matchJSONArr.getJSONObject(i).getJSONObject("match_history_info").getString("state");
                    String dateOfAction = matchJSONArr.getJSONObject(i).getJSONObject("match_history_info").getString("dateOfAction");

                    match_history_info.add(match_id);
                    match_history_info.add(user_id);
                    match_history_info.add(suggestion_id);
                    match_history_info.add(state);
                    match_history_info.add(dateOfAction);

                    ////////////////////////////////////////////////////
                    ArrayList<Object> match_info = new ArrayList<>();

                    int match_info_id = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getInt("id");
                    int match_user_id = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getInt("user_id");
                    int matched_user_id = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getInt("matched_user");
                    int match_score = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getInt("match_score");
                    int giving_book_id = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getInt("giving_book");
                    int wanted_book_id = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getInt("wanted_book");
                    String match_state = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getString("state");
                    String match_date = matchJSONArr.getJSONObject(i).getJSONObject("match_info").getString("match_date");

                    match_info.add(match_info_id);
                    match_info.add(match_user_id);
                    match_info.add(matched_user_id);
                    match_info.add(match_score);
                    match_info.add(giving_book_id);
                    match_info.add(wanted_book_id);
                    match_info.add(match_state);
                    match_info.add(match_date);

                    ////////////////////////////////////////////////
                    Book giving_book = new Book(
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getInt("id"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("title"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("authorName"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("isbn"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publisher"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("publishDate"),
                            matchJSONArr.getJSONObject(i).getJSONObject("giving_book_info").getString("bookPhoto")
                    );

                    Book wanted_book = new Book(
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getInt("id"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("title"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("authorName"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("isbn"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("publisher"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("publishDate"),
                            matchJSONArr.getJSONObject(i).getJSONObject("wanted_book_info").getString("bookPhoto")
                    );

                    match.add(match_history_info);
                    match.add(match_info);
                    match.add(giving_book);
                    match.add(wanted_book);

                    matchArray.add(match);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(matchArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> history_clear() {
        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("history/clear/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    /////////////////////////////////////
    //      HISTORYCONTROLLER ENDS     //
    /////////////////////////////////////
    // ------------------------------------- //
    /////////////////////////////////////
    //          CHATCONTROLLER         //
    /////////////////////////////////////
    public ArrayList<Object> chat_index() {
        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("chat/index/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray chatJSONArr = reader.optJSONArray("chat_info");

            ArrayList<Object> chatArray = null;

            if (chatJSONArr != null) {
                chatArray = new ArrayList<>();

                for (int i = 0; i < chatJSONArr.length(); i++) {
                    ArrayList<Object> chat = new ArrayList<>();

                    JSONObject chatJSON = chatJSONArr.getJSONObject(i);

                    chat.add(chatJSON.getInt("id"));
                    chat.add(chatJSON.getInt("user_id_1"));
                    chat.add(chatJSON.getInt("user_id_2"));
                    chat.add(chatJSON.getString("state_1"));
                    chat.add(chatJSON.getString("state_2"));
                    chat.add(chatJSON.optInt("match_id")); // can be null
                    chat.add(chatJSON.optInt("suggestion_id")); // can be null
                    chatArray.add(chat);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(chatArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> chat_messagelist(int chatID) {

        String json = "{"
                + "\"chat_id\": \"" + chatID + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("chat/messageList/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray chatJSONArr = reader.optJSONArray("message_info");

            ArrayList<Object> chatArray = null;

            if (chatJSONArr != null) {
                chatArray = new ArrayList<>();

                for (int i = 0; i < chatJSONArr.length(); i++) {
                    ArrayList<Object> chat = new ArrayList<>();

                    JSONObject chatJSON = chatJSONArr.getJSONObject(i);

                    String date_info = chatJSON.getString("date_info");
                    boolean isSeen_info = chatJSON.getBoolean("isSeen_info");
                    String text_info = chatJSON.getString("text_info");
                    int sender_info = chatJSON.getInt("sender_info");

                    chat.add(date_info);
                    chat.add(isSeen_info);
                    chat.add(text_info);
                    chat.add(sender_info);

                    chatArray.add(chat);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(chatArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> chat_send(int chatID, String text) {

        String json = "{"
                + "\"chat_id\": \"" + chatID + "\","
                + "\"messageText\": \"" + text + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("chat/send/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> chat_read(int message_id) {
        String json = "{"
                + "\"message_id\": \"" + message_id + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("chat/read/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    /////////////////////////////////////
    //        CHATCONTROLLER ENDS      //
    /////////////////////////////////////
    // ------------------------------------- //
    /////////////////////////////////////
    //        MESSAGECONTROLLER        //
    /////////////////////////////////////
    public ArrayList<Object> message_index(int message_id) {

        String json = "{"
                + "\"message_id\": \"" + message_id + "\""
                + "}";

        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.post("message/index/", json);
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");
            JSONArray JSONArr = reader.optJSONArray("message_info");

            ArrayList<Object> messageArray = null;

            if (JSONArr != null) {
                messageArray = new ArrayList<>();

                for (int i = 0; i < JSONArr.length(); i++) {
                    ArrayList<Object> message_info = new ArrayList<>();

                    int id = JSONArr.getJSONObject(i).getInt("id");
                    String messageText = JSONArr.getJSONObject(i).getString("messageText");
                    String messageDate = JSONArr.getJSONObject(i).getString("messageDate");
                    boolean isSeen = JSONArr.getJSONObject(i).getBoolean("isSeen");
                    int sender_id = JSONArr.getJSONObject(i).getInt("sender_id");
                    int chat_id = JSONArr.getJSONObject(i).getInt("chat_id");

                    message_info.add(id);
                    message_info.add(messageText);
                    message_info.add(messageDate);
                    message_info.add(isSeen);
                    message_info.add(sender_id);
                    message_info.add(chat_id);

                    messageArray.add(message_info);
                }

            }

            arr.add(status);
            arr.add(message);
            arr.add(messageArray);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    /////////////////////////////////////
    //      MESSAGECONTROLLER ENDS     //
    /////////////////////////////////////
    // ------------------------------------- //
    /////////////////////////////////////
    //        ALGORITHMCONTROLLER        //
    /////////////////////////////////////
    public ArrayList<Object> algo_match_algo() {

        // Bunu çağırınca o user için yeni matchler bulup database e ekliyo. 
        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("algo/match_algo/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    public ArrayList<Object> algo_suggestion_algo() {
        // Bunu çağırınca o user için yeni suggestler bulup database e ekliyo.  
        ArrayList<Object> arr = new ArrayList<>();

        try {
            String a = this.get("algo/suggestion_algo/");
            JSONObject reader = new JSONObject(a);

            String status = reader.getString("status");
            String message = reader.getString("message");

            arr.add(status);
            arr.add(message);
            arr.add(null);

        } catch (JSONException e) {
            String methodName = new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName();

            arr.add("error");
            arr.add(methodName + " failed. JSON error.");
            e.printStackTrace();
            return arr;
        }

        return arr;
    }

    /////////////////////////////////////
    //      ALGORITHMCONTROLLER ENDS     //
    /////////////////////////////////////
}
