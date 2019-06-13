/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bookclub.app.bookclub.bookclubapi;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mosma
 */
public class Book implements Serializable{

    private int id;
    private String title,
            authorName,
            isbn,
            publisher,
            edition,
            bookPhotoUrl;
    private double originalPrice;
    private Date publishDate;

    public Book() {

    }

    public Book(
            int id,
            String title,
            String authorName,
            String isbn,
            String publisher,
            String publishDate,
            String bookPhotoUrl
    ) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.isbn = isbn;
        this.publisher = publisher;
        this.setPublishDate(publishDate);
        this.bookPhotoUrl = bookPhotoUrl;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEdition() {
        return this.edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getBookPhotoUrl() {
        return this.bookPhotoUrl;
    }

    public void setBookPhotoUrl(String bookPhotoUrl) {
        this.bookPhotoUrl = bookPhotoUrl;
    }

    public Double getOriginalPrice() {
        return this.originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Date getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(String publishDate) {
        // parameter should be in "yyyy-MM-dd" format

        try {
            this.publishDate = new SimpleDateFormat("yyyy").parse(publishDate);
        } catch (ParseException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String toString() {
        return "\nid: " + this.id
                + "\ntitle: " + this.title
                + "\nauthorName: " + this.authorName
                + "\nisbn: " + this.isbn
                + "\npublisher: " + this.publisher
                + "\npublishDate: " + this.publishDate
                + "\nbookPhoto: " + this.bookPhotoUrl;
    }

}
