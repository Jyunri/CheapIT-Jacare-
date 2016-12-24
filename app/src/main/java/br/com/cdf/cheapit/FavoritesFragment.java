package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {


    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

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
        ListView lvCoupons = (ListView)rootView.findViewById(R.id.lvCoupons);

        //setar o adapter da listview para o nosso adapter
        lvCoupons.setAdapter(listAdapter);

        return rootView;

    }

}
