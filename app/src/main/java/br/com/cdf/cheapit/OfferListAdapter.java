package br.com.cdf.cheapit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jimy on 12/1/16.
 */

public class OfferListAdapter extends ArrayAdapter<Coupon_offer> {


    //Lista de itens para popular a ListView. Essas Listas serao enviadas como argumento.
    ArrayList<Coupon_offer> offers;

    //Construtor do Adapter. Colocar o numero de parametros necessarios para criar as listas de dados
    public OfferListAdapter(Context context, ArrayList<Coupon_offer> offers) {
        super(context, R.layout.coupon_row, offers);
        this.offers = offers;
    }

    //Retorna o objeto que compoe um row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //seta qual row sera utilizado para mostrar os dados
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.coupon_row, parent, false);

        //insere a oferta baseado na posicao da lista
        String item = offers.get(position).partner;
        TextView tvItem = (TextView)customView.findViewById(R.id.tvName);
        tvItem.setText(item);

        String descricao = offers.get(position).description;
        TextView tvDescricao = (TextView)customView.findViewById(R.id.tvDescricao);
        tvDescricao.setText(descricao);

        String imagem = offers.get(position).image;
        ImageView ivImagem = (ImageView)customView.findViewById(R.id.ivCouponVoucher);

        //ivImagem.setImageResource(getContext().getResources().getIdentifier("drawable/"+imagem,null,getContext().getPackageName()));

        //retorna o objeto
        return customView;
    }
}
