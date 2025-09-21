package es.upm.miw.apaw.resources.dtos;

public class Validations {
    public static final String MOBILE = "\\+\\d{8,15}|\\d{9}";
    public static final String MOBILE_RX = "^" + MOBILE + "$";
    public static final String ID_WITH_UUID = "/{id:[0-9a-fA-F\\-]{36}}";
    public static final String ID_WITH_MOBILE = "/{id:(?:" + MOBILE + ")}";

    private Validations() {
        //Empty
    }
}

