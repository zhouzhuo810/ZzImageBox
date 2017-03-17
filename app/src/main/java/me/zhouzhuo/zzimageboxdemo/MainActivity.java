package me.zhouzhuo.zzimageboxdemo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import me.zhouzhuo.zzimagebox.ZzImageBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ZzImageBox imageBox = (ZzImageBox) findViewById(R.id.zz_image_box);
        imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String filePath, ImageView iv) {
                Log.d("ZzImageBox", "image clicked:" + position + "," + filePath);
            }

            @Override
            public void onDeleteClick(int position, String filePath) {
                imageBox.removeImage(position);
                Log.d("ZzImageBox", "delete clicked:" + position + "," + filePath);
                Log.d("ZzImageBox", "all images\n"+imageBox.getAllImages().toString());
            }

            @Override
            public void onAddClick() {
                imageBox.addImage(null);
                Log.d("ZzImageBox", "add clicked");
                Log.d("ZzImageBox", "all images\n"+imageBox.getAllImages().toString());
            }
        });



    }
}
