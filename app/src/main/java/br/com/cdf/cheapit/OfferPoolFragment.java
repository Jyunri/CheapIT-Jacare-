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

    TextView tvMyOffersTitle;
    ListView lvOffers;
    String json;

    ImageButton ibSortMyOffers, ibFilterMyOffers;
    Spinner spSortMyOffers, spFilterMyOffers;

    public OfferPoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_offers_pool, container, false);

        tvMyOffersTitle = (TextView)rootView.findViewById(R.id.tvTitle);

        ibSortMyOffers = (ImageButton) rootView.findViewById(R.id.ibSort);
        ibFilterMyOffers =  (ImageButton) rootView.findViewById(R.id.ibFilter);
        spSortMyOffers = (Spinner) rootView.findViewById(R.id.spSort);
        spFilterMyOffers =  (Spinner) rootView.findViewById(R.id.spFilter);


        //Long pressed helpers
        ibSortMyOffers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Ordenar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        ibFilterMyOffers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Filtrar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ibSortMyOffers.setOnClickListener(this);
        ibFilterMyOffers.setOnClickListener(this);

        // Spinner click listener
        spSortMyOffers.setOnItemSelectedListener(this);
        spFilterMyOffers.setOnItemSelectedListener(this);


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
        spSortMyOffers.setAdapter(sortAdapter);
        spFilterMyOffers.setAdapter(filterAdapter);
        

        // get offers JSON from database (no specific partner nor offer)
        new GetOffers().execute("","");

        //get listView reference
        lvOffers = (ListView)rootView.findViewById(R.id.lvOffers);

        tvMyOffersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvOffers.setSelection(0);
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibSort:
                spSortMyOffers.performClick();
                break;
            case R.id.ibFilter:
                spFilterMyOffers.performClick();
                break;
        }
    }

    //TODO CHECK IF IS LEGACY
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
                e.printStackTrace();
                return "Bad URL";
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

                // Append parameters to URL (Wont be used)
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("partner_id", params[0])
                        .appendQueryParameter("offer_id", params[1]);
                String query = builder.build().getEncodedQuery();
                Log.i("Offers_Query",query);

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

            // create list of offers
            ArrayList<Offer> offers = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray offers_array = jsonObj.getJSONArray("offers_array");
                Log.i("Tamanho do offers_array",String.valueOf(offers_array.length()));

                for (int j = 0; j < offers_array.length(); j++) {
                    JSONObject o = offers_array.getJSONObject(j);
                    Offer offer = new Offer(o.getString("id"),o.getString("partner_name"),o.getString("description"),o.getString("image"));
                    offers.add(offer);
                }

            }catch(Exception e){
                Log.e("erro",e.getMessage());
            }
            Log.i("Result",result);

            ListAdapter listAdapter = new OfferListAdapter(getContext(),offers);

            //set our custom listAdapter to listview
            lvOffers.setAdapter(listAdapter);


            // Check offer information by clicking on list elements
            lvOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Offer offer =  (Offer)parent.getItemAtPosition(position);
                    Toast.makeText(getContext(),offer.partner,Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("offer_id", offer.id);
                    OfferInformation offerInformation = new OfferInformation();
                    offerInformation.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction offerInformationfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    offerInformationfragmentTransaction
                            .replace(R.id.fragment_container, offerInformation)
                            .addToBackStack(null)
                            .commit();
                }

            });
        }
    }
}
