package com.example.util;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import java.text.ParseException;

public class RsaCipher {

  private final RSAKey keyPair;

  public RsaCipher() {
    try {
      this.keyPair = new RSAKeyGenerator(4096).generate();
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  public String getPublicKey() {
    return keyPair.toPublicJWK().toJSONString();
  }

  public String encrypt(String data, String peerJwk) {

    try {
      JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM);
      Payload payload = new Payload(data);
      JWEObject jweObject = new JWEObject(header, payload);
      RSAKey peerPublicKey = JWK.parse(peerJwk).toRSAKey();
      jweObject.encrypt(new RSAEncrypter(peerPublicKey));
      return jweObject.serialize();
    } catch (ParseException | JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  public String decrypt(String jwe) {
    try {
      JWEObject jweObject = JWEObject.parse(jwe);
      jweObject.decrypt(new RSADecrypter(keyPair));
      return jweObject.getPayload().toString();
    } catch (ParseException | JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  public String keyPairJson() {
    return this.keyPair.toJSONString();
  }
}
