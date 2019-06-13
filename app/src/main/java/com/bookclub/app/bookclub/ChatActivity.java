package com.bookclub.app.bookclub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.Message;
import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.bookclub.app.bookclub.bookclubapi.User;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;


public class ChatActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private MessageListAdapter messageListAdapter;
    private ArrayList<Message> messages;
    private int chat;
    private Button sendButton, acceptButton, rejectButton;
    private EditText editText;
    TextView chattedNameText;
    TimerTask doAsynchronousTask;
    Handler handler;
    Runnable handlerTask;
    Dialog dialog;
    AlertDialog alertDialog;
    BookClubAPI api;
    int chatID, chattedUserID;
    String chattedUserName;
    User chattedUser, currentUser;
    boolean receivingMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = new BookClubAPI();
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.reyclerview_message_list);
        chattedUser = null;
        chatID = getIntent().getIntExtra("ChatID", 0);
        chattedUserID = getIntent().getIntExtra("ChattedUserID", 0);
        chattedUserName = getIntent().getStringExtra("ChattedUserName");
        System.out.println("ChatID: " + chatID + " ChattedUserID : "+ chattedUserID + " ChattedUserName: " + chattedUserName);
        chattedNameText = findViewById(R.id.userNameText);
        chattedNameText.setText(chattedUserName);
        receivingMessage = false;
        alertDialog = new SpotsDialog(this);
        alertDialog.show();

        new ChatCreatorTask().execute();

        editText = findViewById(R.id.edittext_chatbox);
        sendButton = findViewById(R.id.button_chatbox_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText() != null && !editText.getText().equals("")){
                    Log.d("send pressed", editText.getText().toString());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    Message message = new Message(editText.getText().toString(), currentUser.getName(), currentUser.getId(),  simpleDateFormat.format(date), false);
                    //messages.add(message);
                    //messageListAdapter.notifyDataSetChanged();
                    new SendMessageTask(editText.getText().toString()).execute();

                    editText.setText("");
                    recyclerView.scrollToPosition(messages.size()-1);

                }
            }
        });



        acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyStandardDialog(ChatActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        .setTopColorRes(R.color.buttonColor)
                        .setButtonsColorRes(R.color.colorAccent)
                        .setIcon(R.drawable.bookclub_icon)
                        .setTitle("Confirm?")
                        .setMessage("Are you sure you want to confirm this trade?")
                        .setPositiveButton("Confirm", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.show();
                                new ConfirmTrade(true).execute();
                                Intent intent = new Intent(ChatActivity.this, ChatListActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        });

        rejectButton = findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setMessage("You are about to reject this trade. Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                //rejection process
                                alertDialog.show();
                                new ConfirmTrade(false).execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });

        handler = new Handler();
        handlerTask = new Runnable() {
            @Override
            public void run() {
                if (!receivingMessage){
                    new ReceiveNewMessages().execute();
                }
                handler.postDelayed(this, 5000);
            }
        };
        handlerTask.run();
    }

    public class ConfirmTrade extends AsyncTask<Void, Void, Void>{
        boolean isConfirmed;

        public ConfirmTrade(boolean isConfirmed){
            this.isConfirmed = isConfirmed;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            System.out.println(api.confirmTrade(chatID, isConfirmed));

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // doAsynchronousTask.cancel();
        handler.removeCallbacks(handlerTask);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, ChatListActivity.class);
        startActivity(intent);
    }

    public class ReceiveNewMessages extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            receivingMessage = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //messageListAdapter.notifyDataSetChanged();
            messageListAdapter = new MessageListAdapter(ChatActivity.this, messages);
            recyclerView.setAdapter(messageListAdapter);
            recyclerView.scrollToPosition(messages.size()-1);
            receivingMessage = false;
        }



        @Override
        protected Void doInBackground(Void... voids) {

            System.out.println("ReceiveNewMessages doinbackground");

            messages = new ArrayList<>();

            ArrayList<Object> chats = (ArrayList<Object>) api.chat_messagelist(chatID).get(2);
            System.out.println("-----------Chats: " + chats);
            currentUser = (User)api.getSession().get(2);
            if (chats != null){
                System.out.println("Current User: " + currentUser);
            }
            else chats = new ArrayList<>();

            for (int i = 0; i < chats.size(); i++){
                ArrayList<Object> chat = (ArrayList<Object>) chats.get(i);
                if ((int)chat.get(3) == currentUser.getId()){

                }
                Message message = new Message(
                        (String)chat.get(2),
                        (int)chat.get(3) == chattedUserID ? chattedUserName : currentUser.getName(),
                        (int)chat.get(3),
                        (String)chat.get(0),
                        (boolean)chat.get(1)
                );

                messages.add(message);
            }

            return null;
        }
    }

    public class SendMessageTask extends AsyncTask<Void, Void, Void>{

        String q;

        public SendMessageTask(String q){
            this.q = q;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //update the chat list
            //messageListAdapter.notifyDataSetChanged();
            messageListAdapter = new MessageListAdapter(ChatActivity.this, messages);
            recyclerView.setAdapter(messageListAdapter);
            recyclerView.scrollToPosition(messages.size()-1);

        }


        @Override
        protected Void doInBackground(Void... voids) {

            Date date = new Date();
            api.chat_send(chatID, q);

            messages = new ArrayList<>();

            ArrayList<Object> chats = (ArrayList<Object>) api.chat_messagelist(chatID).get(2);
            System.out.println(chats);
            currentUser = (User)api.getSession().get(2);
            if (chats != null){
                System.out.println("Current User: " + currentUser);
            }
            else chats = new ArrayList<>();

            for (int i = 0; i < chats.size(); i++){
                ArrayList<Object> chat = (ArrayList<Object>) chats.get(i);
                if ((int)chat.get(3) == currentUser.getId()){

                }
                Message message = new Message(
                        (String)chat.get(2),
                        (int)chat.get(3) == chattedUserID ? chattedUserName : currentUser.getName(),
                        (int)chat.get(3),
                        (String)chat.get(0),
                        (boolean)chat.get(1)
                );

                messages.add(message);
            }

            return null;
        }
    }


    public class MessageListAdapter extends RecyclerView.Adapter{
        private static final int VIEW_TYPE_MESSAGE_SENT = 1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

        Context context;
        ArrayList<Message> messages;
        public MessageListAdapter(Context context, ArrayList<Message> messages){

            this.context = context;
            this.messages = messages;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Message message = messages.get(i);
            switch (viewHolder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) viewHolder).bind(message);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) viewHolder).bind(message);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            Message message = messages.get(position);
            if (message.getUserID()== currentUser.getId()) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

    }

    public class ChatCreatorTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            messageListAdapter = new MessageListAdapter(ChatActivity.this, messages);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(messageListAdapter);
            chattedNameText.setText(chattedUserName);
            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            messages = new ArrayList<>();

            ArrayList<Object> chats = (ArrayList<Object>) api.chat_messagelist(chatID).get(2);
            System.out.println(chats);
            if (chats != null){
                currentUser = (User)api.getSession().get(2);
                System.out.println("Current User: " + currentUser);
            }
            else return null;

            for (int i = 0; i < chats.size(); i++){
                ArrayList<Object> chat = (ArrayList<Object>) chats.get(i);
                if ((int)chat.get(3) == currentUser.getId()){

                }
                Message message = new Message(
                        (String)chat.get(2),
                        (int)chat.get(3) == chattedUserID ? chattedUserName : currentUser.getName(),
                        (int)chat.get(3),
                        (String)chat.get(0),
                        (boolean)chat.get(1)
                );

                messages.add(message);
            }


            return null;
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            Log.d("pizza", "receviedMessageHolder");
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            Log.d("pizza", "bind received");
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.

          //  Date date = message.getDate();
           // DateFormat dateFormat = new SimpleDateFormat("hh:mm");
            //String strDate = dateFormat.format(date);

            //timeText.setText(strDate);
            timeText.setText(message.getDate());
            nameText.setText(message.getName());


        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            Log.d("pizza", "SentMessageHolder");
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            Log.d("pizza", "bind sent");
            messageText.setText(message.getText());

            timeText.setText(message.getDate());


        }
    }


}
