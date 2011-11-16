package net.frontlinesms.plugins.payment.service.dummycash;

import java.math.BigDecimal;

import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.domain.OutgoingPayment;

import net.frontlinesms.data.domain.PersistableSettings;
import net.frontlinesms.plugins.payment.service.PaymentService;
import net.frontlinesms.plugins.payment.service.PaymentServiceException;
import net.frontlinesms.serviceconfig.ConfigurableService;
import net.frontlinesms.serviceconfig.ConfigurableServiceProperties;
import net.frontlinesms.serviceconfig.PasswordString;
import net.frontlinesms.serviceconfig.StructuredProperties;

@ConfigurableServiceProperties(name="DummyCash", icon="/icons/dummycash.png")
public class DummyCashPaymentService implements PaymentService {
	/** Prefix attached to every property name. */
	private static final String PROPERTY_PREFIX = "plugins.payment.service.dummycash.";

	protected static final String PROPERTY_USERNAME = PROPERTY_PREFIX + "username";
	protected static final String PROPERTY_PASSWORD = PROPERTY_PREFIX + "password";
	protected static final String PROPERTY_SERVER_URL = PROPERTY_PREFIX + "server.url";

	private PersistableSettings settings;

	public StructuredProperties getPropertiesStructure() {
		StructuredProperties defaultSettings = new StructuredProperties();
		defaultSettings.put(PROPERTY_USERNAME, "Nathan");
		defaultSettings.put(PROPERTY_PASSWORD, new PasswordString("secret"));
		defaultSettings.put(PROPERTY_SERVER_URL, "http://localhost:8080/dummycash");
		return defaultSettings;
	}

	public PersistableSettings getSettings() {
		return settings;
	}

	public void setSettings(PersistableSettings settings) {
		this.settings = settings;
	}

	public Class<? extends ConfigurableService> getSuperType() {
		return PaymentService.class;
	}

	public void makePayment(Client client, OutgoingPayment outgoingPayment)
			throws PaymentServiceException {
		// TODO Auto-generated method stub

	}

	public void checkBalance() throws PaymentServiceException {
		// TODO Auto-generated method stub

	}

	public BigDecimal getBalanceAmount() {
		// TODO Auto-generated method stub
		return null;
	}

	public void startService() throws PaymentServiceException {
		// TODO Auto-generated method stub

	}

	public void stopService() {
		// TODO Auto-generated method stub

	}

	public boolean isOutgoingPaymentEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
