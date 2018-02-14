package com.grizzly.keepup.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grizzly.keepup.R;
import com.squareup.picasso.Picasso;

/**
 * Created by kubek on 2/14/18.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {

    private View mView;

    public UserViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDetails(Context context, String userNameS, String userImageS, String userMailS){
        TextView userName = mView.findViewById(R.id.search_user_name);
        TextView userMail = mView.findViewById(R.id.search_user_mail);
        ImageView userImage = mView.findViewById(R.id.search_user_image);

        userName.setText(userNameS);
        userMail.setText(userMailS);
        Picasso.with(context).load(userImageS).into(userImage);
    }
}
