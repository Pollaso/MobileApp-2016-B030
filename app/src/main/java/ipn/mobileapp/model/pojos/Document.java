package ipn.mobileapp.model.pojos;

import com.google.gson.Gson;

public class Document {
	private String documentId;
	private String name;
	private String source;
	private String userId;
	private boolean valid;

	public Document() {
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
