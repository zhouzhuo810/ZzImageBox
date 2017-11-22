package me.zhouzhuo.zzimagebox;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zz on 2016/10/9.
 */
public class ZzImageBox extends RecyclerView {

    private int mMaxLine;
    private int mImageSize;
    private int mPadding;
    private int mLeftMargin;
    private int mRightMargin;
    private int mDefaultPicId = -1;
    private int mDeletePicId = -1;
    private int mAddPicId = -1;
    private boolean mDeletable;
    private static final boolean DEFAULT_DELETABLE = true;
    private static final int DEFAULT_MAX_LINE = 1;
    private static final int DEFAULT_IMAGE_SIZE = 4;
    private static final int DEFAULT_IMAGE_PADDING = 5;

    private OnlineImageLoader onlineImageLoader;

    private List<ImageEntity> mDatas;
    private MyAdapter mAdapter;

    private OnImageClickListener mClickListener;

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
        void onLoadImage(ImageView iv, String url);
    }

    public void setOnlineImageLoader(OnlineImageLoader onlineImageLoader) {
        this.onlineImageLoader = onlineImageLoader;
        if (mAdapter != null) {
            mAdapter.setImageLoader(onlineImageLoader);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZzImageBox);
        mLeftMargin = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_left_margin, 0);
        mRightMargin = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_right_margin, 0);
        mMaxLine = a.getInteger(R.styleable.ZzImageBox_zib_max_line, DEFAULT_MAX_LINE);
        mImageSize = a.getInteger(R.styleable.ZzImageBox_zib_img_size_one_line, DEFAULT_IMAGE_SIZE);
        mPadding = a.getDimensionPixelSize(R.styleable.ZzImageBox_zib_img_padding, DEFAULT_IMAGE_PADDING);
        mDefaultPicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_default, -1);
        mDeletePicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_delete, -1);
        mAddPicId = a.getResourceId(R.styleable.ZzImageBox_zib_img_add, -1);
        mDeletable = a.getBoolean(R.styleable.ZzImageBox_zib_img_deletable, DEFAULT_DELETABLE);
        a.recycle();

        initData(context);
    }


    private void initData(Context context) {
        mDatas = new ArrayList<>();
        setHasFixedSize(true);
        setLayoutManager(new GridLayoutManager(context, mImageSize));
        setPadding(mLeftMargin, 0, mRightMargin, 0);
        mAdapter = new MyAdapter(context, mDatas, mImageSize, mDefaultPicId, mDeletePicId, mAddPicId, mDeletable, mPadding, mLeftMargin, mRightMargin, mMaxLine, mClickListener, onlineImageLoader);
        setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    public void setOnImageClickListener(OnImageClickListener mClickListener) {
        this.mClickListener = mClickListener;
        mAdapter.listener = mClickListener;
    }

    public void setmImageSize(int mImageSize) {
        this.mImageSize = mImageSize;
        mAdapter.setImageSize(mImageSize);
        mAdapter.notifyDataSetChanged();
    }

    public void setmDefaultPicId(int mDefaultPicId) {
        this.mDefaultPicId = mDefaultPicId;
        mAdapter.defaultPic = mDefaultPicId;
        mAdapter.notifyDataSetChanged();
    }

    public void setmDeletePicId(int mDeletePicId) {
        this.mDeletePicId = mDeletePicId;
        mAdapter.deletePic = mDeletePicId;
        mAdapter.notifyDataSetChanged();
    }

    public void setmAddPicId(int mAddPicId) {
        this.mAddPicId = mAddPicId;
        mAdapter.addPic = mAddPicId;
        mAdapter.notifyDataSetChanged();
    }

    public void setmDeletable(boolean mDeletable) {
        this.mDeletable = mDeletable;
        mAdapter.deletable = mDeletable;
        mAdapter.notifyDataSetChanged();
    }

    public void setmDatas(List<ImageEntity> mDatas) {
        this.mDatas = mDatas;
        mAdapter.setmDatas(mDatas);
        mAdapter.notifyDataSetChanged();
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void addImage(String imagePath) {
        if (mDatas != null) {
            if (mDatas.size() < mMaxLine * this.mImageSize) {
                mAdapter.lastOne = false;
                ImageEntity entity = new ImageEntity();
                entity.setPicFilePath(imagePath);
                entity.setAdd(false);
                this.mDatas.add(this.mDatas.size() - 1, entity);
            } else {
                mAdapter.lastOne = true;
                this.mDatas.get(this.mDatas.size() - 1).setAdd(false);
                this.mDatas.get(this.mDatas.size() - 1).setPicFilePath(imagePath);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void removeImage(int position) {
        if (mDatas != null) {
            if (position + 1 == mMaxLine * this.mImageSize && mAdapter.lastOne) {
                mAdapter.lastOne = false;
                mDatas.get(position).setAdd(true);
            } else {
                if (mDatas.size() == mMaxLine * this.mImageSize && !mDatas.get(mDatas.size() - 1).isAdd) {
                    mAdapter.lastOne = false;
                    ImageEntity entity = new ImageEntity();
                    entity.setAdd(true);
                    mDatas.add(entity);
                }
                mDatas.remove(position);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private static class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private LayoutInflater mInflater;
        private Context mContext;
        private List<ImageEntity> mDatas;
        private int defaultPic;
        private int deletePic;
        private int addPic;
        private boolean deletable;
        private int padding;
        private int picWidth;
        private int maxLine;
        private int imageSize;
        private int leftMargin;
        private int rightMargin;
        private boolean lastOne;
        private OnImageClickListener listener;
        private OnlineImageLoader imageLoader;

        public MyAdapter(Context context, List<ImageEntity> mDatas, int imageSize, int defaultPic, int deletePic, int addPic, boolean deletable, int padding, int leftMargin, int rightMargin, int maxLine, OnImageClickListener listener, OnlineImageLoader imageLoader) {
            mInflater = LayoutInflater.from(context);
            this.mContext = context;
            this.mDatas = mDatas;
            ImageEntity entity = new ImageEntity();
            entity.setAdd(true);
            this.mDatas.add(entity);
            this.defaultPic = defaultPic;
            this.deletePic = deletePic;
            this.addPic = addPic;
            this.deletable = deletable;
            this.padding = padding;
            this.maxLine = maxLine;
            this.imageSize = imageSize;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.lastOne = false;
            this.listener = listener;
            this.imageLoader = imageLoader;
            this.picWidth = (getScreenWidth(context) - leftMargin - rightMargin) / imageSize - padding * 2;
        }

        public void setImageLoader(OnlineImageLoader imageLoader) {
            this.imageLoader = imageLoader;
        }

        public void setmDatas(List<ImageEntity> mDatas) {
            this.mDatas = mDatas;
            if (mDatas != null && mDatas.size() < maxLine * this.imageSize) {
                ImageEntity entity = new ImageEntity();
                entity.setAdd(true);
                this.mDatas.add(entity);
            }
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
            if (imageSize != 0)
                this.picWidth = (getScreenWidth(mContext) - leftMargin - rightMargin) / imageSize - padding * 2;
            else
                this.picWidth = 0;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.zz_image_box_item, parent, false);
            ImageView iv = (ImageView) itemView.findViewById(R.id.iv_pic);
            iv.setLayoutParams(new RelativeLayout.LayoutParams(picWidth, picWidth));
            ImageView ivDel = (ImageView) itemView.findViewById(R.id.iv_delete);
            ivDel.getLayoutParams().width = picWidth / 3;
            ivDel.getLayoutParams().height = picWidth / 3;
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (holder.getAdapterPosition() == getItemCount() - 1 && !lastOne) {
                holder.ivDelete.setVisibility(GONE);
                holder.ivPic.setImageResource(addPic == -1 ? R.drawable.iv_add : addPic);
                holder.ivPic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onAddClick();
                        }
                    }
                });
            } else {
                String url = mDatas.get(holder.getAdapterPosition()).getPicFilePath();

                if (url != null && url.length() != 0) {
                    if (url.startsWith("http")) {
                        if (imageLoader != null) {
                            //FIXME by zhouzhuo 时间：2017/11/22 上午11:33 修改内容：添加网络图片加载器
                            imageLoader.onLoadImage(holder.ivPic, url);
                        } else {
                            holder.ivPic.setImageResource(defaultPic == -1 ? R.drawable.iv_default : defaultPic);
                        }
                    } else {
                        holder.ivPic.setImageURI(Uri.fromFile(new File(url)));
                    }
                } else {
                    holder.ivPic.setImageResource(defaultPic == -1 ? R.drawable.iv_default : defaultPic);
                }
                if (deletable) {
                    holder.ivDelete.setVisibility(VISIBLE);
                } else {
                    holder.ivDelete.setVisibility(GONE);
                }
                holder.ivDelete.setImageResource(deletePic == -1 ? R.drawable.iv_delete : deletePic);
                holder.ivDelete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onDeleteClick(holder.getAdapterPosition(), mDatas.get(holder.getAdapterPosition()).getPicFilePath());
                        }
                    }
                });
                holder.ivPic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onImageClick(holder.getAdapterPosition(), mDatas.get(holder.getAdapterPosition()).getPicFilePath(), holder.ivPic);
                        }
                    }
                });
            }
            holder.rootView.setPadding(padding, padding, padding, padding);
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }
    }

    public interface OnImageClickListener {
        void onImageClick(int position, String filePath, ImageView iv);

        void onDeleteClick(int position, String filePath);

        void onAddClick();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private View rootView;
        private ImageView ivPic;
        private ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivPic = (ImageView) itemView.findViewById(R.id.iv_pic);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }

    private static class ImageEntity {
        private String picFilePath;
        private boolean isAdd;

        public boolean isAdd() {
            return isAdd;
        }

        public void setAdd(boolean add) {
            isAdd = add;
        }

        public void setPicFilePath(String picFilePath) {
            this.picFilePath = picFilePath;
        }

        public String getPicFilePath() {
            return picFilePath;
        }

    }

    /**
     * return all paths of images.
     *
     * @return paths of images
     */
    public List<String> getAllImages() {
        List<String> allImages = new ArrayList<>();
        if (mDatas != null) {
            for (ImageEntity mData : mDatas) {
                if (!mData.isAdd) {
                    allImages.add(mData.getPicFilePath());
                }
            }
        }
        return allImages;
    }

    /**
     * remove all images.
     */
    public void removeAllImages() {
        if (mDatas != null) {
            mDatas.clear();
            ImageEntity entity = new ImageEntity();
            entity.setAdd(true);
            mDatas.add(entity);
            mAdapter.lastOne = false;
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * return the image path of position.
     *
     * @param position position.
     * @return image path.
     */
    public String getImagePathAt(int position) {
        if (mDatas != null && mDatas.size() > position) {
            return mDatas.get(position).getPicFilePath();
        }
        return null;
    }

}
