package com.example.payments;

import com.example.util.RsaCipher;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaymentsController {
  private final RestTemplate restTemplate;

  public PaymentsController() {
    this.restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @GetMapping("/")
  public String processRefunds() {
    var rsaCipher = new RsaCipher();

    String refundsJwe =
        restTemplate.postForObject(
            "http://localhost:8082/refunds", rsaCipher.getPublicKey(), String.class);
    var refundsJson = rsaCipher.decrypt(refundsJwe);

    System.out.println("Refunds JWE : " + refundsJwe);
    System.out.println("Payments key pair: " + rsaCipher.keyPairJson());
    System.out.println("Decrypted Refunds ...");
    System.out.println(refundsJson);
    return refundsJson;
  }
}
