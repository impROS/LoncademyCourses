package com.ocp.saglayici;

import com.ocp.api.Selamlayici;

public class TurkceSelamlayici implements Selamlayici {
    public TurkceSelamlayici() { }          // servis icin PARAMETRESIZ ctor SART
    @Override public String selamla(String ad) { return "Merhaba, " + ad + "!"; }
}
