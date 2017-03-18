package br.com.cdf.cheapit;

/**
 * Created by Jimy on 3/11/17.
 */
public class Partner {
    String id;
    String name;
    String address;
    String latitude;
    String longitude;

    public Partner(String id, String name, String address, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
