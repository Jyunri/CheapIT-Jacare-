package br.com.cdf.cheapit;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfferPoolFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnTouchListener {

    TextView tvMyOffersTitle;
    ListView lvOffers;
    String json;

    SearchView svOffers;
    OfferListAdapter listAdapter;


    public OfferPoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_offers_pool, container, false);

        tvMyOffersTitle = (TextView)rootView.findViewById(R.id.tvTitle);

        //SEARCHVIEW
        // Get searchView reference
        svOffers = (SearchView)rootView.findViewById(R.id.svOffers);

        // Set searchView listeners to events
        svOffers.setOnQueryTextListener(this);


        //DATABASE
        // get offers JSON from database (no specific partner nor offer)
        new GetOffers().execute("","");

        /* REFRESH BUTTON */
        ImageButton ibRefresh = (ImageButton)getActivity().findViewById(R.id.ibRefresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetOffers().execute("","");
            }
        });

        //get listView reference
        lvOffers = (ListView)rootView.findViewById(R.id.lvOffers);

        tvMyOffersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvOffers.setSelection(0);
            }
        });


        //HIDE KEYBOARD IF BACKGROUND IS CLICKED
        rootView.setOnTouchListener(this);

        return rootView;
    }

    // TODO: 5/9/17 TRY TO MAKE SEARCHVIEW QUERY ON SQL
    //SEARCHVIEW LISTENERS
    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("OfferSearch","Submit "+query);
        listAdapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i("OfferSearch","TextChange "+newText);
        listAdapter.filter(newText);
        return false;
    }

    // TODO: 5/27/17 LOOK FOR BETTER WAY TO DISABLE SOFT KEYBOARD [BUG]
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        return true;
    }

    // TODO: 5/16/17 MAKE ALWAYS RETURN IN NEAREST LOCATION SORT 
    private class GetOffers extends AsyncTask<String,String,String> {

        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // TODO: 5/16/17 THIS IS CAUSING WINDOWLEAK [BUG]
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
            pdLoading = null;

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
            Log.i("Results",result);

            listAdapter = new OfferListAdapter(getContext(),offers);

            //set our custom listAdapter to listview
            lvOffers.setAdapter(listAdapter);


            // Check offer information by clicking on list elements
            lvOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    //HIDE SOFT KEYBOARD
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Offer offer =  (Offer)parent.getItemAtPosition(position);
                    Toast.makeText(getContext(),offer.partner,Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("offer_id", offer.id);
                    OfferInformation2 offerInformation = new OfferInformation2();
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
