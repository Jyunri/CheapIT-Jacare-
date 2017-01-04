package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    TextView tvFavoritesTitle;
    ImageButton ibSortFavorites, ibFilterFavorites;
    Spinner spSortMyCoupons,spFilterMyCoupons;

    public FavoritesFragment() {
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
        spSortMyCoupons = (Spinner) rootView.findViewById(R.id.spSortMyCoupons);
        spFilterMyCoupons =  (Spinner) rootView.findViewById(R.id.spFilterMyCoupons);

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
                spSortMyCoupons.performClick();            }
        });
        ibFilterFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spFilterMyCoupons.performClick();            }
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

}
