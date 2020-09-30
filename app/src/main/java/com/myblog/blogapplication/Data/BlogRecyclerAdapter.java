package com.myblog.blogapplication.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myblog.blogapplication.Model.Blog;
import com.myblog.blogapplication.R;
import com.squareup.picasso.Picasso;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_row,viewGroup,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Blog blog=blogList.get(i);
        String imageUrl=null;

        viewHolder.title.setText(blog.getTitle());
        viewHolder.desc.setText(blog.getDesc());

        java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();
        String formattedDate=dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());

        viewHolder.timeStamp.setText(formattedDate);
        imageUrl=blog.getImage();



        //TODO: USE Piccasso library to load Image

        Picasso.get().load(imageUrl).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title,desc,timeStamp;
        public ImageView image;
        String userId;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context=ctx;

            title=(TextView)itemView.findViewById(R.id.postTitleList);
            desc=(TextView)itemView.findViewById(R.id.postTextList);
            timeStamp=(TextView)itemView.findViewById(R.id.timestampList);
            image=(ImageView)itemView.findViewById(R.id.postImageList);

            userId=null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: MAKE CLICKABLE BLOG POST
                }
            });

        }
    }
}
