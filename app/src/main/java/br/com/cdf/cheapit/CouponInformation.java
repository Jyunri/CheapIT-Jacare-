package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CouponInformation extends Fragment {

    public String couponOfferId = "", couponVoucherSrc = "";
    ImageView couponVoucher;

    public CouponInformation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_coupon_information, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            couponOfferId = bundle.getString("couponOfferId", "0");
            couponVoucherSrc = bundle.getString("couponVoucher", "0");
        }

        couponVoucher = (ImageView)rootview.findViewById(R.id.ivCouponVoucher);
        couponVoucher.setImageResource(getContext().getResources().getIdentifier("drawable/"+couponVoucherSrc,null,getContext().getPackageName()));

        return rootview;
    }

}
