package com.bookclub.app.bookclub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.Book;
import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.bookclub.app.bookclub.bookclubapi.User;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GeneralListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GeneralListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<GeneralListContent> generalListContents;
    private ImageButton preferencesButton, chatButton;
    private AlertDialog alertDialog;
    private OnFragmentInteractionListener mListener;
    private SearchView searchBar;
    ArrayAdapter<GeneralListContent> generalListContentArrayAdapter;
    ListView listView;
    private boolean guestSession;
    public GeneralListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralListFragment newInstance(String param1, String param2) {
        GeneralListFragment fragment = new GeneralListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    private void populateGeneralList(){
        generalListContents = new ArrayList<>();

        BookClubAPI api = new BookClubAPI();
        ArrayList<Object> list = api.mainMenuIndex();
       // Log.d("General List", "List : " + list);
        System.out.println(list);
        ArrayList<Object> tradeList = (ArrayList<Object>) list.get(2);
        Collections.shuffle(tradeList);
        for (int i = 0; i < tradeList.size(); i++){
            ArrayList<Object> trade = (ArrayList<Object>)tradeList.get(i);
            int id = (int)trade.get(0);
            User user = (User)trade.get(1);
            Book book = (Book)trade.get(2);


            generalListContents.add(new GeneralListContent(id, user, book));
        }

        for (GeneralListContent g: generalListContents){
           // System.out.println(g);
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //get items from server here
        guestSession = false;
        System.out.println("onCreate General List");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_general_list, container, false);
        //generalListContent = new ArrayList<>();
        alertDialog = new SpotsDialog(getActivity());
        alertDialog.show();
        new GeneralListCreator().execute();

       // populateGeneralList();
        listView = view.findViewById(R.id.generalList);
        generalListContentArrayAdapter = new GeneralListAdapter(generalListContents, getContext());
        try {
            listView.setAdapter(generalListContentArrayAdapter);

        }catch (NullPointerException exception){
            exception.printStackTrace();
            listView = view.findViewById(R.id.generalList);
            generalListContentArrayAdapter = new GeneralListAdapter(generalListContents, getContext());
            listView.setAdapter(generalListContentArrayAdapter);
          /*  Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            Snackbar.make(view.findViewById(R.id.frameLayout), "An error occured", Snackbar.LENGTH_SHORT).show();
            startActivity(intent);

        */}

        preferencesButton = view.findViewById(R.id.preferencesButton);
        preferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PreferencesActivity.class);
                startActivity(intent);
            }
        });

        chatButton = view.findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatListActivity.class);
                startActivity(intent);
            }
        });


        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                alertDialog.show();
                new BookSearchTask(query).execute();


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });




        Log.d("Fragment Created", "GeneralListFragment Created");
        //alertDialog.dismiss();
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // if (alertDialog != null && alertDialog.isShowing())alertDialog.dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static class ViewHolder{
        ImageButton transactionImageButton;
        TextView authorNameTextView;
        TextView bookTitleTextView;
        TextView usernameTextView;
        ImageButton bookImageButton;
        CardView cv;
    }


    public class BookSearchTask extends AsyncTask<Void, Void, Boolean>{

        String query;

        public BookSearchTask(String q) {
            query = q;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid){
                generalListContentArrayAdapter = new GeneralListAdapter(generalListContents, getContext());
                listView.setAdapter(generalListContentArrayAdapter);
            }
            else{
                Snackbar.make(getView(), "Could not find a book with '" + query + "' in the title.", Snackbar.LENGTH_LONG).show();
            }
            alertDialog.dismiss();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            BookClubAPI api = new BookClubAPI();
            ArrayList<Object> arr = api.searchIndex(query);
            String status = (String)arr.get(0);
            ArrayList<Object> tradeArray = (ArrayList<Object>) arr.get(2);
            if (tradeArray != null){
                Collections.shuffle(tradeArray);
                generalListContents = new ArrayList<>();
                for (int i = 0; i < tradeArray.size(); i++){
                    ArrayList<Object> trade = (ArrayList<Object>)tradeArray.get(i);
                    int id = (int)trade.get(0);
                    User user = (User)trade.get(1);
                    Book book = (Book)trade.get(2);

                    generalListContents.add(new GeneralListContent(id, user, book));
                }

             //   for (GeneralListContent g: generalListContents) System.out.println(g.getBook());

                return true;

            }
            else return false;
        }
    }

    public class GeneralListAdapter extends ArrayAdapter<GeneralListContent> implements View.OnClickListener{

        private ArrayList<GeneralListContent> dataSet;
        Context context;
        Animation scaleUp, fadeIn;


        public GeneralListAdapter(ArrayList<GeneralListContent> data, Context context) {
            super(context, R.layout.general_list_item, data);
            this.dataSet = data;
            this.context=context;
            scaleUp = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_fast);
            fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.move);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final GeneralListContent generalListContent = getItem(position);

            ViewHolder viewHolder;

            final View result;
            Bitmap bitmap = null;
            if (convertView == null){
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.general_list_item, parent, false);
                viewHolder.authorNameTextView = (TextView)convertView.findViewById(R.id.authorTextView);
                viewHolder.bookTitleTextView= (TextView)convertView.findViewById(R.id.bookTitleTextView);
                viewHolder.bookImageButton= (ImageButton) convertView.findViewById(R.id.bookImageButton);
                viewHolder.usernameTextView = (TextView) convertView.findViewById(R.id.username);
                viewHolder.cv = convertView.findViewById(R.id.pad);

                result = convertView;
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
                result = convertView;
            }

            Picasso.get()
                    .load(generalListContent.getBook().getBookPhotoUrl())
                    .resize(300, 400)
                    .error(R.drawable.dead)
                    .placeholder(R.drawable.ic_get_app_black_24dp)
                    .into(viewHolder.bookImageButton);
            System.out.println(generalListContent.getBook().getBookPhotoUrl());

            viewHolder.authorNameTextView.setText(generalListContent.getBook().getAuthorName());
            viewHolder.bookTitleTextView.setText(generalListContent.getBook().getTitle());
            viewHolder.usernameTextView.setText(generalListContent.getUser().getUsername());

            viewHolder.bookImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                    intent.putExtra("BOOK", generalListContent.getBook());
                    startActivity(intent);
                }
            });

            //viewHolder.cv.startAnimation(scaleUp);

            return convertView;
        }

        @Override
        public void onClick(View v) {

        }
    }


    public class GeneralListCreator extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {

            generalListContents = new ArrayList<>();

            BookClubAPI api = new BookClubAPI();
            ArrayList<Object> list = api.mainMenuIndex();
            // Log.d("General List", "List : " + list);
            System.out.println(list);
            ArrayList<Object> tradeList = (ArrayList<Object>) list.get(2);
            Collections.shuffle(tradeList);
            for (int i = 0; i < tradeList.size(); i++){
                ArrayList<Object> trade = (ArrayList<Object>)tradeList.get(i);
                int id = (int)trade.get(0);
                User user = (User)trade.get(1);
                Book book = (Book)trade.get(2);

                //System.out.println("i: " + i + "id: " + id + "\nUser: " + user + "\nBook: " + book);
                // generalListContents.add(new GeneralListContent(id, book.getTitle(), book.getAuthorName(), book.getBookPhotoUrl()));
                generalListContents.add(new GeneralListContent(id, user, book));
            }


            //  populateGeneralList();

            try{
                ArrayList<Object> arr = api.getSession();

                System.out.println("Guest User :" + arr.get(0));
                if (arr.get(2) == null){
                    ((MainActivity)getActivity()).setGuestSession(true);
                    preferencesButton.setVisibility(View.INVISIBLE);
                    chatButton.setVisibility(View.INVISIBLE);
                }
                else   ((MainActivity)getActivity()).setGuestSession(false);

            }catch (NullPointerException exception){
                exception.printStackTrace();
                ((MainActivity)getActivity()).setGuestSession(true);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            generalListContentArrayAdapter = new GeneralListAdapter(generalListContents, getContext());
            listView.setAdapter(generalListContentArrayAdapter);
            if (((MainActivity)getActivity()).isGuestSession()){
                preferencesButton.setVisibility(View.INVISIBLE);
                chatButton.setVisibility(View.INVISIBLE);
            }
            if (alertDialog.isShowing())alertDialog.dismiss();
        }
    }


    class GeneralListContent{

        int tradeID;
        String bookTitle;
        String authorName;
        String bookImageURL;
        User user;
        Book book;
/*
        public GeneralListContent(int tradeID, String bookTitle, String authorName, String bookImageURL) {
            this.tradeID = tradeID;
            this.bookTitle = bookTitle;
            this.authorName = authorName;

            this.bookImageURL = bookImageURL;
        }
        */
        public GeneralListContent(int tradeID, User user, Book book){
            this.tradeID = tradeID;
            this.user = user;
            this.book = book;

        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

        /*
        public int getTradeID() {
            return tradeID;
        }

        public void setTradeID(int transactionType) {
            this.tradeID = transactionType;
        }

        public String toString(){
            return "TradeID: " + tradeID + " Book Title: " + bookTitle + " Author Name: " + authorName + " Image URL: " + bookImageURL;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public void setBookTitle(String bookTitle) {
            this.bookTitle = bookTitle;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public String getBookImageURL() {
            return bookImageURL;
        }

        public void setBookImageURL(String bookImageURL) {
            this.bookImageURL = bookImageURL;
        }
        */

    }
}
