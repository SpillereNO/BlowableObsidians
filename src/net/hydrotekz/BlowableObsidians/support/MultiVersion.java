package net.hydrotekz.BlowableObsidians.support;

import net.hydrotekz.BlowableObsidians.BlowablePlugin;

public abstract class MultiVersion implements VersionSupport {

	private static MultiVersion multiVersion;

	public static MultiVersion get() {
		if (multiVersion != null) {
			return multiVersion;
		}
		else {
			if (BlowablePlugin.mc_version >= 19) {
				return new mc_newer();
			}
			else {
				return new mc_legacy();
			}
		}
	}
}