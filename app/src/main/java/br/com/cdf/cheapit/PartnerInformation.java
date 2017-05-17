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
import android.widget.ExpandableListView;
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
public class PartnerInformation extends Fragment {

    View rootview;
    public String partner_id = "";
    String json;
    ImageView ivPartnerLogo;
    TextView tvPartnerName, tvPartnerAddress, tvPartnerDescription, tvPartnerFacebook;
    ListView lvPartnerOffers;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public PartnerInformation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_partner_information, container, false);
        View header = inflater.inflate(R.layout.fragment_partner_information_header,null);
        View test = inflater.inflate(R.layout.fragment_prizes_header,null);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            partner_id = bundle.getString("partner_id", "0");
        }

        expListView = (ExpandableListView) header.findViewById(R.id.lvPartnerInformation);


        ivPartnerLogo = (ImageView)header.findViewById(R.id.ivPartnerLogo);

//        tvPartnerName = (TextView)header.findViewById(R.id.tvPartnerName);
//        tvPartnerAddress = (TextView)header.findViewById(R.id.tvPartnerAddress);
//        tvPartnerDescription = (TextView)header.findViewById(R.id.tvPartnerDescription);
//        tvPartnerFacebook = (TextView)header.findViewById(R.id.tvPartnerFacebook);
//

        // Get ListView reference
        lvPartnerOffers = (ListView)rootview.findViewById(R.id.lvPartnerOffers);

        // Get Partner Information in a new thread
        new GetPartnerInformation().execute(partner_id,"");

        lvPartnerOffers.addHeaderView(header);
        //lvPartnerOffers.addHeaderView(test);


        // Get Partner Offers in a second thread
        new GetPartnerOffers().execute(partner_id,"");

        return rootview;
    }

    private class GetPartnerInformation extends AsyncTask<String,String,String> {

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
                url = new URL(LoginController.partnerURL);

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
                Log.i("Partner Query",query);

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

            json = result;

            try {
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray coupons_array = jsonObj.getJSONArray("partners_array");
                Log.i("Tamanho do array",String.valueOf(coupons_array.length()));

                Glide.with(getContext()).load(coupons_array.getJSONObject(0).getString("logo_url")).override(200,120).into(ivPartnerLogo);

                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String,List<String>>();

                listDataHeader.add("Nome da Loja");
                listDataHeader.add("Endereco");
                listDataHeader.add("Facebook/Site");
                listDataHeader.add("Descrição");

                List<String> name = new ArrayList<String>();
                name.add(coupons_array.getJSONObject(0).getString("name"));
                listDataChild.put(listDataHeader.get(0),name);

                List<String> address = new ArrayList<String>();
                address.add(coupons_array.getJSONObject(0).getString("address"));
                listDataChild.put(listDataHeader.get(1),address);

                List<String> website = new ArrayList<String>();
                website.add(coupons_array.getJSONObject(0).getString("facebook"));
                listDataChild.put(listDataHeader.get(2),website);

                // TODO: 5/6/17 VERIFY MAX LENGTH (NUM OF CHARS) TO DESCRIPTION
                List<String> description = new ArrayList<String>();
                description.add(coupons_array.getJSONObject(0).getString("description"));
                listDataChild.put(listDataHeader.get(3),description);


                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

                //setting list adapter
                expListView.setAdapter(listAdapter);

            }catch(Exception e){
                //Log.e("erro",e.getMessage());
            }
            Log.i("Partner_array",result);

        }
    }

    private class GetPartnerOffers extends AsyncTask<String,String,String> {

        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(LoginController.offerURL);

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
                        .appendQueryParameter("partner_id", params[0])
                        .appendQueryParameter("offer_id", params[1]);
                String query = builder.build().getEncodedQuery();
                Log.i("Partner Query", query);

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

            json = result;

            //criar listas de itens
            ArrayList<Offer> offers = new ArrayList<>();


            try {
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray coupons_array = jsonObj.getJSONArray("offers_array");
                Log.i("Tamanho do offer_array", String.valueOf(coupons_array.length()));

                for (int j = 0; j < coupons_array.length(); j++) {
                    JSONObject c = coupons_array.getJSONObject(j);
                    Offer offer = new Offer(c.getString("id"), c.getString("partner_name"), c.getString("description"), c.getString("image"));
                    offers.add(offer);
                }

            } catch (Exception e) {
                Log.e("erro", e.getMessage());
            }
            Log.i("Result", result);

            //instanciar o nosso adapter enviando como argumento nossas listas ao construtor
            //ListAdapter listAdapter = new CouponListAdapter(getContext(), couponOffer_id, clientes,descricao, imagens);
            ListAdapter listAdapter = new OfferListAdapter(getContext(), offers);

            //setar o adapter da listview para o nosso adapter
            lvPartnerOffers.setAdapter(listAdapter);

            //eventos ao clicar nos itens da lista
            lvPartnerOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Offer offer =  (Offer)parent.getItemAtPosition(position);
                    Toast.makeText(getContext(),offer.partner,Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("offer_id", offer.id);
                    OfferInformation offerInformation = new OfferInformation();
                    offerInformation.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction couponInformationfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    couponInformationfragmentTransaction
                            .replace(R.id.fragment_container, offerInformation)
                            .addToBackStack(null)
                            .commit();
                }

            });
        }
    }
}
