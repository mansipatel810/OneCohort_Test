
# ============================================================
# update_deliverables.ps1
# Adds missing test cases and updates defect status in
# OneCohort_Deliverables.xlsx > TestCases sheet
# ============================================================
$src = "C:\Users\2479385\Downloads\OneCohort_Deliverables.xlsx"
$bak = "C:\Users\2479385\Downloads\OneCohort_Deliverables_BACKUP.xlsx"
Copy-Item $src $bak -Force
Write-Host "Backup: $bak"

function rgb($r,$g,$b){ $r + $g*256 + $b*65536 }
$clrDone  = rgb 198 239 206
$clrFail  = rgb 255 199 206
$clrDefc  = rgb 255 235 156
$clrHdr   = rgb 198 224 180
$clrWhite = rgb 255 255 255
$clrAlt   = rgb 242 242 242

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false; $excel.DisplayAlerts = $false
$wb = $excel.Workbooks.Open($src)
$ws = $wb.Worksheets.Item("TestCases")

# ── STEP 1: Insert 48 blank rows at row 2 (for LD_TC_001-016) ─────────────
$ws.Rows.Item("2:49").Insert(-4121)
Write-Host "Inserted 48 rows for LD_TC_001-016"

# ── STEP 2: Row writer helper ──────────────────────────────────────────────
function WR($row,$grp,$id,$desc,$dsn,$typ,$frd,$dat,$cpx,$stp,$sd,$se,$act,$sts,$def){
    $v = @($grp,$id,$desc,$dsn,$typ,$frd,$dat,$cpx,[string]$stp,$sd,$se,$act,$sts,$def)
    for($c=1;$c -le 14;$c++){
        $cel = $ws.Cells.Item($row,$c)
        $cel.Value2 = $v[$c-1]
        $cel.Font.Name = "Calibri"; $cel.Font.Size = 8
        $cel.WrapText = $true
        $cel.VerticalAlignment = -4108
        $cel.Interior.Color = $clrWhite
    }
    if($sts -eq "Done"){ $ws.Cells.Item($row,13).Interior.Color = $clrDone }
    if($sts -eq "Fail"){ $ws.Cells.Item($row,13).Interior.Color = $clrFail }
    if($def -ne "")    { $ws.Cells.Item($row,14).Interior.Color = $clrDefc }
    $ws.Rows.Item($row).RowHeight = 45
}

$LD="Leader Dashboard"; $BON="Batch Owner Login Negative"
$IQ="Intern QA"; $f11="FRD 11"; $f13="FRD 13"
$LL="Leader login"; $BL="Batch Owner login"
$D="Done"; $F="Fail"

# ── LD_TC_001 ── verifyLeaderUrlPath ──────────────────────────────────────
$r=2
WR $r $LD "LD_TC_001_LeaderUrlPath" "Verify Leader post-login URL contains /leader/ path segment" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login with Leader credentials using a valid User ID and Service Line ID" "Browser should navigate to the Leader area after successful authentication" "" $D ""
WR ($r+1) $LD "LD_TC_001_LeaderUrlPath" "Verify Leader post-login URL contains /leader/ path segment" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Wait for the post-login redirect to complete and observe the browser address bar" "The full URL should be visible and the page should have finished loading" "" $D ""
WR ($r+2) $LD "LD_TC_001_LeaderUrlPath" "Verify Leader post-login URL contains /leader/ path segment" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Confirm the URL contains '/leader/' path segment as required by FRD 4.1.3" "URL must include '/leader/' e.g. /leader/{serviceLineId}/dashboard" "" $D ""

# ── LD_TC_002 ── verifyUrlEndsWithDashboard ───────────────────────────────
$r=5
WR $r $LD "LD_TC_002_DashboardUrlCheck" "Verify post-login URL ends with /dashboard confirming Leader lands on Dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the redirect to complete after authentication" "Browser should navigate to the Leader dashboard route" "" $D ""
WR ($r+1) $LD "LD_TC_002_DashboardUrlCheck" "Verify post-login URL ends with /dashboard confirming Leader lands on Dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Read the current URL from the browser address bar after redirect" "The URL should show the complete path including the dashboard segment" "" $D ""
WR ($r+2) $LD "LD_TC_002_DashboardUrlCheck" "Verify post-login URL ends with /dashboard confirming Leader lands on Dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the URL contains '/dashboard' as per FRD Section 11 post-login destination" "URL must contain '/dashboard' — Leader lands on Dashboard not Cohorts list" "" $D ""

# ── LD_TC_003 ── verifyDashboardContainerPresent ──────────────────────────
$r=8
WR $r $LD "LD_TC_003_DashboardContainerPresent" "Verify main dashboard container element renders on the page without error" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the dashboard page to fully load" "Dashboard page should render without any blank screen or error" "" $D ""
WR ($r+1) $LD "LD_TC_003_DashboardContainerPresent" "Verify main dashboard container element renders on the page without error" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Inspect the page layout for the main content container element" "The main dashboard container div should be present in the DOM" "" $D ""
WR ($r+2) $LD "LD_TC_003_DashboardContainerPresent" "Verify main dashboard container element renders on the page without error" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify div.dashboard-container is rendered and visible on screen" "The dashboard container must be present — confirms no render error occurred" "" $D ""

# ── LD_TC_004 ── verifyWelcomeMessage ─────────────────────────────────────
$r=11
WR $r $LD "LD_TC_004_WelcomeMessageVisible" "Verify Welcome Leader greeting message is visible in the dashboard header" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and navigate to the dashboard" "Dashboard loads with header and greeting area visible" "" $D ""
WR ($r+1) $LD "LD_TC_004_WelcomeMessageVisible" "Verify Welcome Leader greeting message is visible in the dashboard header" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Look at the top section of the dashboard for a welcome or greeting message" "A personalized greeting message should appear at the top" "" $D ""
WR ($r+2) $LD "LD_TC_004_WelcomeMessageVisible" "Verify Welcome Leader greeting message is visible in the dashboard header" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the text shows 'Welcome, Leader.' in the header section" "Greeting must display 'Welcome, Leader.' as per FRD" "" $D ""

# ── LD_TC_005 ── verifyHeaderRoleText ─────────────────────────────────────
$r=14
WR $r $LD "LD_TC_005_HeaderRoleText" "Verify the header displays Leader as the role label" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and open the dashboard" "Dashboard should be visible with header area loaded" "" $D ""
WR ($r+1) $LD "LD_TC_005_HeaderRoleText" "Verify the header displays Leader as the role label" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Look at the top-right area of the page header for role information" "A role indicator or label should be visible in the header" "" $D ""
WR ($r+2) $LD "LD_TC_005_HeaderRoleText" "Verify the header displays Leader as the role label" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the header role label contains the word 'Leader'" "Role label must show 'Leader' in the header area" "" $D ""

# ── LD_TC_006 ── verifyLdAvatarVisible ───────────────────────────────────
$r=17
WR $r $LD "LD_TC_006_LdAvatarVisible" "Verify LD avatar icon is visible in the dashboard header" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the dashboard to fully render" "Dashboard header should be rendered and visible" "" $D ""
WR ($r+1) $LD "LD_TC_006_LdAvatarVisible" "Verify LD avatar icon is visible in the dashboard header" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Look at the top-right corner of the page header for a profile avatar icon" "An avatar circle or icon should be present in the header" "" $D ""
WR ($r+2) $LD "LD_TC_006_LdAvatarVisible" "Verify LD avatar icon is visible in the dashboard header" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the LD avatar element is displayed in the header right section" "LD avatar must be visible — shown as a rounded blue circle with 'LD'" "" $D ""

# ── LD_TC_007 ── verifyLdAvatarText ──────────────────────────────────────
$r=20
WR $r $LD "LD_TC_007_LdAvatarText" "Verify the avatar icon displays exactly LD as its text content" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and navigate to the dashboard" "Dashboard header should be fully visible with avatar present" "" $D ""
WR ($r+1) $LD "LD_TC_007_LdAvatarText" "Verify the avatar icon displays exactly LD as its text content" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Find the profile avatar element in the top-right of the header" "Avatar element should be located in the header" "" $D ""
WR ($r+2) $LD "LD_TC_007_LdAvatarText" "Verify the avatar icon displays exactly LD as its text content" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Read the text inside the avatar and verify it is exactly 'LD'" "Avatar must display exactly 'LD' — not 'Leader' or any other abbreviation" "" $D ""

# ── LD_TC_008 ── verifySidebarVisible ────────────────────────────────────
$r=23
WR $r $LD "LD_TC_008_SidebarVisible" "Verify the left sidebar navigation panel is visible on the Leader dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the dashboard to load completely" "Dashboard and all UI sections including sidebar should render" "" $D ""
WR ($r+1) $LD "LD_TC_008_SidebarVisible" "Verify the left sidebar navigation panel is visible on the Leader dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Look at the left side of the screen for the navigation sidebar panel" "A vertical sidebar should be visible on the left side of the screen" "" $D ""
WR ($r+2) $LD "LD_TC_008_SidebarVisible" "Verify the left sidebar navigation panel is visible on the Leader dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify that the aside.sidebar element is rendered and displayed" "Sidebar must be visible — it contains logo and navigation links per FRD" "" $D ""

# ── LD_TC_009 ── verifyLogoVisible ───────────────────────────────────────
$r=26
WR $r $LD "LD_TC_009_LogoVisible" "Verify the One Cohort application logo is visible in the sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the dashboard to render" "Sidebar should be fully loaded including the logo area at the top" "" $D ""
WR ($r+1) $LD "LD_TC_009_LogoVisible" "Verify the One Cohort application logo is visible in the sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Look at the top section of the left sidebar for the application logo image" "Logo area should be present at the top of the sidebar" "" $D ""
WR ($r+2) $LD "LD_TC_009_LogoVisible" "Verify the One Cohort application logo is visible in the sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the app logo image element is rendered and visible in the sidebar" "Logo img must be present — displays the One Cohort brand image" "" $D ""

# ── LD_TC_010 ── verifyAppName ────────────────────────────────────────────
$r=29
WR $r $LD "LD_TC_010_AppName" "Verify One Cohort application name is displayed in the sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and open the dashboard" "Dashboard loads with sidebar visible and logo section rendered" "" $D ""
WR ($r+1) $LD "LD_TC_010_AppName" "Verify One Cohort application name is displayed in the sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Find the text element showing the application name near the sidebar logo" "Application name text should appear alongside the logo image" "" $D ""
WR ($r+2) $LD "LD_TC_010_AppName" "Verify One Cohort application name is displayed in the sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the text 'One Cohort' is visible in the sidebar logo section" "App name must read 'One Cohort' — shown inside .logo-section span" "" $D ""

# ── LD_TC_011 ── verifyExactlyTwoNavItems ─────────────────────────────────
$r=32
WR $r $LD "LD_TC_011_ExactlyTwoNavItems" "Verify the Leader sidebar contains exactly 2 navigation links" $IQ "Functional, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the sidebar to fully load" "Sidebar should display all navigation items for the Leader role" "" $D ""
WR ($r+1) $LD "LD_TC_011_ExactlyTwoNavItems" "Verify the Leader sidebar contains exactly 2 navigation links" $IQ "Functional, Positive" $f11 $LL "Simple" 2 "Count the number of navigation links or items visible in the left sidebar" "All nav items should be countable and visible in the sidebar" "" $D ""
WR ($r+2) $LD "LD_TC_011_ExactlyTwoNavItems" "Verify the Leader sidebar contains exactly 2 navigation links" $IQ "Functional, Positive" $f11 $LL "Simple" 3 "Verify there are exactly 2 nav items — Dashboard and Cohorts (not 6 like Super Admin)" "Leader sidebar must have exactly 2 nav items — FRD restricts Leader access to Dashboard and Cohorts" "" $D ""

# ── LD_TC_012 ── verifyDashboardNavItem ───────────────────────────────────
$r=35
WR $r $LD "LD_TC_012_DashboardNavItem" "Verify Dashboard navigation link is present in the Leader sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the sidebar to render" "Sidebar should be visible with navigation links loaded" "" $D ""
WR ($r+1) $LD "LD_TC_012_DashboardNavItem" "Verify Dashboard navigation link is present in the Leader sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Look at the sidebar navigation links and read their labels" "Navigation link texts should be visible and readable" "" $D ""
WR ($r+2) $LD "LD_TC_012_DashboardNavItem" "Verify Dashboard navigation link is present in the Leader sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify a link labelled 'Dashboard' is present in the sidebar" "Dashboard nav link must exist — routes to /leader/{id}/dashboard" "" $D ""

# ── LD_TC_013 ── verifyCohortsNavItem ─────────────────────────────────────
$r=38
WR $r $LD "LD_TC_013_CohortsNavItem" "Verify Cohorts navigation link is present in the Leader sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and navigate to the dashboard" "Sidebar should be visible with nav items" "" $D ""
WR ($r+1) $LD "LD_TC_013_CohortsNavItem" "Verify Cohorts navigation link is present in the Leader sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Review all navigation links listed in the left sidebar" "All nav links should be listed and visible" "" $D ""
WR ($r+2) $LD "LD_TC_013_CohortsNavItem" "Verify Cohorts navigation link is present in the Leader sidebar" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify a link labelled 'Cohorts' is present in the sidebar navigation" "Cohorts nav link must exist — routes to /leader/{id}/cohorts per FRD" "" $D ""

# ── LD_TC_014 ── verifyDashboardHeading ──────────────────────────────────
$r=41
WR $r $LD "LD_TC_014_DashboardHeading" "Verify main heading on the Leader dashboard contains Leader Dashboard text" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and wait for the dashboard to fully load" "Dashboard main content and heading should render" "" $D ""
WR ($r+1) $LD "LD_TC_014_DashboardHeading" "Verify main heading on the Leader dashboard contains Leader Dashboard text" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Locate the main heading element at the top of the dashboard content area" "A prominent heading element should be visible" "" $D ""
WR ($r+2) $LD "LD_TC_014_DashboardHeading" "Verify main heading on the Leader dashboard contains Leader Dashboard text" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the heading text contains 'Leader Dashboard' with the service line name" "Heading must contain 'Leader Dashboard' — e.g. 'QEA Leader Dashboard'" "" $D ""

# ── LD_TC_015 ── verifyLeaderBadge ───────────────────────────────────────
$r=44
WR $r $LD "LD_TC_015_LeaderBadge" "Verify the Leader role badge is displayed next to the dashboard heading" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and open the dashboard" "Dashboard heading area should be visible" "" $D ""
WR ($r+1) $LD "LD_TC_015_LeaderBadge" "Verify the Leader role badge is displayed next to the dashboard heading" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Look for a badge or pill label element adjacent to the main heading" "A badge element should appear next to the dashboard heading" "" $D ""
WR ($r+2) $LD "LD_TC_015_LeaderBadge" "Verify the Leader role badge is displayed next to the dashboard heading" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the badge shows exactly the text 'Leader'" "Badge must display exactly 'Leader' — shown as span.badge per FRD" "" $D ""

# ── LD_TC_016 ── verifyCohortsSectionTitle ───────────────────────────────
$r=47
WR $r $LD "LD_TC_016_CohortsSectionTitle" "Verify Cohorts section title is visible on the Leader dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 1 "Login as Leader and open the dashboard page" "Dashboard should be loaded with all section areas visible" "" $D ""
WR ($r+1) $LD "LD_TC_016_CohortsSectionTitle" "Verify Cohorts section title is visible on the Leader dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 2 "Scroll through the dashboard and look for section group headings" "Multiple section titles should be visible grouping the KPI cards" "" $D ""
WR ($r+2) $LD "LD_TC_016_CohortsSectionTitle" "Verify Cohorts section title is visible on the Leader dashboard" $IQ "UI, Positive" $f11 $LL "Simple" 3 "Verify the text 'Cohorts' is displayed as a section title above the KPI cards" "'Cohorts' section title must be present — groups Total Cohorts / Active / Completed / Upcoming cards" "" $D ""

Write-Host "LD_TC_001-016 written (rows 2-49)"

# ── STEP 3: Update TC_CR_004-018 to Fail/DEF-005 ─────────────────────────
# After inserting 48 rows, original row 101 is now row 149
# TC_CR_004 (originally rows 101-103) -> rows 149-151
# TC_CR_018 (originally rows 142-144) -> rows 190-192  [Wait - let me recalc]
# TC_CR_004 starts at original row 101 (row 92 for TC_CR_001 + 9 rows for 001-003 = row 101)
# Let me verify: LD_TC_017-046 = 30 TCs x3=90 rows = original rows 2-91
# TC_CR_001 = rows 92-94, TC_CR_002=95-97, TC_CR_003=98-100
# TC_CR_004=101-103, ..., TC_CR_018= 101 + (15-1)*3 = 101+42=143 to 145
# After +48: TC_CR_004=149-151, TC_CR_018=191-193

$crActual = "CR Dashboard content not rendered for COH-10001. All sections blank after successful login. Refer DEF-005."
$updateStart = 149   # TC_CR_004 step 1
$updateEnd   = 193   # TC_CR_018 step 3

for ($row = $updateStart; $row -le $updateEnd; $row++) {
    $ws.Cells.Item($row,12).Value2 = $crActual
    $ws.Cells.Item($row,13).Value2 = "Fail"
    $ws.Cells.Item($row,13).Interior.Color = $clrFail
    $ws.Cells.Item($row,14).Value2 = "DEF-005"
    $ws.Cells.Item($row,14).Interior.Color = $clrDefc
}
Write-Host "TC_CR_004-018 updated to Fail/DEF-005 (rows 149-193)"

# ── STEP 4: Add TC_BO_NEG_001-004 at end ─────────────────────────────────
# Current last row after all changes = 163 (original) + 48 (inserted) = 211
$r = 212

# TC_BO_NEG_001
WR $r $BON "TC_BO_NEG_001_EmptyUserId" "Verify submitting Batch Owner login with empty User ID triggers validation alert" $IQ "Functional, Negative" $f13 $BL "Simple" 1 "Open the login page and locate the User ID input field - intentionally leave it blank" "Login page should load with the User ID field visible and empty" "" $D ""
WR ($r+1) $BON "TC_BO_NEG_001_EmptyUserId" "Verify submitting Batch Owner login with empty User ID triggers validation alert" $IQ "Functional, Negative" $f13 $BL "Simple" 2 "Select Batch Owner from the role dropdown without entering any User ID" "Role selection should trigger additional Batch Owner fields but User ID remains blank" "" $D ""
WR ($r+2) $BON "TC_BO_NEG_001_EmptyUserId" "Verify submitting Batch Owner login with empty User ID triggers validation alert" $IQ "Functional, Negative" $f13 $BL "Simple" 3 "Click Login and verify a validation alert fires and the page stays on login" "Alert must appear and user must remain on login page per FRD 13.2.1 - User ID is required" "" $D ""

# TC_BO_NEG_002
$r=215
WR $r $BON "TC_BO_NEG_002_EmptyPocId" "Verify submitting Batch Owner login with empty POC ID triggers validation alert" $IQ "Functional, Negative" $f13 $BL "Simple" 1 "Enter valid User ID and select Batch Owner role then select a Service Line from the dropdown" "Form should show all required fields with Service Line selected and POC ID field visible" "" $D ""
WR ($r+1) $BON "TC_BO_NEG_002_EmptyPocId" "Verify submitting Batch Owner login with empty POC ID triggers validation alert" $IQ "Functional, Negative" $f13 $BL "Simple" 2 "Leave the POC ID input field blank and do not enter any value in it" "All other fields are filled but POC ID field remains empty" "" $D ""
WR ($r+2) $BON "TC_BO_NEG_002_EmptyPocId" "Verify submitting Batch Owner login with empty POC ID triggers validation alert" $IQ "Functional, Negative" $f13 $BL "Simple" 3 "Click Login and verify a validation alert appears and the login page remains on screen" "Alert must fire and page must stay on login per FRD 13.2.2 - POC ID is required" "" $D ""

# TC_BO_NEG_003
$r=218
WR $r $BON "TC_BO_NEG_003_FieldsHiddenBeforeRoleSelected" "Verify Service Line and POC ID fields are hidden before Batch Owner role is selected" $IQ "UI, Negative" $f13 "Default role (not Batch Owner)" "Simple" 1 "Open the login page without changing the role - observe the default state with no role chosen" "Login page should show only the basic User ID and Role fields initially" "" $D ""
WR ($r+1) $BON "TC_BO_NEG_003_FieldsHiddenBeforeRoleSelected" "Verify Service Line and POC ID fields are hidden before Batch Owner role is selected" $IQ "UI, Negative" $f13 "Default role (not Batch Owner)" "Simple" 2 "Check whether Service Line dropdown and POC ID input are visible with the default role" "Neither Service Line nor POC ID field should be visible at this stage" "" $D ""
WR ($r+2) $BON "TC_BO_NEG_003_FieldsHiddenBeforeRoleSelected" "Verify Service Line and POC ID fields are hidden before Batch Owner role is selected" $IQ "UI, Negative" $f13 "Default role (not Batch Owner)" "Simple" 3 "Verify neither Service Line dropdown nor POC ID input appears until Batch Owner is selected" "Both extra fields must be hidden until Batch Owner role is explicitly selected per FRD 13.2.2" "" $D ""

# TC_BO_NEG_004
$r=221
WR $r $BON "TC_BO_NEG_004_AllFieldsBlank" "Verify clicking Login with all fields blank shows a validation alert" $IQ "Functional, Negative" "$f13.1 + $f13.2" "All fields blank" "Simple" 1 "Open the login page and leave every field completely empty including User ID" "Login page is open with all fields blank and no data entered" "" $D ""
WR ($r+1) $BON "TC_BO_NEG_004_AllFieldsBlank" "Verify clicking Login with all fields blank shows a validation alert" $IQ "Functional, Negative" "$f13.1 + $f13.2" "All fields blank" "Simple" 2 "Click the Login button directly without entering any value in any field" "Login is attempted with a completely blank form" "" $D ""
WR ($r+2) $BON "TC_BO_NEG_004_AllFieldsBlank" "Verify clicking Login with all fields blank shows a validation alert" $IQ "Functional, Negative" "$f13.1 + $f13.2" "All fields blank" "Simple" 3 "Verify a validation alert appears and the user remains on the login page" "Alert must fire and page must stay on login per FRD 13.2.1 + 13.2.2 - all fields required" "" $D ""

Write-Host "TC_BO_NEG_001-004 written (rows 212-223)"

# ── STEP 5: Apply consistent Status color to ALL existing done/fail rows ──
Write-Host "Applying status color coding to all rows..."
$lastRow = $ws.UsedRange.Rows.Count
for ($row = 2; $row -le $lastRow; $row++) {
    $sts = $ws.Cells.Item($row,13).Value2
    if ($sts -eq "Done") { $ws.Cells.Item($row,13).Interior.Color = $clrDone }
    elseif ($sts -eq "Fail") { $ws.Cells.Item($row,13).Interior.Color = $clrFail }
    $def = $ws.Cells.Item($row,14).Value2
    if ($def -ne $null -and $def -ne "") { $ws.Cells.Item($row,14).Interior.Color = $clrDefc }
}

# ── STEP 6: Save ─────────────────────────────────────────────────────────
$wb.Save()
$wb.Close($false)
$excel.Quit()
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($excel) | Out-Null
Write-Host ""
Write-Host "DONE: $src"
Write-Host "Total rows: $lastRow (1 header + $($lastRow-1) test case step rows)"
