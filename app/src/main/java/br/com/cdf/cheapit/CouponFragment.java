package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment {

    private RadioGroup radioGroup1;

    public CouponFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_coupon, container, false);

        radioGroup1 = (RadioGroup)rootView.findViewById(R.id.radioGroup1);

        // Checked change Listener for RadioGroup 1
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radioAtivos:
                        Toast.makeText(getContext(), "Cupons Ativos", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioEncerrados:
                        Toast.makeText(getContext(), "Cupons Encerrados", Toast.LENGTH_SHORT).show();
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
        ListView lvCoupons = (ListView)rootView.findViewById(R.id.lvCoupons);

        //setar o adapter da listview para o nosso adapter
        lvCoupons.setAdapter(listAdapter);

        return rootView;
    }

}
