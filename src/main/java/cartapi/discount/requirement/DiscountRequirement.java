package cartapi.discount.requirement;

import cartapi.Cart;

public interface DiscountRequirement {
	public String isApplicable(Cart cart);
}
