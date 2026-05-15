# Visual Diagrams & Flow Charts

## 1. Overall Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                    Spring Boot Application                           │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              NhaGrantApplication.java                        │  │
│  │              @EnableScheduling                              │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                │                                    │
│                                ▼                                    │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │         TokenRefreshAndDataFetchService                      │  │
│  │         @Service                                             │  │
│  │         ┌─────────────────────────────────────────────────┐ │  │
│  │         │ @Scheduled(fixedRate = 3600000)                 │ │  │
│  │         │ refreshTokenAndFetchData()                      │ │  │
│  │         │ └─ Runs every hour automatically               │ │  │
│  │         └─────────────────────────────────────────────────┘ │  │
│  │                                                              │  │
│  │  Token API Methods:                                          │  │
│  │  • fetchToken()                                              │  │
│  │  • createTokenApiHeaders()                                   │  │
│  │                                                              │  │
│  │  Data API Methods:                                           │  │
│  │  • fetchApprovedFamiliesData()                              │  │
│  │  • createDataApiHeaders()                                    │  │
│  │  • createRequestBody()                                       │  │
│  │                                                              │  │
│  │  Helper Methods:                                             │  │
│  │  • getCachedToken()                                          │  │
│  │  • manualRefresh()                                           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                │                                    │
│                                ▼                                    │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              SchedulerController                             │  │
│  │              @RestController                                 │  │
│  │              @RequestMapping("/api/scheduler")               │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────────┐ │  │
│  │  │ POST /trigger-refresh                                 │ │  │
│  │  │ → Manually triggers refreshTokenAndFetchData()        │ │  │
│  │  └────────────────────────────────────────────────────────┘ │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────────┐ │  │
│  │  │ GET /cached-token                                     │ │  │
│  │  │ → Returns currently cached token                      │ │  │
│  │  └────────────────────────────────────────────────────────┘ │  │
│  │                                                              │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │   RestTemplate Bean   │
                    │   (Configured in      │
                    │    NhaGrantApplication)
                    └───────────────────────┘
                                │
                ┌───────────────┴───────────────┐
                ▼                               ▼
        ┌─────────────────┐            ┌─────────────────┐
        │  Token API      │            │   Data API      │
        │  (First Call)   │            │ (Second Call)   │
        └─────────────────┘            └─────────────────┘
```

---

## 2. Execution Flow - Every Hour

```
                    ┌─────────────────────┐
                    │  Scheduled Time     │
                    │  Reached (1 hour)   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ refreshTokenAndFetch │
                    │ Data() Triggered     │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Try Block Starts     │
                    │ Log: "Starting..."   │
                    └──────────┬───────────┘
                               │
                               ▼
            ┌──────────────────────────────────┐
            │ Call fetchToken()                │
            └──────────────┬───────────────────┘
                           │
                           ▼
        ┌──────────────────────────────────────┐
        │ Create Headers for Token API         │
        │ • User-Agent                         │
        │ • Accept                             │
        │ • Authorization headers              │
        │ • CORS headers                       │
        │ • Other security headers             │
        └──────────────┬───────────────────────┘
                       │
                       ▼
        ┌──────────────────────────────────────┐
        │ restTemplate.exchange()              │
        │ POST /awsidam/                       │
        │ Method: HttpMethod.POST              │
        │ Body: Empty                          │
        └──────────────┬───────────────────────┘
                       │
            ┌──────────┴──────────┐
            ▼                     ▼
      ┌────────────┐      ┌───────────────┐
      │ Success    │      │ Error/Timeout │
      │ 2xx Status │      │ Exception     │
      └────────────┘      └───────────────┘
            │                     │
            ▼                     ▼
    ┌──────────────┐      ┌──────────────┐
    │ Token = Body │      │ Log Error    │
    │ Cache Token  │      │ Return Null  │
    └──────┬───────┘      └──────┬───────┘
           │                     │
           │             ┌───────┘
           │             ▼
           │    ┌─────────────────────┐
           │    │ Token = null?       │
           │    │ Yes → Log Warning   │
           │    │ No → Continue       │
           │    └────┬────────────────┘
           │         │
           │    ┌────┴─────┐
           │    ▼          ▼
           │  Token   No Token
           │   OK    → Skip & Return
           │    │
           ▼    ▼
    ┌────────────────────────────────────┐
    │ Call fetchApprovedFamiliesData()   │
    │ Pass token as parameter            │
    └────────────┬─────────────────────┘
                 │
                 ▼
    ┌────────────────────────────────────┐
    │ Create Headers for Data API        │
    │ • Accept: application/json         │
    │ • Authorization: Bearer {token}    │
    │ • Content-Type: application/json   │
    │ • Origin, Referer, User-Agent      │
    │ • Security headers                 │
    └────────────┬─────────────────────┘
                 │
                 ▼
    ┌────────────────────────────────────┐
    │ Create Request Body                │
    │ {                                  │
    │   "type": "PMJAY",                 │
    │   "rpttype": "S",                  │
    │   "state_code": ""                 │
    │ }                                  │
    └────────────┬─────────────────────┘
                 │
                 ▼
    ┌────────────────────────────────────┐
    │ restTemplate.exchange()            │
    │ POST /families/approved            │
    │ Method: HttpMethod.POST            │
    │ Headers: { Bearer token, ... }     │
    │ Body: { JSON request }             │
    └────────────┬─────────────────────┘
                 │
        ┌────────┴─────────┐
        ▼                  ▼
    ┌────────┐      ┌────────────────┐
    │Success │      │ Error/Timeout  │
    │2xx OK  │      │ Exception      │
    └────────┘      └────────────────┘
        │                   │
        ▼                   ▼
    ┌────────┐      ┌─────────────────┐
    │ Log    │      │ Log Error       │
    │Success │      │ Catch Block     │
    │Response│      │ Exception       │
    └────┬───┘      └────┬────────────┘
         │               │
         └───────┬───────┘
                 ▼
        ┌─────────────────────┐
        │ Catch Block         │
        │ Log: "Error: ..."   │
        │ Print Stack Trace   │
        └────────┬────────────┘
                 │
                 ▼
        ┌─────────────────────┐
        │ Finally Block       │
        │ Execution Complete  │
        └────────┬────────────┘
                 │
                 ▼
        ┌─────────────────────┐
        │ Sleep 1 Hour        │
        └────────┬────────────┘
                 │
                 ▼
        ┌─────────────────────┐
        │ REPEAT from start   │
        └─────────────────────┘
```

---

## 3. API Integration Sequence

```
┌──────────────────────────┐
│   TokenRefreshService    │
└───────────────┬──────────┘
                │
                ├──► Create Token API Headers
                │
                ├──► Create HttpEntity with Headers
                │
                ▼
    ┌────────────────────────────────────────┐
    │ Send POST Request to Token API         │
    │ https://apisprod.nha.gov.in/pmjay/... │
    │ /bisdashboard/bis/awsidam/             │
    └────────┬──────────────────────────────┘
             │
             ├─► Network Request Sent
             │
             ├─► Server Processes
             │
             ├─► Server Returns Token
             │
             ▼
    ┌────────────────────────────────────────┐
    │ Response Received                      │
    │ Status Code: 200 (Success)             │
    │ Body: JWT Token String                 │
    └────────┬──────────────────────────────┘
             │
             ├─► Cache Token in Memory
             │   (cachedToken = token)
             │
             ▼
    ┌────────────────────────────────────────┐
    │ Create Data API Headers                │
    │ + Add Authorization: Bearer {token}    │
    │ + Add Content-Type: application/json   │
    │ + Add other required headers           │
    └────────┬──────────────────────────────┘
             │
             ├─► Create Request Body (JSON)
             │   {
             │     "type": "PMJAY",
             │     "rpttype": "S",
             │     "state_code": ""
             │   }
             │
             ├─► Create HttpEntity
             │   (Body + Headers)
             │
             ▼
    ┌────────────────────────────────────────┐
    │ Send POST Request to Data API          │
    │ https://apisprod.nha.gov.in/pmjay/... │
    │ /bisdashboard/bis/families/approved    │
    │ Headers: [Bearer token, ...]           │
    │ Body: {JSON payload}                   │
    └────────┬──────────────────────────────┘
             │
             ├─► Network Request Sent
             │
             ├─► Server Authenticates Token
             │
             ├─► Server Processes Request
             │
             ├─► Server Queries Data
             │
             ├─► Server Returns Response
             │
             ▼
    ┌────────────────────────────────────────┐
    │ Response Received                      │
    │ Status Code: 200 (Success)             │
    │ Body: JSON with Approved Families Data │
    └────────┬──────────────────────────────┘
             │
             ├─► Parse Response
             │
             ├─► Log Response Data
             │
             ├─► (Optional) Save to Database
             │
             ▼
    ┌────────────────────────────────────────┐
    │ Execution Complete                     │
    │ Next execution in 1 hour               │
    └────────────────────────────────────────┘
```

---

## 4. Error Handling Flow

```
                    ┌─ API Call Initiated
                    │
                    ▼
        ┌───────────────────────┐
        │ Try Block             │
        └───────────┬───────────┘
                    │
            ┌───────┴────────┐
            │                │
            ▼                ▼
    ┌──────────────┐  ┌──────────────────┐
    │ Success      │  │ Exception Thrown │
    │ Response OK  │  │ (Network/Parse)  │
    └──────┬───────┘  └────────┬─────────┘
           │                   │
           ▼                   │
    ┌────────────┐             │
    │ Log Info   │             │
    │ "Success"  │             │
    └────────────┘             │
                               ▼
                    ┌──────────────────────┐
                    │ Catch Block          │
                    │ RestClientException  │
                    └───────────┬──────────┘
                                │
                                ▼
                    ┌──────────────────────┐
                    │ Log Severe           │
                    │ "Error: ..."         │
                    │ Print Stack Trace    │
                    └───────────┬──────────┘
                                │
                    ┌───────────┴──────────┐
                    │                      │
                    ▼                      ▼
            ┌────────────────┐   ┌─────────────────┐
            │ Return null    │   │ Clean up        │
            │ or error code  │   │ resources       │
            └────┬───────────┘   └────────┬────────┘
                 │                        │
                 └───────────┬────────────┘
                             │
                             ▼
                    ┌──────────────────────┐
                    │ Finally Block        │
                    │ (Always Executes)    │
                    └───────────┬──────────┘
                                │
                                ▼
                    ┌──────────────────────┐
                    │ Sleep until next     │
                    │ scheduled time       │
                    │ (1 hour from now)    │
                    └──────────────────────┘
```

---

## 5. Data Flow Diagram

```
┌─────────────────────────────────────┐
│   Application Startup               │
│   Spring loads all beans            │
└──────────────┬──────────────────────┘
               │
               ▼
    ┌──────────────────────────────┐
    │ Initialize Spring Scheduler  │
    │ Register Scheduled Tasks     │
    └──────────┬───────────────────┘
               │
               ▼
    ┌──────────────────────────────┐
    │ Start Timer Thread           │
    │ Wait for first hour mark     │
    └──────────┬───────────────────┘
               │
               ▼ (Repeat every hour)
    ┌──────────────────────────────┐
    │ Execute Scheduled Method     │
    │ refreshTokenAndFetchData()   │
    └──────────┬───────────────────┘
               │
        ┌──────┴──────┐
        │             │
        ▼             ▼
    ┌──────────┐  ┌─────────────────┐
    │In-Memory │  │HTTP Requests    │
    │Cache     │  │To External APIs │
    │          │  │                 │
    │cachedToken  │• First API      │
    │(String)  │  │• Second API     │
    │          │  │                 │
    └──────────┘  └─────────────────┘
        ▲                 │
        │                 │
        │        ┌────────┴────────┐
        │        │                 │
        │        ▼                 ▼
        │    ┌─────────┐  ┌──────────────┐
        │    │Token    │  │Data Response │
        │    │String   │  │(JSON)        │
        │    └────┬────┘  └──────┬───────┘
        │         │               │
        └─────────┼───────────────┘
                  │
                  ▼
        ┌──────────────────────┐
        │ Logging System       │
        │ (Java Logging)       │
        │ • Log levels (INFO)  │
        │ • Stack traces       │
        │ • Timestamps         │
        │ • Error messages     │
        └──────────────────────┘
```

---

## 6. Rest Endpoint Timing

```
CLIENT REQUEST                          SERVER RESPONSE
    │                                        │
    ├─ POST /trigger-refresh                ├─ Check token & data refresh
    │                                        │
    │                                        ├─ (< 5 seconds)
    │                                        │
    │<───────── 200 OK + Message ───────────┤
    │   "Successfully triggered"             │
    │                                        │
    └──────────────────────────────────────┘


CLIENT REQUEST                          SERVER RESPONSE
    │                                        │
    ├─ GET /cached-token                    ├─ Look up cached token
    │                                        │
    │                                        ├─ (< 1 ms)
    │                                        │
    │<───── 200 OK + Token String ───────┤
    │   "Current cached token: ..."          │
    │                                        │
    └──────────────────────────────────────┘
```

---

## 7. Scheduled Execution Timeline

```
Time        Action                          Logs
────────────────────────────────────────────────────────────────
10:00:00    Task triggers                   "Starting hourly..."
10:00:00    Fetch token                     "Fetching token from API..."
10:00:02    Token received & cached         "Successfully obtained token"
10:00:02    Fetch data                      "Fetching approved families..."
10:00:04    Data received & logged          "Successfully fetched data"
10:00:04    Complete                        "Successfully completed"
10:00:05    Wait...                         (sleep)

11:00:00    Task triggers again             "Starting hourly..."
11:00:00    Fetch token                     "Fetching token from API..."
11:00:02    Token received & cached         "Successfully obtained token"
11:00:02    Fetch data                      "Fetching approved families..."
11:00:04    Data received & logged          "Successfully fetched data"
11:00:04    Complete                        "Successfully completed"
11:00:05    Wait...                         (sleep)

12:00:00    Task triggers again             "Starting hourly..."
...         (repeats indefinitely)
```

---

## 8. Component Dependencies

```
┌─────────────────────────────────────────────────┐
│      NhaGrantApplication (Main Class)           │
└────────────────────┬────────────────────────────┘
                     │
           ┌─────────┼─────────┐
           │         │         │
           ▼         ▼         ▼
     ┌──────────┐  ┌──────────┐  ┌──────────────────┐
     │@EnableAsync       │  │@EnableScheduling    │
     └──────────┘  └──────────┘  └──────────────────┘
           │         │                    │
           ▼         ▼                    ▼
     ┌──────────────────────────────────────────────┐
     │ Spring Container (Manages All Beans)         │
     │                                              │
     │  ┌──────────────────────────────────────┐   │
     │  │ RestTemplate Bean (Configured)       │   │
     │  └──────────────────────────────────────┘   │
     │              ▲                               │
     │              │                               │
     │  ┌──────────────────────────────────────┐   │
     │  │ TokenRefreshAndDataFetchService      │   │
     │  │                                      │   │
     │  │ @Autowired RestTemplate             │   │
     │  │ @Scheduled(fixedRate = 3600000)     │   │
     │  │                                      │   │
     │  │ - fetchToken()                       │   │
     │  │ - fetchApprovedFamiliesData()        │   │
     │  │ - createHeaders()                    │   │
     │  │ - manualRefresh()                    │   │
     │  └──────────────────────────────────────┘   │
     │              │                               │
     │              └──────────────┬────────────────┤
     │                             │                │
     │  ┌──────────────────────────────────────┐   │
     │  │ SchedulerController                  │   │
     │  │                                      │   │
     │  │ @Autowired Service                  │   │
     │  │                                      │   │
     │  │ - POST /trigger-refresh              │   │
     │  │ - GET /cached-token                  │   │
     │  └──────────────────────────────────────┘   │
     │                                              │
     └──────────────────────────────────────────────┘
           │
           ▼
     ┌──────────────────────────┐
     │ Scheduler Thread Pool    │
     │ (Spring TaskScheduler)   │
     │                          │
     │ Timer: Every 1 hour      │
     └──────────────────────────┘
```

All diagrams are self-contained and show different aspects of the hourly cron job implementation.

