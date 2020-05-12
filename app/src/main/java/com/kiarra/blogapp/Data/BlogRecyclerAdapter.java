package com.kiarra.blogapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiarra.blogapp.Model.Blog;
import com.kiarra.blogapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Blog blog = blogList.get(position);
        String imageUrl = null;

        holder.title.setText(blog.getTitle());
        holder.description.setText(blog.getDescription());



        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimeStamp())).getTime());

        //December 7 2019
        holder.timeStamp.setText(formattedDate);
        imageUrl = blog.getImage();

        //TODO: Use Picasso library to load image
        Picasso.get().load(imageUrl).into(holder.image);    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView timeStamp;
        public ImageView image;
        String userId;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;

            title = view.findViewById(R.id.postTitleList);
            description = view.findViewById(R.id.postTextList);
            image = view.findViewById(R.id.postImageList);
            timeStamp = view.findViewById(R.id.timeStampList);
            userId = null;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Now we can go to the next activity

                }
            });
        }
    }
}