# ZzImageBox

A powerful image container for adding and removing images.

### 功能简介：

1.支持添加、删除、默认图片的配置；

2.支持最大行数限制；

3.支持每行数量修改；

4.支持添加、删除、图片点击回调接口。

5.支持Box的添加(.addImage(String imagePath))和删除(.removeImage(int position))；

6.支持加载本地图片(使用方法.addImage(String imagePath))；

7.支持左右margin设置。

### Gradle


```
compile 'me.zhouzhuo.zzimagebox:zz-image-box:1.0.5'
```

内部依赖项说明：
```
    compile 'com.android.support:recyclerview-v7:25.3.1'
```
也就是说添加ZzImageBox同时会添加RecyclerView包。

### Maven

```xml
<dependency>
  <groupId>me.zhouzhuo.zzimagebox</groupId>
  <artifactId>zz-image-box</artifactId>
  <version>1.0.4</version>
  <type>pom</type>
</dependency>
```

## What does it look like?


![zzimagebox](https://github.com/zhouzhuo810/ZzImageBox/blob/master/zzimagebox.gif)



## How to use it ?


#### 注意：
- 为了保证图片是正方形，需要保证ZzImageBox水平方向是填满屏幕的，也就是说父容器不能有padding和margin。
- ZzImageBox的marginLeft属性使用zib_left_margin代替；
- ZzImageBox的marginRight属性使用zib_right_margin代替；
- 如果不需要显示添加图片，可以设置zib_img_add为透明颜色；

xml:

```xml
    <me.zhouzhuo.zzimagebox.ZzImageBox
        android:id="@+id/zz_image_box"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:zib_left_margin="20dp"
        app:zib_right_margin="20dp"
        app:zib_img_padding="5dp"
        app:zib_img_size_one_line="4"
        app:zib_max_line="3"
        app:zib_img_deletable="true"
        app:zib_img_add="@drawable/iv_add"
        app:zib_img_default="@drawable/iv_default"
        app:zib_img_delete="@drawable/iv_delete"
        />
```


java:

```java
        final ZzImageBox imageBox = (ZzImageBox) findViewById(R.id.zz_image_box);
        //如果需要加载网络图片，添加此监听。
        imageBox.setOnlineImageLoader(new ZzImageBox.OnlineImageLoader() {
            @Override
            public void onLoadImage(ImageView iv, String url) {
                Log.d("ZzImageBox", "url=" + url);
                Glide.with(MainActivity.this).load(url).into(iv);
            }
        });
        imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String filePath) {
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

```

## 属性说明：


```
    <declare-styleable name="ZzImageBox">
        <attr name="zib_max_line" format="integer" />
        <attr name="zib_img_size_one_line" format="integer" />
        <attr name="zib_img_padding" format="dimension|reference" />
        <attr name="zib_img_default" format="color|reference" />
        <attr name="zib_img_delete" format="color|reference" />
        <attr name="zib_img_add" format="color|reference" />
        <attr name="zib_img_deletable" format="boolean" />
        <attr name="zib_left_margin" format="dimension|reference" />
        <attr name="zib_right_margin" format="dimension|reference" />
    </declare-styleable>
```


| 属性名| 属性类型 | 属性功能 |
|:--------- |:-------------|:-----|
| zib_max_line | integer | 最大行数 |
| zib_img_size_one_line | integer | 每行数量 |
| zib_img_padding| dimension | 图片之间的间距 |
| zib_img_default | drawable/color | 默认图片资源id |
| zib_img_delete |drawable/color | 删除图片资源id |
| zib_img_add | drawable/color | 添加图片资源id |
| zib_img_deletable |boolean | 是否显示删除图片 |
| zib_left_margin| dimension | 控件距离屏幕左边距离 |
| zib_right_margin| dimension | 控件距离屏幕右边距离 |

