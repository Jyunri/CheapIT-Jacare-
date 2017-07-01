package br.com.cdf.cheapit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Jimy on 12/1/16.
 */

public class CouponListAdapter extends ArrayAdapter<Coupon> {


    //Lista de itens para popular a ListView. Essas Listas serao enviadas como argumento.
    ArrayList<Coupon> coupons;

    //Construtor do Adapter. Colocar o numero de parametros necessarios para criar as listas de dados
    public CouponListAdapter(Context context, ArrayList<Coupon> coupons) {
        super(context, R.layout.coupon_row, coupons);
        this.coupons = coupons;
    }

    //Retorna o objeto que compoe um row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //seta qual row sera utilizado para mostrar os dados
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.coupon_row, parent, false);

        //insere a oferta baseado na posicao da lista
        String item = coupons.get(position).partner;
        TextView tvItem = (TextView)customView.findViewById(R.id.tvName);
        tvItem.setText(item);

        // TODO: 5/18/17 VERIFY NEED OF DESCRIPTION [UX]
        //String descricao = coupons.get(position).description;
        //TextView tvDescricao = (TextView)customView.findViewById(R.id.tvDescription);
        //tvDescricao.setText(descricao);

        String couponExpirity = coupons.get(position).expires_at;
        TextView tvCouponExpirity = (TextView)customView.findViewById(R.id.tvCouponExpirity);
        tvCouponExpirity.setText(couponExpirity);

        String imagem = coupons.get(position).image;
        ImageView ivImagem = (ImageView)customView.findViewById(R.id.ivCouponVoucher);

        //ivImagem.setImageResource(getContext().getResources().getIdentifier("drawable/"+imagem,null,getContext().getPackageName()));
        Glide.with(getContext()).load(imagem).into(ivImagem);


        //retorna o objeto
        return customView;
    }
}
