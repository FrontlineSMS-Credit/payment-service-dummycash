DummyCash Implementation Notes

What we did:
# create project structure
# updated `src/main/resources/META-INF/frontlinesms/PaymentServices`
# create `ServiceLoaderTest` to make sure `PaymentService` implementation will be loaded
# create outline `PaymentService` implementation at `src/main/java/net/frontlinesms/plugins/payment/service/${paymentServiceName.toLowerCase()}`
# fill in POM details
# add new service to dist pom so that it is included on classpath
# annotate PaymentService class with @ConfigurableServiceProperties
# create icon and place it at location specified in @ConfigurableServiceProperties annotation (`src/main/resources/icons/${serviceName}.png`)
# implement methods in `${paymentServiceName}PaymentService`