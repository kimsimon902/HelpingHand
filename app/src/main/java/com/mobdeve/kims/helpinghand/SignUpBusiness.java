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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpBusiness extends AppCompatActivity {

    private String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private EditText signupBusNameEt, signupBusEmailEt, signupBusPassEt, signupBusConfirmEt, signupBusDescEt;
    private Button signupBusConfirmBtn, signupBusCamBtn, signupBusGalBtn;
    private ImageView signupBusSelectedImageIv;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;


    private StorageReference storageReference;

    //FOR image name and uri
    private String imageName;
    private Uri localUri;
    private String firebaseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_business);

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        signupBusNameEt = findViewById(R.id.signupBusNameEt);
        signupBusEmailEt = findViewById(R.id.signupBusEmailEt);
        signupBusPassEt = findViewById(R.id.signupBusPassEt);
        signupBusConfirmEt = findViewById(R.id.signupBusConfirmEt);
        signupBusDescEt = findViewById(R.id.signupBusDescEt);

        signupBusConfirmBtn = findViewById(R.id.signupBusConfirmBtn);
        signupBusCamBtn = findViewById(R.id.signupBusCamBtn);
        signupBusGalBtn = findViewById(R.id.signupBusGalBtn);

        signupBusSelectedImageIv = findViewById(R.id.signupBusSelectedImageIv);

        progressBar = findViewById(R.id.progressBar);

        signupBusConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        signupBusCamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        signupBusGalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

    }

    private void askCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getBaseContext(), "Camera Permission Required!", Toast.LENGTH_SHORT).show();
            }
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
                signupBusSelectedImageIv.setImageURI(Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                imageName = f.getName();
                localUri = contentUri;

//                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timestamp + "." + getFileExt(contentUri);
                signupBusSelectedImageIv.setImageURI(contentUri);

                imageName = imageFileName;
                localUri = contentUri;
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
                Toast.makeText(SignUpBusiness.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExt(Uri contentUri){
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException{
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    private void registerUser(){
        String name = signupBusNameEt.getText().toString();
        String email = signupBusEmailEt.getText().toString().trim();
        String pass = signupBusPassEt.getText().toString().trim();
        String confirm = signupBusConfirmEt.getText().toString().trim();
        String desc = signupBusDescEt.getText().toString();

        if(name.isEmpty()){
            signupBusNameEt.setError("Business Name is required!");
            signupBusNameEt.requestFocus();
            return;
        }

        if(email.isEmpty()){
            signupBusEmailEt.setError("Email is required!");
            signupBusEmailEt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupBusEmailEt.setError("Invalid email format!");
            signupBusEmailEt.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            signupBusPassEt.setError("Password is required!");
            signupBusPassEt.requestFocus();
            return;
        }

        if(pass.length() < 6){
            signupBusPassEt.setError("Minimum password length should be 6 characters!");
            signupBusPassEt.requestFocus();
            return;
        }

        if(confirm.isEmpty()){
            signupBusConfirmEt.setError("Please confirm your password");
            signupBusConfirmEt.requestFocus();
            return;
        }

        if(!pass.equals(confirm)){
            signupBusPassEt.setError("Passwords do not match!");
            signupBusConfirmEt.setError("Passwords do not match!");
            signupBusConfirmEt.requestFocus();
            signupBusPassEt.requestFocus();
            return;
        }

        if(desc.isEmpty()){
            signupBusDescEt.setError("Business Description is required!");
            signupBusDescEt.requestFocus();
            return;
        }

        if(imageName == null && localUri == null) {
            Toast.makeText(SignUpBusiness.this, "Image is required!", Toast.LENGTH_LONG).show();
            return;
        }


        uploadImageToFirebase(imageName, localUri);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, email, desc, true, imageName);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpBusiness.this, "User has been registered!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(SignUpBusiness.this, Login.class);
                                        startActivity(i);

                                        //redirect to login next
                                    }else{
                                        Toast.makeText(SignUpBusiness.this, "Failed to register. Try again.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

    }

}