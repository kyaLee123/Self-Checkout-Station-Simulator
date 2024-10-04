package com.thelocalmarketplace.software;

import java.math.BigDecimal;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;

public class PaymentProcessor {
	private boolean processingPayment = false;
	private BigDecimal cartValue;
	private AbstractSelfCheckoutStation station;
    private BigDecimal totalPayments = BigDecimal.valueOf(0);
	
    /**
     * Constructor for PaymentProcessor class
     * @param totalCartValue the total amount needed to be paid before ending the station
     * @param station hardware reference for this class
     */
	public PaymentProcessor(BigDecimal totalCartValue, AbstractSelfCheckoutStation station) {
		cartValue = totalCartValue;
		processingPayment = true;
		this.station = station;
	}
	
	/**
	 * A method to insert the coin. If payment is still processing, try to receive it
	 * @param coin
	 * @throws CashOverloadException 
	 * @throws DisabledException 
	 */
	public void insertCoin(Coin coin) throws NoPowerException, DisabledException, CashOverloadException{
		// Get the value of the coin inserted
		BigDecimal coinValue = coin.getValue();
		// Is payment still processing
		if (this.returnIfPaymentStillProcessing()) {
			// If so try to receive it with the coin slot, and subtract the amount needed to be paid by the user
			if (!this.station.coinSlot.isDisabled() && this.station.coinSlot.isActivated()) {
				this.station.coinSlot.receive(coin);
				cartValue = cartValue.subtract(coinValue);
				this.determineIfStillProcessing();
			}
			else if (!this.station.coinSlot.isActivated()) {
				throw new NoPowerException();
			}
			else if(this.station.coinSlot.isDisabled()) {
				throw new DisabledException();
			}
			else {
				// Should never happen
			}
			
		}
		
	}

	/**
	 * A method used to track the total payment paid by the user. If payment is made, use this method
	 * @param payment the amount to be added to total payment
	 */
    public void addPayment(BigDecimal payment) {
		this.totalPayments = this.totalPayments.add(payment);
	}
	
    /**
     * A method to determine if payment is still processing
     */
	private void determineIfStillProcessing() {
		// If cart value is greater than 0, user still hasn't paid all their dues, continue to process payment
		if (cartValue.compareTo(BigDecimal.valueOf(0))  > 0)
			processingPayment = true;
		// Otherwise, stop processing payment
		else
			processingPayment = false;
	}
	
	/**
	 * A method to return the current state whether the PaymentProcessor is still processing
	 * @return
	 */
	public boolean returnIfPaymentStillProcessing() {
		return this.processingPayment;
	}
	
	/**
	 * A method to manually disable paymentProcessor
	 */
	public void disablePayment() {
		this.processingPayment = false;
	}
	
	/**
	 * 
	 * A method to manually enable paymentProcessor
	 */
	public void enablePayment() {
		this.processingPayment = true;
	}
	
	/**
	 *  Clears the payments made to the machine, intended to be used at the end of a transaction
	 */
	public void clearPayments() {
		this.cartValue = BigDecimal.valueOf(0);
	}

	/**
	 * A method to set the cart value to be paid by the user manually
	 * @param price a new custom price
	 */
    public void setCartValue(double price){
        this.cartValue = BigDecimal.valueOf(price);

    }

    /**
	 * A method to return the current amount still need to be paid by the user
	 * @return the current value still need to be paid by the user
	 */
    public BigDecimal getCartValue(){
        return this.cartValue;
    }
    
    /** 
     * A method to return the total payments made by the user
     * @return the total payments made by the user
     */
    public BigDecimal returnTotalPayments() {
    	return this.totalPayments;
    }
    
  //For Add Own Bags methods
    public void addOwnBags() {
    	
    }
    
    public void initializeBaggingArea() {
    	//resetting it for new customers
    }
    
    public void signalAddedBag() {
    	
    }
    
    public boolean confirmBagWeight() {
    	boolean confirm = true;
    	return confirm;
    }
    public void WeightException() {
    	
    }
    public void bagTooHeavy() {
    	
    }
    public void canContinueSignal() {
    	
    }

}