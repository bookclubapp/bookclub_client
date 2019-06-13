package com.bookclub.app.bookclub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.bookclub.app.bookclub.bookclubapi.User;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class ChatListActivity extends AppCompatActivity {

    ArrayList<ChatListContent> chatListContents;
    ListView listView;
    ArrayAdapter<ChatListContent> adapter;
    AlertDialog alertDialog;
    BookClubAPI api;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        api = new BookClubAPI();
        alertDialog = new SpotsDialog(this);
        alertDialog.show();
        new CreateChatListTask().execute();

    }

    private Dialog makeDialog(String username, int userId, int matchId, int suggestionId){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.rating_popup);
        TextView dialogUserName = dialog.findViewById(R.id.username);
        dialogUserName.setText(username);
        RatingBar ratingBar  = dialog.findViewById(R.id.ratingBar);
        ImageButton dialogAccept = dialog.findViewById(R.id.confirmButton);
        dialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
                new RateTask(userId, ratingBar.getNumStars(), matchId, suggestionId).execute();
                dialog.dismiss();
            }
        });
        ImageButton dialogCancel = dialog.findViewById(R.id.cancelButton);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatListActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public class RateTask extends AsyncTask<Void, Void, Void>{

        int userId, rating, matchId, suggestionId;

        public RateTask(int userId, int rating, int matchId, int suggestionId ){
            this.userId = userId;
            this.rating = rating;
            this.matchId = matchId;
            this.suggestionId = suggestionId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            System.out.println("Rate Task: " + api.rate(userId, rating, matchId, suggestionId));

            return null;
        }
    }

    public class CreateChatListTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            listView = findViewById(R.id.listView);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Log.d("chatListActivity", "adasdad");
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("ChatID", chatListContents.get(position).getChatId());
                intent.putExtra("ChattedUserID", chatListContents.get(position).getChattedUser().getId());
                intent.putExtra("ChattedUserName", chatListContents.get(position).getChattedUser().getName());
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%ChatID: " + chatListContents.get(position).getChatId() +
                        " ChattedUserID" +  chatListContents.get(position).getChattedUser().getId()
                        + " ChattedUserName" + chatListContents.get(position).getChattedUser().getName());
                startActivity(intent);
            });

            adapter = new ChatListAdapter(ChatListActivity.this);
            listView.setAdapter(adapter);

            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            chatListContents = new ArrayList<>();

            User currentuser = (User)api.getSession().get(2);
            int userId = currentuser.getId();
            System.out.println("Current User Id : " + userId);
            ArrayList<Object> chatlist = (ArrayList<Object>) api.chat_index().get(2);
            if (chatlist == null) return null;

            int chattedUserId = 0, chattedUserIndex = 0, currentUserIndex = 0;
            for (int i = 0; i < chatlist.size(); i++ ){
                ArrayList<Object> chat = (ArrayList<Object>) chatlist.get(i);
                if ((int)chat.get(1) == userId){
                    chattedUserId = (int) chat.get(2);
                    chattedUserIndex = 2;
                    currentUserIndex = 1;
                }
                else if ((int)chat.get(2) == userId){
                    chattedUserId = (int) chat.get(1);
                    chattedUserIndex = 1;
                    currentUserIndex = 2;
                }

                User chattedUser = (User)api.getUserProfileID(chattedUserId).get(2);
                System.out.println("Match id: " + (int)chat.get(5)+ " Suggestion id: " + (int)chat.get(6));
                ChatListContent chatListContent = new ChatListContent(
                        (int) chat.get(0),
                        (int)chat.get(5),
                        (int)chat.get(6),
                        chattedUser,
                        !chat.get(currentUserIndex + 2).equals("not_confirmed"),
                        !chat.get(chattedUserIndex + 2).equals("not_confirmed")
                        );

                chatListContents.add(chatListContent);

            }

            return null;
        }
    }



    private static class ViewHolder{
        TextView userName, name;
        ImageButton profilePicture, rateButton;
        CardView cardView;
    }

    public class ChatListAdapter extends ArrayAdapter<ChatListContent>{

        Context context;

        public ChatListAdapter(@NonNull Context context) {
            super(context, R.layout.chat_list_item);

        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ChatListContent chatListContent = chatListContents.get(position);

            View result;
            ViewHolder vh;

            if (convertView == null){
                vh = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.chat_list_item, parent, false);

                vh.userName = convertView.findViewById(R.id.userNameText);
                vh.name = convertView.findViewById(R.id.nameText);
                vh.profilePicture = convertView.findViewById(R.id.bookImage);
                vh.cardView = convertView.findViewById(R.id.pad);
                vh.rateButton = convertView.findViewById(R.id.rateButton);

                result = convertView;
                convertView.setTag(vh);

            }
            else{
                vh = (ViewHolder)convertView.getTag();
                result = convertView;
            }

            vh.name.setText(chatListContent.getChattedUser().getName());
            vh.userName.setText(chatListContent.getChattedUser().getUsername());
            vh.profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChatListActivity.this, ProfileActivity.class);
                    intent.putExtra("UserID", chatListContent.getChattedUser().getId());
                    startActivity(intent);
                }
            });

            vh.rateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Rate button");

                    Dialog dialog = new Dialog(ChatListActivity.this);
                    dialog.setTitle("Rate");
                    dialog.setContentView(R.layout.rating_popup);

                    TextView dialogUserName = dialog.findViewById(R.id.username);
                    dialogUserName.setText(chatListContent.getChattedUser().getUsername());
                    RatingBar ratingBar  = dialog.findViewById(R.id.ratingBar);
                    ImageButton dialogAccept = dialog.findViewById(R.id.confirmButton);
                    dialogAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.show();
                            new RateTask(chatListContent.getChattedUser().getId(),
                                    ratingBar.getNumStars(),
                                    chatListContent.getMatchID(),
                                    chatListContent.getSuggestionID()).execute();
                            dialog.dismiss();
                        }
                    });
                    ImageButton dialogCancel = dialog.findViewById(R.id.cancelButton);
                    dialogCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            if (!chatListContent.isChattedUserState() || !chatListContent.isCurrentUserState()){
                vh.rateButton.setVisibility(View.INVISIBLE);

            }

            vh.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("chatListActivity", "adasdad");
                    Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                    intent.putExtra("ChatID", chatListContent.getChatId());
                    intent.putExtra("ChattedUserID", chatListContent.getChattedUser().getId());
                    intent.putExtra("ChattedUserName", chatListContent.getChattedUser().getName());
                    startActivity(intent);
                }
            });

            return result;
        }

        @Override
        public int getCount() {
            return chatListContents.size();
        }
    }





    public class ChatListContent{

        int chatId;
        int matchID, suggestionID;
        User chattedUser;
        boolean currentUserState;
        boolean chattedUserState;

        public ChatListContent(int chatId, int matchID, int suggestionID, User chattedUser, boolean currentUserState, boolean chattedUserState) {
            this.chatId = chatId;
            this.matchID = matchID;
            this.chattedUser = chattedUser;
            this.currentUserState = currentUserState;
            this.chattedUserState = chattedUserState;
            this.suggestionID = suggestionID;
        }

        public int getChatId() {
            return chatId;
        }

        public void setChatId(int chatId) {
            this.chatId = chatId;
        }

        public int getMatchID() {
            return matchID;
        }

        public void setMatchID(int matchID) {
            this.matchID = matchID;
        }

        public User getChattedUser() {
            return chattedUser;
        }

        public void setChattedUser(User chattedUser) {
            this.chattedUser = chattedUser;
        }

        public boolean isCurrentUserState() {
            return currentUserState;
        }

        public void setCurrentUserState(boolean currentUserState) {
            this.currentUserState = currentUserState;
        }

        public int getSuggestionID() {
            return suggestionID;
        }

        public void setSuggestionID(int suggestionID) {
            this.suggestionID = suggestionID;
        }

        public boolean isChattedUserState() {
            return chattedUserState;
        }

        public void setChattedUserState(boolean chattedUserState) {
            this.chattedUserState = chattedUserState;
        }


    }



}
