package br.com.cdf.cheapit;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener {

    MapView mapView;

    LocationRequest mLocationRequest;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    Marker currLocationMarker;
    ListView lvCoupons;

    RadioGroup radioGroup3;
    TextView tvPlaces;
    ImageButton ibSortPlaces, ibFilterPlaces;
    Spinner spSortPlaces,spFilterPlaces;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide, container, false);
        tvPlaces = (TextView)rootView.findViewById(R.id.tvPlaces);

        ibSortPlaces = (ImageButton)rootView.findViewById(R.id.ibSortPlaces);
        ibFilterPlaces =  (ImageButton)rootView.findViewById(R.id.ibFilterPlaces);
        spSortPlaces = (Spinner) rootView.findViewById(R.id.spSortPlaces);
        spFilterPlaces =  (Spinner) rootView.findViewById(R.id.spFilterPlaces);

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

        radioGroup3 = (RadioGroup)rootView.findViewById(R.id.radioGroup3);

        // Checked change Listener for RadioGroup 1
        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radioRestaurants:
                        Toast.makeText(getContext(), "Exibindo Estabelecimentos", Toast.LENGTH_SHORT).show();
                        lvCoupons.setVisibility(View.VISIBLE);
                        mapView.setVisibility(View.GONE);
                        break;
                    case R.id.radioMap:
                        Toast.makeText(getContext(), "Exibindo Mapa", Toast.LENGTH_SHORT).show();
                        lvCoupons.setVisibility(GONE);
                        mapView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        //criar listas de itens
        ArrayList<String> clientes = new ArrayList<>();
        ArrayList<String> descricao = new ArrayList<>();
        ArrayList<String> imagens = new ArrayList<>();

        //recebe os dados do arquivo
        InputStream i = getResources().openRawResource(R.raw.coupons);
        CSVParser csvParser = new CSVParser(i);
        ArrayList<String[]> pizzas = csvParser.read();

        for(String[] pizza:pizzas) {
            clientes.add(pizza[1].replace("\"", ""));
            descricao.add(pizza[2].replace("\"", ""));
            imagens.add(pizza[3].replace("\"", ""));
        }

        //instanciar o nosso adapter enviando como argumento nossas listas ao construtor
        ListAdapter listAdapter = new CouponListAdapter(getContext(), clientes,descricao, imagens);

        //pegar referencia do listview
        lvCoupons = (ListView)rootView.findViewById(R.id.lvPlaces);

        //setar o adapter da listview para o nosso adapter
        lvCoupons.setAdapter(listAdapter);

        //iniciar o listview como invisivel
        lvCoupons.setVisibility(GONE);

        tvPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvCoupons.setSelection(0);
            }
        });

        return rootView;
    }

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

        //callbacks to connect to GoogleAPIClient
        buildGoogleApiClient();

        //connect to GoogleAPIClient
        mGoogleApiClient.connect();

        //add markers
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(-23.298966, -45.955750))
                .title("Pizzaria do Cheff")
                .snippet("Population: 776733"));

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
