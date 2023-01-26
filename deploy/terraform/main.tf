terraform {
  required_version = "~> 1.3.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~>3.40.0"
    }
    pkcs12 = {
      source  = "chilicat/pkcs12"
      version = "~> 0.0"
    }
  }
}

provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy    = true
      recover_soft_deleted_key_vaults = true
    }
  }
}

variable "resource_group_name" {
  type    = string
  default = "example-resources"
}

variable "location" {
  type    = string
  default = "eastus"

}

variable "key_vault_name" {
  type    = string
  default = "example-keyvault"
}

variable "common_name" {
  type    = string
  default = "fizbuzz"
}
  
resource "random_id" "suffix" {
  keepers = {
    # Generate a new id each time we switch to a new AMI id
  }

  byte_length = 3
}

data "azurerm_client_config" "current" {}

resource "azurerm_resource_group" "this" {
  name     = format("%s-%s", var.resource_group_name, random_id.suffix.hex)
  location = var.location
}

resource "azurerm_key_vault" "this" {
  name                       = format("%s-%s", var.key_vault_name, random_id.suffix.hex)
  tenant_id                  = data.azurerm_client_config.current.tenant_id
  location                   = azurerm_resource_group.this.location
  resource_group_name        = azurerm_resource_group.this.name
  soft_delete_retention_days = 7
  purge_protection_enabled   = false

  sku_name = "standard"

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id
    key_permissions = [
      "Create",
      "Get",
      "Update",
      "List",
      "Delete",

    ]

    secret_permissions = [
      "Set",
      "Get",
      "List",
      "Delete",
    ]

    certificate_permissions = [
      "Create",
      "Get",
      "Update",
      "List",
      "Delete",
    ]
    storage_permissions = [
      "Set",
      "Get",
      "Update",
      "List",
    ]
  }
}


resource "azurerm_key_vault_secret" "example" {
  name         = "example-secret"
  value        = "example-value"
  key_vault_id = azurerm_key_vault.this.id
}


# Creates a private key in PEM format
resource "tls_private_key" "private_key" {
  algorithm = "RSA"
}

# Generates a TLS self-signed certificate using the private key
resource "tls_self_signed_cert" "self_signed_cert" {
  private_key_pem = tls_private_key.private_key.private_key_pem

  validity_period_hours = 3600

  subject {
    # The subject CN field here contains the hostname to secure
    common_name = var.common_name
  }

  allowed_uses = ["key_encipherment", "digital_signature", "client_auth"]
}

# To convert the PEM certificate in PFX we need a password
resource "random_password" "self_signed_cert" {
  length  = 24
  special = true
}

# This resource converts the PEM certicate in PFX
resource "pkcs12_from_pem" "self_signed_cert" {
  cert_pem        = tls_self_signed_cert.self_signed_cert.cert_pem
  private_key_pem = tls_private_key.private_key.private_key_pem
  password        = random_password.self_signed_cert.result
}

# Finally we push the PFX certificate in the Azure webspace
resource "azurerm_app_service_certificate" "self_signed_cert" {
  name                = "self-signed"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location

  pfx_blob = pkcs12_from_pem.self_signed_cert.result
  password = pkcs12_from_pem.self_signed_cert.password
}