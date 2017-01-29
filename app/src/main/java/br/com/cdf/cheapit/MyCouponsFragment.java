package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCouponsFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private RadioGroup radioGroup1;
    TextView tvUsername,tvMyCouponsTitle, tvCheapitPoints;

    ListAdapter listAdapter;
    ArrayList<String[]> coupons;
    ListView lvCoupons;

    ImageButton ibSortMyCoupons, ibFilterMyCoupons;
    Spinner spSortMyCoupons, spFilterMyCoupons;

    public MyCouponsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mycoupons, container, false);
        View header = inflater.inflate(R.layout.fragment_profile,null);

        // Header (profile)
        if(LoginController.LoginMethod.equals("facebook")) {
            new DownloadImage((ImageView) header.findViewById(R.id.ivAvatar)).execute(LoginController.getCurrentAvatar());
            tvUsername = (TextView)header.findViewById(R.id.tvUsername);
            tvUsername.setText(LoginController.getCurrentUsername());
        }

        tvCheapitPoints = (TextView)header.findViewById(R.id.tvCheapitPoints);
        tvCheapitPoints.setOnClickListener(this);



        radioGroup1 = (RadioGroup)header.findViewById(R.id.radioGroup1);
        tvMyCouponsTitle = (TextView)header.findViewById(R.id.tvMyCouponsTitle);

        ibSortMyCoupons = (ImageButton) header.findViewById(R.id.ibSortMyCoupons);
        ibFilterMyCoupons =  (ImageButton) header.findViewById(R.id.ibFilterMyCoupons);
        spSortMyCoupons = (Spinner) header.findViewById(R.id.spSortMyCoupons);
        spFilterMyCoupons =  (Spinner) header.findViewById(R.id.spFilterMyCoupons);


        //Long pressed helpers
        ibSortMyCoupons.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Ordenar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        ibFilterMyCoupons.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Filtrar por...",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ibSortMyCoupons.setOnClickListener(this);
        ibFilterMyCoupons.setOnClickListener(this);

        // Spinner click listener
        spSortMyCoupons.setOnItemSelectedListener(this);
        spFilterMyCoupons.setOnItemSelectedListener(this);


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
        spSortMyCoupons.setAdapter(sortAdapter);
        spFilterMyCoupons.setAdapter(filterAdapter);


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
                    case R.id.radioUsed:
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
        coupons = csvParser.read();

        for(String[] coupon: coupons) {
            clientes.add(coupon[1].replace("\"", ""));
            descricao.add(coupon[2].replace("\"", ""));
            imagens.add(coupon[3].replace("\"", ""));
        }

        //instanciar o nosso adapter enviando como argumento nossas listas ao construtor
        listAdapter = new CouponListAdapter(getContext(), clientes,descricao, imagens);

        //pegar referencia do listview
        lvCoupons = (ListView)rootView.findViewById(R.id.lvCoupons);

        //setar o profile como header da listview
        lvCoupons.addHeaderView(header);

        //setar o adapter da listview para o nosso adapter
        lvCoupons.setAdapter(listAdapter);

        //eventos ao clicar nos itens da lista
        lvCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Toast.makeText(getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
            }

        });


        //retornar ao inicio da lista ao clicar no toolbar
        tvMyCouponsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvCoupons.setSelection(0);
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibSortMyCoupons:
                spSortMyCoupons.performClick();
                break;
            case R.id.ibFilterMyCoupons:
                spFilterMyCoupons.performClick();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        switch (parent.getId()){
            case R.id.spSortMyCoupons:
                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

                break;
            case R.id.spFilterMyCoupons:
                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
