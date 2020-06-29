package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.Chat;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int MESSAGE_TYPE_RIGHT=0;
    private static final int MESSAGE_TYPE_LEFT=1;
    private List<Chat>chats;
    private Context context;
    private String imageUrl;

    public MessageAdapter(List<Chat> chats, Context context, String imageUrl) {
        this.chats = chats;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MESSAGE_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageViewHolder(view);
        }else {
            View view=LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Chat chat=chats.get(position);
        holder.message.setText(chat.getMessage());
        if (imageUrl.equals("default")){
            holder.profile_photo.setImageResource(R.mipmap.ic_launcher);
        }else
            Picasso.get().load(imageUrl).into(holder.profile_photo);
        if (position==chats.size()-1){
        if (chat.isIsseen()){
            holder.textseen.setText("seen");
        }else{
            holder.textseen.setText("delivered");
        }
    }else {
            holder.textseen.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_photo;
        TextView message;
        TextView textseen;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_photo=itemView.findViewById(R.id.profile_photo);
            message=itemView.findViewById(R.id.chat_message);
            textseen=itemView.findViewById(R.id.textseen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid())){
            return MESSAGE_TYPE_RIGHT;
        }else
            return MESSAGE_TYPE_LEFT;

    }
}
