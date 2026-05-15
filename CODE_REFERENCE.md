# Code Reference & Implementation Details

## 📚 Service Class Structure

### TokenRefreshAndDataFetchService.java

```
┌─────────────────────────────────────────────────────────────┐
│         TokenRefreshAndDataFetchService                      │
├─────────────────────────────────────────────────────────────┤
│ CONSTANTS                                                   │
│  - TOKEN_API_URL                                            │
│  - DATA_API_URL                                             │
│                                                             │
│ FIELDS                                                      │
│  - RestTemplate (injected)                                  │
│  - cachedToken (String)                                     │
│  - Logger                                                   │
│                                                             │
│ METHODS                                                     │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ @Scheduled(fixedRate = 3600000)                     │  │
│  │ refreshTokenAndFetchData()                          │  │
│  │ └─ Runs every hour automatically                    │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ fetchToken()                                        │  │
│  │ └─ Calls first API, returns token                   │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ fetchApprovedFamiliesData(token)                    │  │
│  │ └─ Calls second API with token                      │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ createTokenApiHeaders()                             │  │
│  │ └─ Creates headers for first API                    │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ createDataApiHeaders(token)                         │  │
│  │ └─ Creates headers for second API (with Bearer)     │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ createRequestBody()                                 │  │
│  │ └─ JSON body for second API                         │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ getCachedToken()                                    │  │
│  │ └─ Returns currently cached token                   │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ manualRefresh()                                     │  │
│  │ └─ Manually trigger refresh (for testing)           │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎛️ Controller Class Structure

### SchedulerController.java

```
┌──────────────────────────────────────┐
│      SchedulerController             │
├──────────────────────────────────────┤
│ @RequestMapping("/api/scheduler")    │
│                                      │
│ ENDPOINTS                            │
│ ┌──────────────────────────────────┐│
│ │ POST /trigger-refresh            ││
│ │ └─ Manually trigger refresh      ││
│ └──────────────────────────────────┘│
│                                      │
│ ┌──────────────────────────────────┐│
│ │ GET /cached-token                ││
│ │ └─ Get current cached token      ││
│ └──────────────────────────────────┘│
└──────────────────────────────────────┘
```

---

## 🔌 Endpoint Details

### Endpoint 1: Manual Trigger
```
METHOD: POST
URL: http://localhost:8080/api/scheduler/trigger-refresh

RESPONSE (Success):
200 OK
"Token refresh and data fetch triggered successfully"

RESPONSE (Error):
500 Internal Server Error
"Error triggering refresh: {error details}"
```

### Endpoint 2: Get Token
```
METHOD: GET
URL: http://localhost:8080/api/scheduler/cached-token

RESPONSE (With Token):
200 OK
"Current cached token: eyJraWQiOiIwZ0pQXC9TY1hTdzBtQUV1dWl5VEh4em1LTWp1b0ZT..."

RESPONSE (No Token):
200 OK
"No cached token available"
```

---

## 🔗 API Integration Flow

```
┌─────────────────────────────────────────────┐
│   Application Starts                        │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│   Spring Loads @Service Beans               │
│   - TokenRefreshAndDataFetchService         │
│   - SchedulerController                     │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│   @EnableScheduling Detects @Scheduled     │
│   - Initializes scheduled task             │
└─────────────────────────────────────────────┘
                    │
                    ▼
            ┌───────────────┐
            │   WAIT 1 HOUR │
            └───────────────┘
                    │
                    ▼
    ┌─────────────────────────────────┐
    │ refreshTokenAndFetchData()      │
    │ Called by Spring Scheduler      │
    └─────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
    ┌────────────┐      ┌───────────────┐
    │fetchToken()│      │(if error)     │
    └────────────┘      │log + exit     │
        │               └───────────────┘
        ▼
    ┌──────────────────────────┐
    │ Call API #1              │
    │ POST /awsidam/           │
    │ Returns: Token           │
    └──────────────────────────┘
        │
        ├─ Success: Token ✓
        │
        ▼
    ┌──────────────────────────┐
    │ Cache Token              │
    └──────────────────────────┘
        │
        ▼
    ┌─────────────────────────────────┐
    │ fetchApprovedFamiliesData(token)│
    └─────────────────────────────────┘
        │
        ▼
    ┌──────────────────────────┐
    │ Call API #2              │
    │ POST /families/approved  │
    │ Header: Bearer {token}   │
    │ Body: {"type":"PMJAY"...}│
    │ Returns: Data            │
    └──────────────────────────┘
        │
        ├─ Success: Data ✓
        │
        ▼
    ┌──────────────────────────┐
    │ Log Response             │
    │ (Optional: Save to DB)   │
    └──────────────────────────┘
        │
        ▼
    ┌──────────────────────────┐
    │ Complete ✓               │
    │ Execution Time: ~4 secs  │
    └──────────────────────────┘
        │
        └──> Wait 1 hour → Repeat
```

---

## 📊 Request/Response Examples

### First API Call

**Request:**
```
POST https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/awsidam/
Content-Type: application/json
User-Agent: Mozilla/5.0...
Accept: application/json, text/plain, */*
Accept-Language: en-US,en;q=0.9
...other headers...
Content-Length: 0

(empty body)
```

**Response:**
```
HTTP/1.1 200 OK
Content-Type: text/plain

eyJraWQiOiIwZ0pQXC9TY1hTdzBtQUV1dWl5VEh4em1LTWp1b0ZTNjBaXC81SDVHZlBwYlE9IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiIyNzZyNzI4anRiZjgzYmdpMHU4NjhvODFqOCIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiZGVmYXVsdC1tMm0tcmVzb3VyY2Utc2VydmVyLWlvLWl4c1wvcmVhZCIsImF1dGhfdGltZSI6MTc3NzI3OTQ2MCwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmFwLXNvdXRoLTEuYW1hem9uYXdzLmNvbVwvYXAtc291dGgtMV9QTWIwVzZPWUIiLCJleHAiOjE3NzcyODY2NjAsImlhdCI6MTc3NzI3OTQ2MCwidmVyc2lvbiI6MiwianRpIjoiMWEwZThhOTMtZmVjZi00NDI2LWFlNWYtY2M5NTkyNTViZjkyIiwiY2xpZW50X2lkIjoiMjc2cjcyOGp0YmY4M2JnaTB1ODY4bzgxajgifQ.iJK8sQuWzCIBb9YuwWeGsesWP1dE9O5zERpwqt0Yrpjo9E6en9WMkStfOBQFLhSaRXi-Enl4sSUx07a3MV-1M_1A0Ulg-hDuxY1WOBGs0daSwKdPW6hHmtYBwWLs6nPR98jRhbR2cEPN7EH8GAqArpMrTjLwM7K44fHnP6CrpIAepajw1bn5-bzpPnv2233Z9M7bjOo0gX-fdkMi8wyypDctduK-VBuGQWOP6y0XghAZPoEajMHEC2lGWYrjuqd-1pW7_wLtZQQptgGMosDdOPCQdrqghdJkAcWtfPvpy4gUz1_Xw9w173nBYh31FdaQceUOJIVKg48FHo8gZadtAg
```

---

### Second API Call

**Request:**
```
POST https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/families/approved
Content-Type: application/json; charset=UTF-8
Authorization: Bearer eyJraWQiOiIwZ0pQXC9TY1hTdzBtQUV1dWl5VEh4em1LTWp1b0ZT...
Accept: application/json
Origin: https://dashboard.nha.gov.in
Referer: https://dashboard.nha.gov.in/
User-Agent: Mozilla/5.0...
...other headers...

{
    "type": "PMJAY",
    "rpttype": "S",
    "state_code": ""
}
```

**Response:**
```
HTTP/1.1 200 OK
Content-Type: application/json

{
    "success": true,
    "data": {
        "families": [...],
        "totalCount": 12345,
        ...
    }
}
```

---

## 🧰 Key Implementation Details

### Scheduling Configuration
```java
@Scheduled(fixedRate = 3600000)  // milliseconds
// = 3,600,000 ms
// = 3,600 seconds
// = 60 minutes
// = 1 hour
```

### HTTP Method Usage
```java
// For token API (no body)
ResponseEntity<String> response = restTemplate.exchange(
    TOKEN_API_URL,
    HttpMethod.POST,
    request,
    String.class
);

// For data API (with body)
String requestBody = "{\"type\":\"PMJAY\",\"rpttype\":\"S\",\"state_code\":\"\"}";
HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
ResponseEntity<String> response = restTemplate.exchange(
    DATA_API_URL,
    HttpMethod.POST,
    request,
    String.class
);
```

### Error Handling Pattern
```java
try {
    // API call
    ResponseEntity<String> response = restTemplate.exchange(...);
    
    if (response.getStatusCode().is2xxSuccessful()) {
        // Success handling
        LOGGER.info("Success: ...");
        return result;
    } else {
        // Failure handling
        LOGGER.log(Level.WARNING, "Failed. Status: " + response.getStatusCode());
        return null;
    }
} catch (RestClientException e) {
    // Exception handling
    LOGGER.log(Level.SEVERE, "Error: " + e.getMessage(), e);
    return null;
}
```

### Logging Pattern
```java
LOGGER.info("Informational message");
LOGGER.log(Level.WARNING, "Warning message");
LOGGER.log(Level.SEVERE, "Error message", exception);
```

---

## 🔐 Security Headers Included

✅ User-Agent
✅ Accept
✅ Accept-Language
✅ Accept-Encoding
✅ Referer
✅ Origin
✅ Authorization (Bearer token)
✅ Cache-Control
✅ Pragma
✅ Sec-Fetch headers
✅ CORS headers

---

## 📈 Performance Considerations

- **Execution Time**: ~4-5 seconds per cycle
- **Memory Usage**: Minimal (token cached in memory)
- **Network**: 2 outbound API calls per hour
- **Logging**: Standard Java logging
- **Thread Pool**: Uses Spring's default scheduler thread pool

---

## 🛡️ Error Recovery

| Scenario | Action |
|----------|--------|
| Token API fails | Log error, skip data API, retry next hour |
| Data API fails | Log error, continue, retry next hour |
| Network timeout | Caught as RestClientException, logged |
| Invalid response | Handled in status code check |
| Application crash | Scheduler restarts with next app start |

---

## 📞 Integration Points

The service can integrate with:
- ✅ Database (save results)
- ✅ Message Queue (publish events)
- ✅ Email (send alerts)
- ✅ Webhook (notify external systems)
- ✅ Cache (store results)
- ✅ Monitoring (metrics collection)

All can be added without modifying the core scheduling logic.

