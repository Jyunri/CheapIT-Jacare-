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

public class ZOLDCouponListAdapter extends ArrayAdapter<String> {


    //Lista de itens para popular a ListView. Essas Listas serao enviadas como argumento.
    ArrayList<String> listaIds;
    ArrayList<String> listaItens;
    ArrayList<String> listaDescricao;
    ArrayList<String> listaImagens;

    //Construtor do Adapter. Colocar o numero de parametros necessarios para criar as listas de dados
    public ZOLDCouponListAdapter(Context context, ArrayList<String> listaItens, ArrayList<String> listaDescricao, ArrayList<String> listaImagens) {
        super(context, R.layout.offer_row, listaItens);
        this.listaItens = listaItens;
        this.listaDescricao = listaDescricao;
        this.listaImagens = listaImagens;
    }

    //Construtor alternativo com id
    public ZOLDCouponListAdapter(Context context, ArrayList<String> listaIds, ArrayList<String> listaItens, ArrayList<String> listaDescricao, ArrayList<String> listaImagens) {
        super(context, R.layout.offer_row, listaItens);
        this.listaIds = listaIds;
        this.listaItens = listaItens;
        this.listaDescricao = listaDescricao;
        this.listaImagens = listaImagens;
    }

    //Retorna o objeto que compoe um row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //seta qual row sera utilizado para mostrar os dados
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.offer_row, parent, false);

        //recebe os itens das listas

//        Recebe ids das listas
//        String offer_id = listaIds.get(position);
//        TextView tvCouponOfferId = (TextView)customView.findViewById(R.id.tvCouponOfferId);
//        tvCouponOfferId.setText(offer_id);

        String item = listaItens.get(position);
        TextView tvItem = (TextView)customView.findViewById(R.id.tvName);
        tvItem.setText(item);

        String descricao = listaDescricao.get(position);
        TextView tvDescricao = (TextView)customView.findViewById(R.id.tvDescription);
        tvDescricao.setText(descricao);

        String imagem = listaImagens.get(position);
        ImageView ivImagem = (ImageView)customView.findViewById(R.id.ivOfferVoucher);

        ivImagem.setImageResource(getContext().getResources().getIdentifier("drawable/"+imagem,null,getContext().getPackageName()));

        //retorna o objeto
        return customView;
    }
}
