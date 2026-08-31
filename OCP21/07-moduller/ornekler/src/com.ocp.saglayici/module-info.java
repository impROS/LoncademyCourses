/*
 * Saglayici modul: arayuzu uygular ve SERVIS olarak sunar.
 */
module com.ocp.saglayici {
    requires com.ocp.api;
    provides com.ocp.api.Selamlayici with com.ocp.saglayici.TurkceSelamlayici;
    // DIKKAT: uygulama sinifinin paketi exports EDILMEK ZORUNDA DEGIL.
}
