package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PointsFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public PointsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_prizes, container, false);
        View header = inflater.inflate(R.layout.header_prizes,null);


        expListView = (ExpandableListView) rootView.findViewById(R.id.lvPoints);
        expListView.addHeaderView(header);

        int[] images = new int[]{
                R.drawable.prizedraw, R.drawable.prizedraw
        };
        ViewPager mViewPager = (ViewPager) header.findViewById(R.id.viewPageAndroid); // detalhe importante: troquei o rootview pelo header.find(...)
        AndroidImageAdapter adapterView = new AndroidImageAdapter(getContext(), images);
        mViewPager.setAdapter(adapterView);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        return rootView;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Para que servem");
        listDataHeader.add("Como adquirir");
        listDataHeader.add("Sorteios");


        // Adding child data
        List<String> hotToRequest = new ArrayList<String>();
        hotToRequest.add("Para gerar os cupons CheapIt basta..");

        List<String> howToUse = new ArrayList<String>();
        howToUse.add("Para poder utilizar os cupons CheapIt que você gerou, apresente..");

        List<String> whatIsPoints = new ArrayList<String>();
        whatIsPoints.add(getString(R.string.whatIsPoints));

        listDataChild.put(listDataHeader.get(0), hotToRequest); // Header, Child data
        listDataChild.put(listDataHeader.get(1), howToUse);
        listDataChild.put(listDataHeader.get(2), whatIsPoints);
    }

}
