package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.UsersAdapter;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.ChatList;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User>users;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<ChatList>userslist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView=view.findViewById(R.id.chats_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        userslist=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("chatlist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList chatList=snapshot.getValue(ChatList.class);
                    if(!userslist.contains(chatList.getId())){
                    userslist.add(chatList);
                    }
                }
                chatlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        reference= FirebaseDatabase.getInstance().getReference("chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userslist.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat=snapshot.getValue(Chat.class);
//                    assert chat != null;
//                    if (chat.getSender().equals(firebaseUser.getUid())){
//                        if(!userslist.contains(chat.getReceiver())){
//                        userslist.add(chat.getReceiver());
//                        }
//                    }else if (chat.getReceiver().equals(firebaseUser.getUid())){
//                        if (!userslist.contains(chat.getSender())){
//                        userslist.add(chat.getSender());
//                        }
//                    }
//                }
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }

    private void chatlist() {
        users=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    for (ChatList chatList : userslist){
                        if (chatList.getId().equals(user.getId())){
                            users.add(user);
                        }
                    }
                }
                usersAdapter=new UsersAdapter(getContext(),users,true);
                recyclerView.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    private void readChats(){
//        users=new ArrayList<>();
//        reference=FirebaseDatabase.getInstance().getReference("users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                users.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    User user=snapshot.getValue(User.class);
//                    for (String id : userslist){
//                        assert user != null;
//                        if (user.getId().equals(id)){
//                            if (users.size()!=0){
//                                for (User user1 : users){
//                                    if (!user.getId().equals(user1.getId())){
//                                        users.add(user);
//                                        break;
//                                    }
//                                }
//                            }else{
//                                users.add(user);
//                                break;
//                            }
//                        }
//                    }
//                }
//                usersAdapter =new UsersAdapter(getContext(),users,true);
//                recyclerView.setAdapter(usersAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
