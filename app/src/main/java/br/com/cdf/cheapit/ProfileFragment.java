package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    ImageView ivAvatar;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);
        if(FacebookController.LoginMethod.equals("facebook")) {
            new DownloadImage((ImageView) rootView.findViewById(R.id.ivAvatar)).execute(FacebookController.getCurrentAvatar());
        }
        //ivAvatar.setImageURI(getActivity().);
        //ivAvatar.setImageResource(getContext().getResources().getIdentifier("drawable/"+couponVoucherSrc,null,getContext().getPackageName()));
        return rootView;
    }

}
