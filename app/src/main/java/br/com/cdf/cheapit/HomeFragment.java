package br.com.cdf.cheapit;


import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    View rootView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        View header = inflater.inflate(R.layout.fragment_home_header,null);

        ViewPager mViewPager = (ViewPager) header.findViewById(R.id.viewPageAndroid); // detalhe importante: troquei o rootview pelo header.find(...)
        AndroidImageAdapter adapterView = new AndroidImageAdapter(getContext());
        mViewPager.setAdapter(adapterView);

        TabLayout tabLayout = (TabLayout) header.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager);

        TextView tvCQDT = (TextView) header.findViewById(R.id.tvCQDT);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/bebas.ttf");
        tvCQDT.setTypeface(type);

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
        ListView lvExpiring = (ListView)rootView.findViewById(R.id.lvExpiring);

        lvExpiring.addHeaderView(header);

        //setar o adapter da listview para o nosso adapter
        lvExpiring.setAdapter(listAdapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
