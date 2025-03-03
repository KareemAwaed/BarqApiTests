package models;

public class UserRequest {
    private String nin;
    private String mobile;
    private boolean termsAndConditions;
    private String termsAndConditionsVersion;

    // Getters and Setters
    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(boolean termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getTermsAndConditionsVersion() {
        return termsAndConditionsVersion;
    }

    public void setTermsAndConditionsVersion(String termsAndConditionsVersion) {
        this.termsAndConditionsVersion = termsAndConditionsVersion;
    }
}
