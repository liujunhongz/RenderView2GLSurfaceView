package com.render.demo.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.Arrays;
import java.util.List;

public class PhotoViewPager extends BaseViewPager {

    protected Context mContext;

    public PhotoViewPager(Context context) {
        this(context, null);
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        if (isInEditMode()) {
            setBackgroundColor(getResources().getColor(android.R.color.black));
            return;
        }
//		setPageTransformer(true, new StackTransformer());
    }

    public void initAdapter(List<String> images) {
        setAdapter(new PhotoPagerAdapter(mContext, images));
    }

    public void initAdapter(String... images) {
        List<String> imgs = Arrays.asList(images);
        setAdapter(new PhotoPagerAdapter(mContext, imgs));
    }

    /**
     * PDFViewPager uses PhotoView, so this bugfix should be added
     * Issue explained in https://github.com/chrisbanes/PhotoView
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
