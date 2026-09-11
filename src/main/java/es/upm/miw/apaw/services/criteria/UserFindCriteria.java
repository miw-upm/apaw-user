package es.upm.miw.apaw.services.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFindCriteria {
    private Boolean active;
    private String mobile;
    private Boolean billable;

    public UserFindCriteria(Boolean active, String mobile) {
        this.active = active;
        this.mobile = mobile;
    }

    public boolean all() {
        return !this.hasActive() && !this.hasMobile() && !this.hasBillable();
    }

    public boolean hasActive() {
        return this.active != null;
    }

    public boolean hasMobile() {
        return this.mobile != null && !this.mobile.isBlank();
    }

    public boolean hasBillable() {
        return this.billable != null;
    }
}
