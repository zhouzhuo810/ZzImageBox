# ZzImageBox

:fire: A powerful image container for adding and removing images.

### 功能简介：

1.支持添加、删除、默认图片的配置；

2.支持最大行数限制；

3.支持每行数量修改；

4.支持添加、删除、图片点击回调接口。

5.支持Box的添加(.addImage(String imagePath))和删除(.removeImage(int position))；

6.支持加载本地和网络图片(使用方法`.addImage(String url)`)；

7.支持自定义图片加载框架(使用`.setOnlineImageLoader(ZzImageBox.OnlineImageLoader listener)`,
如果有网络图片必须使用此方法);

8.支持左右margin设置。

### Gradle


```
    implementation 'com.github.zhouzhuo810:ZzImageBox:1.2.6'
    implementation 'androidx.recyclerview:recyclerview:1.1.0' //版本自己决定
```


## What does it look like?


![zzimagebox](https://github.com/zhouzhuo810/ZzImageBox/blob/master/zz_image_box_demo.gif)



## How to use it ?


#### 注意：
- ZzImageBox的marginLeft属性使用zib_left_margin代替；
- ZzImageBox的marginRight属性使用zib_right_margin代替；
- 如果不需要显示添加图片，可以设置zib_img_add为透明颜色；
- 加载网络图片必须添加`.setOnlineImageLoader();`方法，
在`onLoadImage(ImageView iv, String url)`中自行使用Glide等框架加载；

xml:

```xml
    <me.zhouzhuo.zzimagebox.ZzImageBox
        android:id="@+id/zz_image_box"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:zib_img_padding="5dp"
        app:zib_one_line_img_count="4"
        app:zib_max_line="3"
        app:zib_img_scale_type="CENTER_CROP"
        app:zib_img_deletable="true"
        app:zib_img_addable="true"
        app:zib_img_add="@drawable/iv_add"
        app:zib_img_default="@drawable/iv_default"
        app:zib_img_delete="@drawable/iv_delete"
        tools:itemCount="3"
        tools:listitem="@layout/zz_image_box_item"
        />
```


java:

### 全局图片加载器

```java
        //如果需要加载网络图片，添加此监听。
        imageBox.setGlobalOnLineImageLoader(new ZzImageBox.OnlineImageLoader() {
            @Override
            public void onLoadImage(Context context, ImageView iv, String url, int imgSize, int placeHolder) {
                Log.d("ZzImageBox", "url=" + url);
                Glide.with(MainActivity.this)
                    .load(url)
                    .override(imgSize, imgSize)
                    .placeholder(placeHolder)
                    .into(iv);
            }
        });
```

### 设置图片点击或长按监听

```java

        final ZzImageBox imageBoxAddMode = findViewById(R.id.zz_image_box_add_mode);
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
            }

            @Override
            public void onAddClick() {
                imageBoxAddMode.addImage("http://p1.so.qhimg.com/dmfd/290_339_/t01e15e0f1015e44e41.jpg");
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
            }
        });

```

## 属性说明：


```
    <declare-styleable name="ZzImageBox">
        <attr name="zib_max_line" format="integer" />
        <attr name="zib_one_line_img_count" format="integer" />
        <attr name="zib_max_img_count" format="integer" />
        <attr name="zib_img_padding" format="dimension|reference" />
        <attr name="zib_img_default" format="color|reference" />
        <attr name="zib_img_delete" format="color|reference" />
        <attr name="zib_img_add" format="color|reference" />
        <attr name="zib_img_scale_type" format="enum">
            <enum name="MATRIX" value="0"/>
            <enum name="FIT_XY" value="1"/>
            <enum name="FIT_START" value="2"/>
            <enum name="FIT_CENTER" value="3"/>
            <enum name="FIT_END" value="4"/>
            <enum name="CENTER" value="5"/>
            <enum name="CENTER_CROP" value="6"/>
            <enum name="CENTER_INSIDE" value="7"/>
        </attr>
        <attr name="zib_img_deletable" format="boolean" />
        <attr name="zib_img_addable" format="boolean" />
        <attr name="zib_icon_color" format="color|reference" />
    </declare-styleable>
```


>| 属性名| 属性类型 | 属性功能 |
>|:--------- |:-------------|:-----|
>| zib_max_line | integer | 最大行数 |
>| zib_one_line_img_count | integer | 每行数量 |
>| zib_img_padding| dimension | 图片之间的间距 |
>| zib_img_default | drawable/color | 默认图片资源id |
>| zib_img_delete |drawable/color | 删除图片资源id |
>| zib_img_add | drawable/color | 添加图片资源id |
>| zib_icon_color | color | 添加图片的颜色 |
>| zib_img_deletable |boolean | 是否显示删除图片 |
>| zib_img_addable |boolean | 是否显示添加图片 |
>| zib_img_scale_type| enum | 图片缩放类型 |


### 更新日志

> v1.2.6
- 移除zib_left_margin和zib_right_margin属性；
- 新增`setBoxLeftPadding`、`setBoxRightPadding`、`setBoxTopPadding`、`setBoxBottomPadding`四个方法。

> v1.2.5
- 移除Uri操作；
- 修复左右移动bug;

> v1.2.4
- 修改在线加载回调方法；

> v1.2.3
- 添加图片缩放类型属性zib_img_scale_type；
- 添加新增图片是否显示属性zib_img_addable；

> v1.2.2
- onDeleteClick回调方法中增加ImageView参数；
- 升级AndroidX；

> v1.2.1
- 支持设置新增按钮图片的颜色；

> v1.2.0

- 支持长按图片和长按加号监听；
- 支持图片左右移动方法；

> v1.1.9

- 加强position判断;

> v1.1.8

- holder.getAdapterPosition()改成position；

> v1.1.7

- 新增Tag参数支持；

> v1.1.6

- 修复横竖屏切换时，图片大小未变化问题；

> v1.1.5

- 修改RecyclerView版本，修复RecyclerView图片高度问题；

> v1.1.3

- 修改RecyclerView版本，修复RecyclerView图片高度问题；

> v1.1.2

- 修改RecyclerView版本，修复RecyclerView图片高度问题；

> v1.1.1

- 修改计算图片宽度按控件本身宽度来；

> v1.1.0

- 修改ImageEntity的private为public；

> v1.1.1

- 添加`ZzImageBox#getAllRealPath()`、
`ZzImageBox#getAllRealType`、`ZzImageBox#getAllEntity`
、`ZzImageBox#getRealPathAt`、`ZzImageBox#getRealTypeAt`
、`ZzImageBox#getEntityAt`等方法。


> v1.0.8

- 添加`ZzImageBox#addImageWithRealPathAndType(@NonNull String imagePath, String realPath, int realType)`方法,使用此方法添加一个的自定义的路径和类型
,比如缩略图对应的原图、视频的地址，缩略图对应的文件类型是图片还是视频;
- `setOnImageClickListener`接口有所调整；


> v1.0.7

- 修改依赖为`implementation`，使用时自行添加`recyclerview-v7`库。
- 添加`ZzImageBox#addImageOnline(String url)`方法,使用此方法可强制使用网络加载;

> v1.0.6

- 支持动态设置左间距，使用`setLeftMarginInPixel()`方法；
- 支持动态设置右边距，使用`setRightMarginInPixel()`方法；
- 支持动态设置图片间距，使用`setImagePadding()`方法；
- 支持动态设置单行图片数量，使用`setImageSizeOneLine()`方法；
- 更新RecyclerView版本到`com.android.support:recyclerview-v7:27.0.1`。

> v1.0.5

- 支持加载网络图片；

> v1.0.4

- 点击图片返回图片对象，用于适应转场动画。

> v1.0.3

- 新增zib_left_margin和zib_right_margin设置左右外间距属性。

> v1.0.2

- 首次提交。

### License

```
Copyright © zhouzhuo810

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```