package cartapi.discount;

import cartapi.Cart;
import cartapi.discount.requirement.DiscountRequirement;

import java.util.Date;
import java.util.List;

public abstract class Discount {
	private String code;
	private Date expirationDate;
	private List<DiscountRequirement> requirements;

	public Discount(String code, Date expirationDate, List<DiscountRequirement> requirements) {
		this.code           = code;
		this.expirationDate = expirationDate;
		this.requirements   = requirements;
	}

	public String isApplicable(Cart cart) {
		// If the expiration date has passed, do not apply the discount
		Date currentDate = new Date();
		if (currentDate.after(expirationDate)) {
			return "Discount expired!";
		}
		// If any of the discount requirements are not met, do not apply the discount
		for (DiscountRequirement requirement : requirements) {
			String applicable = requirement.isApplicable(cart);
			if (applicable != null) {
				return applicable;
			}
		}
		return null;
	}

	public abstract float calculateDiscount(Cart cart);

	public String getCode() {
		return code;
	}

	@Override
	public boolean equals(Object discount) {
		if (this == discount) {
			return true;
		}
		if (discount instanceof Discount) {
			return this.code.equals(((Discount) discount).code);
		} else {
			return false;
		}
	}
}
