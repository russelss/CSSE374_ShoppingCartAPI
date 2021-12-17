package cartapi.apiresponse;

import java.util.List;

public class ViewCartResponse {
	public static class ItemDetails {
		public String name;
		public String description;
		public float price;
		public String picture;
		public int quantity;
	}

	public List<ItemDetails> items;
	public float taxEstimate;
}
