package android.net.http;

import android.net.ParseException;
import android.net.WebAddress;
import android.webkit.CookieManager;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;

/* loaded from: RequestHandle.class */
public class RequestHandle {
    private String mUrl;
    private WebAddress mUri;
    private String mMethod;
    private Map<String, String> mHeaders;
    private RequestQueue mRequestQueue;
    private Request mRequest;
    private InputStream mBodyProvider;
    private int mBodyLength;
    private int mRedirectCount;
    private Connection mConnection;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PROXY_AUTHORIZATION_HEADER = "Proxy-Authorization";
    public static final int MAX_REDIRECT_COUNT = 16;

    public RequestHandle(RequestQueue requestQueue, String url, WebAddress uri, String method, Map<String, String> headers, InputStream bodyProvider, int bodyLength, Request request) {
        this.mRedirectCount = 0;
        this.mHeaders = headers == null ? new HashMap() : headers;
        this.mBodyProvider = bodyProvider;
        this.mBodyLength = bodyLength;
        this.mMethod = method == null ? "GET" : method;
        this.mUrl = url;
        this.mUri = uri;
        this.mRequestQueue = requestQueue;
        this.mRequest = request;
    }

    public RequestHandle(RequestQueue requestQueue, String url, WebAddress uri, String method, Map<String, String> headers, InputStream bodyProvider, int bodyLength, Request request, Connection conn) {
        this(requestQueue, url, uri, method, headers, bodyProvider, bodyLength, request);
        this.mConnection = conn;
    }

    public void cancel() {
        if (this.mRequest != null) {
            this.mRequest.cancel();
        }
    }

    public void pauseRequest(boolean pause) {
        if (this.mRequest != null) {
            this.mRequest.setLoadingPaused(pause);
        }
    }

    public void handleSslErrorResponse(boolean proceed) {
        if (this.mRequest != null) {
            this.mRequest.handleSslErrorResponse(proceed);
        }
    }

    public boolean isRedirectMax() {
        return this.mRedirectCount >= 16;
    }

    public int getRedirectCount() {
        return this.mRedirectCount;
    }

    public void setRedirectCount(int count) {
        this.mRedirectCount = count;
    }

    public boolean setupRedirect(String redirectTo, int statusCode, Map<String, String> cacheHeaders) {
        this.mHeaders.remove("Authorization");
        this.mHeaders.remove("Proxy-Authorization");
        int i = this.mRedirectCount + 1;
        this.mRedirectCount = i;
        if (i == 16) {
            this.mRequest.error(-9, R.string.httpErrorRedirectLoop);
            return false;
        }
        if (this.mUrl.startsWith("https:") && redirectTo.startsWith("http:")) {
            this.mHeaders.remove("Referer");
        }
        this.mUrl = redirectTo;
        try {
            this.mUri = new WebAddress(this.mUrl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.mHeaders.remove("Cookie");
        String cookie = CookieManager.getInstance().getCookie(this.mUri);
        if (cookie != null && cookie.length() > 0) {
            this.mHeaders.put("Cookie", cookie);
        }
        if ((statusCode == 302 || statusCode == 303) && this.mMethod.equals("POST")) {
            this.mMethod = "GET";
        }
        if (statusCode == 307) {
            try {
                if (this.mBodyProvider != null) {
                    this.mBodyProvider.reset();
                }
            } catch (IOException e2) {
                return false;
            }
        } else {
            this.mHeaders.remove("Content-Type");
            this.mBodyProvider = null;
        }
        this.mHeaders.putAll(cacheHeaders);
        createAndQueueNewRequest();
        return true;
    }

    public void setupBasicAuthResponse(boolean isProxy, String username, String password) {
        String response = computeBasicAuthResponse(username, password);
        this.mHeaders.put(authorizationHeader(isProxy), "Basic " + response);
        setupAuthResponse();
    }

    public void setupDigestAuthResponse(boolean isProxy, String username, String password, String realm, String nonce, String QOP, String algorithm, String opaque) {
        String response = computeDigestAuthResponse(username, password, realm, nonce, QOP, algorithm, opaque);
        this.mHeaders.put(authorizationHeader(isProxy), "Digest " + response);
        setupAuthResponse();
    }

    private void setupAuthResponse() {
        try {
            if (this.mBodyProvider != null) {
                this.mBodyProvider.reset();
            }
        } catch (IOException e) {
        }
        createAndQueueNewRequest();
    }

    public String getMethod() {
        return this.mMethod;
    }

    public static String computeBasicAuthResponse(String username, String password) {
        Assert.assertNotNull(username);
        Assert.assertNotNull(password);
        return new String(Base64.encodeBase64((username + ':' + password).getBytes()));
    }

    public void waitUntilComplete() {
        this.mRequest.waitUntilComplete();
    }

    public void processRequest() {
        if (this.mConnection != null) {
            this.mConnection.processRequests(this.mRequest);
        }
    }

    private String computeDigestAuthResponse(String username, String password, String realm, String nonce, String QOP, String algorithm, String opaque) {
        Assert.assertNotNull(username);
        Assert.assertNotNull(password);
        Assert.assertNotNull(realm);
        String A1 = username + Separators.COLON + realm + Separators.COLON + password;
        String A2 = this.mMethod + Separators.COLON + this.mUrl;
        String cnonce = computeCnonce();
        String digest = computeDigest(A1, A2, nonce, QOP, "00000001", cnonce);
        String response = "username=" + doubleQuote(username) + ", ";
        String response2 = (((response + "realm=" + doubleQuote(realm) + ", ") + "nonce=" + doubleQuote(nonce) + ", ") + "uri=" + doubleQuote(this.mUrl) + ", ") + "response=" + doubleQuote(digest);
        if (opaque != null) {
            response2 = response2 + ", opaque=" + doubleQuote(opaque);
        }
        if (algorithm != null) {
            response2 = response2 + ", algorithm=" + algorithm;
        }
        if (QOP != null) {
            response2 = response2 + ", qop=" + QOP + ", nc=00000001, cnonce=" + doubleQuote(cnonce);
        }
        return response2;
    }

    public static String authorizationHeader(boolean isProxy) {
        if (!isProxy) {
            return "Authorization";
        }
        return "Proxy-Authorization";
    }

    private String computeDigest(String A1, String A2, String nonce, String QOP, String nc, String cnonce) {
        if (QOP == null) {
            return KD(H(A1), nonce + Separators.COLON + H(A2));
        }
        if (QOP.equalsIgnoreCase("auth")) {
            return KD(H(A1), nonce + Separators.COLON + nc + Separators.COLON + cnonce + Separators.COLON + QOP + Separators.COLON + H(A2));
        }
        return null;
    }

    private String KD(String secret, String data) {
        return H(secret + Separators.COLON + data);
    }

    private String H(String param) {
        if (param != null) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] d = md5.digest(param.getBytes());
                if (d != null) {
                    return bufferToHex(d);
                }
                return null;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private String bufferToHex(byte[] buffer) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        if (buffer != null) {
            int length = buffer.length;
            if (length > 0) {
                StringBuilder hex = new StringBuilder(2 * length);
                for (int i = 0; i < length; i++) {
                    byte l = (byte) (buffer[i] & 15);
                    byte h = (byte) ((buffer[i] & 240) >> 4);
                    hex.append(hexChars[h]);
                    hex.append(hexChars[l]);
                }
                return hex.toString();
            }
            return "";
        }
        return null;
    }

    private String computeCnonce() {
        Random rand = new Random();
        int nextInt = rand.nextInt();
        return Integer.toString(nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt), 16);
    }

    private String doubleQuote(String param) {
        if (param != null) {
            return Separators.DOUBLE_QUOTE + param + Separators.DOUBLE_QUOTE;
        }
        return null;
    }

    private void createAndQueueNewRequest() {
        if (this.mConnection != null) {
            RequestHandle newHandle = this.mRequestQueue.queueSynchronousRequest(this.mUrl, this.mUri, this.mMethod, this.mHeaders, this.mRequest.mEventHandler, this.mBodyProvider, this.mBodyLength);
            this.mRequest = newHandle.mRequest;
            this.mConnection = newHandle.mConnection;
            newHandle.processRequest();
            return;
        }
        this.mRequest = this.mRequestQueue.queueRequest(this.mUrl, this.mUri, this.mMethod, this.mHeaders, this.mRequest.mEventHandler, this.mBodyProvider, this.mBodyLength).mRequest;
    }
}