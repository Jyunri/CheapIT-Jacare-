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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
public class OfferPoolFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    TextView tvMyCouponsTitle;
    ListView lvCoupons;
    String json;

    ImageButton ibSortMyCoupons, ibFilterMyCoupons;
    Spinner spSortMyCoupons, spFilterMyCoupons;

    public OfferPoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_coupons_pool, container, false);

        tvMyCouponsTitle = (TextView)rootView.findViewById(R.id.tvTitle);

        ibSortMyCoupons = (ImageButton) rootView.findViewById(R.id.ibSort);
        ibFilterMyCoupons =  (ImageButton) rootView.findViewById(R.id.ibFilter);
        spSortMyCoupons = (Spinner) rootView.findViewById(R.id.spSort);
        spFilterMyCoupons =  (Spinner) rootView.findViewById(R.id.spFilter);


        //Long pressed helpers
        ibSortMyCoupons.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Ordenar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        ibFilterMyCoupons.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Filtrar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ibSortMyCoupons.setOnClickListener(this);
        ibFilterMyCoupons.setOnClickListener(this);

        // Spinner click listener
        spSortMyCoupons.setOnItemSelectedListener(this);
        spFilterMyCoupons.setOnItemSelectedListener(this);


        // Spinner Drop down elements
        List<String> sortList = new ArrayList<String>();
        sortList.add("A-Z");
        sortList.add("Z-A");
        sortList.add("Data");
        sortList.add("Unidades");
        sortList.add("Maior desconto");

        List<String> filterList = new ArrayList<String>();
        filterList.add("Todos os cupons");
        filterList.add("Restaurantes");
        filterList.add("Lojas");
        filterList.add("Serviços");

        // Creating adapter for spinner
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sortList);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, filterList);

        // Drop down layout style - list view with radio button
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spSortMyCoupons.setAdapter(sortAdapter);
        spFilterMyCoupons.setAdapter(filterAdapter);
        

        //Recebe o arquivo json do banco
        new GetOffers().execute("","");

        //pegar referencia do listview
        lvCoupons = (ListView)rootView.findViewById(R.id.lvCoupons);

        tvMyCouponsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvCoupons.setSelection(0);
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibSort:
                spSortMyCoupons.performClick();
                break;
            case R.id.ibFilter:
                spFilterMyCoupons.performClick();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class GetOffers extends AsyncTask<String,String,String> {

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

            //criar listas de itens
            ArrayList<Offer> offers = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray coupons_array = jsonObj.getJSONArray("offers_array");
                Log.d("Tamanho do array",String.valueOf(coupons_array.length()));

                for (int j = 0; j < coupons_array.length(); j++) {
                    JSONObject c = coupons_array.getJSONObject(j);
                    Offer offer = new Offer(c.getString("id"),c.getString("partner_name"),c.getString("description"),c.getString("image"));
                    offers.add(offer);
                }

            }catch(Exception e){
                Log.d("erro",e.getMessage());
            }
            Log.d("Result",result);

            ListAdapter listAdapter = new OfferListAdapter(getContext(),offers);

            //setar o adapter da listview para o nosso adapter
            lvCoupons.setAdapter(listAdapter);


            //eventos ao clicar nos itens da lista
            lvCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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