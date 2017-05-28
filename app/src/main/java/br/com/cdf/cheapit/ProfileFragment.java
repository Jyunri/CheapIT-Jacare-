package br.com.cdf.cheapit;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment{

    private RadioGroup radioGroup1;
    TextView tvUsername,tvMyCouponsTitle, tvCheapitPoints;
    ImageView ivProfile;

    ListView lvCoupons;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mycoupons, container, false);
        View header = inflater.inflate(R.layout.fragment_profile,null);

        // Header (profile)
        ivProfile = (ImageView)header.findViewById(R.id.ivAvatar);
        Glide.with(getContext())
                .load(LoginController.getCurrentAvatar())
                .transform(new CircleTransform(getContext()))
                .override(220,140)
                .into(ivProfile);
        tvUsername = (TextView)header.findViewById(R.id.tvUsername);
        tvUsername.setText(LoginController.getCurrentUsername());


        tvCheapitPoints = (TextView)header.findViewById(R.id.tvCheapitPoints);

        radioGroup1 = (RadioGroup)header.findViewById(R.id.radioGroup1);
        tvMyCouponsTitle = (TextView)header.findViewById(R.id.tvTitle);

        // Checked change Listener for RadioGroup 1
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radioAtivos:
                        Toast.makeText(getContext(), "Cupons Ativos", Toast.LENGTH_SHORT).show();
                        new GetMyOffers().execute(String.valueOf(LoginController.CurrentUserId),"ORDERED","");
                        break;
                    case R.id.radioUsed:
                        Toast.makeText(getContext(), "Cupons Encerrados", Toast.LENGTH_SHORT).show();
                        new GetMyOffers().execute(String.valueOf(LoginController.CurrentUserId),"USED","");
                        break;
                    default:
                        break;
                }
            }
        });

        //Get offers from user_id
        new GetMyOffers().execute(String.valueOf(LoginController.CurrentUserId),"ORDERED","");

        /* REFRESH BUTTON */
        ImageButton ibRefresh = (ImageButton)getActivity().findViewById(R.id.ibRefresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetMyOffers().execute(String.valueOf(LoginController.CurrentUserId),"ORDERED","");
            }
        });

        //get listView reference
        lvCoupons = (ListView)rootView.findViewById(R.id.lvOffers);

        //Adding header to listview
        lvCoupons.addHeaderView(header);

        //eventos ao clicar nos itens da lista
        lvCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Toast.makeText(getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
            }

        });


        //retornar ao inicio da lista ao clicar no toolbar
        tvMyCouponsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvCoupons.setSelection(0);
            }
        });

        return rootView;
    }


    private class GetMyOffers extends AsyncTask<String,String,String> {
        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            try {
                pdLoading.setMessage("\tCarregando...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }catch (Exception e){
                Log.e("ProgressDialog",e.getMessage());
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(LoginController.my_couponURL);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_id", params[0])
                        .appendQueryParameter("status", params[1])
                        .appendQueryParameter("coupon_id", params[2]);
                String query = builder.build().getEncodedQuery();
                Log.i("Profile Query",query);

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    Toast.makeText(getContext(),"connectionbad",Toast.LENGTH_SHORT).show();
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            // Dismiss the progress dialog
            if (pdLoading.isShowing())
                pdLoading.dismiss();

            pdLoading = null;

            String json = result;

            //criar listas de itens
            ArrayList<Coupon> coupons = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray coupons_array = jsonObj.getJSONArray("coupons_array");
                Log.i("Tamanho do coupon_array",String.valueOf(coupons_array.length()));

                for (int j = 0; j < coupons_array.length(); j++) {
                    JSONObject c = coupons_array.getJSONObject(j);
                    Coupon coupon = new Coupon(c.getString("coupon_id"),c.getString("partner_name"),c.getString("description"),c.getString("coupon_code"),c.getString("image"));
                    coupons.add(coupon);
                }

            }catch(Exception e){
                Log.e("erro",e.getMessage());
            }
            Log.i("Result",result);

            ListAdapter listAdapter = new CouponListAdapter(getContext(),coupons);

            //setar o adapter da listview para o nosso adapter
            lvCoupons.setAdapter(listAdapter);


            //CLICKING ON LISTVIEW
            lvCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Coupon coupon =  (Coupon)parent.getItemAtPosition(position);
                    Toast.makeText(getContext(),coupon.partner,Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("coupon_id", coupon.id);
                    CouponInformation couponInformation = new CouponInformation();
                    couponInformation.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction couponInformationfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    couponInformationfragmentTransaction
                            .replace(R.id.fragment_container, couponInformation)
                            .addToBackStack(null)
                            .commit();
                }

            });
        }
    }
}
