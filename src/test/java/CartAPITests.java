import cartapi.Address;
import cartapi.Cart;
import cartapi.CartAPI;
import cartapi.Item;
import cartapi.apiresponse.AddItemResponse;
import cartapi.apiresponse.ApplyDiscountResponse;
import cartapi.apiresponse.UpdateQuantityResponse;
import cartapi.apiresponse.ViewCartResponse;
import cartapi.discount.Discount;
import cartapi.discount.PercentDiscount;
import cartapi.discount.requirement.DiscountRequirement;
import cartapi.discount.requirement.ItemRequirement;
import cartapi.tax.AddressTax;
import cartapi.tax.PotatoTax;
import cartapi.tax.Tax;
import mock.Database;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CartAPITests {

	private Map<String, Discount> discounts;
	private List<Tax> taxes;
	private Map<Integer, Cart> carts;
	private Database database;
	private CartAPI cartAPI;

	public CartAPITests() {
		this.discounts = new HashMap<>();
		this.taxes     = new ArrayList<>();
		this.carts     = new HashMap<>();
		database = new Database("src/test/resources/database.csv", discounts, taxes);
		cartAPI  = new CartAPI(database, carts);

		taxes.add(new AddressTax());
		taxes.add(new PotatoTax(database.getItemFromID(2)));
	}

	private void reset() {
		discounts.clear();
		taxes.clear();
		carts.clear();
	}

	@Test
	public void viewEmptyCartTest() {
		carts.put(0, new Cart(database, new Address("00000")));

		ViewCartResponse response = cartAPI.viewCart(0);
		assert(response.items.size() == 0);
		assert(response.taxEstimate == 0.0f);

		reset();
	}

	@Test
	public void viewNonEmptyCartTest() {
		carts.put(1, new Cart(database, new Address("00000")));
		cartAPI.addItemToCart(1, 0);
		cartAPI.addItemToCart(1, 5);
		cartAPI.updateQuantity(1, 0, 4);

		ViewCartResponse response = cartAPI.viewCart(1);
		assert(response.items.size() == 2);
		assert(Math.abs(response.taxEstimate - 3.45f) <= 0.01f);

		reset();
	}

	@Test
	public void viewMultiTaxCartTest() {
		carts.put(2, new Cart(database, new Address("00000")));
		cartAPI.addItemToCart(2, 2);
		cartAPI.updateQuantity(2, 2, 5);

		ViewCartResponse response = cartAPI.viewCart(2);
		assert(response.items.size() == 1);
		assert(response.items.get(0).name.equals("Potato"));
		assert(Math.abs(response.taxEstimate - 8.625f) <= 0.01f);

		reset();
	}

	@Test
	public void viewTaxFreeCartTest() {
		carts.put(3, new Cart(database, new Address("47300")));
		cartAPI.addItemToCart(3, 4);

		ViewCartResponse response = cartAPI.viewCart(3);
		assert(response.items.size() == 1);
		assert(response.taxEstimate == 0.0f);

		reset();
	}

	@Test
	public void applyBasicDiscountTest() {
		carts.put(4, new Cart(database, new Address("00000")));

		List<DiscountRequirement> requirements = new ArrayList<>();
		Date expire = new Date();
		expire.setTime(expire.getTime() + 100000000000l);
		Discount discount = new PercentDiscount(database.getItemFromID(2), 50.0f, "00000000", expire, requirements);
		discounts.put(discount.getCode(), discount);

		cartAPI.addItemToCart(4, 2);
		cartAPI.updateQuantity(4, 2, 4);
		ApplyDiscountResponse response = cartAPI.applyDiscount(4, discount.getCode());

		assert(response.successful);

		reset();
	}

	@Test
	public void applyExpiredDiscountTest() {
		carts.put(5, new Cart(database, new Address("00000")));

		List<DiscountRequirement> requirements = new ArrayList<>();
		Date expire = new Date();
		expire.setTime(expire.getTime() - 100000000000l);
		Discount discount = new PercentDiscount(database.getItemFromID(2), 50.0f, "00000001", expire, requirements);
		discounts.put(discount.getCode(), discount);

		cartAPI.addItemToCart(5, 2);
		cartAPI.updateQuantity(5, 2, 4);
		ApplyDiscountResponse response = cartAPI.applyDiscount(5, discount.getCode());

		assert(!response.successful);
		assert(response.message.equals("Discount expired!"));

		reset();
	}

	@Test
	public void applyValidRequirementDiscountTest() {
		carts.put(6, new Cart(database, new Address("00000")));

		List<DiscountRequirement> requirements = new ArrayList<>();
		Map<Item, Integer> itemRequirements = new HashMap<>();
		itemRequirements.put(database.getItemFromID(0), 4);
		itemRequirements.put(database.getItemFromID(3), 1);
		requirements.add(new ItemRequirement(itemRequirements));
		Date expire = new Date();
		expire.setTime(expire.getTime() + 100000000000l);
		Discount discount = new PercentDiscount(database.getItemFromID(2), 50.0f, "00000001", expire, requirements);
		discounts.put(discount.getCode(), discount);

		cartAPI.addItemToCart(6, 0);
		cartAPI.updateQuantity(6, 0, 4);
		cartAPI.addItemToCart(6, 3);
		ApplyDiscountResponse response = cartAPI.applyDiscount(6, discount.getCode());

		assert(response.successful);

		reset();
	}

	@Test
	public void applyInvalidRequirementDiscountTest() {
		carts.put(7, new Cart(database, new Address("00000")));

		List<DiscountRequirement> requirements = new ArrayList<>();
		Map<Item, Integer> itemRequirements = new HashMap<>();
		itemRequirements.put(database.getItemFromID(2), 4);
		itemRequirements.put(database.getItemFromID(3), 1);
		requirements.add(new ItemRequirement(itemRequirements));
		Date expire = new Date();
		expire.setTime(expire.getTime() + 100000000000l);
		Discount discount = new PercentDiscount(database.getItemFromID(2), 50.0f, "00000001", expire, requirements);
		discounts.put(discount.getCode(), discount);

		cartAPI.addItemToCart(7, 2);
		cartAPI.updateQuantity(7, 2, 2);
		cartAPI.addItemToCart(7, 3);
		ApplyDiscountResponse response = cartAPI.applyDiscount(7, discount.getCode());

		assert(!response.successful);
		assert(response.message.equals("Cart does not meet the item requirements for the discount!"));

		reset();
	}

	@Test
	public void invokeTempBanTest() {
		carts.put(5, new Cart(database, new Address("00000")));

		List<DiscountRequirement> requirements = new ArrayList<>();
		Date expire = new Date();
		expire.setTime(expire.getTime() - 100000000000l);
		Discount discount = new PercentDiscount(database.getItemFromID(2), 50.0f, "00000001", expire, requirements);
		discounts.put(discount.getCode(), discount);

		cartAPI.addItemToCart(5, 2);
		cartAPI.updateQuantity(5, 2, 4);
		cartAPI.applyDiscount(5, discount.getCode());
		cartAPI.applyDiscount(5, discount.getCode());
		cartAPI.applyDiscount(5, discount.getCode());
		cartAPI.applyDiscount(5, discount.getCode());
		cartAPI.applyDiscount(5, discount.getCode());
		ApplyDiscountResponse response = cartAPI.applyDiscount(5, discount.getCode());

		assert(!response.successful);
		assert(response.message.equals("Did not add discount!"));

		reset();
	}

	@Test
	public void reapplyBasicDiscountTest() {
		carts.put(4, new Cart(database, new Address("00000")));

		List<DiscountRequirement> requirements = new ArrayList<>();
		Date expire = new Date();
		expire.setTime(expire.getTime() + 100000000000l);
		Discount discount = new PercentDiscount(database.getItemFromID(2), 50.0f, "00000000", expire, requirements);
		discounts.put(discount.getCode(), discount);

		cartAPI.addItemToCart(4, 2);
		cartAPI.updateQuantity(4, 2, 4);
		cartAPI.applyDiscount(4, discount.getCode());
		ApplyDiscountResponse response = cartAPI.applyDiscount(4, discount.getCode());

		assert(!response.successful);
		assert(response.message.equals("Discount already applied!"));

		reset();
	}

	@Test
	public void addItemToCartTest() {
		carts.put(0, new Cart(database, new Address("00000")));

		AddItemResponse response = cartAPI.addItemToCart(0, 0);

		assert(response.successful);

		reset();
	}

	@Test
	public void addOutOfStockItemTest() {
		carts.put(0, new Cart(database, new Address("00000")));

		AddItemResponse response = cartAPI.addItemToCart(0, 1);

		assert(!response.successful);
		assert(response.message.equals("Item is out of stock!"));

		reset();
	}

	@Test
	public void addAlreadyAddedItemTest() {
		carts.put(0, new Cart(database, new Address("00000")));

		cartAPI.addItemToCart(0, 0);
		AddItemResponse response = cartAPI.addItemToCart(0, 0);

		assert(!response.successful);
		assert(response.message.equals("Item already in cart!"));

		reset();
	}

	@Test
	public void updateQuantityBasicTest() {
		Cart cart = new Cart(database, new Address("00000"));
		carts.put(0, cart);

		cartAPI.addItemToCart(0, 0);
		UpdateQuantityResponse response = cartAPI.updateQuantity(0, 0, 5);

		assert(response.successful);
		assert(cart.getContents().get(database.getItemFromID(0)).intValue() == 5);

		reset();
	}

	@Test
	public void updateQuantityBeyondAvailableStockTest() {
		Cart cart = new Cart(database, new Address("00000"));
		carts.put(0, cart);

		cartAPI.addItemToCart(0, 2);
		UpdateQuantityResponse response = cartAPI.updateQuantity(0, 2, 10);

		assert(!response.successful);
		assert(response.message.equals("Requested quantity is greater than available stock!"));
		assert(cart.getContents().get(database.getItemFromID(2)).intValue() == 1);

		reset();
	}

	@Test
	public void updateQuantityNegativeTest() {
		Cart cart = new Cart(database, new Address("00000"));
		carts.put(0, cart);

		cartAPI.addItemToCart(0, 2);
		UpdateQuantityResponse response = cartAPI.updateQuantity(0, 2, -5);

		assert(!response.successful);
		assert(response.message.equals("Quantity cannot be negative!"));
		assert(cart.getContents().get(database.getItemFromID(2)).intValue() == 1);

		reset();
	}

	@Test
	public void updateQuantityNotInCartTest() {
		Cart cart = new Cart(database, new Address("00000"));
		carts.put(0, cart);

		UpdateQuantityResponse response = cartAPI.updateQuantity(0, 2, 1);

		assert(!response.successful);
		assert(response.message.equals("Item is not in cart!"));
		assert(!cart.getContents().containsKey(database.getItemFromID(2)));

		reset();
	}

	@Test
	public void updateQuantityRemoveTest() {
		Cart cart = new Cart(database, new Address("00000"));
		carts.put(0, cart);

		cartAPI.addItemToCart(0, 2);
		UpdateQuantityResponse response = cartAPI.updateQuantity(0, 2, 0);

		assert(response.successful);
		assert(!cart.getContents().containsKey(database.getItemFromID(2)));

		reset();
	}

	@Test
	public void removeDiscountOnQuantityChangeTest() {
		Cart cart = new Cart(database, new Address("00000"));
		carts.put(0, cart);

		cartAPI.addItemToCart(0, 0);
		cartAPI.addItemToCart(0, 4);
		cartAPI.updateQuantity(0, 0, 4);
		cartAPI.updateQuantity(0, 4, 100);

		List<DiscountRequirement> requirements = new ArrayList<>();
		Map<Item, Integer> itemRequirements = new HashMap<>();
		itemRequirements.put(database.getItemFromID(0), 4);
		itemRequirements.put(database.getItemFromID(4), 100);
		requirements.add(new ItemRequirement(itemRequirements));
		Date expire = new Date();
		expire.setTime(expire.getTime() + 100000000000l);
		Discount discount = new PercentDiscount(database.getItemFromID(2), 50.0f, "00000001", expire, requirements);
		discounts.put(discount.getCode(), discount);

		cartAPI.applyDiscount(0, discount.getCode());
		UpdateQuantityResponse response = cartAPI.updateQuantity(0, 0, 1);

		assert(response.successful);
		assert(response.invalidDiscounts.size() == 1);
		assert(response.invalidDiscounts.get(0).equals("00000001"));

		reset();
	}
}
