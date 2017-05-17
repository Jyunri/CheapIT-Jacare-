package br.com.cdf.cheapit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.InputStream;

/**
 * Created by Jimy on 1/18/17.
 */

public class ZOLDDownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Context mContext;

    public ZOLDDownloadImage(Context context, ImageView bmImage) {
        this.mContext  = context;
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        //bmImage.setImageBitmap(result);
        Glide.with(mContext).load(result).override(200,120).into(bmImage);
    }
}