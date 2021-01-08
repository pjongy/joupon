# API spec

## You should know
There are no standard specification for HTTP a multiple same query parameter (repeated)
So, each framework supports their own methodology for represent repeated value for query parameter.
In this case, framework vert.x supports repeated query string as multiple definition like:
```
?status=USED&status=USING
?coupon_ids=91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d&coupon_ids=efeba0ae-28ed-4306-bba5-e595996fc116
```

## Endpoint

### Coupon

### Enums
```kotlin
// handler/coupon/extension/Condition.kt
val availableJoinTypes = listOf("AND", "OR")
val availableOperators = listOf(
  "int_eq",
  "int_neq",
  "int_lte",
  "int_lt",
  "int_gte",
  "int_gt",
  "str_eq",
  "str_neq",
  "contains",
)
```

### API specs

- /coupons
  - / POST
    - purpose: Create a coupon (It can be issued by users)
    - query_string: `Empty`
    - request:
      ```
      {
        "name": "coupon name",
        "category": "coupon category",
        "total_amount": 1,  // Limit for coupon can be issued by users
        "discount_rate": 0,
        "discount_amount": 0.98,
        "description": "coupon description",
        "image_url": "https://image.coupon/url",
        "expired_at": "9999-01-01T00:00:00Z"
        "issuing_condition": { // It is recursive
          "conditions": [
              {
                "key": ...key string for validate with condition...,
                "value": ...value string for validate with condition...,
                "operator": ...operater string for validate with condition...
              },
          ],
          "join_type": ...join type(AND/OR) string for validate with condition...
        },
        "using_condition": { // It is recursive
          "conditions": [
              {
                "key": ...key string for validate with condition...,
                "value": ...value string for validate with condition...,
                "operator": ...operater string for validate with condition...
              },
          ],
          "join_type": ...join type(AND/OR) string for validate with condition...
        }
      }
      ```
    - request-header:
      ```
      {
        X-Internal-Key: ...internal api key defined by config...
      }
      ```
    - response:
      ```
      {
        "id": "91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d",
        "name": "coupon name",
        "category": "COUPON_CATEGORY",
        "total_amount": 1,
        "discount_amount": 0,
        "discount_rate": 0.98,
        "created_at": "9999-01-01T00:00:00Z",
        "expired_at": "9999-01-01T00:00:00Z",
        "description": "coupon description",
        "image_url": "https://image.coupon/url"
        "issuing_condition": { // It is recursive
          "conditions": [
              {
                "key": ...key string for validate with condition...,
                "value": ...value string for validate with condition...,
                "operator": ...operater string for validate with condition...
              },
          ],
          "join_type": ...join type(AND/OR) string for validate with condition...
        },
        "using_condition": { // It is recursive
          "conditions": [
              {
                "key": ...key string for validate with condition...,
                "value": ...value string for validate with condition...,
                "operator": ...operater string for validate with condition...
              },
          ],
          "join_type": ...join type(AND/OR) string for validate with condition...
        }
      }
      ```

  - / GET
    - purpose: Create a coupon (It can be issued by users)
    - query_string:
      ```
      page=...fetch start page...
      page_size=...count for fetch once (page)...
      ```
    - request: `Empty`
    - request-header:
      ```
      {
        X-Internal-Key: ...internal api key defined by config...
      }
      ```
    - response:
      ```
      {
        "coupons": [
          {
            "id": "91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d",
            "name": "coupon name",
            "category": "COUPON_CATEGORY",
            "total_amount": 1,
            "discount_amount": 0,
            "discount_rate": 0.98,
            "created_at": "9999-01-01T00:00:00Z",
            "expired_at": "9999-01-01T00:00:00Z",
            "description": "coupon description",
            "image_url": "https://image.coupon/url"
            "issuing_condition": { // It is recursive
              "conditions": [
                  {
                    "key": ...key string for validate with condition...,
                    "value": ...value string for validate with condition...,
                    "operator": ...operater string for validate with condition...
                  },
              ],
              "join_type": ...join type(AND/OR) string for validate with condition...
            },
            "using_condition": { // It is recursive
              "conditions": [
                  {
                    "key": ...key string for validate with condition...,
                    "value": ...value string for validate with condition...,
                    "operator": ...operater string for validate with condition...
                  },
              ],
              "join_type": ...join type(AND/OR) string for validate with condition...
            }
          }
          ... repeated
        ],
        "total": 1
      }
      ```

  - /fetch-with-issued GET
    - purpose: Get issued coupons' usage status (coupon can be issued by users)
    - query_string:
      ```
      coupon_ids=...fetch target coupon id (UUID)... [repeated]
      ```
    - request: `Empty`
    - request-header:
      ```
      {
        X-Internal-Key: ...internal api key defined by config...
      }
      ```
    - response:
      ```
      {
        "coupons_with_usage_status": [
          {
            "coupon": {
              "id": "91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d",
              "name": "coupon name",
              "category": "COUPON_CATEGORY",
              "total_amount": 1,
              "discount_amount": 0,
              "discount_rate": 0.98,
              "created_at": "9999-01-01T00:00:00Z",
              "expired_at": "9999-01-01T00:00:00Z",
              "description": "coupon description",
              "image_url": "https://image.coupon/url"
              "issuing_condition": { // It is recursive
                "conditions": [
                    {
                      "key": ...key string for validate with condition...,
                      "value": ...value string for validate with condition...,
                      "operator": ...operater string for validate with condition...
                    },
                ],
                "join_type": ...join type(AND/OR) string for validate with condition...
              },
              "using_condition": { // It is recursive
                "conditions": [
                    {
                      "key": ...key string for validate with condition...,
                      "value": ...value string for validate with condition...,
                      "operator": ...operater string for validate with condition...
                    },
                ],
                "join_type": ...join type(AND/OR) string for validate with condition...
              }
            },
            "using": 0,
            "unused": 0,
            "used": 1
          },
        ]
      }
      ```


### CouponWallet

### Enums
```kotlin
// repository/CouponWalletRepository.kt
val AVAILABLE_STRING_TO_STATUS = mapOf(
  "UNUSED" to CouponWalletStatus.UNUSED,
  "USING" to CouponWalletStatus.USING,
  "USED" to CouponWalletStatus.USED,
)
```

### API specs

- /wallets
  - /{owner_id}/availables *GET*
    - purpose: Fetch "owner_id"'s issued coupons with status filter
    - query_string:
      ```
      page=...fetch start page...
      page_size=...count for fetch once (page)...
      status=...status what you want (AVAILABLE_STRING_TO_STATUS)...[repeated]
      ```
    - request: `Empty`
    - request-header: `Empty`
    - response:
      ```
      {
        "coupons": [
          {
            "id": "91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d",
            "name": "coupon name",
            "category": "COUPON_CATEGORY",
            "discount_amount": 0,
            "discount_rate": 0.98,
            "expired_at": "9999-01-01T00:00:00Z",
            "description": "coupon description",
            "image_url": "https://image.coupon/url"
          }
          ... repeated
        ],
        "total": 1
      }
      ```

  - /{owner_id}/coupons/{coupon_id} *POST*
    - purpose: Issue coupon (store coupon to owner's wallet)
    - query_string: `Empty`
    - request:
      ```
      {
      "properties": [
          {
            "key": ...key string for validate with condition...,
            "value": ...value string for validate with condition...
          }
        ]
      }
      ```
    - request-header:
      ```
      {
        X-Internal-Key: ...internal api key defined by config...
      }
      ```
    - response:
      ```
      {
        "owner_id": "owner_id_string",
        "coupon": {
          "id": "91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d",
          "name": "coupon name",
          "category": "COUPON_CATEGORY",
          "discount_amount": 0,
          "discount_rate": 0.98,
          "expired_at": "9999-01-01T00:00:00Z",
          "description": "coupon description",
          "image_url": "https://image.coupon/url"
        }
      }
      ```

  - /{owner_id}/coupons/{coupon_id}/status/{status} *PUT*
    - purpose: Change issued coupon's status (AVAILABLE_STRING_TO_STATUS) by internal server like: prepare payment(USING), after payment (USED)
    - query_string: `Empty`
    - request: `Empty`
    - request-header:
      ```
      {
        X-Internal-Key: ...internal api key defined by config...
      }
      ```
    - response:
      ```
      {
        "owner_id": "owner_id_string",
        "coupon_id": "91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d",
        "status": ...updated status...
      }
      ```

  - /{owner_id}/coupons/{coupon_id}/status *GET*
    - purpose: Get issued coupon's status (AVAILABLE_STRING_TO_STATUS)
    - query_string: `Empty`
    - request: `Empty`
    - request-header: `Empty`
    - response:
      ```
      {
        "owner_id": "owner_id_string",
        "coupon_id": "91d8d329-16f7-4a6e-9c1d-47fe28fc6a6d",
        "status": ...updated status...
      }
      ```
