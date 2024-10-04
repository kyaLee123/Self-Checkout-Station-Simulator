package com.thelocalmarketplace.software;

import java.util.ArrayList;
import java.util.Map;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
//import com.thelocalmarketplace.hardware.external.ProductDatabases; -- If we wanted to access the actual database.

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * This class is to create an instance of a "shopping cart" in the self-checkout system.
 */
public class ShoppingCart {
	
	private ArrayList<BarcodedProduct> Items;
	private Double TotalCost;
	private Double ExpectedWeight;
	private Map<Barcode, BarcodedProduct> database = Map.of(
			new Barcode(new Numeral[] {Numeral.one, Numeral.three}), new BarcodedProduct(new Barcode(new Numeral[] {Numeral.one, Numeral.three}), "Test Product 1", (long) 10, 1),
			new Barcode(new Numeral[] {Numeral.four, Numeral.one}), new BarcodedProduct(new Barcode(new Numeral[] {Numeral.four, Numeral.one}), "Test Product 2", (long) 20, 5)
	);
	
	 //The following is how I would get the database but we need something in the database in order to actually write tests.
	//private Map<Barcode, BarcodedProduct> database = ProductDatabases.BARCODED_PRODUCT_DATABASE;
	
	/**
	 * Create a ShoppingCart object and set initial values.
	 */
	public ShoppingCart() {
		this.TotalCost = 0.00;
		this.ExpectedWeight = 0.0;
		this.Items = new ArrayList<BarcodedProduct>();
		
	}

	/**
	 * What happens when a barcode is scanned.
	 * 			Adds the items information to the ShoppingCart totals.
	 * 
	 * @param barcode
	 * 			The barcode of the scanned item.
	 */
	public void barcodeScanned(Barcode barcode) {
		if(barcode == null) 
			throw new NullPointerSimulationException();

		BarcodedProduct scannedItem = database.get(barcode);
		this.Items.add(scannedItem);
		this.ExpectedWeight += scannedItem.getExpectedWeight();
		this.TotalCost += scannedItem.getPrice();
	}
	
	/**
	 * Get the expected weight.
	 * 
	 * @return the expected weight
	 */
	public Double getExpectedWeight() {
		return this.ExpectedWeight;
	}
	
	/**
	 * Get the total cost.
	 * 
	 * @return The total cost of items in cart
	 */
	public Double getTotalCost() {
		return this.TotalCost;
	}

	/**
	 *Clears the cart. Removes all items in Items ArrayList, Sets totalCost and totalWeight to 0.
	 */
	public void clearCart() {
		this.Items = new ArrayList<BarcodedProduct>();
		this.TotalCost = 0.00;
		this.ExpectedWeight = 0.00;
	}
	
	/**
	 * Get the list of items in the Shopping Cart.
	 * 
	 * @return An ArrayList of barcoded products in the cart
	 */
  	public ArrayList<BarcodedProduct> getItems(){
  		return this.Items;	  
  	}
}
