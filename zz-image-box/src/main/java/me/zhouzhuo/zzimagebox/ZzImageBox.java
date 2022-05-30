package me.zhouzhuo.zzimagebox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
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
import androidx.core.view.ViewCompat;
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
    
    private static ImageLoader sGlobalOnLineImageLoader;
    
    private int mMaxLine;
    private int mMaxImgCount;
    private int mIconColor;
    private int mOneLineImgCount;
    private int mPadding;
    private int mDefaultPicId = -1;
    private int mDeletePicId = -1;
    private int mAddPicId = -1;
    private boolean mDeletable;
    private boolean mAddable;
    private ImageView.ScaleType mImgScaleType = ImageView.ScaleType.CENTER_CROP;
    
    private List<ImageEntity> mDataSource;
    private MyAdapter mAdapter;
    private ImageLoader mImageLoader;
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
    
    public interface ImageLoader {
        /**
         * 图片加载
         *
         * @param context     上下文
         * @param iv          ImageView
         * @param url         Url
         * @param imgSize     图片尺寸
         * @param placeHolder 占位图片
         */
        void onLoadImage(Context context, ImageView iv, @Nullable String url, int imgSize, int placeHolder);
    }
    
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZzImageBox);
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
        mAdapter = new MyAdapter(context, getBoxWidth(), mDataSource, mImgScaleType, mOneLineImgCount, mMaxImgCount, mDefaultPicId,
            mDeletePicId, mAddPicId, mDeletable, mAddable, mPadding, getPaddingStart(), getPaddingEnd(),
            mMaxLine, mIconColor, mClickListener, mImageLoader);
        if (sGlobalOnLineImageLoader != null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        setAdapter(mAdapter);
    }
    
    /**
     * 设置图片加载器
     */
    public ZzImageBox setImageLoader(ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
        if (mAdapter != null) {
            mAdapter.setImageLoader(imageLoader);
        }
        return this;
    }
    
    /**
     * 设置全局图片加载器
     */
    public static void setGlobalOnLineImageLoader(ImageLoader onlineImageLoader) {
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
     * 图片向左移动
     *
     * @param position 位置
     * @return ZzImageBox
     */
    public ZzImageBox swapPositionWithLeft(int position) {
        return swapPositionWithLeft(position, true);
    }
    
    /**
     * 图片向左移动
     *
     * @param position 位置
     */
    public ZzImageBox swapPositionWithLeft(int position, boolean notify) {
        if (position < 1 || position >= mDataSource.size()) {
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
        if (position < 0 || position >= mDataSource.size() - 1) {
            return this;
        }
        Collections.swap(mDataSource, position, position + 1);
        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }
    
    
    /**
     * 新增图片url
     */
    public ZzImageBox addImage(@NonNull String imgUrl) {
        return addImageWithArgs(imgUrl, null);
    }
    
    /**
     * 新增多个图片url
     */
    public ZzImageBox addImages(String... imgUrls) {
        if (sGlobalOnLineImageLoader != null && mAdapter.mImageLoader == null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        if (imgUrls != null) {
            for (String url : imgUrls) {
                if (mDataSource != null && mDataSource.size() < getMaxCount()) {
                    ImageEntity entity = new ImageEntity();
                    entity.setPicUrl(url);
                    this.mDataSource.add(entity);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    /**
     * 新增多个图片url
     */
    public ZzImageBox addImages(@NonNull List<String> imgUrls) {
        if (sGlobalOnLineImageLoader != null && mAdapter.mImageLoader == null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        if (imgUrls != null) {
            for (String url : imgUrls) {
                if (mDataSource != null && mDataSource.size() < getMaxCount()) {
                    ImageEntity entity = new ImageEntity();
                    entity.setPicUrl(url);
                    this.mDataSource.add(entity);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    
    /**
     * 新增图片，带参数
     */
    public ZzImageBox addImageWithArgs(@NonNull String imgUrl, @Nullable Bundle args) {
        if (sGlobalOnLineImageLoader != null && mAdapter.mImageLoader == null) {
            mAdapter.setImageLoader(sGlobalOnLineImageLoader);
        }
        if (mDataSource != null && mDataSource.size() < getMaxCount()) {
            ImageEntity entity = new ImageEntity();
            entity.setPicUrl(imgUrl);
            entity.setArgs(args);
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
    
    public interface OnImageClickListener {
        
        void onImageClick(int position, String url, ImageView iv, @Nullable Bundle args);
        
        void onImageLongPress(int position, String url, ImageView iv, @Nullable Bundle args);
        
        void onDeleteClick(ImageView iv, int position, String url, @Nullable Bundle args);
        
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
        public void onDeleteClick(ImageView iv, int position, String url, @Nullable Bundle args) {
        
        }
        
        @Override
        public void onImageLongPress(int position, String url, ImageView iv, @Nullable Bundle args) {
        
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
            // if (mItemDecoration == null) {
            //     mItemDecoration = new GridSpaceItemDecoration(maxSize, mPadding, mPadding);
            //     addItemDecoration(mItemDecoration);
            // } else {
            //     mItemDecoration.setSpanCount(maxSize);
            //     invalidateItemDecorations();
            // }
            if (mAdapter == null) {
                mAdapter = new MyAdapter(getContext(), getBoxWidth(), mDataSource, mImgScaleType, mOneLineImgCount, mMaxImgCount,
                    mDefaultPicId, mDeletePicId, mAddPicId, mDeletable, mAddable, mPadding, getPaddingStart(), getPaddingEnd(), mMaxLine,
                    mIconColor, mClickListener, mImageLoader);
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
     * 设置左padding
     *
     * @param padding 边距,单位px
     * @return ZzImageBox
     */
    public ZzImageBox setBoxLeftPadding(int padding) {
        if (mAdapter != null) {
            mAdapter.setLeftMargin(padding);
            mAdapter.notifyDataSetChanged();
        }
        setPadding(padding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        return this;
    }
    
    /**
     * 设置右padding
     *
     * @param padding 边距,单位px
     * @return ZzImageBox
     */
    public ZzImageBox setBoxRightPadding(int padding) {
        if (mAdapter != null) {
            mAdapter.setRightMargin(padding);
            mAdapter.notifyDataSetChanged();
        }
        setPadding(getPaddingLeft(), getPaddingTop(), padding, getPaddingBottom());
        return this;
    }
    
    /**
     * 设置上padding
     *
     * @param padding 边距,单位px
     * @return ZzImageBox
     */
    public ZzImageBox setBoxTopPadding(int padding) {
        setPadding(getPaddingLeft(), padding, getPaddingRight(), getPaddingBottom());
        return this;
    }
    
    /**
     * 设置下padding
     *
     * @param padding 边距,单位px
     * @return ZzImageBox
     */
    public ZzImageBox setBoxBottomPadding(int padding) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), padding);
        return this;
    }
    
    /**
     * 设置图片之间的间隙，单位px
     *
     * @param imagePadding padding value.
     */
    public ZzImageBox setImagePadding(int imagePadding) {
        this.mPadding = imagePadding / 2;
        if (mAdapter != null) {
            mAdapter.setImagePadding(this.mPadding);
            mAdapter.notifyDataSetChanged();
            // if (mItemDecoration == null) {
            //     mItemDecoration = new GridSpaceItemDecoration(mOneLineImgCount, mPadding, mPadding);
            //     addItemDecoration(mItemDecoration);
            // } else {
            //     mItemDecoration.setRowSpacing(mPadding);
            //     mItemDecoration.setColumnSpacing(mPadding);
            //     invalidateItemDecorations();
            // }
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
     * 获取自定义参数
     *
     * @param position position.
     * @return 自定义的参数
     */
    public Bundle getArgAt(int position) {
        if (mDataSource != null && mDataSource.size() > position && position >= 0) {
            return mDataSource.get(position).getArgs();
        }
        return null;
    }
    
    
    /**
     * 获取某个位置的图片实体类
     *
     * @param position position.
     * @return 实体类ImageEntity
     */
    public ImageEntity getEntityAt(int position) {
        if (mDataSource != null && mDataSource.size() > position && position >= 0) {
            return mDataSource.get(position);
        }
        return null;
    }
    
    
    /**
     * 获取所有图片url
     *
     * @return 所有图片url
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
     * 获取所有图片实体类
     *
     * @return 实体类ImageEntity集合
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
     * @return 图片数量
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
        
        private final Context mContext;
        private final LayoutInflater mInflater;
        private List<ImageEntity> mDataSource;
        private ImageView.ScaleType mImgScaleType;
        private int mDefaultPicId;
        private int mDeletePicId;
        private int mAddPicId;
        private boolean mDeletable;
        private boolean mAddable;
        private int mBoxWidth;
        private int mPadding;
        private int mPicSize;
        private final int mMaxLine;
        private final int mMaxImgCount;
        private int mOneLineImgCount;
        private int mLeftMargin;
        private int mRightMargin;
        private int mIconColor;
        private OnImageClickListener mListener;
        private ImageLoader mImageLoader;
        
        MyAdapter(Context context, int boxWidth, List<ImageEntity> dataSource, ImageView.ScaleType scaleType, int oneLineImgCount,
                  int maxImgCount, int defaultPicId, int deletePicId, int mAddPicId, boolean deletable, boolean addable, int padding,
                  int leftMargin, int rightMargin, int maxLine, int iconColor, OnImageClickListener listener, ImageLoader imageLoader) {
            this.mContext = context;
            mInflater = LayoutInflater.from(context);
            this.mDataSource = dataSource;
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
            this.mPicSize = (boxWidth - this.mLeftMargin - rightMargin - mPadding * (oneLineImgCount - 1)) / oneLineImgCount;
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
        
        public ImageLoader getImageLoader() {
            return mImageLoader;
        }
        
        public int getIconColor() {
            return mIconColor;
        }
        
        public void setIconColor(int iconColor) {
            this.mIconColor = iconColor;
        }
        
        public int getPicSize() {
            return mPicSize;
        }
        
        public int getPadding() {
            return mPadding;
        }
        
        void setLeftMargin(int leftMargin) {
            this.mLeftMargin = leftMargin;
            this.mPicSize = (mBoxWidth - this.mLeftMargin - mRightMargin - mPadding * (mOneLineImgCount - 1)) / mOneLineImgCount;
        }
        
        void setRightMargin(int rightMargin) {
            this.mRightMargin = rightMargin;
            this.mPicSize = (mBoxWidth - this.mLeftMargin - rightMargin - mPadding * (mOneLineImgCount - 1)) / mOneLineImgCount;
        }
        
        void setImagePadding(int padding) {
            this.mPadding = padding;
            this.mPicSize = (mBoxWidth - this.mLeftMargin - mRightMargin - mPadding * (mOneLineImgCount - 1)) / mOneLineImgCount;
        }
        
        void setImageLoader(ImageLoader imageLoader) {
            this.mImageLoader = imageLoader;
        }
        
        void setDataSource(List<ImageEntity> dataSource) {
            this.mDataSource = dataSource;
        }
        
        public void setOneLineImgCount(int oneLineImgCount) {
            this.mOneLineImgCount = oneLineImgCount;
            if (mOneLineImgCount != 0) {
                this.mPicSize = (mBoxWidth - this.mLeftMargin - mRightMargin - mPadding * (mOneLineImgCount - 1)) / mOneLineImgCount;
            } else {
                this.mPicSize = 0;
            }
        }
        
        public void onConfigurationChanged(int boxWidth) {
            this.mBoxWidth = boxWidth;
            if (mOneLineImgCount != 0) {
                this.mPicSize = (boxWidth - this.mLeftMargin - mRightMargin - mPadding * (mOneLineImgCount - 1)) / mOneLineImgCount;
            } else {
                this.mPicSize = 0;
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
            if (mPicSize > 0) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) holder.itemView.getLayoutParams();
                int col = position % mOneLineImgCount;
                int row = position / mOneLineImgCount;
                
                int totalRow = getItemCount() % mOneLineImgCount == 0 ? getItemCount() / mOneLineImgCount : getItemCount() / mOneLineImgCount + 1;
                
                //间距总和均分值
                int fen = (mPadding * (mOneLineImgCount - 1)) / mOneLineImgCount;
                
                boolean firstRow = row == 0;
                boolean lastRow = row == totalRow - 1;
                boolean firstCol = col == 0;
                boolean lastCol = col == mOneLineImgCount - 1;
                layoutParams.leftMargin = firstCol ? 0 : (lastCol ? fen : fen / 2);
                layoutParams.rightMargin = lastCol ? 0 : (firstCol ? fen : fen / 2);
                layoutParams.topMargin = firstRow ? 0 : (lastRow ? fen : fen / 2);
                layoutParams.bottomMargin = lastRow ? 0 : (firstRow ? fen : fen / 2);
                
                //左右间距均分修正
                if (mOneLineImgCount > 3) {
                    //第1个和第2个Item以及最后一个和倒数第2个Item之间的间隙比mPadding多出来的距离 / 2
                    int left = ((fen + fen / 2) - mPadding) / 2;
                    if (firstCol) {
                        layoutParams.rightMargin -= left;
                    } else if (lastCol) {
                        layoutParams.leftMargin -= left;
                    } else if (col == 1) {
                        layoutParams.leftMargin -= left;
                        layoutParams.rightMargin += left;
                    } else if (col == mOneLineImgCount - 2) {
                        layoutParams.rightMargin -= left;
                        layoutParams.leftMargin += left;
                    } else {
                        layoutParams.leftMargin += left;
                        layoutParams.rightMargin += left;
                    }
                }
                
                //上下间距均分修正
                if (totalRow > 3) {
                    //第1个和第2个Item以及最后一个和倒数第2个Item之间的间隙比mPadding多出来的距离 / 2
                    int surplus = ((fen + fen / 2) - mPadding) / 2;
                    if (firstRow) {
                        layoutParams.bottomMargin -= surplus;
                    } else if (lastRow) {
                        layoutParams.topMargin -= surplus;
                    } else if (row == 1) {
                        layoutParams.topMargin -= surplus;
                        layoutParams.bottomMargin += surplus;
                    } else if (row == totalRow - 2) {
                        layoutParams.bottomMargin -= surplus;
                        layoutParams.topMargin += surplus;
                    } else {
                        layoutParams.topMargin += surplus;
                        layoutParams.bottomMargin += surplus;
                    }
                }
                
                layoutParams.width = mPicSize;
                layoutParams.height = mPicSize;
                
                holder.itemView.setLayoutParams(layoutParams);
            }
            ImageView ivDel = holder.itemView.findViewById(R.id.iv_delete);
            int size = mPicSize / 3;
            if (size > 0) {
                ViewGroup.LayoutParams layoutParams = ivDel.getLayoutParams();
                layoutParams.width = size;
                layoutParams.height = size;
                ivDel.setLayoutParams(layoutParams);
            }
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
                
                if (mImageLoader != null) {
                    mImageLoader.onLoadImage(mContext, holder.ivPic, url, mPicSize, mDefaultPicId == -1 ? R.drawable.iv_default : mDefaultPicId);
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
                            final int pos = holder.getBindingAdapterPosition();
                            if (pos < 0) {
                                return;
                            }
                            mListener.onDeleteClick(holder.ivPic, pos, mDataSource.get(pos).getPicUrl(),
                                mDataSource.get(pos).getArgs());
                        }
                    }
                });
                holder.ivPic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            final int pos = holder.getBindingAdapterPosition();
                            if (pos < 0) {
                                return;
                            }
                            mListener.onImageClick(pos, mDataSource.get(pos).getPicUrl(), holder.ivPic,
                                mDataSource.get(pos).getArgs());
                        }
                    }
                });
                holder.ivPic.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mListener != null) {
                            final int pos = holder.getBindingAdapterPosition();
                            if (pos < 0) {
                                return true;
                            }
                            mListener.onImageLongPress(pos, mDataSource.get(pos).getPicUrl(), holder.ivPic,
                                mDataSource.get(pos).getArgs());
                            return true;
                        }
                        return false;
                    }
                });
            }
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
        private Bundle args;
        
        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
        
        public String getPicUrl() {
            return picUrl;
        }
        
        public Bundle getArgs() {
            return args;
        }
        
        public void setArgs(Bundle args) {
            this.args = args;
        }
    }
    
}