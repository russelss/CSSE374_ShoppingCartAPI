package cartapi.discount;

import cartapi.Cart;
import cartapi.Item;
import cartapi.discount.requirement.DiscountRequirement;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * An example implementation of a Discount
 *
 * Applies a fixed percentage discount to a single type of item
 */
public class PercentDiscount extends Discount {

	private Item item;
	private float percent;

	public PercentDiscount(Item item, float percent, String code, Date expirationDate, List<DiscountRequirement> requirements) {
		super(code, expirationDate, requirements);
		this.item    = item;
		this.percent = percent;
	}

	@Override
	public float calculateDiscount(Cart cart) {
		Map<Item, Integer> cartContents = cart.getContents();
		if (cartContents.containsKey(item)) {
			return item.getPrice() * cartContents.get(item).intValue() * percent;
		}
		return 0;
	}
}
