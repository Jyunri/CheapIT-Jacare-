package br.com.cdf.cheapit;

/**
 * Created by Jimy on 11/30/16.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class AndroidImageAdapter extends PagerAdapter {
    Context mContext;
    private  int[] sliderImagesId;

    AndroidImageAdapter(Context context, int images[]) {
        this.mContext = context;
        sliderImagesId = images;
    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

//    private int[] images = new int[]{
//            R.drawable.slide1, R.drawable.slide1
//    };

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(mContext).load(sliderImagesId[i]).override(500,250).into(mImageView);
        //mImageView.setImageResource(sliderImagesId[i]);
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}
