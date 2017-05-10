package br.com.cdf.cheapit;


import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

import static android.view.View.GONE;
import static br.com.cdf.cheapit.R.id.lvPartners;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener, GoogleMap.OnInfoWindowClickListener {

    MapView mapView;

    LocationRequest mLocationRequest;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    Marker currLocationMarker;

//    ListView lvOffers;
//    RadioGroup radioGroup3;

    TextView tvMap;
    ImageButton ibSortPlaces, ibFilterPlaces;
    Spinner spSortPlaces,spFilterPlaces;

    //database connection
    String json;
    ArrayList<Partner> partners;

    HashMap<Marker,String> markerHash = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide, container, false);
        tvMap = (TextView)rootView.findViewById(R.id.tvMap);

        ibSortPlaces = (ImageButton)rootView.findViewById(R.id.ibSortPlaces);
        ibFilterPlaces =  (ImageButton)rootView.findViewById(R.id.ibFilterPlaces);
        spSortPlaces = (Spinner) rootView.findViewById(R.id.spSortPlaces);
        spFilterPlaces =  (Spinner) rootView.findViewById(R.id.spFilterPlaces);

        //get partners from database
        partners = new ArrayList<>();

        //initialize map, get from mapview
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        //Long pressed helpers
        ibSortPlaces.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Ordenar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        ibFilterPlaces.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Filtrar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //Sorts and Filters
        ibSortPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spSortPlaces.performClick();            }
        });
        ibFilterPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spFilterPlaces.performClick();            }
        });

        // Spinner click listener
        spSortPlaces.setOnItemSelectedListener(this);
        spFilterPlaces.setOnItemSelectedListener(this);

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
        spSortPlaces.setAdapter(sortAdapter);
        spFilterPlaces.setAdapter(filterAdapter);


//        radioGroup3 = (RadioGroup)rootView.findViewById(R.id.radioGroup3);
//
//        // Checked change Listener for RadioGroup 1
//        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId)
//            {
//                switch (checkedId)
//                {
//                    case R.id.radioRestaurants:
//                        Toast.makeText(getContext(), "Exibindo Estabelecimentos", Toast.LENGTH_SHORT).show();
//                        lvOffers.setVisibility(View.VISIBLE);
//                        mapView.setVisibility(View.GONE);
//                        break;
//                    case R.id.radioMap:
//                        Toast.makeText(getContext(), "Exibindo Mapa", Toast.LENGTH_SHORT).show();
//                        lvOffers.setVisibility(GONE);
//                        mapView.setVisibility(View.VISIBLE);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//
//        //criar listas de itens
//        ArrayList<String> clientes = new ArrayList<>();
//        ArrayList<String> descricao = new ArrayList<>();
//        ArrayList<String> imagens = new ArrayList<>();
//
//        //recebe os dados do arquivo
//        InputStream i = getResources().openRawResource(R.raw.coupons);
//        CSVParser csvParser = new CSVParser(i);
//        ArrayList<String[]> pizzas = csvParser.read();
//
//        for(String[] pizza:pizzas) {
//            clientes.add(pizza[1].replace("\"", ""));
//            descricao.add(pizza[2].replace("\"", ""));
//            imagens.add(pizza[3].replace("\"", ""));
//        }

//        //instanciar o nosso adapter enviando como argumento nossas listas ao construtor
//        ListAdapter listAdapter = new CouponListAdapter(getContext(), clientes,descricao, imagens);
//
//        //pegar referencia do listview
//        lvOffers = (ListView)rootView.findViewById(R.id.lvPlaces);
//
//        //setar o adapter da listview para o nosso adapter
//        lvOffers.setAdapter(listAdapter);
//
//        //iniciar o listview como invisivel
//        lvOffers.setVisibility(GONE);
//
//        tvMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                lvOffers.setSelection(0);
//            }
//        });

        return rootView;
    }

    //database connection methods
    private class GetPartners extends AsyncTask<String,String,String>{

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
                Log.i("Map Query",query);

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
                JSONArray partners_array = jsonObj.getJSONArray("partners_array");
                Log.i("Tamanho partner_array",String.valueOf(partners_array.length()));

                for (int j = 0; j < partners_array.length(); j++) {
                    JSONObject p = partners_array.getJSONObject(j);
                    Partner partner = new Partner(p.getString("id"),p.getString("name"),p.getString("address"),p.getString("latitude"),p.getString("longitude"));
                    partners.add(partner);

                    //Add marker in google map
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Float.valueOf(partner.latitude), Float.valueOf(partner.longitude)))
                            .title(partner.name)
                            .snippet(partner.address));

                    markerHash.put(marker,partner.id);

                }

            }catch(Exception e){
                Log.e("erro",e.getMessage());
            }
            Log.i("Result",result);

        }
//
//        @Override
//        public boolean onMarkerClick(Marker marker) {
//            if(markerClicked.get(marker)){
//                markerClicked.put(marker,false);
//                Log.i("Marker click","set false");
//            }
//            else {
//                markerClicked.put(marker, false);
//                Log.i("Marker click","set true");
//            }
//            return false;
//        }
    }


    //google maps connection methods
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnInfoWindowClickListener(this);


        //callbacks to connect to GoogleAPIClient
        buildGoogleApiClient();

        //connect to GoogleAPIClient
        mGoogleApiClient.connect();

        //add markers, get partners from database
        new GetPartners().execute("","");

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i("Map","Clicking marker window");
        String partner_id =  markerHash.get(marker);
        Bundle bundle = new Bundle();
        bundle.putString("partner_id", partner_id);
        PartnerInformation partnerInformation = new PartnerInformation();
        partnerInformation.setArguments(bundle);
        android.support.v4.app.FragmentTransaction partnerInformationfragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        partnerInformationfragmentTransaction
                .replace(R.id.fragment_container, partnerInformation)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onConnected(Bundle bundle) {
        //checking PERMISSIONS
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //get LastLocation (currentLocation)
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //mGoogleMap.clear();

            //get LastLocation LatLong
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            //place marker at current position
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Você está aqui!");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }
        else{
            Toast.makeText(getContext(),"Localização atual não encontrada. Verifique o sinal de GPS",Toast.LENGTH_SHORT).show();
        }

        //NO IDEA, maybe updating currentLocation
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(),"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getContext(),"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Você está aqui!");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
