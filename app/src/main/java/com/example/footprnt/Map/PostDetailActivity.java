package com.example.footprnt.Map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.footprnt.Models.Post;
import com.example.footprnt.R;

public class PostDetailActivity extends AppCompatActivity {
    ImageView mIvBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        final Post post= (Post)bundle.getSerializable(Post.class.getSimpleName());
        mIvBackArrow = findViewById(R.id.ivBack2);
        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
