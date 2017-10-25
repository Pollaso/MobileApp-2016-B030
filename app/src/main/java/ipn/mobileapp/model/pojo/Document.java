package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;

public class Document {
    public static final int PENDING = 0;
    public static final int VALID = 1;
    public static final int INVALID = 2;

    private String id;
    private String name;
    private String source;
    private String userId;
    private int state = PENDING;

    public Document() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
