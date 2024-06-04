package com.lrl.liudrivecore.data.dto;

/**
 * A secure format for SaveConfiguration exposure
 *
 * Work with ObjectSecureResponseDTO
 */
public class SaveConfigurationExposureDTO {

    protected int accessibility;

    protected String drive;

    protected String compressed;

    public int getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public String getCompressed() {
        return compressed;
    }

    public void setCompressed(String compressed) {
        this.compressed = compressed;
    }

    @Override
    public String toString() {
        return "SaveConfigurationExposureDTO{" +
                "accessibility=" + accessibility +
                ", drive='" + drive + '\'' +
                ", compressed='" + compressed + '\'' +
                '}';
    }
}
