package net.frontlinesms.plugins.payment.service.dummycash;

import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.plugins.payment.service.PaymentService;
import net.frontlinesms.plugins.payment.service.PaymentServiceException;

public class DummyCashPaymentServiceTests extends BaseTestCase {
	/** The {@link PaymentService} under test. */
	private DummyCashPaymentService s;

	@Override
	protected void setUp() throws Exception {
		s = new DummyCashPaymentService();
	}
	
	public void testMakePayment() throws PaymentServiceException {
		throw new IllegalStateException("This test is not yet implemented.");
	}

	public void checkBalance() throws PaymentServiceException {
		throw new IllegalStateException("This test is not yet implemented.");
	}

	public void testGetBalanceAmount() {
		throw new IllegalStateException("This test is not yet implemented.");
	}

	public void testStartService() throws PaymentServiceException {
		throw new IllegalStateException("This test is not yet implemented.");
	}

	public void testStopService() {
		throw new IllegalStateException("This test is not yet implemented.");
	}

	public void testIsOutgoingPaymentEnabled() {
		assertTrue(s.isOutgoingPaymentEnabled());
	}
}
