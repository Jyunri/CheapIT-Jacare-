package br.com.cdf.cheapit;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.glxn.qrgen.android.QRCode;

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
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CouponInformation extends Fragment {

    View rootview;
    public String coupon_id = "";
    String json;
    ImageView ivOfferVoucher;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    ImageView ivQrcode;


    public CouponInformation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_coupon_information2, container, false);
        View header = inflater.inflate(R.layout.fragment_coupon_information_header,null);
        View footer = inflater.inflate(R.layout.fragment_coupon_information_footer,null);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            coupon_id = bundle.getString("coupon_id", "0");
        }


        ivOfferVoucher = (ImageView)header.findViewById(R.id.ivOfferVoucher);
        ivQrcode =(ImageView) footer.findViewById(R.id.ivQrcode);


//        tvPartnerName = (TextView)header.findViewById(R.id.tvPartnerName);
//        tvPartnerAddress = (TextView)header.findViewById(R.id.tvPartnerAddress);
//        tvPartnerDescription = (TextView)header.findViewById(R.id.tvPartnerDescription);
//        tvPartnerFacebook = (TextView)header.findViewById(R.id.tvPartnerFacebook);
//

        expListView = (ExpandableListView) rootview.findViewById(R.id.lvCouponInformation);


        // Get Offer Information in a new thread
        new GetCouponInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"","ORDERED", coupon_id);

        expListView.addHeaderView(header);
        expListView.addFooterView(footer);
        //lvPartnerOffers.addHeaderView(test);


        /* REFRESH BUTTON */
        ImageButton ibRefresh = (ImageButton)getActivity().findViewById(R.id.ibRefresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetCouponInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"","ORDERED", coupon_id);
            }
        });

        return rootview;
    }

    private class GetCouponInformation extends AsyncTask<String,String,String> {

        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(getContext());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tCarregando...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(LoginController.my_couponURL);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getContext(),"Malformed",Toast.LENGTH_SHORT).show();
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
                Log.i("Coupon Query",query);

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

            json = result;

            try {
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray coupon_array = jsonObj.getJSONArray("coupons_array");
                Log.i("Tamanho coupon_array",String.valueOf(coupon_array.length()));

                JSONObject jsonObject = coupon_array.getJSONObject(0);

//                expires_at.setText("* Validade: "+offers_array.getJSONObject(0).getString("expires_at"));
//                partner.setText("* Loja: "+offers_array.getJSONObject(0).getString("partner_name"));
//                description.setText("* Descrição do cupom: "+offers_array.getJSONObject(0).getString("description"));
//                if(!offers_array.getJSONObject(0).getString("rules").isEmpty()){
//                    offer_rules.setText(offers_array.getJSONObject(0).getString("rules"));
//                }

                // TODO: 5/28/17 CHANGE IN DATABASE IMAGE -> VOUCHER
                Glide.with(getContext()).load(jsonObject.getString("image")).into(ivOfferVoucher);
                ivOfferVoucher.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red));

                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String,List<String>>();

                listDataHeader.add("Informações Gerais");
                listDataHeader.add("Descrição do Cupom");
                listDataHeader.add("Avisos e Regras");
                listDataHeader.add("Código do Cupom");



                //OFFER DETAILS
                String
                        partner_name = jsonObject.getString("partner_name"),
                        expires_at = jsonObject.getString("expires_at"),
                        description = jsonObject.getString("description"),
                        custom_rules = jsonObject.getString("rules")
                ;

                //COUPON DETAILS
                String
                        coupon_code = jsonObject.getString("coupon_code");
                ;

                String default_rules = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

                List<String> info_array = new ArrayList<String>();
                info_array.add(partner_name+"\n* Válido até: "+expires_at);
                listDataChild.put(listDataHeader.get(0),info_array);

                List<String> description_array = new ArrayList<String>();
                description_array.add(description);
                listDataChild.put(listDataHeader.get(1),description_array);

                List<String> rules_array = new ArrayList<String>();
                if(custom_rules.isEmpty())  rules_array.add(default_rules);
                else    rules_array.add(custom_rules);
                listDataChild.put(listDataHeader.get(2),rules_array);

                List<String> coupon_code_array = new ArrayList<String>();
                coupon_code_array.add(coupon_code);
                listDataChild.put(listDataHeader.get(3),coupon_code_array);

                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

                //setting list adapter
                expListView.setAdapter(listAdapter);

                //expand first box by default
                expListView.expandGroup(0);

                // TODO: 5/28/17 ADD QR CODE BASED ON CODE STRING
                Bitmap qrBitmap = QRCode.from(coupon_code).bitmap();

                ivQrcode.setImageBitmap(qrBitmap);


            }catch(Exception e){
                Log.e("erro",e.getMessage());
            }
            Log.i("Offer Result",result);

        }
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
                Log.i("Coupon Query",query);

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
                    Coupon coupon = new Coupon(c.getString("id"),c.getString("partner_name"),c.getString("description"),c.getString("coupon_code"),c.getString("image"));
                    coupons.add(coupon);
                }

            }catch(Exception e){
                Log.e("erro",e.getMessage());
            }
            Log.i("Result",result);

        }
    }
}
