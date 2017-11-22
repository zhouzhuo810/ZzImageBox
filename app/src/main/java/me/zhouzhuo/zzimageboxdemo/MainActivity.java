package me.zhouzhuo.zzimageboxdemo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.zhouzhuo.zzimagebox.ZzImageBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ZzImageBox imageBox = (ZzImageBox) findViewById(R.id.zz_image_box);
        imageBox.setOnlineImageLoader(new ZzImageBox.OnlineImageLoader() {
            @Override
            public void onLoadImage(ImageView iv, String url) {
                Log.e("TTT", "url=" + url);
                Glide.with(MainActivity.this).load(url).into(iv);
            }
        });
        imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String filePath, ImageView iv) {
                Log.d("ZzImageBox", "image clicked:" + position + "," + filePath);
            }

            @Override
            public void onDeleteClick(int position, String filePath) {
                imageBox.removeImage(position);
                Log.d("ZzImageBox", "delete clicked:" + position + "," + filePath);
                Log.d("ZzImageBox", "all images\n" + imageBox.getAllImages().toString());
            }

            @Override
            public void onAddClick() {
                imageBox.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
                Log.d("ZzImageBox", "add clicked");
                Log.d("ZzImageBox", "all images\n" + imageBox.getAllImages().toString());
            }
        });


    }
}
