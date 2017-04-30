package br.com.cdf.cheapit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Jimy on 12/1/16.
 */

public class ZOLDCSVParser {
    InputStream inputStream;
    BufferedReader reader;

    public ZOLDCSVParser(InputStream inputStream) {
        this.inputStream = inputStream;
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    //retorna a lista de strings
    public ArrayList read()
    {
        ArrayList array = new ArrayList<>();
        String csvLine;
        try {

            // throw away the header
            reader.readLine();

            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                array.add(row);
            }
        }catch (Exception e) {
            e.toString();
        }
        return array;
    }
}