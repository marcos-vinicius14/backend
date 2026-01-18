# Data Model Reference

UUIDs use version 7. Local cache fields are used to avoid round trips to external services.

## users
Manages local users (email + password) and Stripe linkage.

| Column | Type | Description |
| --- | --- | --- |
| id | UUID / BigSerial | Unique user identifier. |
| email | Varchar (Unique) | Login email. |
| password_hash | Varchar | BCrypt hash. |
| full_name | Varchar | Full name. |
| stripe_customer_id | Varchar (Nullable) | Stripe customer id for balance operations. |
| credits_balance | Integer (Default: 0) | Local cache of available credits (kept in sync via Stripe webhooks). |
| created_at | Timestamp | Created at. |

Design note: keeping `credits_balance` locally allows quick checks without Stripe latency.

## user_sessions
Backend-only session tracking; the cookie carries only the `session_id`.

| Column | Type | Description |
| --- | --- | --- |
| id | UUID | Session id (cookie `session_id`). |
| user_id | Foreign Key (`users.id`) | Session owner. |
| expires_at | Timestamp | Expiration (e.g., +7 days). |
| created_at | Timestamp | Created at. |

## optimizations
Core optimization records (PDF -> AI -> JSON).

| Column | Type | Description |
| --- | --- | --- |
| id | UUID | Optimization id. |
| user_id | Foreign Key (`users.id`) | Requesting user (used to debit credits). |
| target_role | Varchar | Target role (e.g., "Product Manager"). |
| s3_file_key | Varchar (Nullable) | S3 path to uploaded PDF (may be pruned later). |
| status | Enum/String | PROCESSING, COMPLETED, FAILED. |
| optimized_content | JSONB | Generated content (headline, about, experiences) to reuse without new charge. |
| credits_used | Integer | Credits debited (usually 1). |
| created_at | Timestamp | Created at. |
