package msftcse.SpringKeyvault;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.certificates.CertificateClientBuilder;
import com.azure.security.keyvault.certificates.models.CertificateProperties;
import com.azure.security.keyvault.certificates.models.KeyVaultCertificate;
import com.azure.security.keyvault.certificates.models.KeyVaultCertificateWithPolicy;
// import com.azure.security.keyvault.jca.KeyVaultJcaProvider;
// import com.azure.security.keyvault.jca.KeyVaultKeyStore;
// import com.azure.security.keyvault.jca.KeyVaultLoadStoreParameter;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

// https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/keyvault/azure-security-keyvault-certificates#retrieve-a-certificate

@Configuration
public class SpringKeyvaultConfiguration {

  // @Value("${connectionString}")
  private String connectionString;

  @Value("${app.foo}")
  private String appFoo;

  @Value("${app.foo2}")
  private String appFoos;

  @Value("${spring.cloud.azure.keyvault.certificate.endpoint}")
  private String keyvaultEndpoint;

  @Bean
  public String certificateMaterial() throws Exception {
    var foo = System.getenv("example-secret");
    var foo2 = this.appFoo;

    var defaultCreds = new DefaultAzureCredentialBuilder().build();

    SecretClient ss = new SecretClientBuilder()
        .vaultUrl(keyvaultEndpoint)
        .credential(defaultCreds)
        .buildClient();

    CertificateClient certificateClient = new CertificateClientBuilder()
        .vaultUrl(keyvaultEndpoint)
        .credential(defaultCreds)
        .buildClient();

    var certName = "sim-dev-000001";
    KeyVaultCertificateWithPolicy certificate = certificateClient.getCertificate(certName);
    System.out.printf("Received certificate with name \"%s\", version %s and secret id %s%n",
        certificate.getProperties().getName(), certificate.getProperties().getVersion(), certificate.getSecretId());

    var cer = certificate.getCer();
    try (FileOutputStream stream = new FileOutputStream("/home/cicorias/g/cse/gm2/labs/spring-keyvault-int/t.cer")) {
      stream.write(cer);
    }

    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    InputStream in = new ByteArrayInputStream(cer);
    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);

    for (CertificateProperties certificateProperties : certificateClient.listPropertiesOfCertificates()) {
      KeyVaultCertificate certificateWithAllProperties = certificateClient
          .getCertificateVersion(certificateProperties.getName(), certificateProperties.getVersion());
      System.out.printf("Received certificate with name %s and secret id %s%n",
          certificateWithAllProperties.getProperties().getName(),
          certificateWithAllProperties.getSecretId());
    }

    // // set som prop
    // Properties jcaProperties = new Properties();
    // jcaProperties.put("azure.keyvault.uri", System.getenv("KEY_VAULT_URI"));
    // jcaProperties.put("azure.keyvault.tenant-id",
    // System.getenv("APP_TENANT_ID"));
    // jcaProperties.put("azure.keyvault.client-id", System.getenv("APP_ID"));
    // jcaProperties.put("azure.keyvault.client-secret",
    // System.getenv("APP_PASSWORD"));

    // System.setProperties(jcaProperties);

    // // try to load KeyStore azureKeyVaultKeyStore =
    // KeyStore.getInstance("AzureKeyVault");
    // KeyStore azureKeyVaultKeyStore = KeyStore.getInstance("AzureKeyVault");
    // KeyVaultLoadStoreParameter parameter = new KeyVaultLoadStoreParameter(
    // System.getProperty("azure.keyvault.uri"),
    // System.getProperty("azure.keyvault.tenant-id"),
    // System.getProperty("azure.keyvault.client-id"),
    // System.getProperty("azure.keyvault.client-secret"));

    // newer jca way.

    // KeyVaultJcaProvider provider = new KeyVaultJcaProvider();
    // Security.addProvider(provider);

    // KeyStore keyStore = KeyVaultKeyStore.getKeyVaultKeyStoreBySystemProperty();

    // SSLContext sslContext = SSLContexts
    // .custom()
    // .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
    // .build();

    // older jca way.

    // KeyStore azureKeyVaultKeyStore = KeyStore.getInstance("AzureKeyVault");

    // KeyVaultLoadStoreParameter parameter = new KeyVaultLoadStoreParameter(
    // System.getProperty("azure.keyvault.uri"),
    // System.getProperty("azure.keyvault.tenant-id"),
    // System.getProperty("azure.keyvault.client-id"),
    // System.getProperty("azure.keyvault.client-secret"));

    // KeyVaultCertificateWithPolicy certificate =
    // certificateClient.getCertificate("<certificate-name>");
    // System.out.printf("Received certificate with name \"%s\", version %s and
    // secret id %s%n",
    // certificate.getProperties().getName(),
    // certificate.getProperties().getVersion(), certificate.getSecretId());

    // azureKeyVaultKeyStore.load(parameter);
    File aFile = new File("/etc/certs");
    Process(aFile);
    return "certificateMaterial";
  }

  static int spc_count = -1;

  static void Process(File aFile) {
    spc_count++;
    String spcs = "";
    for (int i = 0; i < spc_count; i++)
      spcs += " ";
    if (aFile.isFile())
      System.out.println(spcs + "[FILE] " + aFile.getName());
    else if (aFile.isDirectory()) {
      System.out.println(spcs + "[DIR] " + aFile.getName());
      File[] listOfFiles = aFile.listFiles();
      if (listOfFiles != null) {
        for (int i = 0; i < listOfFiles.length; i++)
          Process(listOfFiles[i]);
      } else {
        System.out.println(spcs + " [ACCESS DENIED]");
      }
    }
    spc_count--;
  }
}
