package cartapi.apiresponse;

public class ApplyDiscountResponse {
	public boolean successful;
	public String message;

	private ApplyDiscountResponse() {
		successful = true;
		message = "Discount applied successfully!";
	}

	private ApplyDiscountResponse(String message) {
		successful = false;
		this.message = message;
	}

	public static ApplyDiscountResponse SUCCESS = new ApplyDiscountResponse();
	public static ApplyDiscountResponse success() {
		return SUCCESS;
	}

	public static ApplyDiscountResponse fail(String message) {
		return new ApplyDiscountResponse(message);
	}
}
