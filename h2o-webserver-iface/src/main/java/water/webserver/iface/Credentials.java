package water.webserver.iface;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Representation of the User-Password pair
 */
public class Credentials {

  private final String _user;
  private final String _password;

  public Credentials(String _user, String _password) {
    this._user = _user;
    this._password = _password;
  }

  public String toBasicAuth() {
    return "Basic " + base64EncodeToString(_user + ":" + _password);
  }

  public String toHashFileEntry() {
    return _user + ": " + credentialMD5digest(_password) + "\n";
  }

  public String toDebugString() {
    return "Credentials[_user='" + _user + "', _password='" + _password + "']";
  }

  /**
   * This replaces Jetty's B64Code.encode().
   */
  private static String base64EncodeToString(String s) {
    final byte[] bytes = s.getBytes(StandardCharsets.ISO_8859_1);
    return Base64.encodeBase64String(bytes);
  }

  // following part is copied out of Jetty's class org.eclipse.jetty.util.security.Credential$MD5, because we cannot depend on the library

  private static final String __TYPE = "MD5:";
  private static final Object __md5Lock = new Object();

  private static MessageDigest __md;

  /**
   * This replaces Jetty's Credential.MD5.digest().
   */
  private static String credentialMD5digest(String password) {
    try {
      byte[] digest;
      synchronized (__md5Lock) {
        if (__md == null) {
          try {
            __md = MessageDigest.getInstance("MD5");
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
        }

        __md.reset();
        __md.update(password.getBytes(StandardCharsets.ISO_8859_1));
        digest = __md.digest();
      }

      return __TYPE + toString(digest, 16);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static String toString(byte[] bytes, int base)
  {
    StringBuilder buf = new StringBuilder();
    for (byte b : bytes)
    {
      int bi=0xff&b;
      int c='0'+(bi/base)%base;
      if (c>'9')
        c= 'a'+(c-'0'-10);
      buf.append((char)c);
      c='0'+bi%base;
      if (c>'9')
        c= 'a'+(c-'0'-10);
      buf.append((char)c);
    }
    return buf.toString();
  }

}
