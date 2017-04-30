package br.com.cdf.cheapit;

/**
 * Created by Jimy on 4/29/17.
 */
public class Coupon {
    public String id;
    public String partner;
    public String description;
    public String image;

    public Coupon(String id, String partner, String description, String image) {
        this.id = id;
        this.partner = partner;
        this.description = description;
        this.image = image;
    }
}
