package me.zhouzhuo.zzimageboxdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import me.zhouzhuo.zzimagebox.ZzImageBox;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        RadioGroup rgNumbers = findViewById(R.id.rg_numbers);
        
        rgNumbers.check(R.id.rb_number_three);
        
        //如果统一加载网络图片，可以统一设置此代理
        ZzImageBox.setGlobalOnLineImageLoader(new ZzImageBox.ImageLoader() {
            @Override
            public void onLoadImage(Context context, ImageView iv, String url, int imgSize, int placeHolder) {
                //本例使用Glide加载
                Glide.with(context).load(url)
                    .override(imgSize, imgSize)
                    .placeholder(placeHolder)
                    .into(iv);
            }
            
        });
        
        final ZzImageBox imageBoxAddMode = findViewById(R.id.zz_image_box_add_mode);
        //如果加载网络图片，需要特殊处理，单独设置此代理
        //        imageBoxAddMode.setOnlineImageLoader(new ZzImageBox.OnlineImageLoader() {
        //            @Override
        //            public void onLoadImage(ImageView iv, String url) {
        //                Log.e("TTT", "url=" + url);
        //                //本例使用Glide加载
        //                Glide.with(MainActivity.this).load(url).into(iv);
        //            }
        //        });
        //点击监听
        imageBoxAddMode.setOnImageClickListener(new ZzImageBox.AbsOnImageClickListener() {
            
            
            @Override
            public void onDeleteClick(ImageView iv, int position, String url, @Nullable Bundle args) {
                super.onDeleteClick(iv, position, url, args);
                Glide.with(MainActivity.this).clear(iv);
                Toast.makeText(MainActivity.this, "你点击了+" + position + "的图片:url=" + url + ", args=" + (args == null ? null : args.toString()), Toast.LENGTH_SHORT).show();
                //移除position位置的图片
                imageBoxAddMode.removeImage(position);
            }
            
            @Override
            public void onImageClick(int position, String url, ImageView iv, @Nullable Bundle args) {
                Toast.makeText(MainActivity.this, "你点击了+" + position + "的图片:url=" + url + ", args=" + (args == null ? null : args.toString()), Toast.LENGTH_SHORT).show();
                imageBoxAddMode.replaceImageAt(position, "https://p.ssl.qhimg.com/dm/420_627_/t01b998f20bf6fcbfd4.jpg");
            }
            
            @Override
            public void onAddClick() {
                imageBoxAddMode.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
                // if (imageBoxAddMode.getCount() % 2 == 0) {
                //     imageBoxAddMode.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
                // } else {
                //     imageBoxAddMode.addImage("https://p.ssl.qhimg.com/dm/420_627_/t01b998f20bf6fcbfd4.jpg");
                // }
            }
            
            @Override
            public void onAddLongPress() {
                super.onAddLongPress();
                Toast.makeText(MainActivity.this, "你长按了加号", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onImageLongPress(final int position, String url, ImageView iv, @Nullable Bundle args) {
                super.onImageLongPress(position, url, iv, args);
                Toast.makeText(MainActivity.this, "你长按了+" + position + "的图片:url=" + url + ", args=" + (args == null ? null : args.toString()), Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(MainActivity.this)
                    .setItems(new String[]{"左移", "右移"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    imageBoxAddMode.swapPositionWithLeft(position);
                                    break;
                                case 1:
                                    imageBoxAddMode.swapPositionWithRight(position);
                                    break;
                            }
                        }
                    })
                    .show();
            }
        });
        
        
        /**
         *            显示模式：设置添加图片为透明色并关闭删除功能 即可
         *            app:zib_img_add="@android:color/transparent"
         *            app:zib_img_deletable="false"
         */
        final ZzImageBox imageBoxShowMode = findViewById(R.id.zz_image_box_show_mode);
        
        //因为有阿里OSS服务的需求，因此加了此方法，强制使用网络加载；imageBoxShowMode.addImage如果http开头会默认请求网络；否则默认为本地文件；
        imageBoxShowMode.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        imageBoxShowMode.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        imageBoxShowMode.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        imageBoxShowMode.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        
        //点击监听
        imageBoxShowMode.setOnImageClickListener(new ZzImageBox.AbsOnImageClickListener() {
            
            @Override
            public void onImageClick(int position, String url, ImageView iv, @Nullable Bundle args) {
                Toast.makeText(MainActivity.this, "你点击了+" + position + "的图片:url=" + url + ", args=" + (args == null ? null : args.toString()), Toast.LENGTH_SHORT).show();
            }
            
        });
        
        
        final ZzImageBox imageBoxRight = findViewById(R.id.zz_image_box_right);
        //如果加载网络图片，需要设置此代理
        imageBoxRight.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        imageBoxRight.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        imageBoxRight.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        imageBoxRight.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
        
        rgNumbers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_number_three:
                        imageBoxAddMode.setOneLineImgCount(3);
                        imageBoxShowMode.setOneLineImgCount(3);
                        imageBoxRight.setOneLineImgCount(3);
                        break;
                    case R.id.rb_number_four:
                        imageBoxAddMode.setOneLineImgCount(4);
                        imageBoxShowMode.setOneLineImgCount(4);
                        imageBoxRight.setOneLineImgCount(4);
                        break;
                    case R.id.rb_number_five:
                        imageBoxAddMode.setOneLineImgCount(5);
                        imageBoxShowMode.setOneLineImgCount(5);
                        imageBoxRight.setOneLineImgCount(5);
                        break;
                }
            }
        });
        
        final TextView tvLeftMargin = findViewById(R.id.tv_left_margin);
        final TextView tvRightMargin = findViewById(R.id.tv_right_margin);
        final TextView tvTopMargin = findViewById(R.id.tv_top_margin);
        final TextView tvBottomMargin = findViewById(R.id.tv_bottom_margin);
        final TextView tvImagePadding = findViewById(R.id.tv_image_padding);
        
        SeekBar leftMarginSeekBar = findViewById(R.id.left_margin_seekbar);
        leftMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageBoxAddMode.setBoxLeftPadding(progress);
                imageBoxShowMode.setBoxLeftPadding(progress);
                imageBoxRight.setBoxLeftPadding(progress);
                tvLeftMargin.setText(progress + " px");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            
            }
        });
        SeekBar rightMarginSeekBar = findViewById(R.id.right_margin_seekbar);
        rightMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageBoxAddMode.setBoxRightPadding(progress);
                imageBoxShowMode.setBoxRightPadding(progress);
                imageBoxRight.setBoxRightPadding(progress);
                tvRightMargin.setText(progress + " px");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            
            }
        });
        SeekBar topMarginSeekBar = findViewById(R.id.top_margin_seekbar);
        topMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageBoxAddMode.setBoxTopPadding(progress);
                imageBoxShowMode.setBoxTopPadding(progress);
                imageBoxRight.setBoxTopPadding(progress);
                tvTopMargin.setText(progress + " px");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            
            }
        });
        SeekBar bottomMarginSeekBar = findViewById(R.id.bottom_margin_seekbar);
        bottomMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageBoxAddMode.setBoxBottomPadding(progress);
                imageBoxShowMode.setBoxBottomPadding(progress);
                imageBoxRight.setBoxBottomPadding(progress);
                tvBottomMargin.setText(progress + " px");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            
            }
        });
        SeekBar paddingSeekBar = findViewById(R.id.padding_seekbar);
        paddingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageBoxAddMode.setImagePadding(progress);
                // imageBoxShowMode.setImagePadding(progress);
                // imageBoxRight.setImagePadding(progress);
                tvImagePadding.setText(progress + " px");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            
            }
        });
    }
}
