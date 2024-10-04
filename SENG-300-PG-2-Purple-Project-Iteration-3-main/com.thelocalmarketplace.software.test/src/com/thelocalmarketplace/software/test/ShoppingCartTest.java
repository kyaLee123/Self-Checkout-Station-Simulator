package com.thelocalmarketplace.software.test;


import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.tdc.CashOverloadException;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.ShoppingCart;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

public class ShoppingCartTest {
	private ShoppingCart cart;
	@Before
	public void setUp() {
		cart = new ShoppingCart();
	}
	
	@Test 
	public void testGetExpectedWeight() {
		assertEquals(0.0, cart.getExpectedWeight(), 0.00001);
	}
	@Test 
	public void testGetTotalCost() {
		assertEquals(0.00, cart.getTotalCost(), 0.00001);
	}
	@Test (expected = NullPointerSimulationException.class)
	public void TestBarcodeScannedNull() {
		cart.barcodeScanned(null);
	}
	@Test
	public void TestBarcodeScannedWeight() {
		cart.barcodeScanned(new Barcode(new Numeral[] {Numeral.valueOf((byte) 1), Numeral.valueOf((byte) 3)}));
		assertEquals(1, cart.getExpectedWeight(), 0.0001);
	}
	@Test
	public void TestBarcodeScannedPrice() {
		cart.barcodeScanned(new Barcode(new Numeral[] {Numeral.valueOf((byte) 1), Numeral.valueOf((byte) 3)}));
		assertEquals(10, cart.getTotalCost(), 0.0001);
	}
	@Test
	public void TestGetItems() {
		cart.barcodeScanned(new Barcode(new Numeral[] {Numeral.valueOf((byte) 1), Numeral.valueOf((byte) 3)}));
		assertEquals(cart.getItems().get(0).getDescription(), "Test Product 1");
	}
	@Test
	public void TestClearCartItem() {
		cart.clearCart();
		assertEquals(new ArrayList<BarcodedProduct>(), cart.getItems());
	}
	@Test
	public void TestClearCartWeight() {
		cart.clearCart();
		assertEquals(0.00, cart.getExpectedWeight(), 0.00001);
	}
	
	@Test
	public void TestClearCartPrice() {
		cart.clearCart();
		assertEquals(0.00, cart.getTotalCost(), 0.0001);
	}
}

