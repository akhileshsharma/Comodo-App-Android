package com.example.comodovid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.comodovid.make_video.CreateVideoActivity;
import com.example.comodovid.preview_image.PreviewImageActivity;
import com.example.comodovid.video_album.VideoAlbumActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.create_vid_btn)
    void onClickCreateVidBtn(){
        Intent intent = new Intent(this, CreateVideoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.view_album_btn)
    void onClickViewAlbumBtn(){
        Intent intent = new Intent(this, VideoAlbumActivity.class);
        startActivity(intent);
    }

}
