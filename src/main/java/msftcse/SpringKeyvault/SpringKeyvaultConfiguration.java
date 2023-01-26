package msftcse.SpringKeyvault;

import java.security.KeyStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.security.keyvault.jca.KeyVaultLoadStoreParameter;

@Configuration
public class SpringKeyvaultConfiguration {
    @Bean
    public String certificateMaterial() throws Exception {
        KeyStore azureKeyVaultKeyStore = KeyStore.getInstance("AzureKeyVault");

        KeyVaultLoadStoreParameter parameter = new KeyVaultLoadStoreParameter(
            System.getProperty("azure.keyvault.uri"),
            System.getProperty("azure.keyvault.tenant-id"),
            System.getProperty("azure.keyvault.client-id"),
            System.getProperty("azure.keyvault.client-secret"));


        azureKeyVaultKeyStore.load(parameter);
        return "certificateMaterial";
    }
}
