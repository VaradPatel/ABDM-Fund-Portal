# Hourly Token Refresh & Data Fetch Service

## Overview
This service automatically fetches a token from the NHA API every hour and uses it to retrieve approved families data. The service is fully automated using Spring's `@Scheduled` annotation.

---

## Architecture

### Components Created

#### 1. **TokenRefreshAndDataFetchService** 
   - **Location**: `service/TokenRefreshAndDataFetchService.java`
   - **Purpose**: Handles all API calls and scheduling logic
   - **Key Features**:
     - Runs automatically every hour (3600000 ms)
     - Fetches token from the first API
     - Uses the token to call the second API
     - Caches the token for external access
     - Comprehensive error logging

#### 2. **SchedulerController**
   - **Location**: `controllers/SchedulerController.java`
   - **Purpose**: Provides REST endpoints for manual control
   - **Endpoints**:
     - `POST /api/scheduler/trigger-refresh` - Manually trigger the refresh
     - `GET /api/scheduler/cached-token` - Get the currently cached token

#### 3. **NhaGrantApplication** (Updated)
   - Added `@EnableScheduling` annotation to enable scheduled tasks
   - Existing `RestTemplate` bean is reused

---

## API Flow

```
┌─────────────────────────────────────────────────────────────┐
│         Every Hour - Scheduled Task Triggered               │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│  STEP 1: Fetch Token from First API                         │
│  URL: https://apisprod.nha.gov.in/pmjay/prodbis/...awsidam/ │
│  Method: POST                                               │
│  Response: Token String                                     │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
                   Token Obtained ✓
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│  STEP 2: Fetch Data Using Token                             │
│  URL: https://apisprod.nha.gov.in/pmjay/prodbis/.../families│
│  Method: POST                                               │
│  Header: Authorization: Bearer {token}                      │
│  Body: {"type":"PMJAY","rpttype":"S","state_code":""}       │
│  Response: Approved Families Data                           │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
                   Data Processed ✓
                   Logged Successfully
```

---

## Configuration & Setup

### 1. Scheduling Configuration
The service runs automatically every hour. To change the schedule, modify the `@Scheduled` annotation in `TokenRefreshAndDataFetchService`:

```java
// Current: Every 1 hour (3600000 ms)
@Scheduled(fixedRate = 3600000)

// Alternative options:
@Scheduled(fixedRate = 1800000)        // Every 30 minutes
@Scheduled(fixedRate = 600000)         // Every 10 minutes
@Scheduled(cron = "0 0 * * * *")       // Cron expression (every hour at minute 0)
@Scheduled(cron = "0 */2 * * * *")     // Cron expression (every 2 hours)
```

### 2. No Additional Configuration Needed
- ✅ RestTemplate already configured as a bean
- ✅ Scheduling automatically enabled
- ✅ No additional properties or dependencies needed

---

## Usage

### Automatic Execution
The service starts automatically when the application runs:
```
2026-04-27 10:00:00.000  INFO Starting hourly token refresh and data fetch process...
2026-04-27 10:00:01.000  INFO Fetching token from API...
2026-04-27 10:00:02.000  INFO Successfully obtained token from first API
2026-04-27 10:00:03.000  INFO Fetching approved families data with token...
2026-04-27 10:00:04.000  INFO Successfully fetched approved families data
2026-04-27 11:00:00.000  INFO Starting hourly token refresh and data fetch process...
```

### Manual Trigger (Testing)
Test the service without waiting for the hourly schedule:

```bash
# Trigger the refresh manually
curl -X POST http://localhost:8080/api/scheduler/trigger-refresh

# Response:
# "Token refresh and data fetch triggered successfully"
```

### Get Current Token
Retrieve the currently cached token:

```bash
curl -X GET http://localhost:8080/api/scheduler/cached-token

# Response:
# "Current cached token: eyJraWQiOiIwZ0pQXC9TY1hTdzBtQUV1dWl5VEh4em1LTWp1b0ZTNjBaXC81SDVHZlBwYlE9IiwiYWxnIjoiUlMyNTYifQ..."
```

---

## Key Features

### ✅ Automatic Hourly Execution
- Uses Spring's `@Scheduled` annotation
- No external cron jobs needed
- Runs in the background automatically

### ✅ Token Management
- Fetches fresh token every hour
- Caches token for use in second API call
- Caches available for external access via controller

### ✅ Error Handling
- Comprehensive try-catch blocks
- Detailed logging for debugging
- Graceful failure handling

### ✅ Headers Management
- All required headers from curl commands are included
- User-Agent, Accept, Authorization headers properly set
- CORS and security headers configured

### ✅ Logging
- All API calls logged
- Success/failure states logged
- Errors logged with full stack traces
- Uses Java's standard `java.util.logging.Logger`

---

## API Details

### First API (Token Endpoint)
```
URL: https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/awsidam/
Method: POST
Content-Type: application/json
Response: Token (plain string)
```

### Second API (Data Endpoint)
```
URL: https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/families/approved
Method: POST
Authorization: Bearer {token_from_first_api}
Content-Type: application/json
Request Body:
{
    "type": "PMJAY",
    "rpttype": "S",
    "state_code": ""
}
Response: Approved families data
```

---

## Monitoring & Logging

The service logs to your application logs. Check your logs for:

### Success Logs
```
INFO: Starting hourly token refresh and data fetch process...
INFO: Fetching token from API: https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/awsidam/
INFO: Successfully obtained token from first API
INFO: Fetching approved families data with token...
INFO: Successfully fetched approved families data
INFO: Response: {...}
```

### Error Logs
```
SEVERE: Error calling token API: Connection timeout
SEVERE: Error in scheduled token refresh and data fetch: org.springframework.web.client.HttpClientErrorException
WARNING: Failed to fetch token. Status: 401 Unauthorized
```

---

## Future Enhancements

The service is designed to be easily extended. You can:

1. **Store Data**: Add database persistence for the fetched data
2. **Email Notifications**: Send alerts on failures
3. **Metrics**: Integrate with Prometheus/Micrometer
4. **Multiple States**: Modify request body to fetch for specific states
5. **Data Processing**: Add business logic to process the response
6. **Webhooks**: Trigger webhooks when data is fetched

---

## Troubleshooting

### Scheduled Task Not Running
- ✅ Ensure `@EnableScheduling` is present in main application class (already added)
- ✅ Check that the service is properly injected (`@Service` annotation present)
- ✅ Verify Spring Boot version supports `@Scheduled` (all recent versions do)

### Token Not Being Fetched
- Check logs for API error messages
- Verify the token API endpoint is accessible
- Check network connectivity
- Verify headers are correct

### Second API Call Failing
- The token may be expired or invalid
- Check if authorization header format is correct
- Verify the data API endpoint is accessible
- Check if request body is correct

---

## File Locations

1. **TokenRefreshAndDataFetchService.java**
   - Path: `src/main/java/nha_grant_access/example/nha_grant/service/`

2. **SchedulerController.java**
   - Path: `src/main/java/nha_grant_access/example/nha_grant/controllers/`

3. **NhaGrantApplication.java** (Updated)
   - Path: `src/main/java/nha_grant_access/example/nha_grant/`

---

## Summary

✅ Hourly scheduled service created
✅ Token fetching implemented
✅ Data fetching with token implemented
✅ Manual trigger endpoints provided
✅ Comprehensive error handling
✅ Full logging support
✅ Ready for production use

