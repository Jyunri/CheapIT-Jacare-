package br.com.cdf.cheapit;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfferInformation2 extends Fragment {

    View rootview;
    public String offer_id = "";
    String json;
    ImageView ivOfferVoucher;
    Button btGetCoupon;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    public OfferInformation2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_offer_information, container, false);
        View header = inflater.inflate(R.layout.fragment_offer_information_header,null);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            offer_id = bundle.getString("offer_id", "0");
        }


        ivOfferVoucher = (ImageView)header.findViewById(R.id.ivOfferVoucher);

//        tvPartnerName = (TextView)header.findViewById(R.id.tvPartnerName);
//        tvPartnerAddress = (TextView)header.findViewById(R.id.tvPartnerAddress);
//        tvPartnerDescription = (TextView)header.findViewById(R.id.tvPartnerDescription);
//        tvPartnerFacebook = (TextView)header.findViewById(R.id.tvPartnerFacebook);
//

        expListView = (ExpandableListView) rootview.findViewById(R.id.lvOfferInformation);

        // Get Offer Information in a new thread
        new GetOfferInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"", offer_id);

        expListView.addHeaderView(header);
        //lvPartnerOffers.addHeaderView(test);


        btGetCoupon = (Button)rootview.findViewById(R.id.btGetCoupon);

        // Get Coupon in a second thread
        btGetCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Coupon for user",String.valueOf(LoginController.CurrentUserId));
                new GetCoupon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,String.valueOf(LoginController.CurrentUserId),offer_id);
            }
        });

        /* REFRESH BUTTON */
        ImageButton ibRefresh = (ImageButton)getActivity().findViewById(R.id.ibRefresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetOfferInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"", offer_id);
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
                Log.i("Offer Query",query);

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
                JSONArray offers_array = jsonObj.getJSONArray("offers_array");
                Log.i("Tamanho offer_array",String.valueOf(offers_array.length()));

                JSONObject jsonObject = offers_array.getJSONObject(0);

//                expires_at.setText("* Validade: "+offers_array.getJSONObject(0).getString("expires_at"));
//                partner.setText("* Loja: "+offers_array.getJSONObject(0).getString("partner_name"));
//                description.setText("* Descrição do cupom: "+offers_array.getJSONObject(0).getString("description"));
//                if(!offers_array.getJSONObject(0).getString("rules").isEmpty()){
//                    offer_rules.setText(offers_array.getJSONObject(0).getString("rules"));
//                }

                Glide.with(getContext()).load(jsonObject.getString("image")).into(ivOfferVoucher);
                ivOfferVoucher.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red));

                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String,List<String>>();

                listDataHeader.add("Informações Gerais");
                listDataHeader.add("Descrição do cupom");
                listDataHeader.add("Avisos e Regras");

                List<String> info = new ArrayList<String>();
                info.add(jsonObject.getString("partner_name"));
                listDataChild.put(listDataHeader.get(0),info);

                List<String> address = new ArrayList<String>();
                address.add(jsonObject.getString("description"));
                listDataChild.put(listDataHeader.get(1),address);

                List<String> rules = new ArrayList<String>();
                rules.add(jsonObject.getString("rules"));
                listDataChild.put(listDataHeader.get(2),rules);

                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

                //setting list adapter
                expListView.setAdapter(listAdapter);

                //expand first box by default
                expListView.expandGroup(0);


            }catch(Exception e){
                Log.e("erro",e.getMessage());
            }
            Log.i("Offer Result",result);

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
                url = new URL(LoginController.new_couponURL);

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
                        .appendQueryParameter("offer_id", params[1]);
                String query = builder.build().getEncodedQuery();
                Log.i("Offer Query",query);

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

            if(result.contains("success")){
                Toast.makeText(getContext(),"Cupom criado com sucesso! Verifique no seu PERFIL!",Toast.LENGTH_SHORT).show();
            }
            else if(result.contains("Duplicate entry")){
                Toast.makeText(getContext(),"Você já gerou esse cupom!",Toast.LENGTH_SHORT).show();
            }
            else if(result.contains("Esgotada")){
                Toast.makeText(getContext(),"Oferta Esgotada!",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(),"Desculpe! Cupom nao pôde ser criado..",Toast.LENGTH_SHORT).show();
            }

            Log.i("Result",result);

        }
    }
}
