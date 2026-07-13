package abdm_nha_grant_access.example.nha_grant.enums;

public enum WorkflowAction {
    SUBMITTED("Proposal submitted"),
    ACCEPTED("Accepted by NHA State Coord"),
    QUERY_RAISED("Query raised by NHA State Coord"),
    EDITED_RESUBMITTED("Edited and resubmitted by state");

    private final String label;

    WorkflowAction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
