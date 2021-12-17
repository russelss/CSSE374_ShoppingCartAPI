package cartapi.tax;

import cartapi.Cart;
import cartapi.Item;

import java.util.Map;

public class AddressTax implements Tax {

	public float calculateTax(Cart cart) {
		float totalCost = 0;
		for (Map.Entry<Item, Integer> item : cart.getContents().entrySet()) {
			totalCost += item.getKey().getPrice() * item.getValue().intValue();
		}

		float taxRate = 0.075f;
		if (cart.getAddress().getZipCode() == "47300") {
			taxRate = 0.0f;
		}

		return totalCost * taxRate;
	}
}
