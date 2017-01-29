package br.com.cdf.cheapit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        View header = inflater.inflate(R.layout.webview,null);

        expListView = (ExpandableListView) rootView.findViewById(R.id.lvHelp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        expListView.addHeaderView(header);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        String frameVideo = "<html><style>img{display: inline; height: auto; max-width: 100%;}</style><body><iframe width=\"\" height=\"200\" src=\"https://www.youtube.com/embed/47yJ2XCRLZs\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        WebView displayYoutubeVideo = (WebView) header.findViewById(R.id.mWebView);
        displayYoutubeVideo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = displayYoutubeVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        displayYoutubeVideo.loadData(frameVideo, "text/html", "utf-8");
        return rootView;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Como gerar cupons");
        listDataHeader.add("Como utilizar cupons");
        listDataHeader.add("Pontos Cheapit");
        listDataHeader.add("Pontos Cheapit");
        listDataHeader.add("Pontos Cheapit");
        listDataHeader.add("Pontos Cheapit");

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
