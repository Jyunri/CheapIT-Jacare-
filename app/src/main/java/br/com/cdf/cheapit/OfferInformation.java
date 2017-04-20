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
import android.widget.Button;
import android.widget.ImageView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class OfferInformation extends Fragment {

    public String offer_id = "", couponVoucherSrc = "";
    ImageView offerVoucher;
    TextView expires_at, partner ,description, offer_rules;
    String json;
    Button btGetCoupon;

    public OfferInformation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_coupon_information, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            offer_id = bundle.getString("offer_id", "0");
        }

        offerVoucher = (ImageView)rootview.findViewById(R.id.ivCouponVoucher);
        expires_at = (TextView)rootview.findViewById(R.id.tv_expires_at);
        partner = (TextView)rootview.findViewById(R.id.tv_partner);
        description = (TextView)rootview.findViewById(R.id.tv_description);
        offer_rules = (TextView)rootview.findViewById(R.id.tv_rules);
        btGetCoupon = (Button)rootview.findViewById(R.id.btGetCoupon);

        // Get Offer Information
        new GetOfferInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"", offer_id);

        btGetCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetCoupon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,LoginController.CurrentUserId,offer_id);
            }
        });

        return rootview;
    }

    private class GetOfferInformation extends AsyncTask<String,String,String> {

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
                url = new URL(LoginController.offerURL);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
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
                        .appendQueryParameter("partner_id", params[0])
                        .appendQueryParameter("offer_id", params[1]);
                String query = builder.build().getEncodedQuery();
                Log.d("Query",query);

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
                // TODO Auto-generated catch block
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
                JSONArray offers_array = jsonObj.getJSONArray("offers_array");
                Log.d("Tamanho do array",String.valueOf(offers_array.length()));


                String image = offers_array.getJSONObject(0).getString("image");
                Glide.with(getContext()).load(image).into(offerVoucher);
                //offerVoucher.setImageResource(getContext().getResources().getIdentifier("drawable/"+image,null,getContext().getPackageName()));
                expires_at.setText("* Validade: "+offers_array.getJSONObject(0).getString("expires_at"));
                partner.setText("* Loja: "+offers_array.getJSONObject(0).getString("partner_name"));
                description.setText("* Descrição do cupom: "+offers_array.getJSONObject(0).getString("description"));
                if(!offers_array.getJSONObject(0).getString("rules").isEmpty()){
                    offer_rules.setText(offers_array.getJSONObject(0).getString("rules"));
                }

            }catch(Exception e){
                Log.d("erro",e.getMessage());
            }
            Log.d("Result",result);

        }
    }

    private class GetCoupon extends AsyncTask<String,String,String> {

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
                url = new URL("https://cheapit.000webhostapp.com/page_json_new_coupon_beta.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
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
                        .appendQueryParameter("offer_id", params[1]);
                String query = builder.build().getEncodedQuery();
                Log.d("Query",query);

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
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Toast.makeText(getContext(),"e1",Toast.LENGTH_SHORT).show();
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

            if(result.contains("Error")){
                Toast.makeText(getContext(),"Deu ruim. dev: Tem que tratar qual erro foi!",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(),"Cupom criado com sucesso!",Toast.LENGTH_SHORT).show();
            }
//            json = result;
//
//            try {
//                JSONObject jsonObj = new JSONObject(json);
//                // Getting JSON Array node
//                JSONArray coupons_array = jsonObj.getJSONArray("coupons_array");
//                Log.d("Tamanho do array",String.valueOf(coupons_array.length()));
//
//
//                String image = coupons_array.getJSONObject(0).getString("image");
//                offerVoucher.setImageResource(getContext().getResources().getIdentifier("drawable/"+image,null,getContext().getPackageName()));
//                expires_at.setText("* Validade: "+coupons_array.getJSONObject(0).getString("expires_at"));
//                partner.setText("* Loja: "+coupons_array.getJSONObject(0).getString("partner"));
//                description.setText("* Descrição do cupom: "+coupons_array.getJSONObject(0).getString("description"));
//                if(!coupons_array.getJSONObject(0).getString("rules").isEmpty()){
//                    offer_rules.setText(coupons_array.getJSONObject(0).getString("rules"));
//                }
//
//            }catch(Exception e){
//                Log.d("erro",e.getMessage());
//            }



            Log.d("Result",result);

        }
    }
}
