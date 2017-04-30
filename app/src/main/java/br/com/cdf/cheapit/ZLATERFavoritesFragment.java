package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ZLATERFavoritesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    TextView tvFavoritesTitle;
    ImageButton ibSortFavorites, ibFilterFavorites;
    Spinner spSortFavorites,spFilterFavorites;

    public ZLATERFavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        tvFavoritesTitle = (TextView)rootView.findViewById(R.id.tvFavoritesTitle);

        ibSortFavorites = (ImageButton)rootView.findViewById(R.id.ibSortFavorites);
        ibFilterFavorites =  (ImageButton)rootView.findViewById(R.id.ibFilterFavorites);
        spSortFavorites = (Spinner) rootView.findViewById(R.id.spSortFavorites);
        spFilterFavorites =  (Spinner) rootView.findViewById(R.id.spFilterFavorites);

        //Long pressed helpers
        ibSortFavorites.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Ordenar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        ibFilterFavorites.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Filtrar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //Sorts and Filters
        ibSortFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spSortFavorites.performClick();            }
        });
        ibFilterFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spFilterFavorites.performClick();            }
        });

        // Spinner click listener
        spSortFavorites.setOnItemSelectedListener(this);
        spFilterFavorites.setOnItemSelectedListener(this);

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
        spSortFavorites.setAdapter(sortAdapter);
        spFilterFavorites.setAdapter(filterAdapter);

        //criar listas de itens
        ArrayList<String> clientes = new ArrayList<>();
        ArrayList<String> descricao = new ArrayList<>();
        ArrayList<String> imagens = new ArrayList<>();

        //recebe os dados do arquivo
        InputStream i = getResources().openRawResource(R.raw.coupons);
        ZOLDCSVParser ZOLDCSVParser = new ZOLDCSVParser(i);
        ArrayList<String[]> pizzas = ZOLDCSVParser.read();

        for(String[] pizza:pizzas) {
            clientes.add(pizza[1].replace("\"", ""));
            descricao.add(pizza[2].replace("\"", ""));
            imagens.add(pizza[3].replace("\"", ""));
        }

        //instanciar o nosso adapter enviando como argumento nossas listas ao construtor
        ListAdapter listAdapter = new ZOLDCouponListAdapter(getContext(), clientes,descricao, imagens);

        //pegar referencia do listview
        final ListView lvCoupons = (ListView)rootView.findViewById(R.id.lvFavorites);

        //setar o adapter da listview para o nosso adapter
        lvCoupons.setAdapter(listAdapter);

        tvFavoritesTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvCoupons.setSelection(0);
            }
        });

        return rootView;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
