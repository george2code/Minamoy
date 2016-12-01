package il.co.yomanim.mobileyomanim.params;

import core.enums.RoleType;
import il.co.yomanim.mobileyomanim.compound.FilterItem;

public class HomeworkTaskParams {
    public FilterItem filterItem;
    public RoleType role;

    public HomeworkTaskParams(FilterItem filterItem, RoleType role) {
        this.filterItem = filterItem;
        this.role = role;
    }
}