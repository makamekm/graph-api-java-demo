package com.hendisantika.springboot.swagger.graph;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// import java.io.IOException;
// import java.net.MalformedURLException;
// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.Set;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.function.Consumer;

// import com.microsoft.aad.msal4j.DeviceCode;
// import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
// import com.microsoft.aad.msal4j.IAccount;
// import com.microsoft.aad.msal4j.IAuthenticationResult;
// import com.microsoft.aad.msal4j.MsalException;
// import com.microsoft.aad.msal4j.PublicClientApplication;
// import com.microsoft.aad.msal4j.SilentParameters;
// import com.microsoft.aad.msal4j.UserNamePasswordParameters;

/**
 * Authentication
 */
public class Authentication {

    private static String clientId = Constants.clientId;

    private static String authority = Constants.authority;
    // private static String appScopes = Constants.appScopes;
    private static String scope = Constants.scope;
    private static String keyPath = Constants.keyPath;
    private static String certPath = Constants.certPath;
    
    // Set authority to allow only organizational accounts
    // Device code flow only supports organizational accounts
    // private final static String authority = "https://login.microsoftonline.com/common/";

    // public static void initialize(String applicationId, String authority) {
    //     Authentication.applicationId = applicationId;
    //     Authentication.authority = authority;
    // }

    
    public static IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {

      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Files.readAllBytes(Paths.get(keyPath)));
      PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(spec);

      InputStream certStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(certPath)));
      X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(certStream);

      ConfidentialClientApplication app = ConfidentialClientApplication.builder(
              clientId,
              ClientCredentialFactory.createFromCertificate(key, cert))
              .authority(authority)
              .build();

      // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
      // application permissions need to be set statically (in the portal), and then granted by a tenant administrator

      // String[] scopes = scope.split(",");
      // Set<String> scopeSet = new HashSet<>(Arrays.asList(scopes));
      ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(Collections.singleton(scope))
              .build();

      CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
      return future.get();
  }

  public static String getUsersListFromGraph(String accessToken) throws IOException {
      URL url = new URL("https://graph.microsoft.com/v1.0/users");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", "Bearer " + accessToken);
      conn.setRequestProperty("Accept","application/json");

      int httpResponseCode = conn.getResponseCode();
      if(httpResponseCode == HTTPResponse.SC_OK) {

          StringBuilder response;
          try(BufferedReader in = new BufferedReader(
                  new InputStreamReader(conn.getInputStream()))){

              String inputLine;
              response = new StringBuilder();
              while (( inputLine = in.readLine()) != null) {
                  response.append(inputLine);
              }
          }
          return response.toString();
      } else {
          return String.format("Connection returned HTTP code: %s with message: %s",
                  httpResponseCode, conn.getResponseMessage());
      }
  }


    // public static String getAccessTokenCertificate(String[] scopes, String username) throws Exception {
    //     Set<String> scopeSet = new HashSet<>(Arrays.asList(scopes));
        
    //     PublicClientApplication pca;
    //     try {
    //         pca = PublicClientApplication.builder(applicationId)
    //             .authority(authority)
    //             .build();
    //     } catch (MalformedURLException e) {
    //         return null;
    //     }

    //     Set<IAccount> accountsInCache = pca.getAccounts().join();
    //     IAccount account = getAccountByUsername(accountsInCache, username);

    //     CertificateBasedAuthConfiguration configuration = new CertificateBasedAuthConfiguration();

    //     IAuthenticationResult result;
    //     try {
    //         SilentParameters silentParameters =
    //                 SilentParameters
    //                         .builder(scopeSet)
    //                         .account(account)
    //                         .build();
    //         // Try to acquire token silently. This will fail on the first acquireTokenUsernamePassword() call
    //         // because the token cache does not have any data for the user you are trying to acquire a token for
    //         result = pca.acquireTokenSilently(silentParameters).join();
    //         System.out.println("==acquireTokenSilently call succeeded");
    //     } catch (Exception ex) {
    //         if (ex.getCause() instanceof MsalException) {
    //             System.out.println("==acquireTokenSilently call failed: " + ex.getCause());
    //             UserNamePasswordParameters parameters =
    //                     UserNamePasswordParameters
    //                             .builder(scopeSet, username, password.toCharArray())
    //                             .build();
    //             // Try to acquire a token via username/password. If successful, you should see
    //             // the token and account information printed out to console
    //             result = pca.acquireToken(parameters).join();
    //             System.out.println("==username/password flow succeeded");
    //         } else {
    //             // Handle other exceptions accordingly
    //             throw ex;
    //         }
    //     }
    //     return result.accessToken();
    // }


    // public static String getAccessTokenUsernamePassword(String[] scopes,
    //                                                                   String username,
    //                                                                   String password) throws Exception {


    //     Set<String> scopeSet = new HashSet<>(Arrays.asList(scopes));
        
    //     PublicClientApplication pca;
    //     try {
    //         // Build the MSAL application object with
    //         // app ID and authority
    //         pca = PublicClientApplication.builder(applicationId)
    //             .authority(authority)
    //             .build();
    //     } catch (MalformedURLException e) {
    //         return null;
    //     }

    //     Set<IAccount> accountsInCache = pca.getAccounts().join();
    //     IAccount account = getAccountByUsername(accountsInCache, username);

    //     IAuthenticationResult result;
    //     try {
    //         SilentParameters silentParameters =
    //                 SilentParameters
    //                         .builder(scopeSet)
    //                         .account(account)
    //                         .build();
    //         // Try to acquire token silently. This will fail on the first acquireTokenUsernamePassword() call
    //         // because the token cache does not have any data for the user you are trying to acquire a token for
    //         result = pca.acquireTokenSilently(silentParameters).join();
    //         System.out.println("==acquireTokenSilently call succeeded");
    //     } catch (Exception ex) {
    //         if (ex.getCause() instanceof MsalException) {
    //             System.out.println("==acquireTokenSilently call failed: " + ex.getCause());
    //             UserNamePasswordParameters parameters =
    //                     UserNamePasswordParameters
    //                             .builder(scopeSet, username, password.toCharArray())
    //                             .build();
    //             // Try to acquire a token via username/password. If successful, you should see
    //             // the token and account information printed out to console
    //             result = pca.acquireToken(parameters).join();
    //             System.out.println("==username/password flow succeeded");
    //         } else {
    //             // Handle other exceptions accordingly
    //             throw ex;
    //         }
    //     }
    //     return result.accessToken();
    // }


    // /**
    //  * Helper function to return an account from a given set of accounts based on the given username,
    //  * or return null if no accounts in the set match
    //  */
    // private static IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
    //   if (accounts.isEmpty()) {
    //       System.out.println("==No accounts in cache");
    //   } else {
    //       System.out.println("==Accounts in cache: " + accounts.size());
    //       for (IAccount account : accounts) {
    //           if (account.username().equals(username)) {
    //               return account;
    //           }
    //       }
    //   }
    //   return null;
    // }

    // public static String getUserAccessToken(String[] scopes) {
    //     if (applicationId == null) {
    //         System.out.println("You must initialize Authentication before calling getUserAccessToken");
    //         return null;
    //     }

    //     Set<String> scopeSet = new HashSet<>(Arrays.asList(scopes));

    //     ExecutorService pool = Executors.newFixedThreadPool(1);
    //     PublicClientApplication app;
    //     try {
    //         // Build the MSAL application object with
    //         // app ID and authority
    //         app = PublicClientApplication.builder(applicationId)
    //             .authority(authority)
    //             .executorService(pool)
    //             .build();
    //     } catch (MalformedURLException e) {
    //         return null;
    //     }

    //     // Create consumer to receive the DeviceCode object
    //     // This method gets executed during the flow and provides
    //     // the URL the user logs into and the device code to enter
    //     Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) -> {
    //         // Print the login information to the console
    //         System.out.println(deviceCode.message());
    //     };

    //     // Request a token, passing the requested permission scopes
    //     IAuthenticationResult result = app.acquireToken(
    //         DeviceCodeFlowParameters
    //             .builder(scopeSet, deviceCodeConsumer)
    //             .build()
    //     ).exceptionally(ex -> {
    //         System.out.println("Unable to authenticate - " + ex.getMessage());
    //         return null;
    //     }).join();

    //     pool.shutdown();

    //     if (result != null) {
    //         return result.accessToken();
    //     }

    //     return null;
    // }
}