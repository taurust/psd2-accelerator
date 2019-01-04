package de.adorsys.psd2.sandbox.xs2a.service;

import static de.adorsys.psd2.sandbox.portal.testdata.TestDataService.PSU_ID;
import static de.adorsys.psd2.sandbox.portal.testdata.TestDataService.PSU_PASSWORD;
import static de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.FAILURE;
import static de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.SUCCESS;

import com.google.common.base.Charsets;
import de.adorsys.psd2.sandbox.xs2a.service.domain.SpiAspspAuthorisationData;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.sca.ChallengeData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthorisationService {

  /**
   * Abstract Implementation of requestAvailableScaMethods.
   *
   * @param spiPsuData       spiPsuData
   * @param password         password
   * @param aspspConsentData aspspConsentData
   */
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(
      SpiPsuData spiPsuData, String password, AspspConsentData aspspConsentData) {

    SpiAspspAuthorisationData accessToken = new SpiAspspAuthorisationData(PSU_ID, PSU_PASSWORD);

    byte[] payload = accessToken.toString().getBytes(Charsets.UTF_8);

    if (spiPsuData.getPsuId().equals(PSU_ID) && password.equals(PSU_PASSWORD)) {
      return SpiResponse.<SpiAuthorisationStatus>builder()
          .aspspConsentData(aspspConsentData)
          .payload(SUCCESS)
          .success();
    }

    return SpiResponse.<SpiAuthorisationStatus>builder()
        .aspspConsentData(aspspConsentData)
        .payload(FAILURE)
        .fail(SpiResponseStatus.UNAUTHORIZED_FAILURE);
  }

  /**
   * Abstract Implementation of requestAvailableScaMethods.
   *
   * @param aspspConsentData aspspConsentData
   */
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      AspspConsentData aspspConsentData) {
    SpiAuthenticationObject smsTan = new SpiAuthenticationObject();
    smsTan.setName("SMS_OTP");
    smsTan.setAuthenticationMethodId("SMS_OTP");
    SpiAuthenticationObject pushTan = new SpiAuthenticationObject();
    pushTan.setName("PUSH_OTP");
    pushTan.setAuthenticationMethodId("PUSH_OTP");
    List<SpiAuthenticationObject> spiMethods = new ArrayList<>(Arrays.asList(pushTan, smsTan));

    return SpiResponse.<List<SpiAuthenticationObject>>builder()
        .aspspConsentData(aspspConsentData)
        .payload(spiMethods)
        .success();
  }

  /**
   * Abstract Implementation of requestAuthorisationCode.
   *
   * @param selectedScaMethod selectedScaMethod
   * @param aspspConsentData  aspspConsentData
   */
  public SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      String selectedScaMethod, AspspConsentData aspspConsentData) {
    SpiAuthorizationCodeResult result = new SpiAuthorizationCodeResult();
    SpiAuthenticationObject selected = new SpiAuthenticationObject();
    selected.setAuthenticationMethodId(selectedScaMethod);
    result.setSelectedScaMethod(selected);
    result.setChallengeData(new ChallengeData()); // NPE otherwise
    return new SpiResponse<>(result, aspspConsentData);
  }
}