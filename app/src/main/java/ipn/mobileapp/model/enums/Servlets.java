package ipn.mobileapp.model.enums;

public enum Servlets {
    LOGIN(0),
    REGISTER(1),
    FORGOT_PASSWORD(2),
    VERIFY_PHONE(3),
    USER(4),
    CONTACT(5),
    ALERT(6),
    VEHICLE(7),
    DOCUMENT(8);

    private int value;

    Servlets(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}

