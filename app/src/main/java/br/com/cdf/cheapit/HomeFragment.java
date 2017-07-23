package br.com.cdf.cheapit;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    View root;
    String json;
    ListView lvExpiring;
    LinearLayout llMyCoupons, llOffers, llPlaces, llMap, llPoints, llHelp;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_feed,container,false);

        /* SLIDESHOW */
        int[] images = new int[]{
                R.drawable.slide1, R.drawable.slide2
        };

        // Images from SLIDESHOW
        ViewPager mViewPager = (ViewPager) root.findViewById(R.id.viewPageAndroid); // detalhe importante: troquei o rootview pelo root.find(...)
        AndroidImageAdapter adapterView = new AndroidImageAdapter(getContext(), images);
        mViewPager.setAdapter(adapterView);

        // Dots from SLIDESHOW
        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager);

        // Customizing #CORREQUEDATEMPO text
//        TextView tvCQDT = (TextView) root.findViewById(R.id.tvCQDT);
//        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/bebas.ttf");
//        tvCQDT.setTypeface(type);


        /* BUTTON GRID*/
        llMyCoupons = (LinearLayout)root.findViewById(R.id.llMyCoupons);
        llOffers = (LinearLayout)root.findViewById(R.id.llOffers);
        llPlaces = (LinearLayout)root.findViewById(R.id.llPlaces);
        llMap = (LinearLayout)root.findViewById(R.id.llMap);
        llPoints = (LinearLayout)root.findViewById(R.id.llPoints);
        llHelp = (LinearLayout)root.findViewById(R.id.llHelp);

        llMyCoupons.setOnClickListener(this);
        llOffers.setOnClickListener(this);
        llPlaces.setOnClickListener(this);
        llMap.setOnClickListener(this);
        llPoints.setOnClickListener(this);
        llHelp.setOnClickListener(this);

//         Get offers from database (#CORREQUEDATEMPO/expiring only)
//        new GetOffers().execute("","");

        /* REFRESH BUTTON */
        ImageButton ibRefresh = (ImageButton)getActivity().findViewById(R.id.ibRefresh);
//        ibRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new GetOffers().execute("","");
//            }
//        });

//        //Get listview reference
//        lvExpiring = (ListView)rootView.findViewById(R.id.lvHome);
//
//        //Adding root to listview
//        lvExpiring.addHeaderView(root);

        return root;
    }



    @Override
    public void onClick(View v) {
        Fragment fragment;
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.llMyCoupons:
                fragment = new ProfileFragment();
                fragmentTransaction
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
            case R.id.llOffers:
                fragment = new OfferPoolFragment();
                fragmentTransaction
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
            case R.id.llPlaces:
                fragment = new PartnerPoolFragment();
                fragmentTransaction
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
            case R.id.llMap:
                fragment = new MapFragment();
                fragmentTransaction
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
            case R.id.llPoints:
                fragment = new PointsFragment();
                fragmentTransaction
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
            case R.id.llHelp:
                fragment = new HelpFragment();
                fragmentTransaction
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
        }
    }

//    //MAJOR TODO: TRY TO SIMPLIFY ALL HTTP REQUESTS
//    private class GetOffers extends AsyncTask<String,String,String> {
//
//        public static final int CONNECTION_TIMEOUT=10000;
//        public static final int READ_TIMEOUT=15000;
//
//        ProgressDialog pdLoading = new ProgressDialog(getContext());
//        HttpURLConnection conn;
//        URL url = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            //this method will be running on UI thread
//            try {
//                pdLoading.setMessage("\tCarregando...");
//                pdLoading.setCancelable(false);
//                pdLoading.show();
//            }catch (Exception e){
//                Log.e("ProgressDialog",e.getMessage());
//            }
//
//        }
//
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                // Enter URL address where your php file resides
//                url = new URL(LoginController.offerURL);
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                return "Bad URL";
//            }
//            try {
//                // Setup HttpURLConnection class to send and receive data from php and mysql
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(READ_TIMEOUT);
//                conn.setConnectTimeout(CONNECTION_TIMEOUT);
//                conn.setRequestMethod("POST");
//
//                // setDoInput and setDoOutput method depict handling of both send and receive
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//
//                // Append parameters to URL (Wont be used)
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("partner_id", params[0])
//                        .appendQueryParameter("offer_id", params[1]);
//                String query = builder.build().getEncodedQuery();
//                Log.i("Home_Query",query);
//
//                // Open connection for sending data
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//                conn.connect();
//
//            } catch (IOException e1) {
//                e1.printStackTrace();
//                return "exception";
//            }
//
//            try {
//
//                int response_code = conn.getResponseCode();
//
//                // Check if successful connection made
//                if (response_code == HttpURLConnection.HTTP_OK) {
//
//                    // Read data sent from server
//                    InputStream input = conn.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                    }
//
//                    // Pass data to onPostExecute method
//                    return (result.toString());
//
//                } else {
//                    return ("unsuccessful");
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "exception";
//            } finally {
//                conn.disconnect();
//            }
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // Dismiss the progress dialog
//            if (pdLoading.isShowing())
//                pdLoading.dismiss();
//            pdLoading = null;
//            json = result;
//
//            // Create offer list
//            ArrayList<Offer> offers = new ArrayList<>();
//
//
//            try {
//                JSONObject jsonObj = new JSONObject(json);
//                // Getting JSON Array node
//                JSONArray offers_array = jsonObj.getJSONArray("offers_array");
//                Log.i("Tamanho do offer_array",String.valueOf(offers_array.length()));
//
//                for (int j = 0; j < offers_array.length(); j++) {
//                    JSONObject c = offers_array.getJSONObject(j);
//
//                    //filter: only offers with expirity before one month TODO: TRY TO SET THIS ON SQL [BD]
//                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//                    Date expires_at = dateFormatter.parse(c.getString("expires_at"));   //get date string from db and convert to Date
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(new Date());    //get current date
//                    cal.add(Calendar.MONTH,1);  //add 1 month to current date
//                    Date currentDatePlusOne = cal.getTime();
//
//
//                    if(expires_at.before(currentDatePlusOne) || (Integer.valueOf(c.getString("availability"))<4) ){
//                        Offer offer = new Offer(c.getString("id"),c.getString("partner_name"),c.getString("description"),c.getString("image"));
//                        offers.add(offer);
//                    }
//
//                }
//
//            }catch(Exception e){
//                Log.e("erro",e.getMessage());
//            }
//            Log.i("Home_Result",result);
//
//            ListAdapter listAdapter = new OfferListAdapter(getContext(),offers);
//
//            //set our custom listAdapter to listview
//            lvExpiring.setAdapter(listAdapter);
//
//
//        // Check offer information by clicking on list elements
//        lvExpiring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view,
//                                    int position, long id) {
//                Offer offer =  (Offer)parent.getItemAtPosition(position);
//                Toast.makeText(getContext(),offer.partner,Toast.LENGTH_SHORT).show();
//                Bundle bundle = new Bundle();
//                bundle.putString("offer_id", offer.id);
//                OfferInformation offerInformation = new OfferInformation();
//                offerInformation.setArguments(bundle);
//                android.support.v4.app.FragmentTransaction offerInformationfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                offerInformationfragmentTransaction
//                        .replace(R.id.fragment_container, offerInformation)
//                        .addToBackStack(null)
//                        .commit();
//            }
//
//        });
//        }
//    }

}
