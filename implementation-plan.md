# Garage Management System (web-gara) — Implementation Plan

## 1. Project Overview

A comprehensive garage management system tailored for the Vietnamese market, supporting online booking, repair tracking, inventory management, invoicing, payments, and reporting.

### Tech Stack (Mandatory)

- **Backend:** Java Spring Boot 3.x (Java 17+)
- **Database:** MongoDB 7.x (NoSQL)
- **Containerization:** Docker & Docker Compose
- **Security:** Spring Security + JWT + OTP
- **API Docs:** SpringDoc OpenAPI (Swagger UI)
- **PDF Generation:** iText / OpenPDF
- **File Storage:** Local disk (dev) / MinIO or S3-compatible (prod)
- **Email:** Spring Boot Starter Mail (SMTP)

### User Roles (4 roles)

| Role | Code | Description |
|------|------|-------------|
| Khách hàng | `CUSTOMER` | Đặt lịch, theo dõi sửa chữa, thanh toán, đánh giá |
| Lễ tân | `RECEPTIONIST` | Nhận đơn, xác nhận lịch, quản lý lịch hẹn |
| Kỹ thuật viên | `TECHNICIAN` | Kiểm tra xe, sửa chữa, cập nhật tiến độ |
| Quản lý | `MANAGER` | Quản trị toàn bộ: dịch vụ, nhân sự, kho, báo cáo |

---

## 2. Project Architecture

### Modular Monolith — Layered Structure

```
src/main/java/com/webgara/
├── config/                  # Security, MongoDB, CORS, Swagger configs
├── security/                # JWT filter, OTP service, UserDetails
├── common/                  # Base entities, exceptions, utils, constants
│   ├── exception/           # GlobalExceptionHandler, custom exceptions
│   ├── dto/                 # ApiResponse<T>, PageResponse<T>
│   └── util/                # DateUtil, CurrencyUtil, SlugUtil
├── module/
│   ├── auth/                # Login, Register, OTP, Refresh token
│   ├── user/                # User CRUD, profile management
│   ├── vehicle/             # Vehicle CRUD, maintenance history
│   ├── garage/              # Garage management (multi-garage)
│   ├── service/             # Service catalog CRUD
│   ├── appointment/         # Booking, status workflow
│   ├── repair/              # Repair orders, tasks, technician assignment
│   ├── inventory/           # Parts CRUD, stock transactions
│   ├── invoice/             # Invoice generation, PDF export
│   ├── payment/             # VNPay/MoMo mock, payment records
│   ├── review/              # Customer reviews & ratings
│   ├── notification/        # Email/SMS/In-app notifications
│   └── report/              # Revenue, performance, statistics
└── WebGaraApplication.java
```

Each module follows the pattern:
```
module/{name}/
├── controller/     # REST endpoints
├── service/        # Business logic (interface + impl)
├── repository/     # Spring Data MongoDB repositories
├── model/          # @Document entities
├── dto/            # Request/Response DTOs
└── mapper/         # MapStruct mappers
```

---

## 3. MongoDB Schema Design (Complete)

### 3.1. `users` — Người dùng

```json
{
  "_id": "ObjectId",
  "email": "string (unique)",
  "phone": "string (unique)",
  "password": "string (BCrypt hashed)",
  "fullName": "string",
  "avatar": "string (URL)",
  "role": "CUSTOMER | RECEPTIONIST | TECHNICIAN | MANAGER",
  "address": {
    "street": "string",
    "ward": "string",
    "district": "string",
    "city": "string"
  },
  "otp": {
    "code": "string",
    "expiresAt": "ISODate",
    "verified": "boolean"
  },
  "staffInfo": {
    "employeeId": "string",
    "garageId": "ObjectId (ref: garages)",
    "specializations": ["string"],
    "hireDate": "ISODate",
    "hourlyRate": "number"
  },
  "refreshToken": "string",
  "isActive": "boolean (default: true)",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ email: 1 } unique`, `{ phone: 1 } unique`, `{ role: 1 }`, `{ "staffInfo.garageId": 1 }`

---

### 3.2. `vehicles` — Xe của khách hàng

```json
{
  "_id": "ObjectId",
  "ownerId": "ObjectId (ref: users)",
  "plateNumber": "string (unique)",
  "brand": "string",
  "model": "string",
  "year": "number",
  "color": "string",
  "vin": "string",
  "engineNumber": "string",
  "mileage": "number",
  "fuelType": "GASOLINE | DIESEL | ELECTRIC | HYBRID",
  "maintenanceReminder": {
    "nextDate": "ISODate",
    "nextMileage": "number",
    "intervalMonths": "number",
    "intervalKm": "number"
  },
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ plateNumber: 1 } unique`, `{ ownerId: 1 }`, `{ "maintenanceReminder.nextDate": 1 }`

---

### 3.3. `garages` — Gara (hỗ trợ multi-garage)

```json
{
  "_id": "ObjectId",
  "name": "string",
  "slug": "string (unique)",
  "address": {
    "street": "string",
    "ward": "string",
    "district": "string",
    "city": "string",
    "coordinates": { "lat": "double", "lng": "double" }
  },
  "phone": "string",
  "email": "string",
  "workingHours": {
    "monday":    { "open": "08:00", "close": "17:30" },
    "tuesday":   { "open": "08:00", "close": "17:30" },
    "wednesday": { "open": "08:00", "close": "17:30" },
    "thursday":  { "open": "08:00", "close": "17:30" },
    "friday":    { "open": "08:00", "close": "17:30" },
    "saturday":  { "open": "08:00", "close": "12:00" },
    "sunday":    null
  },
  "capacity": "number (max vehicles per day)",
  "description": "string",
  "images": ["string (URL)"],
  "isActive": "boolean",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ slug: 1 } unique`, `{ "address.city": 1 }`, `{ isActive: 1 }`

---

### 3.4. `services` — Danh mục dịch vụ

```json
{
  "_id": "ObjectId",
  "garageId": "ObjectId (ref: garages)",
  "name": "string",
  "description": "string",
  "category": "OIL_CHANGE | MAINTENANCE | ENGINE | ELECTRICAL | TIRE | WASH | BODY | OTHER",
  "standardTime": "number (minutes)",
  "pricing": [
    {
      "vehicleType": "SEDAN | SUV | TRUCK | VAN | MOTORCYCLE",
      "minPrice": "number (VND)",
      "maxPrice": "number (VND)"
    }
  ],
  "isActive": "boolean",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ garageId: 1 }`, `{ category: 1 }`, `{ isActive: 1 }`

---

### 3.5. `appointments` — Lịch hẹn & đặt lịch

```json
{
  "_id": "ObjectId",
  "appointmentNumber": "string (auto: APT-20260301-001)",
  "customerId": "ObjectId (ref: users)",
  "vehicleId": "ObjectId (ref: vehicles)",
  "garageId": "ObjectId (ref: garages)",
  "serviceIds": ["ObjectId (ref: services)"],
  "appointmentDate": "ISODate",
  "timeSlot": {
    "start": "08:00",
    "end": "09:30"
  },
  "description": "string (mô tả lỗi từ khách)",
  "media": [
    {
      "url": "string",
      "type": "IMAGE | VIDEO",
      "originalName": "string",
      "uploadedAt": "ISODate"
    }
  ],
  "status": "PENDING_CONFIRMATION | CONFIRMED | VEHICLE_RECEIVED | INSPECTING | AWAITING_QUOTE_APPROVAL | REPAIRING | COMPLETED | DELIVERED | CANCELLED",
  "source": "WEB | WALK_IN | PHONE",
  "assignedReceptionistId": "ObjectId (ref: users)",
  "estimatedCost": {
    "min": "number (VND)",
    "max": "number (VND)"
  },
  "staffNotes": "string",
  "cancelReason": "string",
  "statusHistory": [
    {
      "fromStatus": "string",
      "toStatus": "string",
      "changedBy": "ObjectId (ref: users)",
      "timestamp": "ISODate",
      "note": "string"
    }
  ],
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ appointmentNumber: 1 } unique`, `{ customerId: 1 }`, `{ garageId: 1, appointmentDate: 1 }`, `{ status: 1 }`, `{ vehicleId: 1 }`, `{ source: 1 }`

**Status Flow:**
```
PENDING_CONFIRMATION → CONFIRMED → VEHICLE_RECEIVED → INSPECTING
    → AWAITING_QUOTE_APPROVAL → REPAIRING → COMPLETED → DELIVERED
    (Any stage → CANCELLED)
```

---

### 3.6. `repair_orders` — Phiếu sửa chữa

```json
{
  "_id": "ObjectId",
  "repairOrderNumber": "string (auto: RO-20260301-001)",
  "appointmentId": "ObjectId (ref: appointments)",
  "customerId": "ObjectId (ref: users)",
  "vehicleId": "ObjectId (ref: vehicles)",
  "garageId": "ObjectId (ref: garages)",
  "inspectionChecklist": [
    {
      "item": "string (e.g. 'Hệ thống phanh')",
      "condition": "GOOD | FAIR | POOR | NEEDS_REPAIR",
      "note": "string"
    }
  ],
  "proposedItems": [
    {
      "type": "SERVICE | PART",
      "refId": "ObjectId",
      "name": "string",
      "quantity": "number",
      "unitPrice": "number (VND)",
      "discount": "number (%)",
      "subtotal": "number (VND)"
    }
  ],
  "laborCost": "number (VND)",
  "totalEstimate": "number (VND)",
  "customerApproved": "boolean (default: false)",
  "approvedAt": "ISODate",
  "status": "DRAFT | PENDING_APPROVAL | APPROVED | IN_PROGRESS | COMPLETED",
  "startedAt": "ISODate",
  "completedAt": "ISODate",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ repairOrderNumber: 1 } unique`, `{ appointmentId: 1 }`, `{ garageId: 1 }`, `{ status: 1 }`

---

### 3.7. `repair_tasks` — Công việc sửa chữa (phân công thợ)

```json
{
  "_id": "ObjectId",
  "repairOrderId": "ObjectId (ref: repair_orders)",
  "technicianId": "ObjectId (ref: users)",
  "description": "string",
  "serviceId": "ObjectId (ref: services, optional)",
  "partsUsed": [
    {
      "partId": "ObjectId (ref: parts)",
      "partName": "string",
      "quantity": "number"
    }
  ],
  "status": "ASSIGNED | IN_PROGRESS | COMPLETED | BLOCKED",
  "priority": "LOW | MEDIUM | HIGH | URGENT",
  "estimatedMinutes": "number",
  "actualMinutes": "number",
  "notes": "string",
  "startedAt": "ISODate",
  "completedAt": "ISODate",
  "createdAt": "ISODate"
}
```

**Indexes:** `{ repairOrderId: 1 }`, `{ technicianId: 1, status: 1 }`, `{ status: 1 }`

---

### 3.8. `parts` — Kho phụ tùng

```json
{
  "_id": "ObjectId",
  "code": "string (unique, e.g. PT-001)",
  "name": "string",
  "description": "string",
  "category": "ENGINE | BRAKE | ELECTRICAL | TIRE | FILTER | OIL | BODY | OTHER",
  "brand": "string",
  "compatibleVehicles": [
    { "brand": "string", "models": ["string"] }
  ],
  "quantity": "number",
  "minQuantity": "number (alert threshold)",
  "unit": "string (cái, lít, bộ, hộp...)",
  "importPrice": "number (VND)",
  "sellPrice": "number (VND)",
  "supplier": {
    "name": "string",
    "phone": "string",
    "email": "string"
  },
  "location": "string (vị trí trong kho)",
  "garageId": "ObjectId (ref: garages)",
  "isActive": "boolean",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ code: 1 } unique`, `{ garageId: 1 }`, `{ category: 1 }`, `{ quantity: 1, minQuantity: 1 }` (for low-stock alerts), `{ name: "text" }` (text search)

---

### 3.9. `inventory_transactions` — Lịch sử xuất/nhập kho

```json
{
  "_id": "ObjectId",
  "partId": "ObjectId (ref: parts)",
  "garageId": "ObjectId (ref: garages)",
  "type": "IMPORT | EXPORT | ADJUSTMENT | RETURN",
  "quantity": "number (positive=in, negative=out)",
  "previousQuantity": "number",
  "newQuantity": "number",
  "reason": "string",
  "referenceType": "REPAIR_ORDER | INVOICE | MANUAL",
  "referenceId": "ObjectId",
  "unitPrice": "number (VND, for import)",
  "performedBy": "ObjectId (ref: users)",
  "createdAt": "ISODate"
}
```

**Indexes:** `{ partId: 1, createdAt: -1 }`, `{ garageId: 1 }`, `{ type: 1 }`, `{ referenceType: 1, referenceId: 1 }`

---

### 3.10. `invoices` — Hóa đơn

```json
{
  "_id": "ObjectId",
  "invoiceNumber": "string (auto: INV-20260301-001)",
  "appointmentId": "ObjectId (ref: appointments)",
  "repairOrderId": "ObjectId (ref: repair_orders)",
  "customerId": "ObjectId (ref: users)",
  "garageId": "ObjectId (ref: garages)",
  "items": [
    {
      "type": "SERVICE | PART | LABOR",
      "refId": "ObjectId",
      "name": "string",
      "quantity": "number",
      "unitPrice": "number (VND)",
      "discount": "number (%)",
      "subtotal": "number (VND)"
    }
  ],
  "subtotal": "number (VND)",
  "taxRate": "number (%, default: 10 for VAT)",
  "taxAmount": "number (VND)",
  "discountTotal": "number (VND)",
  "totalAmount": "number (VND)",
  "currency": "VND",
  "pdfUrl": "string",
  "status": "DRAFT | ISSUED | PAID | PARTIALLY_PAID | OVERDUE | CANCELLED",
  "dueDate": "ISODate",
  "notes": "string",
  "issuedBy": "ObjectId (ref: users)",
  "issuedAt": "ISODate",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ invoiceNumber: 1 } unique`, `{ customerId: 1 }`, `{ garageId: 1, issuedAt: -1 }`, `{ status: 1 }`, `{ appointmentId: 1 }`

---

### 3.11. `payments` — Thanh toán

```json
{
  "_id": "ObjectId",
  "invoiceId": "ObjectId (ref: invoices)",
  "customerId": "ObjectId (ref: users)",
  "garageId": "ObjectId (ref: garages)",
  "amount": "number (VND)",
  "currency": "VND",
  "method": "CASH | BANK_TRANSFER | VNPAY | MOMO",
  "status": "PENDING | SUCCESS | FAILED | REFUNDED",
  "transactionId": "string (from payment gateway)",
  "gatewayResponse": "object (raw response from VNPay/MoMo)",
  "bankInfo": {
    "bankName": "string",
    "accountNumber": "string",
    "transferNote": "string"
  },
  "refundReason": "string",
  "paidAt": "ISODate",
  "createdAt": "ISODate"
}
```

**Indexes:** `{ invoiceId: 1 }`, `{ customerId: 1 }`, `{ garageId: 1, paidAt: -1 }`, `{ method: 1 }`, `{ status: 1 }`, `{ transactionId: 1 }`

---

### 3.12. `reviews` — Đánh giá & Review

```json
{
  "_id": "ObjectId",
  "appointmentId": "ObjectId (ref: appointments)",
  "customerId": "ObjectId (ref: users)",
  "garageId": "ObjectId (ref: garages)",
  "technicianId": "ObjectId (ref: users, optional)",
  "overallRating": "number (1-5)",
  "ratings": {
    "serviceQuality": "number (1-5)",
    "technicianSkill": "number (1-5)",
    "waitingTime": "number (1-5)",
    "pricing": "number (1-5)",
    "cleanliness": "number (1-5)"
  },
  "comment": "string",
  "media": ["string (URL)"],
  "reply": {
    "content": "string",
    "repliedBy": "ObjectId (ref: users)",
    "repliedAt": "ISODate"
  },
  "isVisible": "boolean (default: true)",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

**Indexes:** `{ appointmentId: 1 } unique`, `{ garageId: 1, overallRating: -1 }`, `{ customerId: 1 }`, `{ technicianId: 1 }`

---

### 3.13. `notifications` — Thông báo

```json
{
  "_id": "ObjectId",
  "userId": "ObjectId (ref: users)",
  "type": "APPOINTMENT_CONFIRMED | APPOINTMENT_REMINDER | VEHICLE_RECEIVED | QUOTE_READY | QUOTE_APPROVED | REPAIR_UPDATE | REPAIR_COMPLETE | PAYMENT_RECEIVED | MAINTENANCE_DUE | LOW_STOCK_ALERT | REVIEW_REQUEST",
  "channel": "EMAIL | SMS | IN_APP",
  "title": "string",
  "message": "string",
  "referenceType": "APPOINTMENT | INVOICE | VEHICLE | REPAIR_ORDER | PART",
  "referenceId": "ObjectId",
  "status": "PENDING | SENT | FAILED | READ",
  "metadata": {
    "emailTo": "string",
    "smsTo": "string",
    "errorMessage": "string",
    "retryCount": "number"
  },
  "scheduledAt": "ISODate (for scheduled notifications)",
  "sentAt": "ISODate",
  "readAt": "ISODate",
  "createdAt": "ISODate"
}
```

**Indexes:** `{ userId: 1, status: 1, createdAt: -1 }`, `{ status: 1, scheduledAt: 1 }`, `{ referenceType: 1, referenceId: 1 }`

---

### 3.14. Collection Relationship Diagram

```
users (CUSTOMER) ──1:N──▶ vehicles
users (CUSTOMER) ──1:N──▶ appointments
users (CUSTOMER) ──1:N──▶ reviews
users (STAFF)    ──N:1──▶ garages

garages ──1:N──▶ services
garages ──1:N──▶ appointments
garages ──1:N──▶ parts

appointments ──1:1──▶ repair_orders ──1:N──▶ repair_tasks
appointments ──1:1──▶ invoices ──1:N──▶ payments
appointments ──1:1──▶ reviews

parts ──1:N──▶ inventory_transactions
repair_tasks ──uses──▶ parts

users ──1:N──▶ notifications
```

---

## 4. Spring Boot Modules & Dependencies

### Core Dependencies (pom.xml)

```xml
<!-- Spring Boot Starters -->
spring-boot-starter-web              <!-- REST API -->
spring-boot-starter-data-mongodb     <!-- MongoDB -->
spring-boot-starter-security         <!-- Authentication & Authorization -->
spring-boot-starter-validation       <!-- Bean Validation (@Valid, @NotNull...) -->
spring-boot-starter-mail             <!-- Email notifications -->
spring-boot-starter-aop              <!-- Aspect-Oriented Programming (logging, audit) -->

<!-- JWT -->
jjwt-api, jjwt-impl, jjwt-jackson   <!-- io.jsonwebtoken 0.12.x -->

<!-- Documentation -->
springdoc-openapi-starter-webmvc-ui  <!-- Swagger UI -->

<!-- Mapping & Utility -->
lombok                               <!-- Reduce boilerplate -->
mapstruct + mapstruct-processor      <!-- Entity ↔ DTO mapping -->

<!-- PDF Generation -->
openpdf                              <!-- Invoice PDF generation -->

<!-- File Upload -->
commons-io                           <!-- File handling utilities -->

<!-- Testing -->
spring-boot-starter-test             <!-- JUnit 5, Mockito -->
testcontainers (mongodb)             <!-- MongoDB integration tests -->
spring-security-test                 <!-- Security testing -->

<!-- Dev Tools -->
spring-boot-devtools                 <!-- Hot reload (dev only) -->
```

---

## 5. API Endpoints Design (Complete)

### 5.1. Auth (`/api/v1/auth`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/register` | PUBLIC | Đăng ký khách hàng |
| POST | `/login` | PUBLIC | Đăng nhập (email/phone + password) |
| POST | `/send-otp` | PUBLIC | Gửi OTP qua email/SMS |
| POST | `/verify-otp` | PUBLIC | Xác thực OTP |
| POST | `/refresh-token` | AUTH | Refresh JWT token |
| POST | `/logout` | AUTH | Đăng xuất (invalidate refresh token) |
| POST | `/forgot-password` | PUBLIC | Quên mật khẩu |
| POST | `/reset-password` | PUBLIC | Đặt lại mật khẩu |

### 5.2. Users (`/api/v1/users`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/me` | AUTH | Lấy thông tin cá nhân |
| PUT | `/me` | AUTH | Cập nhật thông tin cá nhân |
| PUT | `/me/avatar` | AUTH | Upload avatar |
| PUT | `/me/password` | AUTH | Đổi mật khẩu |
| GET | `/` | MANAGER | Danh sách users (paginated, filterable) |
| GET | `/{id}` | MANAGER | Chi tiết user |
| POST | `/staff` | MANAGER | Tạo tài khoản nhân viên |
| PUT | `/{id}` | MANAGER | Cập nhật user |
| PATCH | `/{id}/status` | MANAGER | Kích hoạt/vô hiệu hóa |
| GET | `/technicians` | MANAGER, RECEPTIONIST | Danh sách KTV (cho phân công) |
| GET | `/technicians/{id}/stats` | MANAGER | Thống kê năng suất KTV |

### 5.3. Vehicles (`/api/v1/vehicles`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/` | CUSTOMER | Danh sách xe của tôi |
| POST | `/` | CUSTOMER | Thêm xe |
| GET | `/{id}` | CUSTOMER | Chi tiết xe |
| PUT | `/{id}` | CUSTOMER | Cập nhật thông tin xe |
| DELETE | `/{id}` | CUSTOMER | Xóa xe |
| GET | `/{id}/history` | CUSTOMER | Lịch sử bảo dưỡng xe |
| PUT | `/{id}/mileage` | CUSTOMER | Cập nhật số km |

### 5.4. Garages (`/api/v1/garages`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/` | PUBLIC | Danh sách gara |
| GET | `/{id}` | PUBLIC | Chi tiết gara |
| POST | `/` | MANAGER | Tạo gara |
| PUT | `/{id}` | MANAGER | Cập nhật gara |
| GET | `/{id}/availability` | AUTH | Xem lịch rảnh theo ngày |
| GET | `/{id}/reviews` | PUBLIC | Đánh giá của gara |

### 5.5. Services (`/api/v1/services`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/` | PUBLIC | Danh sách dịch vụ (theo gara) |
| GET | `/{id}` | PUBLIC | Chi tiết dịch vụ |
| POST | `/` | MANAGER | Tạo dịch vụ |
| PUT | `/{id}` | MANAGER | Cập nhật dịch vụ |
| PATCH | `/{id}/status` | MANAGER | Kích hoạt/tắt dịch vụ |
| GET | `/estimate` | AUTH | Tính báo giá sơ bộ (vehicleType + serviceIds) |

### 5.6. Appointments (`/api/v1/appointments`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/` | CUSTOMER | Đặt lịch mới |
| GET | `/my` | CUSTOMER | Danh sách lịch hẹn của tôi |
| GET | `/{id}` | AUTH | Chi tiết lịch hẹn |
| PATCH | `/{id}/cancel` | CUSTOMER | Hủy lịch hẹn |
| GET | `/` | STAFF | Danh sách tất cả lịch hẹn (filter, paginate) |
| PATCH | `/{id}/confirm` | RECEPTIONIST | Xác nhận lịch hẹn |
| PATCH | `/{id}/reschedule` | RECEPTIONIST | Đề xuất đổi giờ |
| PATCH | `/{id}/status` | STAFF | Cập nhật trạng thái |
| POST | `/{id}/media` | CUSTOMER | Upload ảnh/video |
| GET | `/calendar` | STAFF | Lịch dạng calendar (ngày/tuần) |

### 5.7. Repair Orders (`/api/v1/repair-orders`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/` | STAFF | Tạo phiếu sửa chữa |
| GET | `/{id}` | AUTH | Chi tiết phiếu |
| PUT | `/{id}/inspection` | TECHNICIAN | Cập nhật checklist kiểm tra |
| PUT | `/{id}/quote` | STAFF | Cập nhật báo giá |
| PATCH | `/{id}/approve` | CUSTOMER | Khách duyệt báo giá |
| POST | `/{id}/tasks` | STAFF | Phân công công việc cho KTV |
| GET | `/{id}/tasks` | STAFF | Danh sách công việc |
| PATCH | `/tasks/{taskId}/status` | TECHNICIAN | KTV cập nhật tiến độ |

### 5.8. Inventory / Parts (`/api/v1/parts`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/` | STAFF | Danh sách phụ tùng (search, filter) |
| POST | `/` | MANAGER | Thêm phụ tùng |
| GET | `/{id}` | STAFF | Chi tiết phụ tùng |
| PUT | `/{id}` | MANAGER | Cập nhật phụ tùng |
| POST | `/{id}/import` | MANAGER | Nhập kho |
| POST | `/{id}/export` | STAFF | Xuất kho |
| GET | `/low-stock` | MANAGER | Danh sách sắp hết hàng |
| GET | `/{id}/transactions` | MANAGER | Lịch sử xuất/nhập |

### 5.9. Invoices (`/api/v1/invoices`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/` | STAFF | Tạo hóa đơn |
| GET | `/{id}` | AUTH | Chi tiết hóa đơn |
| PUT | `/{id}` | STAFF | Cập nhật hóa đơn (DRAFT only) |
| PATCH | `/{id}/issue` | STAFF | Phát hành hóa đơn |
| GET | `/{id}/pdf` | AUTH | Tải PDF hóa đơn |
| POST | `/{id}/send-email` | STAFF | Gửi hóa đơn qua email |
| GET | `/my` | CUSTOMER | Hóa đơn của tôi |

### 5.10. Payments (`/api/v1/payments`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/vnpay/create` | CUSTOMER | Tạo link thanh toán VNPay |
| GET | `/vnpay/callback` | PUBLIC | VNPay callback (IPN) |
| POST | `/momo/create` | CUSTOMER | Tạo link thanh toán MoMo |
| POST | `/momo/callback` | PUBLIC | MoMo callback |
| POST | `/cash` | STAFF | Ghi nhận thanh toán tiền mặt |
| POST | `/bank-transfer` | STAFF | Ghi nhận chuyển khoản |
| GET | `/{id}` | AUTH | Chi tiết thanh toán |

### 5.11. Reviews (`/api/v1/reviews`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/` | CUSTOMER | Tạo đánh giá (sau DELIVERED) |
| GET | `/my` | CUSTOMER | Đánh giá của tôi |
| GET | `/{id}` | PUBLIC | Chi tiết đánh giá |
| POST | `/{id}/reply` | MANAGER | Phản hồi đánh giá |
| PATCH | `/{id}/visibility` | MANAGER | Ẩn/hiện đánh giá |

### 5.12. Notifications (`/api/v1/notifications`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/` | AUTH | Danh sách thông báo của tôi |
| GET | `/unread-count` | AUTH | Số thông báo chưa đọc |
| PATCH | `/{id}/read` | AUTH | Đánh dấu đã đọc |
| PATCH | `/read-all` | AUTH | Đánh dấu tất cả đã đọc |

### 5.13. Reports (`/api/v1/reports`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/revenue` | MANAGER | Doanh thu (theo ngày/tháng/quý) |
| GET | `/revenue/by-service` | MANAGER | Doanh thu theo loại dịch vụ |
| GET | `/revenue/by-source` | MANAGER | Doanh thu theo nguồn (web/walk-in) |
| GET | `/top-services` | MANAGER | Top dịch vụ phổ biến |
| GET | `/peak-hours` | MANAGER | Giờ cao điểm |
| GET | `/customer-retention` | MANAGER | Tỉ lệ khách quay lại |
| GET | `/technician-performance` | MANAGER | Năng suất KTV |
| GET | `/inventory-summary` | MANAGER | Tổng quan kho |

---

## 6. Authentication & Authorization

### JWT Flow

```
1. Customer registers → receives JWT (access + refresh)
2. Login with email/phone + password → JWT issued
3. OTP flow: send OTP → verify → mark phone/email verified
4. Access token: 15min expiry, sent in Authorization header
5. Refresh token: 7 days expiry, stored in DB (users.refreshToken)
6. Logout: clear refresh token from DB
```

### RBAC Matrix

| Resource | CUSTOMER | RECEPTIONIST | TECHNICIAN | MANAGER |
|----------|----------|--------------|------------|---------|
| Own profile | RW | RW | RW | RW |
| Vehicles (own) | CRUD | R | R | CRUD |
| Appointments (own) | CR | — | — | — |
| Appointments (all) | — | CRUD | R | CRUD |
| Repair orders | R (own) | CRUD | RU | CRUD |
| Repair tasks | — | CR | RU | CRUD |
| Parts inventory | — | R | R | CRUD |
| Invoices (own) | R | — | — | — |
| Invoices (all) | — | CRUD | — | CRUD |
| Payments | Create | CRUD | — | CRUD |
| Reviews | CR | — | — | CRUD |
| Reports | — | — | — | R |
| User management | — | — | — | CRUD |
| Services | R | R | R | CRUD |
| Garages | R | R | R | CRUD |

---

## 7. Implementation Phases (Sprints)

### Phase 1: Foundation & Core Setup (2 weeks)

**Sprint 1.1 — Project Setup (Week 1)**
- [ ] Initialize Spring Boot project (Spring Initializr)
- [ ] Configure Docker (Dockerfile + docker-compose.yml)
- [ ] MongoDB configuration & connection
- [ ] Base classes: BaseDocument, ApiResponse<T>, PageResponse<T>
- [ ] Global exception handler
- [ ] CORS configuration
- [ ] Swagger/OpenAPI setup

**Sprint 1.2 — Auth & Users (Week 2)**
- [ ] User entity & repository
- [ ] JWT service (generate, validate, refresh)
- [ ] Spring Security configuration (filter chain)
- [ ] Register endpoint (CUSTOMER)
- [ ] Login endpoint (email/phone + password)
- [ ] OTP service (generate, send, verify)
- [ ] Staff account creation (MANAGER only)
- [ ] Profile management endpoints

### Phase 2: Core Business — Booking & Workflow (3 weeks)

**Sprint 2.1 — Vehicles & Garages (Week 3)**
- [ ] Vehicle CRUD (CUSTOMER)
- [ ] Garage CRUD (MANAGER)
- [ ] Service catalog CRUD (MANAGER)
- [ ] Service estimate calculation endpoint
- [ ] Garage availability checking

**Sprint 2.2 — Appointments (Week 4)**
- [ ] Appointment creation (booking flow)
- [ ] Time slot conflict validation
- [ ] Appointment number auto-generation
- [ ] Status transition workflow (state machine)
- [ ] Calendar view endpoint (day/week)
- [ ] Filter & search appointments
- [ ] File upload (images/videos) with local storage

**Sprint 2.3 — Repair Workflow (Week 5)**
- [ ] Repair order creation from appointment
- [ ] Inspection checklist management
- [ ] Quote building & submission
- [ ] Customer quote approval flow
- [ ] Repair task creation & assignment
- [ ] Technician progress updates
- [ ] Status propagation (task → order → appointment)

### Phase 3: Operations & Inventory (2 weeks)

**Sprint 3.1 — Parts & Inventory (Week 6)**
- [ ] Part CRUD with text search
- [ ] Import stock (inventory transaction)
- [ ] Export stock (linked to repair orders)
- [ ] Low-stock alerts (quantity <= minQuantity)
- [ ] Inventory transaction history
- [ ] Auto-deduct stock on invoice creation

**Sprint 3.2 — Notifications (Week 7)**
- [ ] Notification entity & service
- [ ] Email notification (Spring Mail)
- [ ] SMS notification (mock/log-based)
- [ ] In-app notification endpoints
- [ ] Event-driven notifications:
  - Appointment confirmed
  - Quote ready for approval
  - Repair completed
  - Invoice issued
  - Maintenance reminder (scheduled)

### Phase 4: Billing, Payments & Reviews (2 weeks)

**Sprint 4.1 — Invoicing & Payments (Week 8)**
- [ ] Invoice creation from repair order
- [ ] Invoice line items (services + parts + labor)
- [ ] Tax calculation (VAT 10%)
- [ ] PDF generation (OpenPDF)
- [ ] Send invoice via email
- [ ] VNPay mock payment integration
- [ ] MoMo mock payment integration
- [ ] Cash/bank transfer recording
- [ ] Payment status management

**Sprint 4.2 — Reviews & Reports (Week 9)**
- [ ] Customer review creation (post-delivery)
- [ ] Multi-criteria ratings
- [ ] Manager reply to reviews
- [ ] Revenue reports (daily/monthly/quarterly)
- [ ] Revenue by service type, by source
- [ ] Top services ranking
- [ ] Peak hours analysis
- [ ] Customer retention rate
- [ ] Technician performance stats
- [ ] Inventory summary report

### Phase 5: Testing, Polish & Deployment (1 week)

**Sprint 5.1 — Quality & Deploy (Week 10)**
- [ ] Unit tests (services layer) — target 80% coverage
- [ ] Integration tests (Testcontainers + MongoDB)
- [ ] API tests (MockMvc)
- [ ] Security tests (role-based access)
- [ ] Production Docker Compose
- [ ] Environment-specific profiles (dev, staging, prod)
- [ ] MongoDB indexes verification
- [ ] API documentation review
- [ ] Performance testing (basic load test)
- [ ] Data seeding script (sample data)

---

## 8. Docker Setup

### docker-compose.yml

```yaml
version: '3.8'

services:
  backend-api:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: webgara-api
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - MONGODB_URI=mongodb://mongodb:27017/webgara
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=900000
      - MAIL_HOST=${MAIL_HOST}
      - MAIL_PORT=${MAIL_PORT}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - UPLOAD_DIR=/app/uploads
    volumes:
      - upload-data:/app/uploads
    depends_on:
      mongodb:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - webgara-network

  mongodb:
    image: mongo:7.0
    container_name: webgara-mongodb
    ports:
      - "27017:27017"
    environment:
      - MONGO_INITDB_DATABASE=webgara
    volumes:
      - mongo-data:/data/db
      - ./docker/mongo-init.js:/docker-entrypoint-initdb.d/init.js:ro
    healthcheck:
      test: echo 'db.runCommand("ping").ok' | mongosh --quiet
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped
    networks:
      - webgara-network

  mongo-express:
    image: mongo-express:1.0
    container_name: webgara-mongo-express
    ports:
      - "8081:8081"
    environment:
      - ME_CONFIG_MONGODB_URL=mongodb://mongodb:27017/webgara
      - ME_CONFIG_BASICAUTH=false
    depends_on:
      mongodb:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - webgara-network
    profiles:
      - dev

volumes:
  mongo-data:
  upload-data:

networks:
  webgara-network:
    driver: bridge
```

### Dockerfile (Multi-stage)

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 9. Security Considerations

- **Password:** BCrypt (strength 12)
- **JWT:** RS256 or HS256 with strong secret, short-lived access tokens
- **CORS:** Whitelist allowed origins
- **Rate Limiting:** Login attempts (5/min per IP)
- **Input Validation:** @Valid on all request DTOs
- **File Upload:** Validate file type, max size (10MB images, 50MB videos)
- **XSS:** Sanitize user inputs (description, comments, reviews)
- **MongoDB Injection:** Use parameterized queries (Spring Data handles this)
- **Audit Trail:** statusHistory on appointments, inventory_transactions for stock

---

## 10. Testing Strategy

| Layer | Tool | Target |
|-------|------|--------|
| Unit Tests | JUnit 5 + Mockito | Service layer: business logic, validations |
| Integration Tests | Testcontainers (MongoDB) | Repository layer: queries, indexes |
| API Tests | MockMvc + Spring Security Test | Controller layer: endpoints, auth, roles |
| E2E Tests | Postman / Newman | Full flow: booking → repair → invoice → payment |
| Load Tests | Apache JMeter (optional) | Performance under concurrent requests |

### Test Coverage Target: 80%+ on service layer

---

## 11. Environment Profiles

| Profile | Database | Logging | Swagger | Features |
|---------|----------|---------|---------|----------|
| `dev` | Local MongoDB (Docker) | DEBUG | Enabled | Hot reload, Mongo Express |
| `docker` | Docker MongoDB | INFO | Enabled | Containerized deployment |
| `staging` | Remote MongoDB | INFO | Enabled | Pre-production testing |
| `prod` | Remote MongoDB (Atlas/VPS) | WARN | Disabled | Production, no dev tools |
