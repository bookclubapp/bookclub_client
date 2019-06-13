package com.bookclub.app.bookclub;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.Book;
import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.bookclub.app.bookclub.bookclubapi.User;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class HistoryActivity extends AppCompatActivity {

    ArrayList<HistoryListContent> historyListContents;
    HistoryListAdapter adapter;
    ListView listView;
    BookClubAPI api;
    RadioGroup radioGroup;
    RadioButton matchRadioButton, suggestionRadioButton;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        api = new BookClubAPI();
        alertDialog = new SpotsDialog(this);
        alertDialog.show();
        listView = findViewById(R.id.listView);
        new GetMatchHistoryTask().execute();

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.matchRadio){
                    alertDialog.show();
                    new GetMatchHistoryTask().execute();
                }
                else if (checkedId == R.id.suggestionRadio){
                    alertDialog.show();
                    new GetSuggestionHistoryTask().execute();
                }

            }
        });
        matchRadioButton = findViewById(R.id.matchRadio);
        suggestionRadioButton = findViewById(R.id.suggestionRadio);


    }

    public class GetMatchHistoryTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new HistoryListAdapter(HistoryActivity.this);
            listView.setAdapter(adapter);
            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //match history
            //suggestion history
            historyListContents = new ArrayList<>();
            ArrayList<Object> a = api.history_index_match();
            System.out.println(a);
            ArrayList<Object> arr = (ArrayList<Object>) a.get(2);
            if (arr == null){
                return null;
            }
            User currentUser = (User)api.getSession().get(2);

            for (int i = 0; i < arr.size(); i++){
                ArrayList<Object> histories = (ArrayList<Object>) arr.get(i);
                ArrayList<Object> match_history_info = (ArrayList<Object>) histories.get(0);
                ArrayList<Object> matchInfo = (ArrayList<Object>)histories.get(1);
                Book givingBook = (Book)histories.get(2);
                Book wantedBook = (Book)histories.get(3);

                HistoryListContent historyListContent = new HistoryListContent(
                        currentUser,
                        (User)api.getUserProfileID((int)matchInfo.get(2)).get(2),
                        givingBook,
                        wantedBook,
                        ((String)matchInfo.get(6)).equals("confirmed"),
                        (String)matchInfo.get(7),
                        true

                );
                historyListContents.add(historyListContent);

            }
            return null;
        }
    }


    public class GetSuggestionHistoryTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new HistoryListAdapter(HistoryActivity.this);
            listView.setAdapter(adapter);
            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            historyListContents = new ArrayList<>();
            ArrayList<Object> arr = (ArrayList<Object>) api.history_index_suggestion().get(2);
            if (arr == null){
                return null;
            }
            User currentUser = (User)api.getSession().get(2);

            for (int i = 0; i < arr.size(); i++){
                ArrayList<Object> histories = (ArrayList<Object>) arr.get(i);
                ArrayList<Object> match_history_info = (ArrayList<Object>) histories.get(0);
                ArrayList<Object> matchInfo = (ArrayList<Object>)histories.get(1);
                Book givingBook = (Book)histories.get(2);
                Book wantedBook = (Book)histories.get(3);

                HistoryListContent historyListContent = new HistoryListContent(
                        currentUser,
                        (User)api.getUserProfileID((int)matchInfo.get(2)).get(2),
                        givingBook,
                        wantedBook,
                        ((String)matchInfo.get(6)).equals("confirmed"),
                        (String)matchInfo.get(7),
                        false

                );
                historyListContents.add(historyListContent);

            }

            return null;
        }
    }


    private static class ViewHolder {
        TextView username, author, bookTitle, date;
        TextView username2, author2, book2Title;
        ImageView state;
        ImageButton bookImage, book2Image;
    }


    public class HistoryListAdapter extends ArrayAdapter<HistoryListContent> {

        Context context;

        public HistoryListAdapter(Context context) {
            super(context, R.layout.history_list_item, historyListContents);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final HistoryListContent historyListContent= getItem(position);
            final View result;
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.history_list_item, parent, false);

                //Text Views
                viewHolder.username = convertView.findViewById(R.id.userNameText);
                viewHolder.bookTitle = convertView.findViewById(R.id.bookTitle);
                viewHolder.author = convertView.findViewById(R.id.authorName);
                viewHolder.username2 = convertView.findViewById(R.id.userName2Text);
                viewHolder.book2Title = convertView.findViewById(R.id.book2Title);
                viewHolder.author2 = convertView.findViewById(R.id.author2Name);
                viewHolder.date = convertView.findViewById(R.id.dateText);

                //Image View
                viewHolder.state = convertView.findViewById(R.id.stateImage);

                //Image Button
                viewHolder.bookImage = convertView.findViewById(R.id.bookImage);
                viewHolder.book2Image = convertView.findViewById(R.id.book2Image);
                result = convertView;

                viewHolder.username.setText(historyListContent.getCurrentUser().getUsername());
                viewHolder.bookTitle.setText(historyListContent.getGivenBook().getTitle());
                viewHolder.author.setText(historyListContent.getGivenBook().getAuthorName());
                //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                viewHolder.date.setText(historyListContent.getDate());

                viewHolder.username2.setText(historyListContent.getMatchedUser().getUsername());
                viewHolder.book2Title.setText(historyListContent.getWantedBook().getTitle());
                viewHolder.author2.setText(historyListContent.getWantedBook().getAuthorName());


                Picasso.get()
                        .load(historyListContent.getGivenBook().getBookPhotoUrl())
                        .resize(300, 400)
                        .error(R.drawable.error)
                        .placeholder(R.drawable.loading)
                        .into(viewHolder.bookImage);

                Picasso.get()
                        .load(historyListContent.getWantedBook().getBookPhotoUrl())
                        .resize(300, 400)
                        .error(R.drawable.error)
                        .placeholder(R.drawable.loading)
                        .into(viewHolder.book2Image);

                //viewHolder.bookImage.setImageBitmap(Bitmap.createScaledBitmap(historyListContent.getBookImage(), 300, 300, false));

                convertView.setTag(viewHolder);
                if (historyListContent.isState()){
                    viewHolder.state.setImageResource(R.drawable.thumb_up_green);
                }
                else{
                    viewHolder.state.setImageResource(R.drawable.thumb_down);
                }
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }


            return convertView;
        }

        @Override
        public int getCount() {
            return historyListContents.size();
        }
    }



    public class HistoryListContent {

        User currentUser, matchedUser;
        Book givenBook, wantedBook;
        boolean state, isMatch;
        String date;

        public HistoryListContent(User currentUser, User matchedUser, Book givenBook, Book wantedBook, boolean state, String date, boolean isMatch) {
            this.currentUser = currentUser;
            this.matchedUser = matchedUser;
            this.givenBook = givenBook;
            this.wantedBook = wantedBook;
            this.state = state;
            this.date = date;
            this.isMatch = isMatch;
        }

        public User getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(User currentUser) {
            this.currentUser = currentUser;
        }

        public User getMatchedUser() {
            return matchedUser;
        }

        public void setMatchedUser(User matchedUser) {
            this.matchedUser = matchedUser;
        }

        public Book getGivenBook() {
            return givenBook;
        }

        public void setGivenBook(Book givenBook) {
            this.givenBook = givenBook;
        }

        public Book getWantedBook() {
            return wantedBook;
        }

        public void setWantedBook(Book wantedBook) {
            this.wantedBook = wantedBook;
        }

        public boolean isState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }


}