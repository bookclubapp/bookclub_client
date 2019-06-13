package com.bookclub.app.bookclub;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.Book;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class BookDetailActivity extends AppCompatActivity {

    private Book book;
    private TextView bookTitleText, authorNameText, publicationDateText, publisherText, isbnText;
    private ImageView bookImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Intent intent = getIntent();
        book = (Book)intent.getSerializableExtra("BOOK");

        bookTitleText = findViewById(R.id.titleText);
        authorNameText = findViewById(R.id.authorNametext);
        publicationDateText = findViewById(R.id.publicationDateText);
        publisherText = findViewById(R.id.publisherText);
        bookImage = findViewById(R.id.bookImage);

        bookTitleText.setText(book.getTitle());
        authorNameText.setText(book.getAuthorName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");
        publicationDateText.setText(simpleDateFormat.format(book.getPublishDate()));
        publisherText.setText(book.getPublisher());

        isbnText = findViewById(R.id.isbnNo);
        isbnText.setText(book.getIsbn());

        Picasso.get()
                .load(book.getBookPhotoUrl())
                .resize(600, 800)
                .error(R.drawable.book)
                .into(bookImage);

    }
}
