package cartapi.tax;

import cartapi.Cart;
import cartapi.Item;

import java.util.Map;

public class PotatoTax implements Tax {

	public Item potato;

	public PotatoTax(Item potato) {
		this.potato = potato;
	}

	@Override
	public float calculateTax(Cart cart) {
		Map<Item, Integer> cartContents = cart.getContents();
		if (cartContents.containsKey(potato)) {
			return cartContents.get(potato) * potato.getPrice() * 0.50f;
		} else {
			return 0.0f;
		}
	}
}
