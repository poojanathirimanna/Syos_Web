package com.syos.web.domain.enums;

/**
 * Enum representing user roles in the system
 */
public enum Role {
    MANAGER(1, "Manager"),
    CASHIER(2, "Cashier"),
    CUSTOMER(3, "Customer");

    private final int roleId;
    private final String roleName;

    Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    /**
     * Get Role from role_id
     */
    public static Role fromId(int roleId) {
        for (Role role : Role.values()) {
            if (role.roleId == roleId) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role ID: " + roleId);
    }

    /**
     * Get Role from role name
     */
    public static Role fromName(String roleName) {
        for (Role role : Role.values()) {
            if (role.roleName.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role name: " + roleName);
    }
}