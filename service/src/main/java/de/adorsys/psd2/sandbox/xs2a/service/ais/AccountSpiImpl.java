package de.adorsys.psd2.sandbox.xs2a.service.ais;

import de.adorsys.psd2.sandbox.portal.testdata.TestDataService;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Balance;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountBalance;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiBalanceType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransactionReport;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiAmount;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import de.adorsys.psd2.xs2a.spi.service.AccountSpi;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AccountSpiImpl implements AccountSpi {

  private TestDataService testDataService;

  public AccountSpiImpl(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  @Override
  public SpiResponse<List<SpiAccountDetails>> requestAccountList(
      @NotNull SpiContextData ctx,
      boolean b,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    List<SpiAccountReference> accountList = spiAccountConsent.getAccess().getAccounts();
    List<String> ibans = new ArrayList<>();

    for (SpiAccountReference account : accountList) {
      ibans.add(account.getIban());
    }

    Optional<String> optionalPsuId = testDataService.getPsuByIban(ibans.get(0));

    if (!optionalPsuId.isPresent()) {
      return SpiResponse.<List<SpiAccountDetails>>builder()
          .aspspConsentData(aspspConsentData)
          .fail(SpiResponseStatus.TECHNICAL_FAILURE);
    }

    Optional<List<Account>> optionalAccounts = testDataService
        .getRequestedAccounts(optionalPsuId.get(), ibans);

    if (!optionalAccounts.isPresent()) {
      return SpiResponse.<List<SpiAccountDetails>>builder()
          .aspspConsentData(aspspConsentData)
          .fail(SpiResponseStatus.TECHNICAL_FAILURE);
    }

    List<SpiAccountDetails> spiAccountDetails = optionalAccounts.get().stream()
        .map(this::mapAccountToSpiAccount)
        .collect(Collectors.toList());

    return SpiResponse.<List<SpiAccountDetails>>builder()
        .aspspConsentData(aspspConsentData)
        .payload(spiAccountDetails)
        .success();
  }

  private SpiAccountDetails mapAccountToSpiAccount(Account account) {
    return new SpiAccountDetails(
        account.getAccountId(),
        "",
        account.getIban(),
        "",
        "",
        "",
        "",
        account.getCurrency(),
        "",
        account.getProduct(),
        SpiAccountType.getByValue(account.getCashAccountType().value()).get(),
        null,
        "",
        "",
        null,
        "",
        new ArrayList<>(Arrays.asList(mapBalanceToSpiBalance(account.getBookedBalance()),
            mapBalanceToSpiBalance(account.getAvailableBalance()))));
  }

  private SpiAccountBalance mapBalanceToSpiBalance(Balance balance) {
    SpiAccountBalance spiAccountBalance = new SpiAccountBalance();

    SpiAmount spiAmount = new SpiAmount(balance.getBalanceAmount().getCurrency(),
        balance.getBalanceAmount().getAmount());
    spiAccountBalance.setSpiBalanceAmount(spiAmount);
    spiAccountBalance.setSpiBalanceType(SpiBalanceType.AVAILABLE);

    return spiAccountBalance;
  }

  @Override
  public SpiResponse<SpiAccountDetails> requestAccountDetailForAccount(
      @NotNull SpiContextData ctx,
      boolean b,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<SpiAccountDetails>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }

  @Override
  public SpiResponse<SpiTransactionReport> requestTransactionsForAccount(
      @NotNull SpiContextData ctx,
      String s, boolean b,
      @NotNull LocalDate localDate, @NotNull LocalDate localDate1,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent, @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<SpiTransactionReport>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }

  @Override
  public SpiResponse<SpiTransaction> requestTransactionForAccountByTransactionId(
      @NotNull SpiContextData ctx,
      @NotNull String s, @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<SpiTransaction>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }

  @Override
  public SpiResponse<List<SpiAccountBalance>> requestBalancesForAccount(
      @NotNull SpiContextData ctx,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<List<SpiAccountBalance>>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }
}
