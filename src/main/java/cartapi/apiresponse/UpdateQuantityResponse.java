package cartapi.apiresponse;

import java.util.ArrayList;
import java.util.List;

public class UpdateQuantityResponse {
	public boolean successful;
	public String message;
	public List<String> invalidDiscounts;

	public UpdateQuantityResponse() {
		this.successful       = true;
		this.message          = "Quantity updated successfull!";
		this.invalidDiscounts = new ArrayList<>();
	}

	private UpdateQuantityResponse(String message) {
		this.successful       = false;
		this.message          = message;
		this.invalidDiscounts = new ArrayList<>();
	}

	public static UpdateQuantityResponse fail(String message) {
		return new UpdateQuantityResponse(message);
	}
}
