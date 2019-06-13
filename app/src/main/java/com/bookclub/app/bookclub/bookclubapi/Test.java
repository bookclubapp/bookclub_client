/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bookclub.app.bookclub.bookclubapi;

import java.util.Date;

/**
 *
 * @author mosma
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        BookClubAPI api = new BookClubAPI();

        // TODO code application logic here
        //        String responseString = run("https://yakamos.com/yakamos/api/lectures");
//        JSONObject reader = new JSONObject(responseString);
//        JSONArray sys  = reader.getJSONArray("lectures");
//        System.out.println(sys.getJSONObject(0).getString("code"));
//        JSONObject reader = new JSONObject(this.post("forgotPassword/", "{\"username\": \"mihhiz\",\"mail\": \"bdis@baris.com\"}"));
//        System.out.println(reader.toString());
//        JSONObject reader = new JSONObject(this.post("login/", "{\"username\": \"mihhiz\",\"password\": \"eTjp256t\"}"));
//        System.out.println(reader.toString());
//        System.out.println(forgotPassword("mert", "mert@mail.com").toString());
//        System.out.println(api.seeOtherUserProfile("mert").toString());
        System.out.println(api.login("rengul41", "jrbcou").toString());
//        System.out.println(api.signup("mert33", "123", "mert@mail.com", "Mert", "Dy", "Turkey", "555", new Date()).toString());
//        System.out.println(api.getSession().toString());
//        System.out.println(api.signout().toString());
//        System.out.println(api.suggestionListIndex().toString());
//        System.out.println(api.getSession().toString());
//        System.out.println(api.searchIndex("Deadly").toString());
//        System.out.println(api.accountSettings_index().toString());
//        System.out.println(api.accountSettings_reset().toString());
//        System.out.println(api.accountSettings_changeAvailability().toString());
//        System.out.println(api.accountSettings_changeMessagable().toString());
//        System.out.println(api.accountSettings_lastSeen().toString());
//        System.out.println(api.accountSettings_changePicture("mert.jpg").toString());
//        System.out.println(api.accountSettings_changePhoneNumber("555 555 55 55").toString());
//        System.out.println(api.accountSettings_changeMail("mert@dy.com").toString());
//        System.out.println(api.accountSettings_changeLocation(10, 10, "Mars").toString());
//        System.out.println(api.accountSettings_onlineState().toString());
//        System.out.println(api.accountSettings_changePassword("1234").toString());
        System.out.println(api.suggestionListIndex().toString());
        System.out.println(api.matchListIndex().toString());
        System.out.println(api.chat_index().toString());

//System.out.println(api.getURL("https://stars.bilkent.edu.tr/bildorm/index", "PHPSESSID=iu9pskvri1qi6pc1hhlr78ejo5; HttpOnly;"));
    }

}
