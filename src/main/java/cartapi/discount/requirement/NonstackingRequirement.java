package cartapi.discount.requirement;

import cartapi.Cart;
import cartapi.discount.Discount;

import java.util.List;

public class NonstackingRequirement implements DiscountRequirement {
	private List<Discount> incompatibleDiscounts;

	public NonstackingRequirement(List<Discount> incompatibleDiscounts) {
		this.incompatibleDiscounts = incompatibleDiscounts;
	}

	@Override
	public String isApplicable(Cart cart) {
		for (Discount discount : cart.getDiscounts()) {
			if (incompatibleDiscounts.contains(discount)) {
				return "Discount conflicts with another discount already applied!";
			}
		}
		return null;
	}
}
