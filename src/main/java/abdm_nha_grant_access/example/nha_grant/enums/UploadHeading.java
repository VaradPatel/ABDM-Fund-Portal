package abdm_nha_grant_access.example.nha_grant.enums;

public enum UploadHeading {
    UC("UC", "UC"),
    RECONCILE("Reconcile", "Reconcile"),
    FUND_ALLOCATION("Fund Allocation", "FundAllocation"),
    UNSPEND_BALANCE("Unspend Balance", "UnspendBalance"),
    OTHERS("Others", "Others");

    // folderName: literal nested folder name on disk under {uploadDir}/{requestId}/
    // jsonKey: key used in the GET /proposal/summary "files" response map (no spaces)
    private final String folderName;
    private final String jsonKey;

    UploadHeading(String folderName, String jsonKey) {
        this.folderName = folderName;
        this.jsonKey = jsonKey;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getJsonKey() {
        return jsonKey;
    }
}
