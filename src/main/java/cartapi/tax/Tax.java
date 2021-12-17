package cartapi.tax;

import cartapi.Cart;

public interface Tax {
	public float calculateTax(Cart cart);
}
