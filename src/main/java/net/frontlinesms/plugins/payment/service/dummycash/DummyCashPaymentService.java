package net.frontlinesms.plugins.payment.service.dummycash;

import java.math.BigDecimal;

import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.domain.OutgoingPayment;

import net.frontlinesms.data.domain.PersistableSettings;
import net.frontlinesms.plugins.payment.service.PaymentService;
import net.frontlinesms.plugins.payment.service.PaymentServiceException;
import net.frontlinesms.serviceconfig.ConfigurableService;
import net.frontlinesms.serviceconfig.ConfigurableServiceProperties;
import net.frontlinesms.serviceconfig.StructuredProperties;

@ConfigurableServiceProperties(name="DummyCash", icon="/icons/dummycash.png")
public class DummyCashPaymentService implements PaymentService {

	public StructuredProperties getPropertiesStructure() {
		// TODO Auto-generated method stub
		return null;
	}

	public PersistableSettings getSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSettings(PersistableSettings settings) {
		// TODO Auto-generated method stub

	}

	public Class<? extends ConfigurableService> getSuperType() {
		// TODO Auto-generated method stub
		return null;
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

	public void initSettings(PersistableSettings settings) {
		// TODO Auto-generated method stub

	}

	public boolean isOutgoingPaymentEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
