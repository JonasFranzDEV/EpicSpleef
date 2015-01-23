package de.oppermann.bastian.spleef.util;

public class EpicSpleefVersion {
	
	// the version's title
	private String versionName;

    // the version's link
	private String versionLink;

    // the version's release type
	private String versionType;

    // the version's file name
	private String versionFileName;

    // the version's game version
	private String versionGameVersion;
	
	/**
	 * Class constructor.
	 * 
	 * @param versionName The version's title.
	 * @param versionLink The version's link.
	 * @param versionType The version's release type.
	 * @param versionFileName The version's file name.
	 * @param versionGameVersion The version's game version.
	 */
	public EpicSpleefVersion(String versionName, String versionLink, String versionType, String versionFileName, String versionGameVersion) {
		this.versionName = versionName;
		this.versionLink = versionLink;
		this.versionType = versionType;
		this.versionFileName = versionFileName;
		this.versionGameVersion = versionGameVersion;
	}

	/**
	 * Gets the version's title.
	 */
	public String getVersionName() {
		return versionName;
	}

	/**
	 * Gets the version's link.
	 */
	public String getVersionLink() {
		return versionLink;
	}

	/**
	 * Gets the version's release type.
	 */
	public String getVersionType() {
		return versionType;
	}

	/**
	 * Gets the version's file name.
	 */
	public String getVersionFileName() {
		return versionFileName;
	}

	/**
	 * Gets the version's game version.
	 */
	public String getVersionGameVersion() {
		return versionGameVersion;
	}

}
