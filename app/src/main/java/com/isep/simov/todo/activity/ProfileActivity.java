package com.isep.simov.todo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isep.simov.todo.R;
import com.isep.simov.todo.model.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.toString();
    private static final int CAMERA_REQUEST_CODE = 1;
    FirebaseFirestore firebaseDB;
    FirebaseAuth auth;
    @BindView(R.id.userMail)
    TextInputEditText userMail;
    @BindView(R.id.userFirstname)
    TextInputEditText userFirstname;
    @BindView(R.id.userLastname)
    TextInputEditText userLastname;
    @BindView(R.id.userAge)
    TextInputEditText userAge;
    @BindView(R.id.updateUser)
    Button updateUserButton;
    Button mUploadBtn;
    User curremtUser;
    private ImageView mImageView;
    private StorageReference mStorage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        mImageView = (ImageView) findViewById(R.id.profile_image);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        ButterKnife.bind(this);
        userMail.setFocusable(false);

        DocumentReference docRef = firebaseDB.collection("users").document(auth.getCurrentUser().getUid());
        if (docRef != null) {
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    curremtUser = documentSnapshot.toObject(User.class);
                    String email = curremtUser.getUserEmail();
                    userMail.setText(email);
                    String firstName = curremtUser.getUserFirstname();
                    userFirstname.setText(firstName);
                    String lastName = curremtUser.getUserLastname();
                    userLastname.setText(lastName);
                    int age = curremtUser.getUserAge();
                    userAge.setText(String.valueOf(age));
                    mImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    });
                }
            });
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }


        updateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fn, ln;
                int age;

                age = Integer.parseInt(userAge.getText().toString());
                fn = userFirstname.getText().toString();
                ln = userLastname.getText().toString();

                curremtUser.setUserFirstname(fn);
                curremtUser.setUserAge(age);
                curremtUser.setUserLastname(ln);

                firebaseDB.collection("users").document(auth.getCurrentUser().getUid()).set(curremtUser).addOnCompleteListener(task -> {
                    Toast.makeText(ProfileActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            mProgress.setMessage("uploading image...");
            mProgress.show();

            Bitmap photo = (Bitmap) data.getExtras().get("data");


            Uri tempUri = getImageUri(getApplicationContext(), photo);

            Toast.makeText(this, "Here " + tempUri, Toast.LENGTH_LONG).show();

            Toast.makeText(this, "Real path for URI : " + getRealPathFromURI(tempUri), Toast.LENGTH_SHORT).show();

            if (tempUri == null) {
                Toast.makeText(this, "Cant get data from camera...", Toast.LENGTH_SHORT).show();
                mProgress.dismiss();

            } else {
                StorageReference filepath = mStorage.child("Photos").child(tempUri.getLastPathSegment());
                filepath.putFile(tempUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgress.dismiss();
                        Toast.makeText(ProfileActivity.this, "Uploading Finished", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = taskSnapshot.getUploadSessionUri();
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Picasso.get().load(uri).into(mImageView);
                            }
                        });

                    }
                });

            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

}
