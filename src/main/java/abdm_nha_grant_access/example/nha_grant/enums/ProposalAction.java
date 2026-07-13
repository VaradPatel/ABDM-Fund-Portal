package abdm_nha_grant_access.example.nha_grant.enums;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;

public enum ProposalAction {
    ACCEPT(ProposalStatus.ACCEPTED_BY_NHA_STATE_COORD, false),
    RAISE_QUERY(ProposalStatus.PENDING_AT_STATE, true);

    private final ProposalStatus resultingStatus;
    private final boolean remarksRequired;

    ProposalAction(ProposalStatus resultingStatus, boolean remarksRequired) {
        this.resultingStatus = resultingStatus;
        this.remarksRequired = remarksRequired;
    }

    public ProposalStatus getResultingStatus() {
        return resultingStatus;
    }

    public boolean isRemarksRequired() {
        return remarksRequired;
    }

    public static ProposalAction fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new GrantException("action is required and must be one of ACCEPT, RAISE_QUERY");
        }
        try {
            return ProposalAction.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GrantException("action must be one of ACCEPT, RAISE_QUERY");
        }
    }
}
