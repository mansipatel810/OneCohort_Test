package com.cts.mfrp.onecohort.constants;

public class AppConstants {

    private AppConstants() {}

    // Page titles
    public static final String LOGIN_PAGE_TITLE     = "OneCohort | Login";
    public static final String DASHBOARD_PAGE_TITLE = "OneCohort | Dashboard";

    // Timeouts
    public static final int SHORT_WAIT  = 5;
    public static final int MEDIUM_WAIT = 10;
    public static final int LONG_WAIT   = 20;

    // Test data file
    public static final String LOGIN_DATA_SHEET = "LoginData";
    public static final String TESTDATA_PATH    = "src/test/resources/testdata/LoginData.xlsx";

    // Report & screenshot paths
    public static final String REPORT_PATH      = "test-output/reports/ExtentReport.html";
    public static final String SCREENSHOT_PATH  = "test-output/screenshots/";

    // Config property keys
    public static final String PROP_BASE_URL        = "base.url";
    public static final String PROP_BROWSER         = "browser";
    public static final String PROP_HEADLESS        = "headless";
    public static final String PROP_IMPLICIT_WAIT   = "implicit.wait";
    public static final String PROP_EXPLICIT_WAIT   = "explicit.wait";
    public static final String PROP_USERNAME        = "username";
    public static final String PROP_PASSWORD        = "password";

    // Login validation alert messages (sourced from login.ts onLogin())
    public static final String ALERT_EMPTY_USER_ID       = "Please enter a User ID";
    public static final String ALERT_SELECT_SERVICE_LINE = "Please select a Service Line";
    public static final String ALERT_ENTER_POC_ID        = "Please enter a POC ID";
    public static final String ALERT_ENTER_COHORT_ID     = "Please enter a Cohort ID";

    // URL path segments
    public static final String URL_SUPER_ADMIN  = "/super-admin";
    public static final String URL_MANAGER      = "/manager/";
    public static final String URL_LEADER       = "/leader/";
    public static final String URL_BATCH_OWNER  = "/batch-owner/";
    public static final String URL_CR           = "/cr/";
}
