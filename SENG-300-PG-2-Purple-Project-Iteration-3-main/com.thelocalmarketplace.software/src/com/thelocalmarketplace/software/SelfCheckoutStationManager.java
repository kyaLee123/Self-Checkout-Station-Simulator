
package com.thelocalmarketplace.software;

import com.thelocalmarketplace.hardware.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.*;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.jjjwelectronics.scale.*;
import java.util.List;

public class SelfCheckoutStationManager  {

    public boolean blocked = false;
    private ShoppingCart Cart;
    private PaymentProcessor Pay;
    private boolean sessionStarted = false;
    private boolean attendantSignalled = false;
    private boolean customerSignalled = false;
    private InnerListener listener;
	private double currentMass;
    private double tolerance;
    IBarcodeScanner barcodeScanner;
    IElectronicScale scale;
    List<BigDecimal> denominations = new ArrayList<BigDecimal>(Arrays.asList(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.1),BigDecimal.valueOf(0.25),BigDecimal.valueOf(1),BigDecimal.valueOf(2)));
    CoinValidator validator = new CoinValidator(Currency.getInstance("CAD"), denominations);

    /**
	 * Inner class used for listening to events and performing actions on the class
	 */
	private class InnerListener implements BarcodeScannerListener, ElectronicScaleListener, CoinValidatorObserver {

		/**
		 * Not used
		 */
		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}

		/**
		 * Not Used
		 */
		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}

		/**
		 * Not Used
		 */
		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}

		/**
		 * Not Used
		 */
		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}


		/**
		 * Listens for a barcode to be scanned. 
		 * If a session is active and there is no weight discrepancy active, add that item to the cart.
		 */
		@Override
		public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
			if (sessionStarted && !blocked) {
				Cart.barcodeScanned(barcode);
				Pay.setCartValue(Cart.getTotalCost());
			} else {
				//nothing should happen, the barcode gets ignored
			}
		}

		/**
		 * Listens for when the mass on the scale changes. 
		 * When such an event happens, update the current mass and check for a weight discrepancy.
		 */
		@Override
		public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
			
			currentMass = mass.inGrams().doubleValue();
			
		}

		/**
		 * Not Used
		 */
		@Override
		public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {}
		
		/**
		 * Not Used
		 */
		@Override
		public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {}
		
		/**
		 * Not Used
		 */
		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {}

		/**
		 * Not Used
		 */
		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {}

		/**
		 * Not Used
		 */
		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {}

		/**
		 * Not Used
		 */
		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {}

		/**
		 * Listens for valid coins being inserted into the machine.
		 * If a session is active, and no weight discrepancy is active, add the amount inserted to the total funds.
		 * Then, If the new total amount inserted into the machine can pay for the purchase, end the session.
		 * 
		 * @param validator
		 * 				a CoinValidator
		 * 
		 * @param value
		 * 				a BigDecimal value
		 */
		@Override
		public void validCoinDetected(CoinValidator validator, BigDecimal value) {
			if (sessionStarted && !blocked) {
				Pay.addPayment(value);
			} else {
				//Nothing will happen, for iteration 1 we just need to add the coin
			}
		}

		/**
		 * Not Used
		 */
		@Override
		public void invalidCoinDetected(CoinValidator validator) {}
	}
	
	
	
	
	/**
	 * Constructor for the control software. 
	 * @param tolerance
	 * 				The amount of tolerance used when calculating weight discrepancy, in grams.
	 * @param machine
	 * 				The machine to be attached to the software.
	 */
	public SelfCheckoutStationManager(AbstractSelfCheckoutStation machine, double tolerance) {
		
		this.Pay = new PaymentProcessor(BigDecimal.valueOf(0), machine);
		this.tolerance = tolerance;
		this.listener = new InnerListener();
		this.Cart = new ShoppingCart();

		machine.mainScanner.register(listener);
		machine.baggingArea.register(listener);
		machine.coinValidator.attach(listener);
	}
    
	public boolean weightDiscrepency() throws InterruptedException {
		if(Math.abs(this.Cart.getExpectedWeight()-currentMass)<tolerance) {
			blocked = false;
		} else {
			blocked = true;
		}
		
		return blocked;
	}

     /**
	 * Starts a 'transaction', allows items to start being added to the cart.
	 */
	public void startSession() {
		this.Cart = new ShoppingCart();
        this.sessionStarted = true;
    }
	
	
    /**
     * Ends the current Session. Clears credit on the machine and the cart.
     */
    public void endSession() {
        this.sessionStarted = false;
        this.Cart.clearCart();
        this.Pay.clearPayments();
        this.blocked = false;
    }
    
    /**
     * see if session has been started
     * @return sessionStarted
     * 					a value that says if session has been started
     */
    public boolean getSessionStarted() {
    	return this.sessionStarted;
    }
    
    /**
     * the carts total cost
     * @return getTotalCost
     * 					the value of total costs
     */
    public double getTotalPrice() {
    	return this.Cart.getTotalCost();
    }

    public BigDecimal getTotalPaid() {
    	return this.Pay.returnTotalPayments();
    }
    
    public ShoppingCart getCart() {
    	return this.Cart;
    }
    
    public void setCurrentMass(double newMass) {
    	//this.currentMass = newMass;
    	this.listener.theMassOnTheScaleHasChanged(scale, new Mass(newMass));
    }

    public boolean getBlockedStatus() {
    	return this.blocked;
    }
    
    public void addItem(Barcode barcode) {
    	this.listener.aBarcodeHasBeenScanned(barcodeScanner, barcode);
    }

    public void insertCoin(BigDecimal coin) {
    	this.listener.validCoinDetected(validator, coin);
    }

}