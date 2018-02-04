package com.grizzly.keepup.mainFragments.newsPage;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grizzly.keepup.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFeedFragment extends Fragment {

    private FirebaseAuth mAuth;

    private FirebaseListAdapter<NewsFeed> adapter;
    private View mView;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mView = view;
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayNewsFeed();
    }


    private void displayNewsFeed() {
        ListView newsList = mView.findViewById(R.id.news_feed_list);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid().toString()).child("runs");
        adapter = new FirebaseListAdapter<NewsFeed>(getActivity(), NewsFeed.class, R.layout.news_row,
                ref) {
            @Override
            protected void populateView(View v, NewsFeed model, int position) {
                //get reference to to the value og list_item.xml
                TextView runTime;
                ImageView imageUri;

                imageUri = v.findViewById(R.id.news_post_image);
                runTime = v.findViewById(R.id.news_post_date);

                Picasso.with(getContext()).load(model.getSpecific_run_image()).into(imageUri);
                runTime.setText(DateFormat.format("HH:mm", model.getRun_date()));

            }
        };
        newsList.setAdapter(adapter);
    }

}
