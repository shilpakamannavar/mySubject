# Microservices Architecture

## Bounded Context Split

The monolith maps cleanly to **4 microservices** along DDD bounded-context lines.
Each service owns its own database schema and communicates via REST or events.

```
┌──────────────────────────────────────────────────────────────────────────┐
│                       Study Management Platform                           │
│                                                                           │
│  ┌──────────────────┐   REST    ┌──────────────────┐                    │
│  │  study-service   │ ────────▶ │ payment-service  │                    │
│  │  (port 8081)     │           │  (port 8082)     │                    │
│  │                  │◀──────── │                  │                    │
│  │  /api/studies/** │  events   │ /api/payments/** │                    │
│  └──────────────────┘           └──────────────────┘                    │
│         │                                │                               │
│         │ REST (validate promo)          │ REST (validate + record use) │
│         ▼                                ▼                               │
│  ┌──────────────────┐           ┌──────────────────┐                    │
│  │  promo-service   │           │ customer-service  │                    │
│  │  (port 8083)     │           │  (port 8084)     │                    │
│  │ /api/promo-codes │           │ /api/customers/  │                    │
│  │ /api/promo-codes │◀─────────│                  │                    │
│  │   /{code}/assign │  REST     └──────────────────┘                    │
│  └──────────────────┘                                                    │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 1. study-service (Port 8081)

**Responsibility**: Study and visit lifecycle. Owns the study aggregate.

| Owns              | Description                                      |
|-------------------|--------------------------------------------------|
| `study`           | Aggregate root; optionally linked to a customer  |
| `study_visit`     | Auto-generated visit rows (one per visit slot)   |

**API endpoints**:

| Method | Path                                              | Description                                            |
|--------|---------------------------------------------------|--------------------------------------------------------|
| POST   | `/api/studies?customerId=`                        | Create study + auto-generate visits (optional customer)|
| PUT    | `/api/studies/{id}/visits`                        | Save visit details (visitNumber is locked)             |
| POST   | `/api/studies/{id}/proceed-payment?promoCode=`    | Calculate fee + initiate or waive payment              |
| POST   | `/api/studies/{id}/payment-callback`              | UPI gateway webhook (success / fail)                   |
| GET    | `/api/studies/my-studies`                         | Fetch all ACTIVE studies                               |
| GET    | `/api/studies/{id}`                               | Fetch one study by DB id                               |

**Domain events published** (via Kafka / RabbitMQ):
- `StudyCreated { studyId, indication, patients, totalVisits, customerId }`
- `StudyVisitsSaved { studyId }`

**Hexagonal package**:
```
study-service/
├── domain/model/       Study, StudyVisit, StudyStatus, VisitType, Customer
├── domain/port/in/     StudyUseCase
├── domain/port/out/    StudyPort, CustomerPort
├── application/        StudyApplicationService
└── infrastructure/
    ├── persistence/    StudyPersistenceAdapter, JpaStudyRepository,
    │                   CustomerPersistenceAdapter, JpaCustomerRepository
    └── web/            StudyController, CustomerController
```

---

## 2. payment-service (Port 8082)

**Responsibility**: Fee calculation, promo eligibility check, invoicing, Razorpay / UPI payment, usage logging.

| Owns                     | Description                                     |
|--------------------------|-------------------------------------------------|
| `invoice`                | Payment record per study                        |
| `promo_code_usage_log`   | Immutable audit log of every promo redemption   |

**API endpoints**:

| Method | Path                                           | Description                                                |
|--------|------------------------------------------------|------------------------------------------------------------|
| POST   | `/api/studies/{id}/proceed-payment?promoCode=` | Calculate fee + eligibility check; returns Razorpay order  |
| POST   | `/api/studies/{id}/payment-callback`           | Generic success/fail webhook (UPI demo mode)               |
| POST   | `/api/razorpay/callback`                       | Frontend Razorpay callback — verify signature + activate   |
| POST   | `/api/razorpay/webhook`                        | Razorpay server-to-server webhook (payment.captured/failed)|

**Payment gateway profiles**:

| Spring Profile | Adapter                        | Behaviour                                          |
|----------------|--------------------------------|----------------------------------------------------|
| *(default)*    | `UpiPaymentGatewayAdapter`     | Synchronous mock — study activates immediately     |
| `razorpay`     | `RazorpayPaymentGatewayAdapter`| Async — creates Razorpay order, waits for webhook  |

**Razorpay flow**:
```
POST /api/studies/{id}/proceed-payment
  → creates Razorpay order (order_XXXX)
  → study = PAYMENT_PENDING, invoice = INITIATED
  → response: { razorpayOrderId, razorpayKeyId, requiresCheckout: true }

Frontend renders Razorpay Checkout widget
  → user pays
  → Razorpay returns { orderId, paymentId, signature }

POST /api/razorpay/callback  (called by frontend)
  → verifyPaymentSignature(orderId, paymentId, signature)
  → confirmPayment → study = ACTIVE, invoice = SUCCESS

POST /api/razorpay/webhook   (called by Razorpay servers)
  → verifyWebhookSignature(rawBody, X-Razorpay-Signature)
  → payment.captured → confirmPayment
  → payment.failed   → failPayment
```

**Calls out to**:
- `promo-service` → `GET /api/promo-codes/validate?code=` (sync, circuit-breaker)
- `promo-service` → eligibility check via `PromoCodeAssignmentPort`
- Razorpay REST API (when profile = razorpay)

**Domain events consumed**:
- `StudyVisitsSaved` → study becomes eligible for payment initiation

**Domain events published**:
- `PaymentCompleted { studyId, invoiceId, totalAmount, paymentRef }`
- `PaymentFailed { studyId, reason }`

**Hexagonal package**:
```
payment-service/
├── domain/model/       Invoice, PaymentStatus, Money, DiscountType,
│                       PromoCodeUsageLog
├── domain/service/     FeeCalculatorDomainService
├── domain/port/in/     PaymentUseCase
├── domain/port/out/    StudyPort (stub), PromoCodePort (HTTP client),
│                       PromoCodeAssignmentPort, PromoCodeUsagePort,
│                       PaymentGatewayPort
├── application/        PaymentApplicationService
└── infrastructure/
    ├── persistence/    InvoicePersistenceAdapter,
    │                   PromoCodeUsagePersistenceAdapter
    ├── payment/        UpiPaymentGatewayAdapter    (@Profile !razorpay)
    │                   RazorpayPaymentGatewayAdapter (@Profile razorpay)
    │                   RazorpayConfig
    ├── client/         PromoCodeServiceClient (Feign/WebClient)
    └── web/            RazorpayWebhookController  (@Profile razorpay)
```

---

## 3. promo-service (Port 8083)

**Responsibility**: Manage promo codes, provision them to customers, expose usage history.

| Owns                     | Description                                                   |
|--------------------------|---------------------------------------------------------------|
| `promo_code`             | Code definitions, discount type, expiry, usage counter        |
| `promo_code_assignment`  | Which codes are granted to which customers (or ALL)           |

**API endpoints**:

| Method | Path                                         | Description                                       |
|--------|----------------------------------------------|---------------------------------------------------|
| GET    | `/api/promo-codes`                           | List all promo codes (admin)                      |
| GET    | `/api/promo-codes/validate?code=`            | Validate a code (called by payment-service)       |
| POST   | `/api/promo-codes/{code}/assign`             | Provision a code to one customer or ALL           |
| GET    | `/api/promo-codes/{code}/assignments`        | List all assignments for a code                   |
| GET    | `/api/promo-codes/{code}/usage`              | Usage log for a specific code                     |
| GET    | `/api/promo-codes/usage`                     | Full usage log — all codes, all customers         |
| GET    | `/api/promo-codes/usage/customer/{customerId}` | Usage log filtered by customer                  |

**Provisioning rules**:
- `customer_id = NULL` in assignment → globally available to ALL customers
- `customer_id = N` → available only to that specific customer
- At payment time: code must have a specific or global assignment for the study's customer

**Hexagonal package**:
```
promo-service/
├── domain/model/       PromoCode, DiscountType, PromoCodeAssignment
├── domain/port/in/     PromoCodeUseCase, PromoCodeAssignmentUseCase
├── domain/port/out/    PromoCodePort, PromoCodeAssignmentPort, PromoCodeUsagePort
├── application/        PromoCodeApplicationService,
│                       PromoCodeAssignmentApplicationService
└── infrastructure/
    ├── persistence/    PromoCodePersistenceAdapter,
    │                   PromoCodeAssignmentPersistenceAdapter,
    │                   PromoCodeUsagePersistenceAdapter,
    │                   JpaPromoCodeAssignmentRepository,
    │                   JpaPromoCodeUsageRepository
    └── web/            PromoCodeController
```

---

## 4. customer-service (Port 8084)

**Responsibility**: Manage customer (sponsor/researcher) profiles. Serves as the identity source for study ownership and promo eligibility.

| Owns       | Description                                  |
|------------|----------------------------------------------|
| `customer` | Name, email, phone, registration date        |

**API endpoints**:

| Method | Path                  | Description                    |
|--------|-----------------------|--------------------------------|
| POST   | `/api/customers`      | Register a new customer        |
| GET    | `/api/customers`      | List all customers (admin)     |
| GET    | `/api/customers/{id}` | Get customer by ID             |

**Hexagonal package**:
```
customer-service/
├── domain/model/       Customer
├── domain/port/in/     CustomerUseCase
├── domain/port/out/    CustomerPort
├── application/        CustomerApplicationService
└── infrastructure/
    ├── persistence/    CustomerPersistenceAdapter, JpaCustomerRepository
    └── web/            CustomerController
```

---

## Communication Patterns

| Pattern          | Used for                                                   |
|------------------|------------------------------------------------------------|
| Sync REST        | Promo validation and eligibility (payment → promo-service) |
| Sync REST        | Customer lookup (study-service → customer-service)         |
| Async events     | Study activated after payment (avoids tight coupling)      |
| Webhook          | UPI gateway → payment-service                              |

---

## Database per Service

| Service          | Database      | Tables                                      |
|------------------|---------------|---------------------------------------------|
| study-service    | `studydb`     | `study`, `study_visit`                      |
| payment-service  | `paymentdb`   | `invoice`, `promo_code_usage_log`           |
| promo-service    | `promodb`     | `promo_code`, `promo_code_assignment`       |
| customer-service | `customerdb`  | `customer`                                  |

Each service has its own connection pool and schema. No cross-service JOINs.

---

## Promo Provisioning Flow

```
Admin
  │
  ├─ POST /api/customers                  → create customer
  │
  ├─ POST /api/promo-codes/{code}/assign  → grant to specific customer
  │    body: { "customerId": 1 }
  │
  └─ POST /api/promo-codes/{code}/assign  → grant to ALL customers
       body: {} or { "customerId": null }

At payment time (payment-service):
  promoCode supplied?
    └─ YES → isEligible(promoCodeId, study.customerId)
               ├─ assignment exists (specific or global) → proceed
               └─ no assignment → 409 "Promo not provisioned for this customer"
```

---

## Docker Compose Sketch

```yaml
services:
  customer-service:
    image: study-mgmt/customer-service:latest
    ports: ["8084:8084"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/customerdb

  study-service:
    image: study-mgmt/study-service:latest
    ports: ["8081:8081"]
    environment:
      CUSTOMER_SERVICE_URL: http://customer-service:8084
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/studydb

  payment-service:
    image: study-mgmt/payment-service:latest
    ports: ["8082:8082"]
    environment:
      PROMO_SERVICE_URL: http://promo-service:8083
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/paymentdb

  promo-service:
    image: study-mgmt/promo-service:latest
    ports: ["8083:8083"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/promodb
```
