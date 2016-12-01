package br.com.cdf.cheapit;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
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

public class CouponListAdapter extends ArrayAdapter<String> {

    //Lista de itens para popular a ListView. Essas Listas serao enviadas como argumento.
    ArrayList<String> listaItens;
    ArrayList<String> listaDescricao;
    ArrayList<String> listaImagens;

    //Construtor do Adapter. Colocar o numero de parametros necessarios para criar as listas de dados
    public CouponListAdapter(Context context, ArrayList<String> listaItens, ArrayList<String> listaDescricao, ArrayList<String> listaImagens) {
        super(context, R.layout.coupon_row, listaItens);
        this.listaItens = listaItens;
        this.listaDescricao = listaDescricao;
        this.listaImagens = listaImagens;
    }

    //Retorna o objeto que compoe um row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //seta qual row sera utilizado para mostrar os dados
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.coupon_row, parent, false);

        //recebe os itens das listas

        String item = listaItens.get(position);
        TextView tvItem = (TextView)customView.findViewById(R.id.tvItem);
        tvItem.setText(item);

        String descricao = listaDescricao.get(position);
        TextView tvDescricao = (TextView)customView.findViewById(R.id.tvDescricao);
        tvDescricao.setText(descricao);

        String imagem = listaImagens.get(position);
        ImageView ivImagem = (ImageView)customView.findViewById(R.id.ivImagem);

        ivImagem.setImageResource(getContext().getResources().getIdentifier("drawable/"+imagem,null,getContext().getPackageName()));

        //retorna o objeto
        return customView;
    }
}
