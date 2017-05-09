package br.com.cdf.cheapit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Jimy on 3/11/17.
 */

public class PartnerListAdapter extends ArrayAdapter<Partner> {
    //Lista de itens para popular a ListView. Essas Listas serao enviadas como argumento.
    ArrayList<Partner> partners;

    ArrayList<Partner> filterList;  // Auxiliar arrayList to searchView

    //Construtor do Adapter. Colocar o numero de parametros necessarios para criar as listas de dados
    public PartnerListAdapter(Context context, ArrayList<Partner> dbPartners) {
        super(context, R.layout.partner_row,dbPartners);
        this.partners = dbPartners;

        // create a new instance to copy only values, not reference
        filterList = new ArrayList<>();
        this.filterList.addAll(dbPartners);
    }

    // Returns the object that populate a row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //set the row used to display data
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.partner_row, parent, false);

        //insere a oferta baseado na posicao da lista
        String name = partners.get(position).name;
        TextView tvItem = (TextView) customView.findViewById(R.id.tvName);
        tvItem.setText(name);

        //insere a oferta baseado na posicao da lista
        String address = partners.get(position).address;
        TextView tvEndereco = (TextView) customView.findViewById(R.id.tvEndereco);
        tvEndereco.setText(address);

//        String descricao = partners.get(position).description;
//        TextView tvDescricao = (TextView)customView.findViewById(R.id.tvDescricao);
//        tvDescricao.setText(descricao);
//
//        String imagem = partners.get(position).image;
//        ImageView ivImagem = (ImageView)customView.findViewById(R.id.ivCouponVoucher);

        //ivImagem.setImageResource(getContext().getResources().getIdentifier("drawable/"+imagem,null,getContext().getPackageName()));

        //retorna o objeto
        return customView;
    }

    public void filter(String filterText)
    {
        Log.i("Adapter Filter","Filtering "+filterText);
        filterText = filterText.toLowerCase(Locale.getDefault());
        partners.clear();
        if (filterText.length() == 0) {
            partners.addAll(filterList);
        } else{
            for(Partner p:filterList){
                if (p.name.toLowerCase(Locale.getDefault()).contains(filterText)) {
                    Log.i("Partner on filter",p.name);
                    partners.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

}
