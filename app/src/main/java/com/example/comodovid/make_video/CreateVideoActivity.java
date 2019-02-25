package com.example.comodovid.make_video;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.comodovid.Constants;
import com.example.comodovid.R;
import com.example.comodovid.glide.GlideApp;
import com.example.comodovid.glide.MyAppGlideModule;
import com.example.comodovid.utils.ImageUtils;
import com.example.comodovid.utils.PermissionUtils;
import com.example.comodovid.video_album.VideoAlbumActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.comodovid.Constants.ActivityResultConstants.READ_REQUEST_CODE;
import static com.example.comodovid.Constants.ActivityResultConstants.TAKE_PHOTO_REQUEST;
import static com.example.comodovid.Constants.PermissionConstants.CAMERA_PERMISSION_REQUEST_CODE;
import static com.example.comodovid.Constants.PermissionConstants.READ_EXTERNAL_STORAGE_REQUEST_CODE;

public class CreateVideoActivity extends AppCompatActivity {

    private String mCurrentPhotoPath;
    private String filePath;

    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.click_image_btn)
    void onClickTakeImageBtn(){
        if (PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
            openCameraIntent();
        } else {
            PermissionUtils.requestPermission(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @OnClick(R.id.pick_image_btn)
    void onClickPickImageBtn(){
        if (PermissionUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openFilesIntent();
        } else {
            PermissionUtils.requestPermission(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    private void openCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.comodovid.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openFilesIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent = Intent.createChooser(intent, "Choose a file");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCameraIntent();
        }

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilesIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                filePath = ImageUtils.getPath(this, uri);
                System.out.println("filepath "+filePath);
                if (filePath != null) {
                    String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1);
                    if (fileExtension.equalsIgnoreCase("jpg") ||
                            fileExtension.equalsIgnoreCase("jpeg")
                            || fileExtension.equalsIgnoreCase("png")) {
                        imageView.setImageDrawable(null);
                        imageView.setVisibility(View.VISIBLE);
                        File file = new File(filePath);
                        GlideApp.with(this)
                                .load(file)
                                .placeholder(R.drawable.ic_picture)
                                .error(R.drawable.ic_picture)
                                .into(imageView);

                    } else {
                        Toast.makeText(this,"Unsupported file format", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this,"Unsupported file format", Toast.LENGTH_SHORT).show();
                }

            }
        }
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            filePath = mCurrentPhotoPath;
            File file = new File(filePath);
            Uri uri = Uri.fromFile(file);
            Bitmap bitmap = ImageUtils.getBitmapFromUri(uri, this);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
        }
    }
}
