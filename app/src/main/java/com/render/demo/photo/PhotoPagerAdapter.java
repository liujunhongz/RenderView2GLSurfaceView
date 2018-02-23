package com.render.demo.photo;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.render.demo.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * @author 诸葛不亮
 * @version 1.0
 * @description
 */

public class PhotoPagerAdapter extends PagerAdapter {
    //用于存储回收掉的View
    private List<WeakReference<View>> viewList;
    private LayoutInflater inflater;
    private List<String> images;
    private Context mContext;

    public PhotoPagerAdapter(Context context, List<String> images) {
        mContext = context;
        this.images = images;
        viewList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images == null ? 0 : images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        // 从废弃的里去取 取到则使用 取不到则创建
        if (viewList.size() > 0) {
            if (viewList.get(0) != null) {
                view = initView(viewList.get(0).get(), position);
                viewList.remove(0);
                container.addView(view);
                return view;
            }
        }
        view = initView(null, position);
        container.addView(view);
        return view;
    }

    private View initView(View view, int position) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_photo, null);
            viewHolder = new ViewHolder();
            viewHolder.mPhotoView = view.findViewById(R.id.item_photo_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        /**
         * 初始化数据
         */
        if (images != null && images.size() > 0) {
            String path = images.get(position);
            Uri uri = Uri.parse(path);
            if (path != null) {
                if (path.endsWith(".gif")) {
                    Glide.with(mContext)
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(viewHolder.mPhotoView);
                } else {
                    Glide.with(mContext)
                            .load(uri)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(viewHolder.mPhotoView);
                }
            }
        }
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        viewList.add(new WeakReference<>(view));
        object = null;
    }

    private class ViewHolder {
        PhotoView mPhotoView;
    }
}
