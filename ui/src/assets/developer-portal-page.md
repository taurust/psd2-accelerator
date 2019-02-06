# Developer Portal

---

## Introduction

The [Payment Service Directive 2 (PSD2)](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN)
instructs banks to provide a fully productive Access-to-Account (XS2A)
interface to Third Party Providers (TPPs) until September 2019. XS2A
itself consists of banking services to initiate payments (PIS), request
account data (AIS) and get the confirmation of the availability of funds
(PIIS). In order to guarantee the compliance of this deadline due to
adaptions and bugs, PSD2 claims the banks to provide a functional
sandbox offering the XS2A services in a non-productive environment until
March 2019.

Central component of the PSD2 Accelerator is the XS2A interface which
meets the requirements of the [Berlin Group](https://www.berlin-group.org/)
(Version 1.2) and is based on test data. Besides the actual interface,
PSD2 instructs banks to offer a technical documentation free of charge containing
amongst others, information about supported payment products and payment
services.

Usually, before accessing the XS2A services a TPP would need to register
at its National Competent Authority (NCA) and request an
[eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN)
certificate at an appropriate Trust Service Provider (TSP). Issuing a
real certificate just for testing purposes would be too much effort,
which is why the PSD2 Accelerator is additionally simulating a fictional
TSP issuing Qualified Website Authentication Certificates (QWAC). A QWAC
is part of eIDAS and might be better known as [X.509](https://www.ietf.org/rfc/rfc3739.txt)
certificate. For PSD2-purposes the certificate gets extended by the QcStatement
containing appropriate values such as the role(s) of the PSP (see
[ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

After embedding the QWAC in the actual XS2A request, the role and the
signature get validated at a central reverse proxy before it gets
finally passed to the interface where the banking logic happens. You can
visit our documentation here [XS2A Swagger UI](./assets/resources/swagger-ui.html).

The described components with their connection to each other are
displayed in Figure 1.1:

![PSD2 Accelerator](assets/accelerator.svg 'Figure 1.1:')\
_Components of the PSD2 Accelerator_

### Active XS2A Configuration (Bank Profile)

```
        ---
        setting:
          frequencyPerDay: 5
          combinedServiceIndicator: false
          scaApproach: REDIRECT
          tppSignatureRequired: false
          bankOfferedConsentSupport: false
          pisRedirectUrlToAspsp: https://sandbox-api.dev.adorsys.de/v1/online-banking/init/pis/:redirect-id
          pisPaymentCancellationRedirectUrlToAspsp: https://sandbox-api.dev.adorsys.de/v1/online-banking/cancel/pis/:redirect-id
          aisRedirectUrlToAspsp: https://sandbox-api.dev.adorsys.de/v1/online-banking/init/ais/:redirect-id
          multicurrencyAccountLevel: SUBACCOUNT
          availableBookingStatuses:
            - BOOKED
            - PENDING
          supportedAccountReferenceFields:
            - MSISDN
          consentLifetime: 0
          transactionLifetime: 0
          allPsd2Support: false
          transactionsWithoutBalancesSupported: false
          signingBasketSupported: false
          paymentCancellationAuthorizationMandated: true
          piisConsentSupported: false
          deltaReportSupported: false
          redirectUrlExpirationTimeMs: 600000
          notConfirmedConsentExpirationPeriodMs: 86400000
          notConfirmedPaymentExpirationPeriodMs: 86400000
          supportedPaymentTypeAndProductMatrix:
            SINGLE:
              - sepa-credit-transfers
            PERIODIC:
              - sepa-credit-transfers
          paymentCancellationRedirectUrlExpirationTimeMs: 600000
```

---

## Getting started

In order to test the services of XS2A you need to execute the following
steps:

- To generate a QWAC Certificate follow this link [Certificate Service UI](./certificate-service) and
  embed the .pem and .key files in your requests.
- The most popular use cases are described in the following section.

---

## Use Cases with PSU Data

To execute services of XS2A using the EMBEDDED SCA approach, you have to
use these global PSU credentials:

- Password: 12345
- TAN: 54321
  The REST API is available here: [https://sandbox-api.dev.adorsys.de](https://sandbox-api.dev.adorsys.de).

Each use case contains a table with the fitting PSU-IDs, the Iban and
the expected status. Therefore, you can check if your outcome is
correct. Furthermore, some use cases contain an example request and its
response.

### Simulation of SCA

SCA is mandated for the initiation and cancellation of payments as well
as for the creation of consents. To simulate the different SCA
responses, PSU-IDs and corresponding Ibans are defined. A successful SCA
can therefore only be achieved when performing the requests with the
Iban "DE11760365688833114935" and the corresponding PSU-ID
"PSU-Successful". The same goes for all other combinations of PSUs.
After sending your payment or consent request, you receive a scaRedirect
url. Insert this url in your browser and attach your PSU-ID as
Query-Parameter to the url. And example would be:
`?psu-id=PSU-Successful`
