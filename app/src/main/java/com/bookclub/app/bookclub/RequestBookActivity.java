package com.bookclub.app.bookclub;

import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.Book;
import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;


public class RequestBookActivity extends AppCompatActivity {


    ListView listView;
    ArrayList<RequestBookListItem> requestBookListItems;
    SearchView searchBar;

    BookClubAPI api;
    ArrayAdapter<RequestBookListItem> adapter;
    AlertDialog alertDialog;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_book);
        api = new BookClubAPI();
        linearLayout = findViewById(R.id.linearLayout);
        alertDialog = new SpotsDialog(this);
        listView = findViewById(R.id.bookList);


        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                alertDialog.show();
                new SearchBookTask(searchBar.getQuery().toString()).execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RequestBookActivity.this, WishListActivity.class);
        startActivity(intent);
    }

    public static class ViewHolder{
        TextView author, bookTitle;
        ImageButton bookImage, wishButton;
        CardView cardView;
    }

    public class RequestBookListAdapter extends ArrayAdapter<RequestBookListItem>{

        Animation scaleUp;


        public RequestBookListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Nullable
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final RequestBookListItem requestBookListItem= requestBookListItems.get(position);
            ViewHolder viewHolder;

            final View result;
            if (convertView == null){
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.request_book_list_item, parent, false);
                viewHolder.cardView = convertView.findViewById(R.id.pad);
                viewHolder.author = convertView.findViewById(R.id.authorName);
                viewHolder.bookTitle = convertView.findViewById(R.id.bookTitle);
                viewHolder.bookImage = convertView.findViewById(R.id.bookImage);
                viewHolder.wishButton = convertView.findViewById(R.id.wishButton);
                result = convertView;
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
                result = convertView;
            }

            Picasso.get()
                    .load(requestBookListItem.getBook().getBookPhotoUrl())
                    .resize(300, 400)
                    .error(R.drawable.dead)
                    .placeholder(R.drawable.ic_get_app_black_24dp)
                    .into(viewHolder.bookImage);
            System.out.println(requestBookListItem.getBook().getBookPhotoUrl());

            viewHolder.author.setText(requestBookListItem.getBook().getAuthorName());
            viewHolder.bookTitle.setText(requestBookListItem.getBook().getTitle());


            viewHolder.wishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO wish operation
                    int bookId = requestBookListItem.getBook().getId();
                    requestBookListItems.remove(position);
                    adapter.notifyDataSetChanged();
                    new WishBookTask(bookId).execute();
                }
            });


            return convertView;

        }


        @Override
        public int getCount() {
            return requestBookListItems.size();
        }
    }

    public class RequestBookListItem{

        Book book;

        public RequestBookListItem(Book book) {
            this.book = book;
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }
    }

    public class SearchBookTask extends AsyncTask<Void, Void, Void>{

        String query;
        boolean state;
        public SearchBookTask(String query) {
            this.query = query;
            state = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!state){
                Snackbar.make(linearLayout, "Could not find any book with the  indicated piece in the title", Snackbar.LENGTH_SHORT).show();
            }
            else {
                adapter = new RequestBookListAdapter(RequestBookActivity.this, R.layout.request_book_list_item);
                listView.setAdapter(adapter);
            }

            alertDialog.dismiss();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            requestBookListItems = new ArrayList<>();
            ArrayList<Object> arr = api.searchBook(query);
            String status = (String) arr.get(0);
            ArrayList<Object> array = (ArrayList<Object>) arr.get(2);
            if (array == null || array.size() == 0){
                state = false;
                return null;
            }
            for (int i = 0; i < array.size(); i++){
                RequestBookListItem requestBookListItem = new RequestBookListItem((Book) array.get(i));
                System.out.println((Book)array.get(i));
                requestBookListItems.add(requestBookListItem);
            }

            return null;
        }




    }

    public class WishBookTask extends AsyncTask<Void, Void, String>{

        int bookId;

        public WishBookTask(int bookId) {
            this.bookId = bookId;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("this book is already in your tradelist")){
                Snackbar.make(linearLayout, "You cannot add a book to your wishlist that is already in your tradelist.", Snackbar.LENGTH_SHORT).show();
            }
            else if (s.equals("this book is already in your wishlist")){
                Snackbar.make(linearLayout, "You cannot add a book to your wishlist that is already in your wishlist.", Snackbar.LENGTH_SHORT).show();
            }
            else Snackbar.make(linearLayout, "The book was successfully added to your wishlist", Snackbar.LENGTH_SHORT).show();

           // alertDialog.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {

            return (String) api.wishlist_add(bookId).get(1);
        }
    }


    /*public class WishBookTask extends AsyncTask<Void, Void, Void>{

        int bookId;

        public WishBookTask(int bookId) {
            this.bookId = bookId;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println(api.wishlist_add(bookId));
            return null;
        }
    }
*/

}
