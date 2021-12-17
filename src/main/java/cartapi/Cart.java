package cartapi;

import cartapi.apiresponse.UpdateQuantityResponse;
import cartapi.discount.Discount;
import mock.Database;

import java.util.*;

public class Cart {
	private Map<Item, Integer> contents;
	private List<Discount> discounts;
	private Address address;

	private List<Date> discountInfractions;
	private Date discountBan;

	private Database database;

	public Cart(Database database, Address address) {
		this.contents            = new HashMap<>();
		this.discounts           = new ArrayList<>();
		this.address             = address;
		this.discountInfractions = new ArrayList<>();
		this.discountBan         = null;
		this.database            = database;
	}

	public Map<Item, Integer> getContents() {
		return contents;
	}

	public List<Discount> getDiscounts() {
		return discounts;
	}

	public Address getAddress() {
		return address;
	}

	public String addItem(int itemId) {
		Item item = database.getItemFromID(itemId);
		if (contents.containsKey(item)) {
			return "Item already in cart!";
		} else if (database.getStock(item) == 0) {
			return "Item is out of stock!";
		} else {
			contents.put(item, 1);
			return null;
		}
	}

	public UpdateQuantityResponse updateQuantity(int itemId, int quantity) {
		Item item = database.getItemFromID(itemId);
		if (quantity < 0) {
			return UpdateQuantityResponse.fail("Quantity cannot be negative!");
		}
		if (database.getStock(item) < quantity) {
			return UpdateQuantityResponse.fail("Requested quantity is greater than available stock!");
		}
		if (contents.containsKey(item)) {
			UpdateQuantityResponse response = new UpdateQuantityResponse();
			if (quantity == 0) {
				contents.remove(item);
			} else {
				contents.put(item, quantity);
			}
			for (int i = 0; i < discounts.size(); ++i) {
				if (discounts.get(i).isApplicable(this) != null) {
					response.invalidDiscounts.add(discounts.get(i).getCode());
					discounts.remove(i);
					--i;
				}
			}
			return response;
		}
		return UpdateQuantityResponse.fail("Item is not in cart!");
	}

	public String applyDiscount(String discountCode) {
		if (discountBan != null && discountBan.after(new Date())) {
			return "Did not add discount!";
		}
		Discount discount = database.getDiscountFromCode(discountCode);
		if (discounts.contains(discount)) {
			return "Discount already applied!";
		}
		String applicable = discount.isApplicable(this);
		if (applicable == null) {
			discounts.add(discount);
			return null;
		} else {
			invokeDiscountInfraction();
			return applicable;
		}
	}

	private void invokeDiscountInfraction() {
		Date current = new Date();
		for (int i = 0; i < discountInfractions.size(); ++i) {
			if (discountInfractions.get(i).before(current)) {
				discountInfractions.remove(i);
				--i;
			}
		}
		discountInfractions.add(date24HoursFromCurrent());
		if (discountInfractions.size() == 5) {
			discountInfractions.clear();
			discountBan = date24HoursFromCurrent();
		}
	}

	private Date date24HoursFromCurrent() {
		Date future = new Date();
		future.setTime(future.getTime() + 86400000);
		return future;
	}
}
