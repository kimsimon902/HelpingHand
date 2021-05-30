package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPost extends AppCompatActivity {


    private ProgressBar progressBar;
    private EditText postEt, descEt;
    private ImageView postIv;
    private Button createPostBtn, camBtn, galBtn;
    private String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private Post post;
    private String name, desc, username,uid;


    private StorageReference storageReference;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;


    //FOR image name and uri
    private String image_name;
    private Uri localUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);


        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.myRef = this.firebaseDatabase.getReference("Posts");
        storageReference = FirebaseStorage.getInstance().getReference();

        postEt = findViewById(R.id.addPostNameEt);
        descEt = findViewById(R.id.addPostDescEt);
        postIv = findViewById(R.id.addPostSelectedImageIv);
        createPostBtn = findViewById(R.id.addPostCreateBtn);
        camBtn = findViewById(R.id.addPostCamBtn);
        galBtn = findViewById(R.id.addPostGalBtn);
        progressBar = findViewById(R.id.progressBar);

        postIv.setVisibility(View.GONE);

        Intent i = getIntent();
        username = i.getStringExtra("username");
        uid = i.getStringExtra("uid");

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        galBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = postEt.getText().toString();
                desc = descEt.getText().toString();
                createPost();

            }
        });


    }               //end of oncreate

    private void askCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            dispatchTakePictureIntent();
        }

    }

    private void openCamera(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                postIv.setImageURI(Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                image_name = f.getName();
                localUri = contentUri;
                postIv.setVisibility(View.VISIBLE);
//                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timestamp + "." + getFileExt(contentUri);
                postIv.setImageURI(contentUri);

                image_name = imageFileName;
                localUri = contentUri;
                postIv.setVisibility(View.VISIBLE);
//                uploadImageToFirebase(imageFileName, contentUri);
            }
        }
    }


    private void uploadImageToFirebase(String name, Uri contentUri){
        StorageReference image = storageReference.child("images/" + name);

        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URL is: " + uri.toString());
//                        Picasso.get().load(uri).into(signupBusSelectedImageIv);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPost.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExt(Uri contentUri){
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }



    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* Directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
//        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
        File photoFile = null;
        try{
            photoFile = createImageFile();
        }catch (IOException ex){

        }

        // Continue only if the File was successfully created
        if(photoFile != null){
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.google.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
//        }
    }

    private void createPost(){
        uploadImageToFirebase(image_name, localUri);


        String key = myRef.push().getKey();
        post = new Post(name, desc, image_name, username, uid, key);
        myRef.child(key).setValue(post);
        progressBar.setVisibility(View.VISIBLE);

        Handler h = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Intent intent = new Intent(AddPost.this, Feed.class);
                intent.putExtra("username", username);
                intent.putExtra("uid", uid);
                intent.putExtra("post", post);
                startActivity(intent);
                finish();
            }
        };

        h.sendEmptyMessageDelayed(0, 3000);
    }


}