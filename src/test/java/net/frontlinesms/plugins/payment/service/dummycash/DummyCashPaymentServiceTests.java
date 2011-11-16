package net.frontlinesms.plugins.payment.service.dummycash;

import java.math.BigDecimal;

import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.domain.OutgoingPayment;
import org.creditsms.plugins.paymentview.data.domain.OutgoingPayment.Status;

import net.frontlinesms.data.domain.PersistableSettings;
import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.plugins.payment.service.PaymentJob;
import net.frontlinesms.plugins.payment.service.PaymentService;
import net.frontlinesms.plugins.payment.service.PaymentServiceException;
import net.frontlinesms.serviceconfig.PasswordString;

import static org.mockito.Mockito.*;

public class DummyCashPaymentServiceTests extends BaseTestCase {
	private static final String TEST_USERNAME = "barry";
	private static final String TEST_PASSWORD = "white";
	private static final String TEST_URL = "no-protocol:example";
	
	/** The {@link PaymentService} under test. */
	private DummyCashPaymentService s;
	private DummyCashHttpJobber httpJobber;

	@Override
	protected void setUp() throws Exception {
		s = new DummyCashPaymentService();
		PersistableSettings settings = new PersistableSettings(s.getSuperType(), s.getClass());
		settings.set(DummyCashPaymentService.PROPERTY_USERNAME, TEST_USERNAME);
		settings.set(DummyCashPaymentService.PROPERTY_PASSWORD, new PasswordString(TEST_PASSWORD));
		settings.set(DummyCashPaymentService.PROPERTY_SERVER_URL, TEST_URL);
		settings.set(DummyCashPaymentService.PROPERTY_OUTGOING_ENABLED, true);
		settings.set(DummyCashPaymentService.PROPERTY_BALANCE, new BigDecimal("0"));
		s.setSettings(settings);
		
		httpJobber = mock(DummyCashHttpJobber.class);
		s.setHttpJobber(httpJobber);
		
		s.startService();
	}
	
	@Override
	protected void tearDown() throws Exception {
		s.stopService();
	}
	
	public void testMakePayment() throws Exception {
		// given
		String destinationPhoneNumber = "+23456789";
		String paymentAmount = "123.45";
		
		Client dummyClient = mock(Client.class);
		when(dummyClient.getPhoneNumber()).thenReturn(destinationPhoneNumber);
		
		OutgoingPayment dummyOutgoingPayment = mock(OutgoingPayment.class);
		when(dummyOutgoingPayment.getClient()).thenReturn(dummyClient);
		when(dummyOutgoingPayment.getAmountPaid()).thenReturn(new BigDecimal(paymentAmount));
		
		when(httpJobber.get(anyString(), (String[]) anyVararg())).thenReturn("OK");

		// when
		s.makePayment(dummyOutgoingPayment);
		waitForBackgroundJob();
		
		// then
		verify(httpJobber).get(TEST_URL + "/send/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD,
				"to", destinationPhoneNumber,
				"amount", paymentAmount});
		
		verify(dummyOutgoingPayment).setStatus(Status.CONFIRMED);
	}
	
	public void testMakePaymentFailure() {
		throw new IllegalStateException("Please implement this method.");
	}

	public void testBalance() throws PaymentServiceException {
		// given
		when(httpJobber.get(anyString(), (String[]) anyVararg())).thenReturn("0", "20500.20", "-399.99");

		// when
		s.checkBalance();
		// then
		waitForBackgroundJob();
		assertEquals("0", s.getBalanceAmount().toString());
		
		// when
		s.checkBalance();
		// then
		waitForBackgroundJob();
		assertEquals("20500.20", s.getBalanceAmount().toString());
		
		// when
		s.checkBalance();
		// then
		waitForBackgroundJob();
		assertEquals("-399.99", s.getBalanceAmount().toString());
		
		verify(httpJobber, times(3)).get(TEST_URL + "/balance/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD});
	}
	
	public void testCheckBalanceFailure() {
		throw new IllegalStateException("Please implement this method.");
	}

	public void testIsOutgoingPaymentEnabled() {
		assertTrue(s.isOutgoingPaymentEnabled());
		
		s.setOutgoingPaymentEnabled(false);
		assertFalse(s.isOutgoingPaymentEnabled());
		
		s.setOutgoingPaymentEnabled(true);
		assertTrue(s.isOutgoingPaymentEnabled());
	}

	private void waitForBackgroundJob() {
		WaitingJob.waitForEvent(s);
	}

	public void testIncomingPaymentProcessing() {
		throw new IllegalStateException("Please implement this method.");
	}
}

class WaitingJob implements PaymentJob {
	private final DummyCashPaymentService s;
	private boolean running;
	
	private WaitingJob(DummyCashPaymentService s) {
		assert s != null: "Please set a payment service.";
		this.s = s;
	}
	
	private void block() {
		running = true;
		s.queueJob(this);
		while(running) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}
	
	public void run() {
		running = false;
	}
	
	/** Put a job on the UI event queue, and block until it has been run. */
	public static void waitForEvent(DummyCashPaymentService s) {
		new WaitingJob(s).block();
	}
}
