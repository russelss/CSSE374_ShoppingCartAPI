package cartapi;

public class Item {
	public int id;
	public String name;
	public String description;
	public float basePrice; // Should not use float for money, but it's not a real system so... ¯\_(ツ)_/¯
	public float discountRate;
	public String picture;

	public float getPrice() {
		return basePrice * (1 - discountRate * 0.01f);
	}

	@Override
	public int hashCode() {
		return id;
	}
}