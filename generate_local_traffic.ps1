# Traffic Generator for Pecas Por Codigo (Production Target)
# Run this script to generate random traffic to populate Prometheus/Grafana metrics on PRODUCTION.

$BaseUrl = if ($args[0]) { $args[0] } else { "http://localhost:8080/api/v1" }

# Hardcoded list of realistic codes and descriptions
$Codes = @(
    "3040250", "4C4513A350AA", "00005034", "5034", "6C347A581AA", 
    "6C3Z7A581A", "93221087", "E7SC7A581AA", "E7SZ7A581A", "707600B",
    "TE3903221", "TE3903221X", "12345", "ABC999", "XYZ123", 
    "OIL555", "BRK202", "BOSCH123", "FILTRO202", "VELA99"
)

$Descriptions = @(
    "INTERRUPTOR", "RETENTOR", "BUCHA", "FILTRO", "OLEO", 
    "PNEU", "PARAFUSO", "AMORTECEDOR", "PASTILHA", "DISCO",
    "MOTOR", "RADIADOR", "BOMBA", "CORREIA", "SENSOR"
)

$Suppliers = @("Bosch", "Magneti Marelli", "Delphi", "Valeo", "Continental", "Denso")
$Events = @("PAYMENT_RECEIVED", "PAYMENT_OVERDUE", "PAYMENT_CONFIRMED", "SUBSCRIPTION_CANCELED")
$Cnpjs = @("12345678000199", "98765432000155", "11222333000188", "44555666000177")

function Invoke-RandomRequest {
    $r = Get-Random -Minimum 0 -Maximum 100
    
    # 1. Item Search by Code (30%)
    if ($r -lt 30) {
        $c = $Codes | Get-Random
        Try { Invoke-RestMethod -Uri "$BaseUrl/pecas/codigo/$c?page=0&size=10" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Write-Host "Searched Item Code: $c"
    }
    # 2. Item Search by Description (15%)
    elseif ($r -lt 45) {
        $d = $Descriptions | Get-Random
        Try { Invoke-RestMethod -Uri "$BaseUrl/pecas/descricao?descricao=$d&page=0&size=10" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Write-Host "Searched Item Desc: $d"
    }
    # 3. Stock Search by Code (10%)
    elseif ($r -lt 55) {
        $c = $Codes | Get-Random
        Try { Invoke-RestMethod -Uri "$BaseUrl/estoque/codigo/$c?page=0&size=10" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Write-Host "Searched Stock Code: $c"
    }
    # 4. Global Listings (Brand/State/Plans) (5%)
    elseif ($r -lt 60) {
        Try { Invoke-RestMethod -Uri "$BaseUrl/marcas" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Try { Invoke-RestMethod -Uri "$BaseUrl/estados" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Try { Invoke-RestMethod -Uri "$BaseUrl/planos" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Write-Host "Listed Brands/States/Plans"
    }
    # 5. Login Attempt (5%)
    elseif ($r -lt 65) {
        Try { 
            Invoke-RestMethod -Uri "$BaseUrl/login" -Method Post -Body (@{email = "test@example.com" } | ConvertTo-Json) -ContentType "application/json" -ErrorAction SilentlyContinue 
        }
        Catch {}
        Write-Host "Login Attempted"
    }
    # 6. Webhook (Subscription) (5%)
    elseif ($r -lt 70) {
        $evt = $Events | Get-Random
        # Create a full dummy payload matching AsaasWebhook.kt
        $payload = @{
            id          = "evt_000001"
            event       = $evt
            dateCreated = "2023-01-01"
            payment     = @{
                object                 = "payment"
                id                     = "pay_000001"
                dateCreated            = "2023-01-01"
                customer               = "cus_000005161869"
                subscription           = "sub_000001"
                paymentLink            = $null
                value                  = 100.0
                netValue               = 95.0
                originalValue          = $null
                interestValue          = $null
                description            = "Subscription"
                billingType            = "CREDIT_CARD"
                canBePaidAfterDueDate  = $false
                pixTransaction         = $null
                status                 = "RECEIVED"
                dueDate                = "2023-01-01"
                originalDueDate        = "2023-01-01"
                paymentDate            = "2023-01-01"
                clientPaymentDate      = "2023-01-01"
                installmentNumber      = $null
                invoiceUrl             = $null
                invoiceNumber          = $null
                externalReference      = $null
                deleted                = $false
                anticipated            = $false
                anticipable            = $false
                creditDate             = $null
                estimatedCreditDate    = $null
                transactionReceiptUrl  = $null
                nossoNumero            = $null
                bankSlipUrl            = $null
                lastInvoiceViewedDate  = $null
                lastBankSlipViewedDate = $null
                discount               = @{ value = 0.0; limitDate = $null; dueDateLimitDays = 0; type = "FIXED" }
                fine                   = @{ value = 0.0; type = "FIXED" }
                interest               = @{ value = 0.0; type = "FIXED" }
                postalService          = $false
                custody                = $null
                refunds                = $null
            }
        }
        Try {
            # Fix: Remove double /api/v1
            Invoke-RestMethod -Uri "$BaseUrl/payments/status" -Method Post -Body ($payload | ConvertTo-Json -Depth 10) -ContentType "application/json" -ErrorAction SilentlyContinue
        }
        Catch {}
        Write-Host "Webhook Sent: $evt"
    }
    # 7. Category Search (5%)
    elseif ($r -lt 75) {
        $cat = $Descriptions | Get-Random
        Try { Invoke-RestMethod -Uri "$BaseUrl/categorias/search?name=$cat" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Write-Host "Searched Category: $cat"
    }
    # 8. Supplier Search (5%)
    elseif ($r -lt 80) {
        $cnpj = $Cnpjs | Get-Random
        Try { Invoke-RestMethod -Uri "$BaseUrl/fornecedores/cnpj?cnpj=$cnpj" -Method Get -ErrorAction SilentlyContinue } Catch {}
        Write-Host "Searched Supplier CNPJ: $cnpj"
    }
    # 9. Contact Form (5%)
    elseif ($r -lt 85) {
        $payload = @{
            name    = "Test User"
            email   = "test@user.com"
            subject = "Support Request"
            message = "I need help with part X"
        }
        Try {
            # Fix: Remove double /api/v1
            Invoke-RestMethod -Uri "$BaseUrl/contact-form" -Method Post -Body ($payload | ConvertTo-Json) -ContentType "application/json" -ErrorAction SilentlyContinue
        }
        Catch {}
        Write-Host "Contact Form Submitted"
    }
    # 10. Stock Search Variations (15%)
    else {
        $subR = Get-Random -Minimum 0 -Maximum 3
        if ($subR -eq 0) {
            $sup = $Suppliers | Get-Random
            Try { Invoke-RestMethod -Uri "$BaseUrl/estoque/fornecedor?nome=$sup" -Method Get -ErrorAction SilentlyContinue } Catch {}
            Write-Host "Searched Stock Supplier Name: $sup"
        }
        elseif ($subR -eq 1) {
            # Stock by Description
            $d = $Descriptions | Get-Random
            Try { Invoke-RestMethod -Uri "$BaseUrl/estoque/item?descricao=$d&page=0&size=10" -Method Get -ErrorAction SilentlyContinue } Catch {}
            Write-Host "Searched Stock Desc: $d"
        }
        else {
            # Stock by Supplier ID (Mock ID)
            $id = Get-Random -Minimum 1 -Maximum 50
            Try { Invoke-RestMethod -Uri "$BaseUrl/estoque/fornecedor/$id?page=0&size=10" -Method Get -ErrorAction SilentlyContinue } Catch {}
            Write-Host "Searched Stock Supplier ID: $id"
        }
    }
}

Write-Host "Starting Production Traffic Generator... Press Ctrl+C to stop."
Write-Host "Targeting: $BaseUrl"
while ($true) {
    Invoke-RandomRequest
    Start-Sleep -Milliseconds 200 # Fast traffic
}
