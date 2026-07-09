package abdm_nha_grant_access.example.nha_grant.enums;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;

public enum ProposalStatus {
    PENDING_AT_STATE(1, "Pending at state due to query raised"),
    PENDING_AT_NHA_STATE_COORD(2, "Pending at NHA State Coord"),
    ACCEPTED_BY_NHA_STATE_COORD(3, "Accepted by NHA State Coord"),
    REJECTED_BY_NHA_STATE_COORD(4, "Rejected by NHA State Coord");

    private final int id;
    private final String label;

    ProposalStatus(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static ProposalStatus fromId(Integer id) {
        if (id == null) {
            throw new GrantException("status is required");
        }
        for (ProposalStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new GrantException("status must be one of 1 (Pending at state due to query raised), "
                + "2 (Pending at NHA State Coord), 3 (Accepted by NHA State Coord), 4 (Rejected by NHA State Coord)");
    }
}
