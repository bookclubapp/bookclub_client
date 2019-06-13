package com.bookclub.app.bookclub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bookclub.app.bookclub.bookclubapi.Book;
import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.bookclub.app.bookclub.bookclubapi.User;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import dmax.dialog.SpotsDialog;

import static android.widget.Toast.LENGTH_SHORT;

public class WishListActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    WishListAdapter adapter;
    ArrayList<WishListContent> wishListContents;
    ListView listView;
    Button wishBookButton;
    BookClubAPI api;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        api = new BookClubAPI();
        alertDialog = new SpotsDialog(this);
        alertDialog.show();
        linearLayout = findViewById(R.id.linearLayout);
        listView = findViewById(R.id.wishList);
        new GetWishListTask().execute();

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener = new SwipeToDismissTouchListener<>(
                new ListViewAdapter(listView),
                new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListViewAdapter view, int position) {
                        int wishlistId = wishListContents.get(position).getWishlistId();
                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
                        new WishlistDeleteTask(wishlistId).execute();
                    }
                });

        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    Toast.makeText(WishListActivity.this, "Position " + position, LENGTH_SHORT).show();
                }
            }
        });

        wishBookButton = findViewById(R.id.wishBookButton);
        wishBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishListActivity.this, RequestBookActivity.class);
                startActivity(intent);
            }
        });

    }

    private static class ViewHolder{
        TextView authorText, bookText;
        ImageButton bookImage, up, down;
        CardView cardView;
    }

    public class WishListAdapter extends ArrayAdapter<WishListContent> implements View.OnClickListener {

        ArrayList<WishListContent> dataSet;
        Context context;

        public WishListAdapter(ArrayList<WishListContent> data, Context context) {
            super(context, R.layout.wish_list_item, data);
            this.dataSet = data;
            this.context=context;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final WishListContent wishListContent = wishListContents.get(position);
            ViewHolder viewHolder;

            final View result;

            if (convertView == null){
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.wish_list_item, parent, false);
                viewHolder.cardView = convertView.findViewById(R.id.pad);
                viewHolder.authorText = convertView.findViewById(R.id.authorTextView);
                viewHolder.bookText = convertView.findViewById(R.id.bookTitleTextView);
                viewHolder.bookImage = convertView.findViewById(R.id.bookImageView);
                viewHolder.up = convertView.findViewById(R.id.upArrow);
                viewHolder.down = convertView.findViewById(R.id.downArrow);
                result = convertView;
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
                result = convertView;
            }

            Picasso.get()
                    .load(wishListContent.getBook().getBookPhotoUrl())
                    .resize(300, 400)
                    .error(R.drawable.dead)
                    .placeholder(R.drawable.ic_get_app_black_24dp)
                    .into(viewHolder.bookImage);

            viewHolder.authorText.setText(wishListContent.getBook().getAuthorName());
            viewHolder.bookText.setText(wishListContent.getBook().getTitle());

            if (position == 0){
                viewHolder.up.setVisibility(View.INVISIBLE);
                viewHolder.down.setVisibility(View.VISIBLE);
            }
            else if (position == wishListContents.size()-1){
                viewHolder.up.setVisibility(View.VISIBLE);
                viewHolder.down.setVisibility(View.INVISIBLE);
            }
            else{
                viewHolder.up.setVisibility(View.VISIBLE);
                viewHolder.down.setVisibility(View.VISIBLE);
            }


            viewHolder.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.show();
                    new WishListDragTask(wishListContent.getWishlistId(), true).execute();
                }
            });

            viewHolder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.show();
                    new WishListDragTask(wishListContent.getWishlistId(), false).execute();
                }
            });

            viewHolder.bookImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WishListActivity.this, BookDetailActivity.class);
                    intent.putExtra("BOOK", wishListContent.getBook());
                    startActivity(intent);
                }
            });

            return convertView;
        }


        public void remove(int position) {
            wishListContents.remove(position);
            notifyDataSetChanged();
        }


        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(WishListActivity.this, PreferencesActivity.class);
        startActivity(i);
    }

    public class WishListDragTask extends AsyncTask<Void, Void, Void>{

        boolean isGoingUp;
        int wishListId;

        public WishListDragTask(int wishListId, boolean isGoingUp){
            this.isGoingUp = isGoingUp;
            this.wishListId = wishListId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("Before drag " + isGoingUp + " Wishlist: " + wishListId);
            System.out.println(api.wishlist_drag(wishListId, isGoingUp));

            ArrayList<Object> arr = api.wishlist_index();
            ArrayList<Object> wishes = (ArrayList<Object>) arr.get(2); //matchArray
            wishListContents = new ArrayList<>();
            if (wishes != null && wishes.size() > 0){

         //       System.out.println("423842837487468765----------Wishes: " + wishes + "------------------218378273897934");
                for (int i = 0; i < wishes.size(); i++){
                    ArrayList<Object> wish = (ArrayList<Object>) wishes.get(i); //match
                 //   System.out.println("Wish: " + wish);
                    ArrayList<Object> wishlistInfo = ((ArrayList)(wish.get(0))); //matchlistinfo
                    Book book = (Book)wish.get(1); //book_info
                  //  System.out.println("-------------Book: " + book+ "-----------------------");
                    WishListContent w = new WishListContent(
                            book,
                            (int)wishlistInfo.get(3),
                            (int)wishlistInfo.get(0));
                    wishListContents.add(w);
                }
                Collections.sort(wishListContents);
                for (WishListContent wishListContent: wishListContents)
                    System.out.println("Books : " + wishListContent.getBook().getTitle() + "Order : " + wishListContent.getOrder());
            }

            return null;
        }
    }

    public class WishlistDeleteTask extends AsyncTask<Void, Void, Void>{

        int wishListId;

        public WishlistDeleteTask(int wishlistId){

            this.wishListId = wishlistId;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("WishlistDeleteTask: " + wishListId);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            System.out.println(api.wishlist_delete(wishListId));

            return null;
        }
    }


    public class GetWishListTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new WishListAdapter(wishListContents, WishListActivity.this);
            listView.setAdapter(adapter);
            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<Object> arr = api.wishlist_index();
            ArrayList<Object> wishes = (ArrayList<Object>) arr.get(2); //matchArray
            wishListContents = new ArrayList<>();
            if (wishes != null && wishes.size() > 0){

                System.out.println("423842837487468765----------Wishes: " + wishes + "------------------218378273897934");
                for (int i = 0; i < wishes.size(); i++){
                    ArrayList<Object> wish = (ArrayList<Object>) wishes.get(i); //match
                    System.out.println("Wish: " + wish);
                    ArrayList<Object> wishlistInfo = ((ArrayList)(wish.get(0))); //matchlistinfo
                    Book book = (Book)wish.get(1); //book_info
                    System.out.println("-------------Book: " + book+ "-----------------------");
                    WishListContent w = new WishListContent(
                            book,
                            (int)wishlistInfo.get(3),
                            (int)wishlistInfo.get(0));
                    wishListContents.add(w);
                }
                Collections.sort(wishListContents);
                for (WishListContent wishListContent: wishListContents)
                    System.out.println("Books : " + wishListContent.getBook().getTitle());
            }

            return null;
        }
    }


    class WishListContent implements Comparable<WishListContent>{

        Book book;
        private int  wishlistId;
        private int order;


        public WishListContent(Book book, int order, int wishListId) {

            this.order = order;
            this.book = book;
            this.wishlistId = wishListId;
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

        public int getWishlistId() {
            return wishlistId;
        }

        public void setWishlistId(int wishlistId) {
            this.wishlistId = wishlistId;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        @Override
        public int compareTo(@NonNull WishListContent o) {
            return this.getOrder() - o.getOrder();
        }
    }


}
