package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.Currency;

import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.software.PaymentProcessor;


import powerutility.NoPowerException;
import powerutility.PowerGrid;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;

import powerutility.PowerGrid;


import com.tdc.coin.Coin;

public class PaymentProcessorTest {

    private PaymentProcessor paymentProcessor;
    private SelfCheckoutStation station;
    private BigDecimal cartValue;
    private Coin coin;

    @Before
    public void setUp() {
    	PowerGrid grid = PowerGrid.instance();
        cartValue = new BigDecimal("10.0");
        station = new SelfCheckoutStation();
        station.plugIn(grid);
        paymentProcessor = new PaymentProcessor(cartValue, station);
        coin = new Coin(Currency.getInstance("CAD"), new BigDecimal("1.0"));
        this.station.coinSlot.enable();
        this.station.turnOn();
    }

    @Test
    public void constructor_initializesCorrectly() {
        assertEquals("Cart value should be initialized correctly", cartValue, paymentProcessor.getCartValue());
        assertTrue("Payment processing should be enabled", paymentProcessor.returnIfPaymentStillProcessing());
    }
    
    @Test
    public void test_IfPaymentStillProcessing() {
    	for (int i = 0; i < 11; i++) {

            try {
				paymentProcessor.insertCoin(coin);
			} catch (NoPowerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CashOverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	assertFalse("Payment should not be processing if excess coins", paymentProcessor.returnIfPaymentStillProcessing());
    }

    @Test
    public void insertCoin_decreasesCartValue() {

        try {
			paymentProcessor.insertCoin(coin);
		} catch (NoPowerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DisabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        assertEquals("Cart value should decrease by the coin's value", new BigDecimal("9.0"), paymentProcessor.getCartValue());
    }

    @Test
    public void addPayment_increasesTotalPayments() {
        BigDecimal payment = new BigDecimal("5.0");
        paymentProcessor.addPayment(payment);
        assertEquals("Total payments should increase by the payment amount", payment, paymentProcessor.returnTotalPayments());
    }

    @Test
    public void disablePayment_stopsPaymentProcessing() {
        paymentProcessor.disablePayment();
        assertFalse("Payment processing should be stopped", paymentProcessor.returnIfPaymentStillProcessing());
    }

    @Test
    public void enablePayment_startsPaymentProcessing() {
        paymentProcessor.disablePayment(); 
        paymentProcessor.enablePayment();
        assertTrue("Payment processing should be started", paymentProcessor.returnIfPaymentStillProcessing());
    }

    @Test
    public void clearPayments_resetsCartValue() {
        paymentProcessor.clearPayments();
        assertEquals("Cart value should be reset", BigDecimal.ZERO, paymentProcessor.getCartValue());
    }

    @Test
    public void setCartValue_updatesCartValue() {
        BigDecimal newCartValue = new BigDecimal("15.0");
        paymentProcessor.setCartValue(newCartValue.doubleValue());
        assertEquals("Cart value should be updated", newCartValue, paymentProcessor.getCartValue());
    }


    @Test 
    public void testInsertCoinWhenStationTurnedOff() {
    	this.station.turnOff();
        try {
			paymentProcessor.insertCoin(coin);
		} catch (NoPowerException e) {
			return;
		} catch (DisabledException e) {
			fail("Wrong Exception");
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			fail("Wrong exception");
		}
        fail("Exception expected");
    }
    
    @Test 
    public void testInsertCoinWhenCoinSlotDisabled() {
    	this.station.coinSlot.disable();
        try {
			paymentProcessor.insertCoin(coin);
		} catch (NoPowerException e) {
			fail("Wrong Exception");
		} catch (DisabledException e) {
			return;
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			fail("Wrong exception");
		}
        fail("Exception expected");
    }


}
