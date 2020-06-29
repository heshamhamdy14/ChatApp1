package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message_Act extends AppCompatActivity {
CircleImageView userimage;
TextView username;
FirebaseUser firebaseUser;
DatabaseReference  reference;
EditText message;
ImageButton send;
RecyclerView recyclerView;
MessageAdapter messageAdapter;
List<Chat>chats;
ValueEventListener seenListener;
String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Message_Act.this,Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        userimage=findViewById(R.id.profile_picture);
        username=findViewById(R.id.user_name);
        message=findViewById(R.id.messsage);
        send=findViewById(R.id.send);

        Intent  intent= getIntent();
         userid= intent.getExtras().getString("userid");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if (!msg.equals("")){
                    //el parameters shayla id bta3 el user(sender) + id reciever + message
                    sendmessage(firebaseUser.getUid() , userid , msg);
                }else{
                    Toast.makeText(Message_Act.this, "you cannot send empty message!!", Toast.LENGTH_SHORT).show();
                }
                message.setText("");

            }
        });



        reference= FirebaseDatabase.getInstance().getReference("users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageUri().equals("default")){
                    userimage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageUri()).into(userimage);
                }
                view_message(firebaseUser.getUid() , userid , user.getImageUri());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seen_messages(userid);

    }

    //method to cheack if message is seen or not
    private void seen_messages(final String userid ){
         reference=FirebaseDatabase.getInstance().getReference("chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String , Object>hashMap=new HashMap<>();
                        hashMap.put("isseen" , true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//hn3ml child gdiid fe realtimedatabase 3lshan ysheel el chats
    private void sendmessage(String sender , String reciever , String message ) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String , Object> hashMap=new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", reciever);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        reference.child("chats").push().setValue(hashMap);

        final DatabaseReference chatref=FirebaseDatabase.getInstance().getReference("chatlist").child(firebaseUser.getUid()).child(userid);
        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatref.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //bnrg3 el data mn chats_child ll recyclerview bta3tna
    private void view_message(final String myid ,final String userid ,final String imageUri){
        chats=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    assert chat != null;
                    //b cheack en el sender w el receiver kol wa7d m3ah id bta3o wndeef variable chat fe list chats bta3tha
                    if (chat.getReceiver().equals(myid)&&chat.getSender().equals(userid)||
                        chat.getReceiver().equals(userid)&& chat.getSender().equals(myid)){
                        chats.add(chat);
                    }
                }
                messageAdapter=new MessageAdapter(chats , Message_Act.this,imageUri);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //method to check if user online or offline
    private void status(String status){
        reference=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String , Object>hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        reference.removeEventListener(seenListener);
    }
}
