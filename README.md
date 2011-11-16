DummyCash Implementation Notes

What we did:
# create project structure
# updated `src/main/resources/META-INF/frontlinesms/PaymentServices`
# create `ServiceLoaderTest` to make sure `PaymentService` implementation will be loaded
# create outline `PaymentService` implementation at `src/main/java/net/frontlinesms/plugins/payment/service/${paymentServiceName.toLowerCase()}.${paymenServiceName}PaymentService.java`
# fill in POM details
# add new service to dist pom so that it is included on classpath
# annotate PaymentService class with @ConfigurableServiceProperties

    @ConfigurableServiceProperties(name="${paymentServiceName}", icon="/icons/${paymentServiceName.toLowerCase()}.png")

# create icon and place it at location specified in @ConfigurableServiceProperties annotation (`src/main/resources/icons/${serviceName}.png`)
# implement methods in `${paymentServiceName}PaymentService`
# add properties to PaymentService class for use in settings
# add translation strings for properties to paymentview project in `src/main/resources/org/creditsms/plugins/paymentview/PaymentViewPluginControllerText.properties`
    TODO these should be moved into the individual plugins!!!

# create unit test class for `${}PaymentService` at `src/test/java/net/frontlinesms/plugins/payment/service/`

	package ${packageName};

	import net.frontlinesms.junit.BaseTestCase;
	import net.frontlinesms.plugins.payment.service.PaymentService;
	import net.frontlinesms.plugins.payment.service.PaymentServiceException;

	public class ${className}Tests extends BaseTestCase {
		/** The {@link PaymentService} under test. */
		private ${className} s;

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
			assert<True|False>(s.isOutgoingPaymentEnabled());
		}
	}
