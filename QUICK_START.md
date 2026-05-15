# Quick Start Guide - Hourly Cron Job

## 🚀 Get Started in 3 Steps

### Step 1: Start Your Application
```bash
mvn clean install
mvn spring-boot:run
```

### Step 2: Wait for First Execution
The service automatically starts at application startup and then every hour:
- First run: Immediately when app starts
- Subsequent runs: Every hour on the hour

### Step 3: Monitor the Logs
You'll see logs like:
```
INFO: Starting hourly token refresh and data fetch process...
INFO: Successfully obtained token from first API
INFO: Successfully fetched approved families data
```

---

## 🧪 Test Without Waiting

### Trigger Manually
```bash
curl -X POST http://localhost:8080/api/scheduler/trigger-refresh
```

**Response:**
```
Token refresh and data fetch triggered successfully
```

### Get Current Token
```bash
curl -X GET http://localhost:8080/api/scheduler/cached-token
```

**Response:**
```
Current cached token: eyJraWQiOiIwZ0pQXC9TY1hTdzBtQUV1dWl5VEh...
```

---

## 📊 What Happens Automatically

Every hour, this happens in order:

1. ✅ **API Call #1**: Fetches token from `https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/awsidam/`
   - Method: POST
   - Returns: Token string

2. ✅ **API Call #2**: Uses token to fetch data from `https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/families/approved`
   - Method: POST
   - Sends: Token in Authorization header
   - Body: `{"type":"PMJAY","rpttype":"S","state_code":""}`

3. ✅ **Logging**: All results logged for monitoring

---

## 🔍 Check Status

### View Application Logs
```bash
# If running with mvn
mvn spring-boot:run 2>&1 | grep "TokenRefresh"

# If running as JAR
java -jar target/nha_grant-0.0.1-SNAPSHOT.jar | grep "TokenRefresh"
```

### Success Indicators
```
✅ "Successfully obtained token from first API"
✅ "Successfully fetched approved families data"
✅ "Successfully completed hourly token refresh and data fetch"
```

---

## 🛠️ Configuration Changes (Optional)

### Change Frequency
Edit `TokenRefreshAndDataFetchService.java`:

**From:**
```java
@Scheduled(fixedRate = 3600000) // Every 1 hour
```

**To:**
```java
@Scheduled(fixedRate = 1800000) // Every 30 minutes
@Scheduled(fixedRate = 600000)  // Every 10 minutes
@Scheduled(cron = "0 0 * * * *") // Cron expression
```

Then rebuild and restart:
```bash
mvn clean install
mvn spring-boot:run
```

---

## ⚠️ Troubleshooting

### Service not running?
- ✅ Check that `@EnableScheduling` is in `NhaGrantApplication.java` (already added)
- ✅ Check that the service has `@Service` annotation (included)
- ✅ Check logs for errors

### Token not being fetched?
- Check if API URL is accessible
- Verify network connectivity
- Check logs for detailed error message

### Second API failing?
- Token might be expired
- Verify the API endpoint is working
- Check request body format

---

## 📱 View Complete Logs

### Enable Debug Logging
Add to `application.properties`:
```properties
logging.level.nha_grant_access.example.nha_grant.service=DEBUG
logging.level.org.springframework.scheduling=DEBUG
```

Then restart the application.

---

## 🎯 What Files Were Created

| File | Purpose |
|------|---------|
| `TokenRefreshAndDataFetchService.java` | Main service with scheduled task |
| `SchedulerController.java` | REST endpoints for manual control |
| `TokenRefreshLogDTO.java` | (Optional) DTO for data logging |
| `CRON_JOB_README.md` | Detailed documentation |
| `NhaGrantApplication.java` | Updated with @EnableScheduling |

---

## ✅ Ready to Deploy?

The service is production-ready. No additional configuration needed!

Just:
1. ✅ Build: `mvn clean install`
2. ✅ Run: `mvn spring-boot:run` or deploy JAR
3. ✅ Monitor: Check logs every hour for execution

The cron job will run automatically without any manual intervention.

---

## 💡 Pro Tips

- Use the manual trigger endpoint for testing
- Check logs regularly for any failures
- The token is cached for 1 hour
- All headers from curl commands are properly implemented
- Error messages are detailed for debugging

**You're all set! The hourly cron job is ready to use.** 🚀

