package abdm_nha_grant_access.example.nha_grant.enums;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;

public enum ProposalCategory {
    HR(1),
    IEC_CB(2),
    ADMIN(3);

    private final int id;

    ProposalCategory(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ProposalCategory fromId(Integer id) {
        if (id == null) {
            throw new GrantException("category_id is required");
        }
        for (ProposalCategory category : values()) {
            if (category.id == id) {
                return category;
            }
        }
        throw new GrantException("category_id must be one of 1 (HR), 2 (IEC-CB), 3 (Admin)");
    }
}
