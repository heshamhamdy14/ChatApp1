package com.example.chatapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
 CircleImageView profile_image;
 TextView user_name;
FirebaseUser firebaseUser;
DatabaseReference reference;
  private static final int Image_Request_code=1;
  Uri imageUri;
  StorageReference storageReference;
  StorageTask ubloadtask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        profile_image=view.findViewById(R.id.profile_image);
        user_name=view.findViewById(R.id.profile_user_name);
        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user=dataSnapshot.getValue(User.class);
                    user_name.setText(user.getUsername());
                    if (user.getImageUri().equals("default")){
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    }else {
                        Picasso.get().load(user.getImageUri()).into(profile_image);
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,Image_Request_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Image_Request_code && resultCode== RESULT_OK && data!= null && data.getData()!=null){
            imageUri=data.getData();
            if (ubloadtask!=null && ubloadtask.isInProgress()) {
                Toast.makeText(getContext(), "upload in progress", Toast.LENGTH_SHORT).show();
            }else{
                ubloadImage();
            }
        }
    }
    private String getfileextention(Uri uri){
        ContentResolver cr=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void ubloadImage(){
        if (imageUri!=null){
            final ProgressDialog progressDialog=new ProgressDialog(getContext());
            progressDialog.setMessage("uploading");
            progressDialog.show();
        final StorageReference filereference=storageReference.child(System.currentTimeMillis()+"."+getfileextention(imageUri));
        ubloadtask=filereference.putFile(imageUri);
        ubloadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return filereference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri= task.getResult();
                    String mUri=downloadUri.toString();
                    reference=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                    HashMap<String , Object>hashMap=new HashMap<>();
                    hashMap.put("imageUri",mUri);
                    reference.updateChildren(hashMap);
                    progressDialog.dismiss();
                }else {
                    Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }else{
            Toast.makeText(getContext(), "no image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
