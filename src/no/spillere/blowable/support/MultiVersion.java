package no.spillere.blowable.support;

import no.spillere.blowable.BlowablePlugin;

public abstract class MultiVersion implements VersionSupport {

    private static MultiVersion multiVersion;

    public static MultiVersion get() {
        if (multiVersion != null) {
            return multiVersion;
        } else {
            if (BlowablePlugin.mc_version >= 19) {
                multiVersion = new mc_newer();
                return multiVersion;
            } else {
                multiVersion = new mc_legacy();
                return multiVersion;
            }
        }
    }
}