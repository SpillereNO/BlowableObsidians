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
				multiVersion = new mc_newer();
				return multiVersion;
			}
			else {
				multiVersion = new mc_legacy();
				return multiVersion;
			}
		}
	}
}