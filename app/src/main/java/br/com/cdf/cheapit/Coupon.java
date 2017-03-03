package br.com.cdf.cheapit;

import java.util.Date;

/**
 * Created by Jimy on 1/17/17.
 */

public class Coupon {
    // Constants
    public static final int AVAILABLE = 0;	// disponivel, nenhum user associado
    public static final int ORDERED = 1;	// usuario gerou cupom mas nao utilizou
    public static final int USED = 2;		// usuario gerou cupom e utilizou
    public static final int EXPIRED = 3;	// cupom expirou independente se tem usuario associado ou nao


//    String coupon_id;
//    Coupon_offer coupon_offer; // oferta associada ao cupom
//    String coupon_code;	// codigo de cupom gerado para cada usuario
//    int coupon_status;
//    Date coupon_generated_at;   // TODO: 1/17/17  tem uma biblioteca Date.sql, se precisar alterar depois
//    User user;
//
//    public Coupon(Coupon_offer coupon_offer){
//        this.coupon_id = getCoupon_id();	// recebe um id ao criar o cupom
//        this.coupon_status = AVAILABLE;		// inicia o cupom como disponivel
//        this.coupon_offer = coupon_offer;	// associa uma oferta ao cupom
//        //COUPON_DATABASE.add(this);    // TODO: 1/17/17 inserir cupom criado ao banco de dados
//    }
//
//    // TODO gerar id para o cupom
//    public String getCoupon_id()
//    {
//        return "1";
//    }
//
//    public boolean generateCoupon(User request_user)
//    {
//        String user_id = request_user.user_id;
//
//        // verificacoes
//        //if(COUPON_DATABASE.has(coupon_id,user_id))	return false;	// verifica se o usuario ja tentou gerar esse cupom
//        if(coupon_offer.offer_availability <= 0) return false;	// verifica disponibilidade
//
//        // atualiza CUPOM
//        coupon_code = user_id + coupon_id + coupon_offer.offer_availability; // gera um id para o cupom: id do cliente + id do cupom + disponibilidade
//        this.coupon_status = ORDERED; // altera status do cupom
//
//        // atualiza OFERTA
//        coupon_offer.user_availability.put(Integer.parseInt(user_id),false);	// evita que o usuario gere novamente o cupom
//        coupon_offer.offer_availability--; // decrementa disponibilidade
//
//        // atualiza USUARIO
//        request_user.coupon_generated++;
//
//
//        return true;
//    }
}
