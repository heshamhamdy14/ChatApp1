package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Message_Act;
import com.example.chatapp.R;
import com.example.chatapp.Models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    List<User> users;
    Context context;
    boolean ischat;
    public UsersAdapter(Context context, List<User> users , boolean ischat){
        this.context=context;
        this.users= users;
        this.ischat=ischat;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_row,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user=users.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageUri().equals("default")){
            holder.userimage.setImageResource(R.mipmap.ic_launcher);
        }else
        Picasso.get().load(user.getImageUri()).into(holder.userimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent=new Intent(context, Message_Act.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
        if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userimage;
        TextView username;
        CircleImageView img_on, img_off;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userimage=itemView.findViewById(R.id.profle_photo);
            username=itemView.findViewById(R.id.text_user_name);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
        }
    }
}
