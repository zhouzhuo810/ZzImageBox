package me.zhouzhuo.zzimagebox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ZzImageBox-A powerful Image Container.
 * Created by zz on 2016/10/9.
 */
public class ZzImageBox extends RecyclerView {
    
    private static final boolean DEFAULT_DELETABLE = true;
    private static final int DEFAULT_MAX_LINE = 1;
    private static final int DEFAULT_IMAGE_SIZE = 4;
    private static final int DEFAULT_IMAGE_PADDING = 4;
    private static final int DEFAULT_DEFAULT_ICON_COLOR = 0x0;
    
    private static OnlineImageLoader sGlobalOnLineImageLoader;
    
    private int mMaxLine;
    private int mMaxImgCount;
    private int mIconColor;
    private int mOneLineImgCount;
    private int mPadding;
    private int mLeftMargin;
    private int mRightMargin;
    private int mDefaultPicId = -1;
    private int mDeletePicId = -1;
    private int mAddPicId = -1;
    private boolean mDeletable;
    private boolean mAddable;
    private ImageView.ScaleType mImgScaleType = ImageView.ScaleType.CENTER_CROP;
    
    private List<ImageEntity> mDataSource;
    private MyAdapter mAdapter;
    private OnlineImageLoader mOnlineImageLoader;
    private AbsOnImageClickListener mClickListener;
    
    public ZzImageBox(Context context) {
        super(context);
        init(context, null);
    }
    
    public ZzImageBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public ZzImageBox(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    
    public interface OnlineImageLoader {
        void onLoadImage(Context context, ImageView iv, String url, int imgSize, int placeHolder);
    }
    
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZzImageBox);
        mLeftMargin = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_left_margin, 0);
        mRightMargin = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_right_margin, 0);
        mMaxLine = a.getInteger(R.styleable.ZzImageBox_zib_max_line, DEFAULT_MAX_LINE);
        mOneLineImgCount = a.getInteger(R.styleable.ZzImageBox_zib_one_line_img_count, DEFAULT_IMAGE_SIZE);
        mMaxImgCount = a.getInteger(R.styleable.ZzImageBox_zib_max_img_count, 0);
        mIconColor = a.getColor(R.styleable.ZzImageBox_zib_icon_color, DEFAULT_DEFAULT_ICON_COLOR);
        mPadding = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_img_padding, DEFAULT_IMAGE_PADDING);
        mPadding = mPadding / 2;
        mDefaultPicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_default, -1);
        mDeletePicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_delete, -1);
        mAddPicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_add, -1);
        if (a.hasValue(R.styleable.ZzImageBox_zib_img_scale_type)) {
            int value = a.getInt(R.styleable.ZzImageBox_zib_img_scale_type, 6);
            switch (value) {
                case 0:
                    mImgScaleType = ImageView.ScaleType.MATRIX;
                    break;
                case 1:
                    mImgScaleType = ImageView.ScaleType.FIT_XY;
                    break;
                case 2:
                    mImgScaleType = ImageView.ScaleType.FIT_START;
                    break;
                case 3:
                    mImgScaleType = ImageView.ScaleType.FIT_CENTER;
                    break;
                case 4:
                    mImgScaleType = ImageView.ScaleType.FIT_END;
                    break;
                case 5:
                    mImgScaleType = ImageView.ScaleType.CENTER;
                    break;
                case 6:
                    mImgScaleType = ImageView.ScaleType.CENTER_CROP;
                    break;
                case 7:
                    mImgScaleType = ImageView.ScaleType.CENTER_INSIDE;
                    break;
            }
        }
        mDeletable = a.getBoolean(R.styleable.ZzImageBox_zib_img_deletable, DEFAULT_DELETABLE);
        mAddable = a.getBoolean(R.styleable.ZzImageBox_zib_img_addable, DEFAULT_DELETABLE);
        a.recycle();
        
        initData(context);
    }
    
    
    private void initData(final Context context) {
        mDataSource = new ArrayList<>();
        setHasFixedSize(true);
        setLayoutManager(new GridLayoutManager(context, mOneLineImgCount));
        setPadding(mLeftMargin, 0, mRightMargin, 0);
        mAdapter = new MyAdapter(context, getBoxWidth(), mDataSource, mImgScaleType, mOneLineImgCount, mMaxImgCount, mDefaultPicId,
            mDeletePicId, mAddPicId, mDeletable, mAddable, mPadding, mLeftMargin, mRightMargin,
            mMaxLine, mIconColor, mClickListener, mOnlineImageLoader);
        if (sGlobalOnLineImageLoader != null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        setAdapter(mAdapter);
    }
    
    /**
     * 设置图片加载器
     */
    public ZzImageBox setOnlineImageLoader(OnlineImageLoader onlineImageLoader) {
        this.mOnlineImageLoader = onlineImageLoader;
        if (mAdapter != null) {
            mAdapter.setImageLoader(onlineImageLoader);
        }
        return this;
    }
    
    /**
     * 设置全局图片加载器
     */
    public static void setGlobalOnLineImageLoader(OnlineImageLoader onlineImageLoader) {
        sGlobalOnLineImageLoader = onlineImageLoader;
    }
    
    /**
     * 设置图片点击或长按事件
     *
     * @param mClickListener AbsOnImageClickListener
     * @return ZzImageBox
     */
    public ZzImageBox setOnImageClickListener(AbsOnImageClickListener mClickListener) {
        this.mClickListener = mClickListener;
        mAdapter.mListener = mClickListener;
        return this;
    }
    
    /**
     * 设置占位图片资源Id
     */
    public ZzImageBox setDefaultPicId(@DrawableRes int defaultPicId) {
        return setDefaultPicId(defaultPicId, true);
    }
    
    public ZzImageBox setDefaultPicId(@DrawableRes int defaultPicId, boolean notify) {
        this.mDefaultPicId = defaultPicId;
        if (mAdapter != null) {
            mAdapter.mDefaultPicId = defaultPicId;
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    /**
     * 设置删除图片资源id
     */
    public ZzImageBox setDeletePicId(@DrawableRes int deletePicId) {
        return setDeletePicId(deletePicId, true);
    }
    
    /**
     * 设置删除图片资源id
     */
    public ZzImageBox setDeletePicId(@DrawableRes int deletePicId, boolean notify) {
        this.mDeletePicId = deletePicId;
        if (mAdapter != null) {
            mAdapter.mDeletePicId = mDeletePicId;
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    /**
     * 设置新增图片资源id
     */
    public ZzImageBox setAddPicId(@DrawableRes int addPicId) {
        return setAddPicId(addPicId, true);
    }
    
    /**
     * 设置新增图片资源id
     */
    public ZzImageBox setAddPicId(@DrawableRes int addPicId, boolean notify) {
        this.mAddPicId = addPicId;
        if (mAdapter != null) {
            mAdapter.mAddPicId = mAddPicId;
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    /**
     * 设置是否显示新增图片
     *
     * @param addable 是否显示
     * @return ZzImageBox
     */
    public ZzImageBox setAddable(boolean addable) {
        return setAddable(addable, true);
    }
    
    /**
     * 设置是否显示新增图片
     *
     * @param addable 是否显示
     * @param notify  是否刷新
     * @return ZzImageBox
     */
    public ZzImageBox setAddable(boolean addable, boolean notify) {
        this.mAddable = addable;
        if (mAdapter != null) {
            mAdapter.mAddable = addable;
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    /**
     * 设置是否显示删除图片
     *
     * @param deletable 是否显示
     * @return ZzImageBox
     */
    public ZzImageBox setDeletable(boolean deletable) {
        return setDeletable(deletable, true);
    }
    
    /**
     * 设置是否显示删除图片
     *
     * @param deletable 是否显示
     * @param notify    是否刷新
     * @return ZzImageBox
     */
    public ZzImageBox setDeletable(boolean deletable, boolean notify) {
        this.mDeletable = deletable;
        if (mAdapter != null) {
            mAdapter.mDeletable = deletable;
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    /**
     * 设置新增图片的渲染颜色
     */
    public ZzImageBox setIconColor(int iconColor) {
        return setIconColor(iconColor, true);
    }
    
    /**
     * 设置新增图片的渲染颜色
     */
    public ZzImageBox setIconColor(int iconColor, boolean notify) {
        this.mIconColor = iconColor;
        if (mAdapter != null) {
            mAdapter.setIconColor(iconColor);
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    /**
     * 设置数据源
     */
    public ZzImageBox setDataSource(List<ImageEntity> dataSource) {
        return setDataSource(dataSource, true);
    }
    
    /**
     * 设置数据源
     */
    public ZzImageBox setDataSource(List<ImageEntity> dataSource, boolean notify) {
        this.mDataSource = dataSource;
        if (mAdapter != null) {
            mAdapter.setDataSource(mDataSource);
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    public int getBoxWidth() {
        return getMeasuredWidth();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAdapter.onConfigurationChanged(w);
    }
    
    /**
     * 新增本地图片
     *
     * @param imgFilePath the path of image.
     */
    public ZzImageBox addImageLocal(@NonNull String imgFilePath) {
        return addImageLocal(imgFilePath, true);
    }
    
    /**
     * 新增本地图片
     *
     * @param imgFilePath the path of image.
     * @param notify      是否立即刷新.
     */
    public ZzImageBox addImageLocal(@NonNull String imgFilePath, boolean notify) {
        if (mDataSource != null && mDataSource.size() < getMaxCount()) {
            ImageEntity entity = new ImageEntity();
            entity.setPicUrl(imgFilePath);
            entity.setOnLine(false);
            this.mDataSource.add(entity);
        }
        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }
    
    public ZzImageBox swapPositionWithLeft(int position) {
        return swapPositionWithLeft(position, true);
    }
    
    /**
     * 图片向左移动
     *
     * @param position 位置
     */
    public ZzImageBox swapPositionWithLeft(int position, boolean notify) {
        if (position < 2 || position >= mDataSource.size() - 1) {
            return this;
        }
        Collections.swap(mDataSource, position, position - 1);
        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }
    
    /**
     * 图片向右移动
     *
     * @param position 位置
     */
    public ZzImageBox swapPositionWithRight(int position) {
        return swapPositionWithRight(position, true);
    }
    
    /**
     * 图片向右移动
     *
     * @param position 位置
     * @param notify   是否刷新
     */
    public ZzImageBox swapPositionWithRight(int position, boolean notify) {
        if (position < 0 || position >= mDataSource.size() - 2) {
            return this;
        }
        Collections.swap(mDataSource, position, position + 1);
        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }
    
    /**
     * 新增本地图片，带真实路径和类型(用于视频预览图及视频真实播放地址）
     */
    public ZzImageBox addLocalImageWithRealPathAndType(@NonNull String imagePath, String realPath, int realType) {
        return addLocalImageWithRealPathAndType(imagePath, realPath, realType, null);
    }
    
    /**
     * 新增本地图片，带真实路径和类型，以及标签(用于视频预览图及视频真实播放地址，视频标签）
     */
    public ZzImageBox addLocalImageWithRealPathAndType(@NonNull String imagePath, String realPath, int realType, String tag) {
        if (mDataSource != null && mDataSource.size() < getMaxCount()) {
            ImageEntity entity = new ImageEntity();
            entity.setPicUrl(imagePath);
            entity.setRealPath(realPath);
            entity.setRealType(realType);
            entity.setOnLine(false);
            entity.setTag(tag);
            this.mDataSource.add(entity);
        }
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    /**
     * 新增网络图片Uri
     */
    public ZzImageBox addImageOnline(@NonNull Uri imgUri) {
        if (sGlobalOnLineImageLoader != null && mAdapter.mImageLoader == null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        if (mDataSource != null && mDataSource.size() < getMaxCount()) {
            ImageEntity entity = new ImageEntity();
            entity.setPicUri(imgUri);
            entity.setOnLine(true);
            this.mDataSource.add(entity);
        }
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    /**
     * 新增网络图片url
     */
    public ZzImageBox addImageOnline(@NonNull String imgUrl) {
        if (sGlobalOnLineImageLoader != null && mAdapter.mImageLoader == null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        if (mDataSource != null && mDataSource.size() < getMaxCount()) {
            ImageEntity entity = new ImageEntity();
            entity.setPicUrl(imgUrl);
            entity.setOnLine(true);
            this.mDataSource.add(entity);
        }
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    /**
     * 新增网络图片，带真实路径和类型(用于视频预览图及视频真实播放地址）
     */
    public ZzImageBox addImageOnlineWithRealPathAndType(@NonNull String imagePath, String realPath, int realType) {
        return addImageOnlineWithRealPathAndType(imagePath, realPath, realType, null);
    }
    
    /**
     * 新增网络图片，带真实路径和类型，以及标签(用于视频预览图及视频真实播放地址，视频标签）
     */
    public ZzImageBox addImageOnlineWithRealPathAndType(@NonNull String imagePath, String realPath, int realType, String tag) {
        if (sGlobalOnLineImageLoader != null && mAdapter.mImageLoader == null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        if (mDataSource != null && mDataSource.size() < getMaxCount()) {
            ImageEntity entity = new ImageEntity();
            entity.setPicUrl(imagePath);
            entity.setRealPath(realPath);
            entity.setRealType(realType);
            entity.setOnLine(true);
            entity.setTag(tag);
            this.mDataSource.add(entity);
        }
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    private int getMaxCount() {
        if (mMaxImgCount > 0) {
            return mMaxImgCount;
        }
        return mMaxLine * mOneLineImgCount;
    }
    
    /**
     * 移除某一张图片
     */
    public void removeImage(int position) {
        if (position < 0) {
            return;
        }
        if (mDataSource != null) {
            mDataSource.remove(position);
        }
        mAdapter.notifyDataSetChanged();
    }
    
    
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mAdapter != null) {
            if (mAdapter.mBoxWidth == 0) {
                mAdapter.mBoxWidth = getBoxWidth();
                mAdapter.setOneLineImgCount(mAdapter.mOneLineImgCount);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    
    public interface OnImageClickListener {
        
        void onImageClick(int position, String url, String realPath, int realType, ImageView iv, String tag);
        
        void onImageClick(int position, Uri uri, String realPath, int realType, ImageView iv, String tag);
        
        void onImageLongPress(int position, String url, String realPath, int realType, ImageView iv, String tag);
        
        void onImageLongPress(int position, Uri uri, String realPath, int realType, ImageView iv, String tag);
        
        void onDeleteClick(int position, String url, String realPath, int realType, String tag);
        
        void onDeleteClick(int position, Uri uri, String realPath, int realType, String tag);
        
        void onDeleteClick(ImageView ivPic, int position, String url, String realPath, int realType, String tag);
        
        void onDeleteClick(ImageView ivPic, int position, Uri uri, String realPath, int realType, String tag);
        
        void onAddClick();
        
        void onAddLongPress();
    }
    
    public static abstract class AbsOnImageClickListener implements OnImageClickListener {
        
        @Override
        public void onAddClick() {
        }
        
        @Override
        public void onAddLongPress() {
        }
        
        @Override
        public void onDeleteClick(int position, String url, String realPath, int realType, String tag) {
        }
        
        
        @Override
        public void onDeleteClick(int position, Uri uri, String realPath, int realType, String tag) {
        }
        
        
        @Override
        public void onDeleteClick(ImageView ivPic, int position, String url, String realPath, int realType, String tag) {
        }
        
        @Override
        public void onDeleteClick(ImageView ivPic, int position, Uri uri, String realPath, int realType, String tag) {
        
        }
        
        @Override
        public void onImageLongPress(int position, String url, String realPath, int realType, ImageView iv, String tag) {
        }
        
        @Override
        public void onImageLongPress(int position, Uri uri, String realPath, int realType, ImageView iv, String tag) {
        
        }
        
        @Override
        public void onImageClick(int position, Uri uri, String realPath, int realType, ImageView iv, String tag) {
        
        }
        
        @Override
        public void onImageClick(int position, String url, String realPath, int realType, ImageView iv, String tag) {
        
        }
    }
    
    
    /**
     * 移除所有图片
     */
    public void removeAllImages() {
        if (mDataSource != null) {
            mDataSource.clear();
        }
        mAdapter.notifyDataSetChanged();
    }
    
    
    /**
     * 设置图片缩放方式
     */
    public ZzImageBox setImageScaleType(ImageView.ScaleType scaleType) {
        return setImageScaleType(scaleType, true);
    }
    
    /**
     * 设置图片缩放方式
     */
    public ZzImageBox setImageScaleType(ImageView.ScaleType scaleType, boolean notify) {
        this.mImgScaleType = scaleType;
        if (mAdapter != null) {
            mAdapter.setImgScaleType(scaleType);
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    /**
     * 设置单行最多显示图片数量
     */
    public ZzImageBox setOneLineImgCount(int maxSize) {
        this.mOneLineImgCount = maxSize;
        if (mAdapter != null) {
            setLayoutManager(new GridLayoutManager(getContext(), maxSize));
            if (mAdapter == null) {
                mAdapter = new MyAdapter(getContext(), getBoxWidth(), mDataSource, mImgScaleType, mOneLineImgCount, mMaxImgCount,
                    mDefaultPicId, mDeletePicId, mAddPicId, mDeletable, mAddable, mPadding, mLeftMargin, mRightMargin, mMaxLine,
                    mIconColor, mClickListener, mOnlineImageLoader);
            } else {
                mAdapter.setOneLineImgCount(mOneLineImgCount);
            }
            if (sGlobalOnLineImageLoader != null && mAdapter.mImageLoader == null) {
                mAdapter.setImageLoader(sGlobalOnLineImageLoader);
            }
            setAdapter(mAdapter);
        }
        return this;
    }
    
    /**
     * 设置左边距
     */
    public ZzImageBox setLeftMarginInPixel(int leftMarginPx) {
        this.mLeftMargin = leftMarginPx;
        setPadding(this.mLeftMargin, 0, this.mRightMargin, 0);
        mAdapter.setLeftMargin(this.mLeftMargin);
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    /**
     * 设置右边距
     */
    public ZzImageBox setRightMarginInPixel(int rightMarginPx) {
        this.mRightMargin = rightMarginPx;
        setPadding(this.mLeftMargin, 0, this.mRightMargin, 0);
        if (mAdapter != null) {
            mAdapter.setRightMargin(this.mRightMargin);
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }
    
    /**
     * 设置图片之间的padding
     */
    public ZzImageBox setImagePadding(int imagePadding) {
        return setImagePadding(imagePadding, true);
    }
    
    /**
     * Set the padding of each one image.
     *
     * @param imagePadding padding value.
     */
    public ZzImageBox setImagePadding(int imagePadding, boolean notify) {
        this.mPadding = imagePadding / 2;
        if (mAdapter != null) {
            mAdapter.setImagePadding(this.mPadding);
            if (notify) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }
    
    
    /**
     * Return the image path of position.
     *
     * @param position position.
     * @return image path.
     */
    public String getImagePathAt(int position) {
        if (mDataSource != null && mDataSource.size() > position && position >= 0) {
            return mDataSource.get(position).getPicUrl();
        }
        return null;
    }
    
    /**
     * 返回图片Uri
     *
     * @param position 位置
     */
    public Uri getImageUriAt(int position) {
        if (mDataSource != null && mDataSource.size() > position && position >= 0) {
            return mDataSource.get(position).getPicUri();
        }
        return null;
    }
    
    /**
     * Return the custom path of position.
     *
     * @param position position.
     * @return custom path.
     */
    public String getRealPathAt(int position) {
        if (mDataSource != null && mDataSource.size() > position && position >= 0) {
            return mDataSource.get(position).getRealPath();
        }
        return null;
    }
    
    /**
     * Return the custom type of position.
     *
     * @param position position.
     * @return custom type, default = 0.
     */
    public int getRealTypeAt(int position) {
        if (mDataSource != null && mDataSource.size() > position && position >= 0) {
            return mDataSource.get(position).getRealType();
        }
        return 0;
    }
    
    /**
     * Return the custom type of position.
     *
     * @param position position.
     * @return custom type, default = 0.
     */
    public ImageEntity getEntityAt(int position) {
        if (mDataSource != null && mDataSource.size() > position && position >= 0) {
            return mDataSource.get(position);
        }
        return null;
    }
    
    
    /**
     * return all paths of images.
     *
     * @return paths of images
     */
    public List<String> getAllImages() {
        final List<String> allImages = new ArrayList<>();
        if (mDataSource != null) {
            for (ImageEntity mData : mDataSource) {
                allImages.add(mData.getPicUrl());
            }
        }
        return allImages;
    }
    
    /**
     * return all paths of images.
     *
     * @return paths of images
     */
    public List<Uri> getAllImageUri() {
        final List<Uri> allImages = new ArrayList<>();
        if (mDataSource != null) {
            for (ImageEntity mData : mDataSource) {
                allImages.add(mData.getPicUri());
            }
        }
        return allImages;
    }
    
    /**
     * Get all custom paths
     *
     * @return Custom path
     */
    public List<String> getAllRealPath() {
        final List<String> allImages = new ArrayList<>();
        if (mDataSource != null) {
            for (ImageEntity mData : mDataSource) {
                allImages.add(mData.getRealPath());
            }
        }
        return allImages;
    }
    
    /**
     * Get all custom types
     *
     * @return Custom type
     */
    public List<Integer> getAllRealType() {
        final List<Integer> types = new ArrayList<>();
        if (mDataSource != null) {
            for (ImageEntity mData : mDataSource) {
                types.add(mData.getRealType());
            }
        }
        return types;
    }
    
    /**
     * Get all custom tags
     *
     * @return Custom tag
     */
    public List<String> getAllTags() {
        final List<String> types = new ArrayList<>();
        if (mDataSource != null) {
            for (ImageEntity mData : mDataSource) {
                types.add(mData.getTag());
            }
        }
        return types;
    }
    
    /**
     * Get all entity classes
     *
     * @return Entity class
     */
    public List<ImageEntity> getAllEntity() {
        final List<ImageEntity> entities = new ArrayList<>();
        if (mDataSource != null) {
            entities.addAll(mDataSource);
        }
        return entities;
    }
    
    /**
     * 获取图片数量
     *
     * @return Total number of images.
     */
    public int getCount() {
        if (mDataSource != null) {
            return mDataSource.size();
        }
        return 0;
    }
    
    private static void setImageViewColor(ImageView icon, int color) {
        if (color == 0) {
            try {
                icon.setColorFilter(null);
            } catch (Exception ignored) {
            }
            return;
        }
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int a = Color.alpha(color);
        float[] colorMatrix = new float[]{0, 0, 0, 0, r, 0, 0, 0, 0, g, 0, 0, 0, 0, b, 0, 0, 0, (float) a / 255, 0};
        try {
            icon.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        } catch (Exception e) {
        }
    }
    
    
    private static class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        
        private static final int ITEM_TYPE_NORMAL = 0;
        private static final int ITEM_TYPE_ADD = 1;
        
        private Context mContext;
        private final LayoutInflater mInflater;
        private List<ImageEntity> mDataSource;
        private ImageView.ScaleType mImgScaleType = ImageView.ScaleType.CENTER_CROP;
        private int mDefaultPicId;
        private int mDeletePicId;
        private int mAddPicId;
        private boolean mDeletable;
        private boolean mAddable;
        private int mBoxWidth;
        private int mPadding;
        private int mPicWidth;
        private final int mMaxLine;
        private final int mMaxImgCount;
        private int mOneLineImgCount;
        private int mLeftMargin;
        private int mRightMargin;
        private int mIconColor;
        private OnImageClickListener mListener;
        private OnlineImageLoader mImageLoader;
        
        MyAdapter(Context context, int boxWidth, List<ImageEntity> mDatas, ImageView.ScaleType scaleType, int oneLineImgCount,
                  int maxImgCount, int defaultPicId, int deletePicId, int mAddPicId, boolean deletable, boolean addable, int padding,
                  int leftMargin, int rightMargin, int maxLine, int iconColor, OnImageClickListener listener, OnlineImageLoader imageLoader) {
            this.mContext = context;
            mInflater = LayoutInflater.from(context);
            this.mDataSource = mDatas;
            this.mImgScaleType = scaleType;
            this.mBoxWidth = boxWidth;
            this.mDefaultPicId = defaultPicId;
            this.mDeletePicId = deletePicId;
            this.mAddPicId = mAddPicId;
            this.mDeletable = deletable;
            this.mAddable = addable;
            this.mPadding = padding;
            this.mMaxLine = maxLine;
            this.mOneLineImgCount = oneLineImgCount;
            this.mMaxImgCount = maxImgCount;
            this.mLeftMargin = leftMargin;
            this.mRightMargin = rightMargin;
            this.mListener = listener;
            this.mImageLoader = imageLoader;
            this.mIconColor = iconColor;
            this.mPicWidth = (boxWidth - this.mLeftMargin - rightMargin) / oneLineImgCount;
        }
        
        @Override
        public int getItemViewType(int position) {
            if (position < mDataSource.size()) {
                return ITEM_TYPE_NORMAL;
            } else {
                return ITEM_TYPE_ADD;
            }
        }
        
        public void setImgScaleType(ImageView.ScaleType imgScaleType) {
            mImgScaleType = imgScaleType;
        }
        
        public OnlineImageLoader getImageLoader() {
            return mImageLoader;
        }
        
        public int getIconColor() {
            return mIconColor;
        }
        
        public void setIconColor(int iconColor) {
            this.mIconColor = iconColor;
        }
        
        public int getPicWidth() {
            return mPicWidth;
        }
        
        public int getPadding() {
            return mPadding;
        }
        
        void setLeftMargin(int leftMargin) {
            this.mLeftMargin = leftMargin;
            this.mPicWidth = (mBoxWidth - this.mLeftMargin - mRightMargin) / mOneLineImgCount;
        }
        
        void setRightMargin(int rightMargin) {
            this.mRightMargin = rightMargin;
            this.mPicWidth = (mBoxWidth - this.mLeftMargin - rightMargin) / mOneLineImgCount;
        }
        
        void setImagePadding(int padding) {
            this.mPadding = padding;
            this.mPicWidth = (mBoxWidth - this.mLeftMargin - mRightMargin) / mOneLineImgCount;
        }
        
        void setImageLoader(OnlineImageLoader imageLoader) {
            this.mImageLoader = imageLoader;
        }
        
        void setDataSource(List<ImageEntity> dataSource) {
            this.mDataSource = dataSource;
        }
        
        public void setOneLineImgCount(int oneLineImgCount) {
            this.mOneLineImgCount = oneLineImgCount;
            if (mOneLineImgCount != 0) {
                this.mPicWidth = (mBoxWidth - this.mLeftMargin - mRightMargin) / mOneLineImgCount;
            } else {
                this.mPicWidth = 0;
            }
        }
        
        public void onConfigurationChanged(int boxWidth) {
            this.mBoxWidth = boxWidth;
            if (mOneLineImgCount != 0) {
                this.mPicWidth = (boxWidth - this.mLeftMargin - mRightMargin) / mOneLineImgCount;
            } else {
                this.mPicWidth = 0;
            }
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.zz_image_box_item, parent, false);
            return new ViewHolder(itemView);
        }
        
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            ImageView iv = holder.itemView.findViewById(R.id.iv_pic);
            if (mPicWidth > 0) {
                ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
                layoutParams.width = mPicWidth;
                layoutParams.height = mPicWidth;
                iv.setLayoutParams(layoutParams);
            }
            ImageView ivDel = holder.itemView.findViewById(R.id.iv_delete);
            int size = mPicWidth / 3;
            if (size > 0) {
                ViewGroup.LayoutParams layoutParams = ivDel.getLayoutParams();
                layoutParams.width = size;
                layoutParams.height = size;
                ivDel.setLayoutParams(layoutParams);
            }
            View group = holder.itemView.findViewById(R.id.item_root);
            int w = group.getMeasuredWidth();
            int h = group.getMeasuredHeight();
            int type = getItemViewType(holder.getAdapterPosition());
            if (type == ITEM_TYPE_ADD) {
                holder.ivPic.setScaleType(ImageView.ScaleType.FIT_CENTER);
                holder.ivDelete.setVisibility(GONE);
                holder.ivPic.setImageResource(mAddPicId == -1 ? R.drawable.iv_add : mAddPicId);
                setImageViewColor(holder.ivPic, mIconColor);
                holder.ivPic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onAddClick();
                        }
                    }
                });
                holder.ivPic.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mListener != null) {
                            mListener.onAddLongPress();
                            return true;
                        }
                        return false;
                    }
                });
            } else {
                holder.ivPic.setScaleType(mImgScaleType);
                String url = mDataSource.get(holder.getAdapterPosition()).getPicUrl();
                Uri uri = mDataSource.get(holder.getAdapterPosition()).getPicUri();
                boolean forceOnLine = mDataSource.get(holder.getAdapterPosition()).isOnLine();
                
                if (url != null && url.length() != 0) {
                    if (url.startsWith("http") || forceOnLine) {
                        if (mImageLoader != null) {
                            mImageLoader.onLoadImage(mContext ,holder.ivPic, url, mPicWidth, mDefaultPicId == -1 ? R.drawable.iv_default : mDefaultPicId);
                        } else {
                            holder.ivPic.setImageResource(mDefaultPicId == -1 ? R.drawable.iv_default : mDefaultPicId);
                        }
                    } else {
                        holder.ivPic.setImageURI(uri);
                    }
                } else {
                    holder.ivPic.setImageResource(mDefaultPicId == -1 ? R.drawable.iv_default : mDefaultPicId);
                }
                setImageViewColor(holder.ivPic, 0);
                if (mDeletable) {
                    holder.ivDelete.setVisibility(VISIBLE);
                } else {
                    holder.ivDelete.setVisibility(GONE);
                }
                holder.ivDelete.setImageResource(mDeletePicId == -1 ? R.drawable.iv_delete : mDeletePicId);
                holder.ivDelete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            final int pos = holder.getAdapterPosition();
                            mListener.onDeleteClick(pos, mDataSource.get(pos).getPicUrl(),
                                mDataSource.get(pos).getRealPath(), mDataSource.get(pos).getRealType(),
                                mDataSource.get(pos).getTag());
                            mListener.onDeleteClick(holder.ivPic, pos, mDataSource.get(pos).getPicUrl(),
                                mDataSource.get(pos).getRealPath(), mDataSource.get(pos).getRealType(),
                                mDataSource.get(pos).getTag());
                        }
                    }
                });
                holder.ivPic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            final int pos = holder.getAdapterPosition();
                            mListener.onImageClick(pos, mDataSource.get(pos).getPicUrl(),
                                mDataSource.get(pos).getRealPath(), mDataSource.get(pos).getRealType(), holder.ivPic,
                                mDataSource.get(pos).getTag());
                        }
                    }
                });
                holder.ivPic.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mListener != null) {
                            final int pos = holder.getAdapterPosition();
                            mListener.onImageLongPress(pos, mDataSource.get(pos).getPicUrl(),
                                mDataSource.get(pos).getRealPath(), mDataSource.get(pos).getRealType(), holder.ivPic,
                                mDataSource.get(pos).getTag());
                            return true;
                        }
                        return false;
                    }
                });
            }
            holder.rootView.setPadding(
                showPaddingLeft(holder) ? mPadding : 0,
                showPaddingTop(holder) ? mPadding : 0,
                showPaddingRight(holder) ? mPadding : 0,
                showPaddingBottom(holder) ? mPadding : 0
            );
        }
        
        private boolean showPaddingTop(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition >= mOneLineImgCount;
        }
        
        private boolean showPaddingBottom(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            int lineCount = getMaxCount() / mOneLineImgCount;
            if (getMaxCount() % mOneLineImgCount != 0) {
                lineCount++;
            }
            return adapterPosition < (lineCount - 1) * mOneLineImgCount;
        }
        
        private boolean showPaddingLeft(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition % mOneLineImgCount != 0;
        }
        
        private boolean showPaddingRight(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return (adapterPosition + 1) % mOneLineImgCount != 0;
        }
        
        private boolean needShowAddItem() {
            return mAddable && mDataSource.size() < getMaxCount();
        }
        
        
        private int getMaxCount() {
            if (mMaxImgCount > 0) {
                return mMaxImgCount;
            }
            return mMaxLine * mOneLineImgCount;
        }
        
        
        @Override
        public int getItemCount() {
            int count = mDataSource == null ? 0 : mDataSource.size();
            return needShowAddItem() ? count + 1 : count;
        }
    }
    
    private static class ViewHolder extends RecyclerView.ViewHolder {
        
        private View rootView;
        private ImageView ivPic;
        private ImageView ivDelete;
        
        ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivPic = itemView.findViewById(R.id.iv_pic);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
    
    public static class ImageEntity {
        private String picUrl;
        private Uri picUri;
        private boolean onLine;
        private String realPath;
        private int realType;
        private String tag;
        
        public Uri getPicUri() {
            return picUri;
        }
        
        public void setPicUri(Uri picUri) {
            this.picUri = picUri;
            if (picUri != null) {
                this.picUrl = picUri.toString();
            }
        }
        
        public String getTag() {
            return tag;
        }
        
        public void setTag(String tag) {
            this.tag = tag;
        }
        
        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
            try {
                this.picUri = Uri.parse(picUrl);
            } catch (Exception ignored) {
            }
        }
        
        public String getPicUrl() {
            return picUrl;
        }
        
        public boolean isOnLine() {
            return onLine;
        }
        
        public void setOnLine(boolean onLine) {
            this.onLine = onLine;
        }
        
        public String getRealPath() {
            return realPath;
        }
        
        public void setRealPath(String realPath) {
            this.realPath = realPath;
        }
        
        public int getRealType() {
            return realType;
        }
        
        public void setRealType(int realType) {
            this.realType = realType;
        }
    }
    
    
}
