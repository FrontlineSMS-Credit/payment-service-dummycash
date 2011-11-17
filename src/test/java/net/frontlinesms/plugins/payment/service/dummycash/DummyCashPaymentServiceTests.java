package net.frontlinesms.plugins.payment.service.dummycash;

import java.math.BigDecimal;

import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.domain.IncomingPayment;
import org.creditsms.plugins.paymentview.data.domain.OutgoingPayment;
import org.creditsms.plugins.paymentview.data.domain.OutgoingPayment.Status;
import org.creditsms.plugins.paymentview.data.repository.IncomingPaymentDao;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import net.frontlinesms.data.domain.PersistableSettings;
import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.plugins.payment.service.PaymentJob;
import net.frontlinesms.plugins.payment.service.PaymentService;
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
		
		when(httpJobber.get(eq(TEST_URL + "/send/"), (String[]) anyVararg())).thenReturn("OK");

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
	
	public void testMakePaymentFailure_errorUsernameOrPassword() throws Exception {
		// given
		String destinationPhoneNumber = "+23456789";
		String paymentAmount = "123.45";
		
		Client dummyClient = mock(Client.class);
		when(dummyClient.getPhoneNumber()).thenReturn(destinationPhoneNumber);
		
		OutgoingPayment dummyOutgoingPayment = mock(OutgoingPayment.class);
		when(dummyOutgoingPayment.getClient()).thenReturn(dummyClient);
		when(dummyOutgoingPayment.getAmountPaid()).thenReturn(new BigDecimal(paymentAmount));
		
		when(httpJobber.get(eq(TEST_URL + "/send/"), (String[]) anyVararg())).thenReturn("ERROR: Username or password incorrect.");

		// when
		s.makePayment(dummyOutgoingPayment);
		waitForBackgroundJob();
		
		// then
		verify(httpJobber).get(TEST_URL + "/send/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD,
				"to", destinationPhoneNumber,
				"amount", paymentAmount});
		
		verify(dummyOutgoingPayment).setStatus(Status.ERROR);
	}
	
	public void testMakePaymentFailure_errorCredit() throws Exception {
		// given
		String destinationPhoneNumber = "+23456789";
		String paymentAmount = "123.45";
		
		Client dummyClient = mock(Client.class);
		when(dummyClient.getPhoneNumber()).thenReturn(destinationPhoneNumber);
		
		OutgoingPayment dummyOutgoingPayment = mock(OutgoingPayment.class);
		when(dummyOutgoingPayment.getClient()).thenReturn(dummyClient);
		when(dummyOutgoingPayment.getAmountPaid()).thenReturn(new BigDecimal(paymentAmount));
		
		when(httpJobber.get(eq(TEST_URL + "/send/"), (String[]) anyVararg())).thenReturn("ERROR: Not enough credit in account.");

		// when
		s.makePayment(dummyOutgoingPayment);
		waitForBackgroundJob();
		
		// then
		verify(httpJobber).get(TEST_URL + "/send/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD,
				"to", destinationPhoneNumber,
				"amount", paymentAmount});
		
		verify(dummyOutgoingPayment).setStatus(Status.ERROR);
	}
	
	public void testMakePaymentFailure_badResponse() throws Exception {
		// given
		String destinationPhoneNumber = "+23456789";
		String paymentAmount = "123.45";
		
		Client dummyClient = mock(Client.class);
		when(dummyClient.getPhoneNumber()).thenReturn(destinationPhoneNumber);
		
		OutgoingPayment dummyOutgoingPayment = mock(OutgoingPayment.class);
		when(dummyOutgoingPayment.getClient()).thenReturn(dummyClient);
		when(dummyOutgoingPayment.getAmountPaid()).thenReturn(new BigDecimal(paymentAmount));
		
		when(httpJobber.get(eq(TEST_URL + "/send/"), (String[]) anyVararg())).thenReturn("THISISNOTAPROPERRESPONSE");

		// when
		s.makePayment(dummyOutgoingPayment);
		waitForBackgroundJob();
		
		// then
		verify(httpJobber).get(TEST_URL + "/send/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD,
				"to", destinationPhoneNumber,
				"amount", paymentAmount});
		
		verify(dummyOutgoingPayment).setStatus(Status.ERROR);
	}
	
	public void testMakePaymentFailure_serverUnavailable() throws Exception {
		// given
		String destinationPhoneNumber = "+23456789";
		String paymentAmount = "123.45";
		
		Client dummyClient = mock(Client.class);
		when(dummyClient.getPhoneNumber()).thenReturn(destinationPhoneNumber);
		
		OutgoingPayment dummyOutgoingPayment = mock(OutgoingPayment.class);
		when(dummyOutgoingPayment.getClient()).thenReturn(dummyClient);
		when(dummyOutgoingPayment.getAmountPaid()).thenReturn(new BigDecimal(paymentAmount));
		
		when(httpJobber.get(eq(TEST_URL + "/send/"), (String[]) anyVararg())).thenThrow(new DummyCashServerCommsException());

		// when
		s.makePayment(dummyOutgoingPayment);
		waitForBackgroundJob();
		
		// then
		verify(httpJobber).get(TEST_URL + "/send/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD,
				"to", destinationPhoneNumber,
				"amount", paymentAmount});
		
		verify(dummyOutgoingPayment).setStatus(Status.ERROR);
	}

	public void testBalance() throws Exception {
		// given
		when(httpJobber.get(eq(TEST_URL + "/balance/"), (String[]) anyVararg())).thenReturn("0", "20500.20", "-399.99");

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
	
	public void testCheckBalanceFailure() throws Exception {
		// given
		when(httpJobber.get(eq(TEST_URL + "/balance/"), (String[]) anyVararg())).thenAnswer(new Answer<String>() {
			private int counter;
			public String answer(InvocationOnMock invocation) throws Throwable {
				switch(++counter) {
					case 1: return "12";
					case 2: throw new DummyCashServerCommsException();
					case 3: return "350.25";
					default: throw new RuntimeException();
				}
			}
		});

		// when
		s.checkBalance();
		// then
		waitForBackgroundJob();
		assertEquals("12", s.getBalanceAmount().toString());
		
		// when
		s.checkBalance();
		// then
		waitForBackgroundJob();
		assertEquals("12", s.getBalanceAmount().toString());
		
		// when
		s.checkBalance();
		// then
		waitForBackgroundJob();
		assertEquals("350.25", s.getBalanceAmount().toString());
		
		verify(httpJobber, times(3)).get(TEST_URL + "/balance/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD});
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

	public void testIncomingPaymentProcessing() throws Exception {
		// setup
		when(httpJobber.get(eq(TEST_URL + "/incoming/"), (String[]) anyVararg())).
				thenReturn("[ {\"amount\":\"400\", \"sender\":\"asdfghjk\", \"date\":\"2011-10-06 12:29:11 +0100\"}, {\"amount\":\"12\", \"sender\":\"0324567\", \"date\":\"2011-10-06 12:24:11 -0200\"} ]", "[]");
		IncomingPaymentDao dao = mock(IncomingPaymentDao.class);
		s.setIncomingDao(dao);
		
		// when
		s.doCheckForIncomingPayments();
		
		// then
		verify(httpJobber).get(TEST_URL + "/incoming/", new String[] {
				"u", TEST_USERNAME,
				"p", TEST_PASSWORD});
		waitForBackgroundJob();
		verify(dao, times(2)).saveIncomingPayment(any(IncomingPayment.class));
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
