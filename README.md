# EcoCommerce - Production-Ready E-commerce Blueprint

Tài liệu + skeleton dự án E-commerce production-ready theo yêu cầu: storefront, admin dashboard, backend API, bảo mật, SEO, hiệu năng, CI/CD, Docker.

## 1) Tổng quan kiến trúc

### Lựa chọn stack
- **Frontend:** Next.js 15 (App Router, SSR/ISR) + React + TypeScript.
- **Backend:** **Spring Boot 3 (Java 21)**.
- **DB:** PostgreSQL 16.
- **Cache + queue nhẹ:** Redis 7.
- **Search:** Meilisearch (nhẹ, dễ vận hành hơn Elasticsearch cho giai đoạn đầu).
- **Object storage:** MinIO (S3 compatible), có thể thay S3 thật khi production.
- **Auth:** Access JWT (short-lived) + Refresh Token (rotating).
- **Queue event:** RabbitMQ (ưu tiên cho async nghiệp vụ) + Redis cache.
- **Container:** Docker + Docker Compose.
- **CI/CD:** GitHub Actions.
- **Deploy:** VPS (Docker) hoặc Kubernetes.

### Vì sao chọn Spring Boot thay Laravel
1. Type-safe mạnh với Java + validation + compiler-time checks.
2. Dễ scale enterprise: transaction, batch, messaging, observability.
3. Hệ sinh thái tốt cho clean architecture, event-driven, security nâng cao.
4. Tích hợp tốt với RabbitMQ, Redis, OpenTelemetry, Prometheus.

### Kiến trúc tổng thể
- **Modular Monolith** (khuyến nghị phase 1-2): tách module nghiệp vụ rõ ràng, deploy 1 service để giảm độ phức tạp vận hành.
- Khi tăng trưởng có thể tách dần module (`catalog`, `order`, `payment`, `notification`) thành microservices.

```text
Client (Next.js SSR/ISR)
  -> API Gateway/Nginx
    -> Spring Boot API (modular monolith)
       -> PostgreSQL (OLTP)
       -> Redis (cache/session/rate-limit)
       -> RabbitMQ (events/jobs)
       -> Meilisearch (full-text search)
       -> MinIO/S3 (images)
```

### Layering (Backend)
- `controller` -> `application service/usecase` -> `domain` -> `repository`
- DTO mapper tách riêng (`dto`, `mapper`)
- Nguyên tắc: SOLID + dependency inversion + transaction boundaries ở service layer.

---

## 2) Danh sách tính năng

### Storefront
- Auth: đăng ký, đăng nhập, refresh token, quên mật khẩu.
- Catalog: list/search/filter/sort sản phẩm.
- PDP: gallery, mô tả, specs, tồn kho, rating, related products.
- Cart + wishlist + coupon.
- Checkout: địa chỉ, shipping, payment.
- Order tracking + cancel hợp lệ + lịch sử mua.
- Review sau mua hàng.

### Admin
- Dashboard: revenue, orders, top products/customers, conversion.
- CRUD product/category/brand/inventory/banner/coupon.
- Quản lý orders, payments, reviews, customers.
- RBAC: admin/staff với permissions chi tiết.
- Audit log mọi thao tác admin.

---

## 3) Cấu trúc thư mục frontend/backend

```text
.
├── frontend/
│   ├── app/
│   │   ├── (store)/
│   │   ├── admin/
│   │   ├── product/[slug]/
│   │   ├── cart/
│   │   ├── checkout/
│   │   └── profile/
│   ├── components/
│   ├── lib/
│   └── Dockerfile
├── backend/
│   ├── src/main/java/com/eco/
│   │   ├── auth/ product/ cart/ checkout/ order/
│   │   ├── payment/ coupon/ review/ admin/
│   │   ├── security/ cache/ notification/
│   │   └── ...
│   ├── src/main/resources/application.yml
│   └── Dockerfile
├── database/schema.sql
├── docs/api/openapi.yaml
├── docker-compose.yml
├── .env.example
└── .github/workflows/ci.yml
```

Frontend React/Next.js đã được bổ sung với các trang chính: Home, Product Listing, Product Detail (kèm JSON-LD SEO), Cart, Checkout, Profile, Order History, Admin Dashboard, Admin Product Management, Admin Order Management.

---

## 4) Thiết kế database

Chi tiết đầy đủ trong [`database/schema.sql`](database/schema.sql).

### Quan hệ chính
- `users` n-n `roles` qua `user_roles`.
- `roles` n-n `permissions` qua `role_permissions`.
- `products` 1-n `product_variants`, 1-n `product_images`, n-1 `brands`, n-1 `categories`.
- `product_variants` 1-1 `inventories`.
- `carts` 1-n `cart_items`.
- `orders` 1-n `order_items`, 1-n `payments`, n-1 `addresses`.
- `coupons` 1-n `coupon_usages`.
- `products` 1-n `reviews`.
- `users` 1-n `wishlists`, `notifications`, `addresses`.
- `audit_logs` track actor + entity + before/after json.

### Soft delete
- Cột `deleted_at` trên bảng business quan trọng (`users`, `products`, `categories`, `brands`, `coupons`, `reviews`).
- Query mặc định luôn `WHERE deleted_at IS NULL`.

### Audit thay đổi
- App-level audit: ghi vào `audit_logs` cho admin actions.
- DB trigger (optional) cho bảng nhạy cảm (`orders`, `payments`).

### Index gợi ý
- Catalog: `(category_id, status)`, `(brand_id, status)`, `price`, `created_at`, `sold_count`.
- Cart/order/payment: `user_id`, `order_no`, `status`, `created_at`.
- Coupon: `code UNIQUE`, `valid_from`, `valid_to`, `is_active`.
- Review: `(product_id, rating, created_at)`.

---

## 5) Danh sách API

OpenAPI mẫu: [`docs/api/openapi.yaml`](docs/api/openapi.yaml).

### Error format chuẩn
```json
{
  "timestamp": "2026-04-25T12:00:00Z",
  "traceId": "3f6...",
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "errors": [{"field":"email","message":"invalid format"}]
}
```

### Pagination/filter/sort
- Query chuẩn: `?page=1&size=20&sort=price,asc&category=...&brand=...&rating_gte=4`.

### Idempotency
- `POST /api/v1/checkout` và payment create yêu cầu header `Idempotency-Key`.
- Lưu key theo `user_id + endpoint + key`, TTL 24h.

---

## 6) Luồng nghiệp vụ quan trọng (flow + rules)

1. **Đăng ký/đăng nhập**
   - Register: validate email/password -> hash bcrypt -> create user -> send verify email.
   - Login: verify hash -> issue access (15m) + refresh (7d, rotate) -> ghi session token hash DB.
   - Rule: khóa account sau N lần sai (rate-limit + lockout).

2. **Add to cart**
   - Check variant active + stock >= qty.
   - Upsert cart item theo `(cart_id, variant_id)`.
   - Rule: qty max per user configurable.

3. **Checkout**
   - Validate cart, address, shipping method, coupon, payment method.
   - Tạo checkout snapshot để chống thay đổi giá bất ngờ.

4. **Create order**
   - Transaction: tạo `orders` + `order_items` + reserve inventory.
   - Publish `ORDER_CREATED` event.

5. **Payment success/fail**
   - Webhook verify signature.
   - Idempotent update payment + order status.
   - Success -> `PAID`; Fail -> `PAYMENT_FAILED`.

6. **Inventory update**
   - Reserve lúc đặt hàng, giảm thật khi payment success (hoặc ngay lúc đặt hàng cho COD).
   - Release reserve khi timeout/cancel.

7. **Cancel order**
   - Chỉ cho trạng thái `PENDING`/`CONFIRMED` chưa shipped.
   - Release inventory + trigger refund nếu đã thanh toán.

8. **Refund**
   - Tạo refund request + gọi PSP API + chờ webhook xác nhận.

9. **Apply coupon**
   - Check active/date/usage_limit/min_spend/user_limit.
   - Ghi `coupon_usages` khi order thành công.

10. **Send confirmation email**
   - Queue `SEND_ORDER_CONFIRMATION` -> worker gửi email -> retry exponential backoff.

11. **Review after purchase**
   - User chỉ review nếu có order item đã delivered.
   - 1 user 1 review/variant/order item.

---

## 7) Thiết kế bảo mật
- JWT access token ngắn hạn + refresh token rotate + revoke list.
- Password hash BCrypt/Argon2.
- CSRF: bật cho cookie-based admin; với JWT bearer, dùng SameSite + anti-replay.
- XSS: sanitize input, encode output, CSP header.
- SQL injection: chỉ dùng JPA parameterized query.
- RBAC + permission guard ở method level.
- Rate-limit login (Redis bucket) + captcha sau nhiều fail.
- Verify webhook signature (HMAC-SHA256) + timestamp tolerance.
- Upload file: validate MIME + extension + size + antivirus scan (optional ClamAV).
- Security headers: HSTS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy.

---

## 8) Chiến lược SEO (Next.js)
- Trang Home/Category/Product dùng **SSR + ISR**.
- Product detail generate JSON-LD (`Product`, `AggregateRating`, `BreadcrumbList`).
- Dynamic metadata: title/description/OG/Twitter cards.
- `sitemap.xml`, `robots.txt`, canonical URL.
- Preload critical assets, image optimization next/image, CDN cache.

---

## 9) CI/CD + Docker
- `docker-compose.yml` dựng đầy đủ: frontend, backend, postgres, redis, rabbitmq, meilisearch, minio, nginx.
- GitHub Actions:
  - lint + unit test frontend/backend
  - build docker image
  - scan basic (Trivy optional)
  - deploy staging (manual approval)
- Backup:
  - PostgreSQL base backup + WAL archiving.
  - MinIO versioning lifecycle.
- Rollback:
  - Blue/green hoặc image tag rollback + DB migration backward-compatible.

---

## 10) Code mẫu các phần quan trọng

Xem các file trong `backend/src/main/java/com/eco/`:
- Authentication: `auth/AuthController.java`, `security/JwtService.java`, `security/SecurityConfig.java`
- Product CRUD: `product/ProductController.java`
- Cart: `cart/CartController.java`
- Checkout: `checkout/CheckoutService.java`
- Order creation: `order/OrderService.java`
- Payment webhook: `payment/PaymentWebhookController.java`
- Coupon apply: `coupon/CouponService.java`
- Review: `review/ReviewController.java`
- Admin RBAC: `admin/RbacService.java`
- Redis cache: `cache/CacheConfig.java`
- Queue email: `notification/OrderConfirmationEmailJob.java`

---

## 11) Checklist production-ready
- [ ] Secrets qua vault/secret manager, không hardcode.
- [ ] HTTPS everywhere + WAF/CDN.
- [ ] Migration strategy + rollback tested.
- [ ] SLO/SLI + alerting (latency, error rate, queue lag).
- [ ] Audit log + data retention policy.
- [ ] Load test checkout/payment peak.
- [ ] Pen-test + dependency vulnerability scan.
- [ ] Backup/restore drill định kỳ.

---

## 12) Roadmap phát triển

### Phase 1 (MVP 6-8 tuần)
- Catalog, cart, checkout, COD/1 PSP, admin cơ bản, SSR SEO.

### Phase 2 (Scale 8-12 tuần)
- Multi-warehouse inventory, advanced search, recommendation, analytics.

### Phase 3 (Enterprise)
- Tách microservices, event streaming, A/B testing, internationalization, loyalty.
