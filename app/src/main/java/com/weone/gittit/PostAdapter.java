package com.weone.gittit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aditya Shirole on 5/17/2015.
 */
public class PostAdapter extends ArrayAdapter<HashMap<String,String>> {
    protected Context mContext;
    protected ArrayList<HashMap<String,String>> mBlogPosts;

    public PostAdapter(Context context, ArrayList<HashMap<String, String>> posts) {
        super(context, R.layout.post_item, posts);

        mContext = context;
        mBlogPosts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.post_item,null);

            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(mBlogPosts.get(position).get("title"));
        holder.author.setText(mBlogPosts.get(position).get("author"));


        String photoUrl = mBlogPosts.get(position).get("thumbnail");

        Picasso.with(mContext)
                .load(photoUrl)
                .into(holder.thumbnail);

        return convertView;
    }

    class ViewHolder{
        ImageView thumbnail;
        TextView title;
        TextView author;
    }
}
