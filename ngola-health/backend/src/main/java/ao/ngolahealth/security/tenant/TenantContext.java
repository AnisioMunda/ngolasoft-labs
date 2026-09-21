package ao.ngolahealth.security.tenant;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> currentHospital = new ThreadLocal<>();

    private TenantContext() {}

    public static void setCurrentHospital(UUID hospitalId) {
        currentHospital.set(hospitalId);
    }

    public static UUID getCurrentHospital() {
        return currentHospital.get();
    }

    public static void clear() {
        currentHospital.remove();
    }
    
}
