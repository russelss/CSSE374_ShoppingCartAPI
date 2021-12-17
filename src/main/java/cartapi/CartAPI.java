package cartapi;

import cartapi.apiresponse.AddItemResponse;
import cartapi.apiresponse.ApplyDiscountResponse;
import cartapi.apiresponse.UpdateQuantityResponse;
import cartapi.apiresponse.ViewCartResponse;
import cartapi.tax.Tax;
import mock.Database;

import java.util.ArrayList;
import java.util.Map;

public class CartAPI {

	private Map<Integer, Cart> carts;

	private Database database;

	public CartAPI(Database database, Map<Integer, Cart> carts) {
		this.database = database;
		this.carts    = carts;
	}

	public ViewCartResponse viewCart(int userId) {
		Map<Item, Integer> cartContents = carts.get(userId).getContents();

		ViewCartResponse response  = new ViewCartResponse();
		response.items             = new ArrayList<>();
		for (Map.Entry<Item, Integer> item : cartContents.entrySet()) {
			ViewCartResponse.ItemDetails itemDetails = new ViewCartResponse.ItemDetails();
			itemDetails.name        = item.getKey().name;
			itemDetails.description = item.getKey().description;
			itemDetails.price       = item.getKey().getPrice();
			itemDetails.picture     = item.getKey().picture;
			itemDetails.quantity    = item.getValue().intValue();
			response.items.add(itemDetails);
		}

		response.taxEstimate = 0.0f;
		for (Tax tax : database.getTaxes()) {
			response.taxEstimate += tax.calculateTax(carts.get(userId));
		}

		return response;
	}

	public ApplyDiscountResponse applyDiscount(int userId, String discountCode) {
		String message = carts.get(userId).applyDiscount(discountCode);
		if (message == null) {
			return ApplyDiscountResponse.success();
		} else {
			return ApplyDiscountResponse.fail(message);
		}
	}

	public AddItemResponse addItemToCart(int userId, int itemId) {
		String message = carts.get(userId).addItem(itemId);
		if (message == null) {
			return AddItemResponse.success();
		} else {
			return AddItemResponse.fail(message);
		}
	}

	public UpdateQuantityResponse updateQuantity(int userId, int itemId, int quantity) {
		return carts.get(userId).updateQuantity(itemId, quantity);
	}
}
