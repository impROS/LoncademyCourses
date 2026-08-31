package com.ocp.uygulama;

import com.ocp.api.Selamlayici;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        System.out.println("modul adi   : " + Main.class.getModule().getName());
        System.out.println("named mi?   : " + Main.class.getModule().isNamed());

        ServiceLoader<Selamlayici> yukleyici = ServiceLoader.load(Selamlayici.class);
        int sayac = 0;
        for (Selamlayici s : yukleyici) {
            System.out.println("bulunan servis: " + s.getClass().getName()
                    + " -> " + s.selamla("Ridvan"));
            sayac++;
        }
        System.out.println("toplam servis : " + sayac);
    }
}
