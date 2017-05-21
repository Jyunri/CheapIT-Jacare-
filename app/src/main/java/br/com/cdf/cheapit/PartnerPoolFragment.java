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


/**
 * A simple {@link Fragment} subclass.
 */
public class PartnerPoolFragment extends Fragment implements SearchView.OnQueryTextListener {

    TextView tvTitle;
    ListView lvPartners;
    String json;

    SearchView svPartner;
    PartnerListAdapter listAdapter;

    public PartnerPoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_partner_pool, container, false);

        tvTitle = (TextView)rootView.findViewById(R.id.tvTitle);

        //SEARCHVIEW
        // Get searchView reference
        svPartner = (SearchView)rootView.findViewById(R.id.svPartner);

        // Set searchView listeners to events
        svPartner.setOnQueryTextListener(this);

        //DATABASE
        //Recebe o arquivo json do banco
        new GetPartners().execute("","");

        /* REFRESH BUTTON */
        ImageButton ibRefresh = (ImageButton)getActivity().findViewById(R.id.ibRefresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetPartners().execute("","");
            }
        });

        //pegar referencia do listview
        lvPartners = (ListView)rootView.findViewById(R.id.lvPartners);


        //eventos ao clicar nos itens da lista
        lvPartners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
            }

        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvPartners.setSelection(0);
            }
        });

        return rootView;
    }


    // TODO: 5/8/17 VERIFY THE NEED OF TEXTCHANGE LISTENER [UX]
    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("Search","Submit "+query);
        listAdapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i("Search","TextChange "+newText);
        listAdapter.filter(newText);
        return false;
    }

    private class GetPartners extends AsyncTask<String,String,String> {

        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(getContext());
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
                url = new URL(LoginController.partnerURL);

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
            // Dismiss the progress dialog
            if (pdLoading.isShowing())
                pdLoading.dismiss();

            pdLoading = null;

            json = result;

            //criar listas de itens
            ArrayList<Partner> partners = new ArrayList<>();
            final ArrayList<String> couponOffer_id = new ArrayList<>();
            ArrayList<String> clientes = new ArrayList<>();
            ArrayList<String> descricao = new ArrayList<>();
            final ArrayList<String> imagens = new ArrayList<>();

            try {
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray partners_array = jsonObj.getJSONArray("partners_array");
                Log.i("Tamanho do array",String.valueOf(partners_array.length()));

                for (int j = 0; j < partners_array.length(); j++) {
                    JSONObject p = partners_array.getJSONObject(j);
                    Partner partner = new Partner(p.getString("id"),p.getString("name"),p.getString("address"),p.getString("latitude"),p.getString("longitude"));
                    partners.add(partner);
                }

            }catch(Exception e){
                Log.e("erro",e.getMessage());
            }
            Log.i("Partner pool",result);

            listAdapter = new PartnerListAdapter(getContext(),partners);

            //setar o adapter da listview para o nosso adapter
            lvPartners.setAdapter(listAdapter);


            //eventos ao clicar nos itens da lista
            lvPartners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Partner partner =  (Partner) parent.getItemAtPosition(position);
                    Toast.makeText(getContext(),partner.name,Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("partner_id", partner.id);
                    PartnerInformation partnerInformation = new PartnerInformation();
                    partnerInformation.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction partnerInformationfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    partnerInformationfragmentTransaction
                            .replace(R.id.fragment_container, partnerInformation)
                            .addToBackStack(null)
                            .commit();
                }

            });
        }
    }
}
