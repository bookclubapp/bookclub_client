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

public class TradeListActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    TradeListAdapter adapter;
    ArrayList<TradeListContent> tradeListContents;
    ListView listView;
    Button tradeBookButton;
    BookClubAPI api;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_list);
        api = new BookClubAPI();
        alertDialog = new SpotsDialog(this);
        alertDialog.show();
        linearLayout = findViewById(R.id.linearLayout);
        listView = findViewById(R.id.tradeList);
        new GetTradeListTask().execute();

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener = new SwipeToDismissTouchListener<>(
                new ListViewAdapter(listView),
                new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListViewAdapter view, int position) {
                        int tradelistId = tradeListContents.get(position).getTradelistId();
                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
                        new TradelistDeleteTask(tradelistId).execute();
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
                    Toast.makeText(TradeListActivity.this, "Position " + position, LENGTH_SHORT).show();
                }
            }
        });

        tradeBookButton = findViewById(R.id.tradeBookButton);
        tradeBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TradeListActivity.this, TradeBookActivity.class);
                startActivity(intent);
            }
        });

    }

    private static class ViewHolder{
        TextView authorText, bookText;
        ImageButton bookImage;
        CardView cardView;
    }

    public class TradeListAdapter extends ArrayAdapter<TradeListContent> implements View.OnClickListener {

        ArrayList<TradeListContent> dataSet;
        Context context;

        public TradeListAdapter(ArrayList<TradeListContent> data, Context context) {
            super(context, R.layout.trade_list_item, data);
            this.dataSet = data;
            this.context=context;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final TradeListContent tradeListContent = tradeListContents.get(position);
            ViewHolder viewHolder;

            final View result;

            if (convertView == null){
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.trade_list_item, parent, false);
                viewHolder.cardView = convertView.findViewById(R.id.pad);
                viewHolder.authorText = convertView.findViewById(R.id.authorTextView);
                viewHolder.bookText = convertView.findViewById(R.id.bookTitleTextView);
                viewHolder.bookImage = convertView.findViewById(R.id.bookImageView);

                result = convertView;
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
                result = convertView;
            }

            Picasso.get()
                    .load(tradeListContent.getBook().getBookPhotoUrl())
                    .resize(300, 400)
                    .error(R.drawable.dead)
                    .placeholder(R.drawable.ic_get_app_black_24dp)
                    .into(viewHolder.bookImage);

            viewHolder.authorText.setText(tradeListContent.getBook().getAuthorName());
            viewHolder.bookText.setText(tradeListContent.getBook().getTitle());



            viewHolder.bookImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TradeListActivity.this, BookDetailActivity.class);
                    intent.putExtra("BOOK", tradeListContent.getBook());
                    startActivity(intent);
                }
            });

            return convertView;
        }


        public void remove(int position) {
            tradeListContents.remove(position);
            notifyDataSetChanged();
        }


        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(TradeListActivity.this, PreferencesActivity.class);
        startActivity(i);
    }


    public class TradelistDeleteTask extends AsyncTask<Void, Void, Void>{

        int tradeListId;

        public TradelistDeleteTask(int tradelistId){

            this.tradeListId = tradelistId;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("TradelistDeleteTask: " + tradeListId);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            System.out.println(api.tradelist_delete(tradeListId));

            return null;
        }
    }


    public class GetTradeListTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new TradeListAdapter(tradeListContents, TradeListActivity.this);
            listView.setAdapter(adapter);
            alertDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<Object> arr = api.tradelist_index();
            ArrayList<Object> tradees = (ArrayList<Object>) arr.get(2); //matchArray
            tradeListContents = new ArrayList<>();
            if (tradees != null && tradees.size() > 0){

                System.out.println("423842837487468765----------Tradees: " + tradees + "------------------218378273897934");
                for (int i = 0; i < tradees.size(); i++){
                    ArrayList<Object> trade = (ArrayList<Object>) tradees.get(i); //match
                    System.out.println("Trade: " + trade);
                    ArrayList<Object> tradelistInfo = ((ArrayList)(trade.get(0))); //matchlistinfo
                    Book book = (Book)trade.get(1); //book_info
                    System.out.println("-------------Book: " + book+ "-----------------------");
                    TradeListContent w = new TradeListContent(
                            book,
                            (int)tradelistInfo.get(0));
                    tradeListContents.add(w);
                }
                for (TradeListContent tradeListContent: tradeListContents)
                    System.out.println("Books : " + tradeListContent.getBook().getTitle());
            }

            return null;
        }
    }


    class TradeListContent{

        Book book;
        private int  tradelistId;


        public TradeListContent(Book book, int tradeListId) {


            this.book = book;
            this.tradelistId = tradeListId;
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

        public int getTradelistId() {
            return tradelistId;
        }

        public void setTradelistId(int tradelistId) {
            this.tradelistId = tradelistId;
        }

  }


}
