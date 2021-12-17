package cartapi.apiresponse;

public class AddItemResponse {
	public boolean successful;
	public String message;

	private AddItemResponse() {
		successful = true;
		message = "Item added to cart successfully!";
	}

	private AddItemResponse(String message) {
		successful = false;
		this.message = message;
	}

	public static AddItemResponse SUCCESS = new AddItemResponse();
	public static AddItemResponse success() {
		return SUCCESS;
	}

	public static AddItemResponse fail(String message) {
		return new AddItemResponse(message);
	}
}
