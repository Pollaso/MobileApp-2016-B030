package ipn.mobileapp.model.pojos;

import java.util.List;

public class Contact {
    private String photo;
    private String id;
    private String firstName;
    private String lastName;
    private List<String> phoneNumbers;

    public Contact(String photo, String id, String firstName, List<String> phoneNumbers)
    {
        this.photo = photo;
        this.id = id;
        this.firstName = firstName;
        //this.lastName = lastName;
        this.phoneNumbers = phoneNumbers;
    }
}
