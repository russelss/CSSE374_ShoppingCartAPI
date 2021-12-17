package mock;

import cartapi.Item;
import cartapi.discount.Discount;
import cartapi.tax.Tax;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Database {

	private static class Stock {
		public Item item;
		public int quantity;
	}

	private Map<Integer, Stock> itemDatabase;
	private Map<String, Discount> discountDatabase;
	private List<Tax> taxDatabase;

	public Database(String itemFilePath, Map<String, Discount> discounts, List<Tax> taxes) {
		itemDatabase = new HashMap<>();
		discountDatabase = new HashMap<>();
		taxDatabase = new ArrayList<>();

		try {
			Scanner scanner = new Scanner(new FileInputStream(itemFilePath));
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split(",");
				Item item = new Item();
				item.id           = Integer.parseInt(line[0]);
				item.name         = line[1];
				item.description  = line[2];
				item.basePrice    = Float.parseFloat(line[3]);
				item.discountRate = Float.parseFloat(line[4]);
				item.picture      = line[5];

				Stock stock = new Stock();
				stock.item     = item;
				stock.quantity = Integer.parseInt(line[6]);

				itemDatabase.put(item.id, stock);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		this.discountDatabase = discounts;
		this.taxDatabase      = taxes;
	}

	public Item getItemFromID(int itemId) {
		return itemDatabase.get(itemId).item;
	}

	public int getStock(Item item) {
		return itemDatabase.get(item.id).quantity;
	}

	public Discount getDiscountFromCode(String code) {
		return discountDatabase.get(code);
	}

	public List<Tax> getTaxes() {
		return taxDatabase;
	}
}
