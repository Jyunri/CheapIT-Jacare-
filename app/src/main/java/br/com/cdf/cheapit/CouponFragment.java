package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment implements View.OnClickListener{


    Button btAvailableCoupons, btMyCoupons;

    public CouponFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_coupon, container, false);

        btAvailableCoupons = (Button)rootview.findViewById(R.id.btAvailableCoupons);
        btMyCoupons = (Button)rootview.findViewById(R.id.btMyCoupons);

        btAvailableCoupons.setOnClickListener(this);
        btMyCoupons.setOnClickListener(this);

        return rootview;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //// TODO: 12/28/16 adicionar os cupons disponiveis
            case R.id.btAvailableCoupons:
                CouponsPoolFragment couponsPoolFragment = new CouponsPoolFragment();
                android.support.v4.app.FragmentTransaction couponpoolfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                couponpoolfragmentTransaction
                        .replace(R.id.fragment_container, couponsPoolFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btMyCoupons:
                MyCouponsFragment myCouponsFragment = new MyCouponsFragment();
                android.support.v4.app.FragmentTransaction mycouponfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                mycouponfragmentTransaction
                        .replace(R.id.fragment_container, myCouponsFragment)
                        .addToBackStack(null)
                        .commit();
                break;

        }
    }
}
