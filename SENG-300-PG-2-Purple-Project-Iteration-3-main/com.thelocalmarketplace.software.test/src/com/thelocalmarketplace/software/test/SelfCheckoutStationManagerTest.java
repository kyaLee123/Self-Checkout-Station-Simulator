package com.thelocalmarketplace.software.test;



	import static org.junit.Assert.*;

	import java.math.BigDecimal;
	import java.util.ArrayList;
	import java.util.Currency;

	import org.junit.*;

	import com.jjjwelectronics.Mass;
	import com.jjjwelectronics.Numeral;
	import com.jjjwelectronics.scanner.Barcode;
	import com.jjjwelectronics.scanner.BarcodedItem;
	import com.thelocalmarketplace.hardware.BarcodedProduct;
	import com.thelocalmarketplace.hardware.Product;
	import com.tdc.CashOverloadException;
	import com.tdc.DisabledException;
	import com.tdc.coin.Coin;
	import com.thelocalmarketplace.hardware.SelfCheckoutStation;
	import com.thelocalmarketplace.hardware.external.ProductDatabases;
	import com.thelocalmarketplace.software.SelfCheckoutStationManager;
import com.thelocalmarketplace.software.ShoppingCart;

import powerutility.PowerGrid;
	
public class SelfCheckoutStationManagerTest {
		SelfCheckoutStation hardware;
		
		//Test Variables
		Numeral[] code1 = {Numeral.zero, Numeral.one};
		Barcode barcode1 = new Barcode(code1);
		Numeral[] code2 = {Numeral.zero, Numeral.two};
		Barcode barcode2 = new Barcode(code2);
		Numeral[] code3 = {Numeral.zero, Numeral.three};
		Barcode barcode3 = new Barcode(code3);
		
		double tolerance = 50;
		double largeTolerance = 5000;
		double smallTolerance = 2;
		
		Coin validCoin = new Coin(Currency.getInstance("CAD"), BigDecimal.ONE);
		Coin invalidCoin = new Coin(Currency.getInstance("USD"), BigDecimal.TEN);
		BarcodedItem item1 = new BarcodedItem(barcode1, new Mass(100000000));
		BarcodedProduct product1 = new BarcodedProduct(barcode1, "one", 5, 100);
		BarcodedItem item2 = new BarcodedItem(barcode2, new Mass(150000000));
		BarcodedProduct product2 = new BarcodedProduct(barcode2, "two", 10, 150);
		BarcodedItem item3 = new BarcodedItem(barcode3, new Mass(200000000));
		BarcodedProduct product3 = new BarcodedProduct(barcode3, "three", 20, 200);

		
		@Before
		public void setup() {
			hardware = new SelfCheckoutStation();
			PowerGrid power = PowerGrid.instance();
			PowerGrid.engageUninterruptiblePowerSource();
			hardware.plugIn(power);
			hardware.turnOn();
			
		}
		@After
		public void tearDown() {
			ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		}
		/** Test invalid Scenarios for adding an item via barcode scanning.
		 * 	Scanning should not work if you: have not started a session or have an active weight discrepancy.
		 * @throws DisabledException
		 * @throws CashOverloadException
		 */
		@Test
		public void testPayByCoinValid() throws DisabledException, CashOverloadException {
			ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, product1);
			SelfCheckoutStationManager control = new SelfCheckoutStationManager(hardware, tolerance);
			control.startSession();
			//Payment should be accepted as a valid coin was paid after an item was scanned and no discrepancies are active.
			control.insertCoin(BigDecimal.valueOf(1));
			
			assertEquals("Total paid should be 1", BigDecimal.ONE, control.getTotalPaid());
		}
		/**Test if you can pay for the transaction
		 * @throws DisabledException
		 * @throws CashOverloadException
		 */
		@Test
        public void testStartSession() {
            SelfCheckoutStation machine = new SelfCheckoutStation(); // Initialize with required parameters
            SelfCheckoutStationManager manager = new SelfCheckoutStationManager(machine, 10.0); // Assuming a tolerance of 10 grams

            manager.startSession();
            assertTrue("Session should have started", manager.getSessionStarted());
        }
		@Test
        public void testWeightDiscrepencyNotBlocked() throws InterruptedException{
            SelfCheckoutStationManager selfCheckoutStationManager = new SelfCheckoutStationManager(new SelfCheckoutStation(), 0.1);
            selfCheckoutStationManager.startSession();
            selfCheckoutStationManager.addItem(new Barcode(new Numeral[] {Numeral.one, Numeral.three}));
            selfCheckoutStationManager.setCurrentMass(1);
            selfCheckoutStationManager.weightDiscrepency();
            
            assertFalse(selfCheckoutStationManager.getBlockedStatus());
        }
		@Test
        public void testWeightDiscrepencyBlocked() throws InterruptedException{
            SelfCheckoutStationManager selfCheckoutStationManager = new SelfCheckoutStationManager(new SelfCheckoutStation(), 0.1);
            selfCheckoutStationManager.startSession();
            selfCheckoutStationManager.addItem(new Barcode(new Numeral[] {Numeral.one, Numeral.three}));
            selfCheckoutStationManager.setCurrentMass(2);
            selfCheckoutStationManager.weightDiscrepency();
            assertTrue(selfCheckoutStationManager.getBlockedStatus());
		}
		@Test
        public void testWeightDiscrepencyCartAddingWhileBlocked() throws InterruptedException{
            SelfCheckoutStationManager selfCheckoutStationManager = new SelfCheckoutStationManager(new SelfCheckoutStation(), 0.1);
            selfCheckoutStationManager.startSession();
            selfCheckoutStationManager.addItem(new Barcode(new Numeral[] {Numeral.one, Numeral.three}));
            selfCheckoutStationManager.setCurrentMass(2);
            selfCheckoutStationManager.weightDiscrepency();
            selfCheckoutStationManager.addItem(new Barcode(new Numeral[] {Numeral.one, Numeral.three}));
            assertEquals(1, selfCheckoutStationManager.getCart().getItems().size());
		}
		
		@Test
		public void testGetCurrentPrice() {
			SelfCheckoutStationManager selfCheckoutStationManager = new SelfCheckoutStationManager(new SelfCheckoutStation(), 0.1);
            selfCheckoutStationManager.startSession();
            selfCheckoutStationManager.addItem(new Barcode(new Numeral[] {Numeral.one, Numeral.three}));
            assertEquals(selfCheckoutStationManager.getTotalPrice(), 10, 0);
		}
		@Test
		public void testEndSession() {
			SelfCheckoutStation machine = new SelfCheckoutStation(); // Initialize with required parameters
            SelfCheckoutStationManager manager = new SelfCheckoutStationManager(machine, 10.0); // Assuming a tolerance of 10 grams

            manager.startSession();
            manager.endSession();
            assertFalse("Session should have started", manager.getSessionStarted());
		}
	}