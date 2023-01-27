KeyVaultEnvironmentPostProcessor







```bash
certthumbprint="0C802EFBED9CE6B4A6A3369456635296539B7796"
az resource list \
        --resource-type "Microsoft.KeyVault/vaults" \
        --query "[].name" -o tsv |
    xargs -rn 1 az keyvault certificate list \
        --query "[?x509ThumbprintHex == '0C802EFBED9CE6B4A6A3369456635296539B7796'].id" \
        -o tsv --vault-name
```


```bash
kvname="scicoria-???"
az keyvault certificate list \
        --query "[?name == 'self-signed-certificate']" \
        -o tsv --vault-name $kvname
```

[?contains(name, 'mycontainer2')]

```bash
kvname="scicoria-???"
az keyvault certificate list \
        --query "[?contains(name, 'self-')]" \
        -o tsv --vault-name $kvname
```
