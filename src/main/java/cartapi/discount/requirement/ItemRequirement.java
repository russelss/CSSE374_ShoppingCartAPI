package cartapi.discount.requirement;

import cartapi.Cart;
import cartapi.Item;

import java.util.Map;

public class ItemRequirement implements DiscountRequirement {
	private Map<Item, Integer> requirements;

	public ItemRequirement(Map<Item, Integer> requirements) {
		this.requirements = requirements;
	}

	@Override
	public String isApplicable(Cart cart) {
		Map<Item, Integer> cartContents = cart.getContents();
		for (Map.Entry<Item, Integer> requiredItem : requirements.entrySet()) {
			if (cartContents.containsKey(requiredItem.getKey())) {
				// Oof
				// equivalently: if (numInCart < numRequired)
				if (cartContents.get(requiredItem.getKey()).compareTo(requiredItem.getValue()) < 0) {
					return "Cart does not meet the item requirements for the discount!";
				}
			} else {
				return "Cart does not meet the item requirements for the discount!";
			}
		}
		return null;
	}
}
