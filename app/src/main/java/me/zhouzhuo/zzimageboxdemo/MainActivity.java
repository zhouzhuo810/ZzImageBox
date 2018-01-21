package me.zhouzhuo.zzimageboxdemo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import me.zhouzhuo.zzimagebox.ZzImageBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ZzImageBox imageBox = (ZzImageBox) findViewById(R.id.zz_image_box);
        //如果加载网络图片，需要设置此代理
        imageBox.setOnlineImageLoader(new ZzImageBox.OnlineImageLoader() {
            @Override
            public void onLoadImage(ImageView iv, String url) {
                Log.e("TTT", "url=" + url);
                //本例使用Glide加载
                Glide.with(MainActivity.this).load(url).into(iv);
            }
        });
        //点击监听
        imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String filePath, ImageView iv) {
                Toast.makeText(MainActivity.this, "你点击了+" + position + "的图片:url=" + filePath, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position, String filePath) {
                //移除position位置的图片
                imageBox.removeImage(position);
            }

            @Override
            public void onAddClick() {
                //添加网络图片
                imageBox.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
            }
        });


    }
}
