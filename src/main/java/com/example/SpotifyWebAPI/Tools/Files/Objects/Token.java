package com.example.SpotifyWebAPI.Tools.Files.Objects;

public class Token {
    private String key;
    private String value;
    private boolean isNumber;
    private boolean isBoolean;
    private boolean seen = false;
    private boolean sensitiveInfo = false;
    private String categoryType = "";

    public Token(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Token(String key) {
        this.key = key;
        this.value = "";
    }

    //getters
    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public boolean getIsNumber() {
        if (this.isBoolean) {
            return false;
        }
        return this.isNumber;
    }

    public boolean getIsBoolean() {
        if (this.isNumber) {
            return false;
        }
        return this.isBoolean;
    }

    public boolean isSeen() {
        return this.seen;
    }

    public boolean isSensitiveInfo() {
        return this.sensitiveInfo;
    }

    public String getCategoryType() {
        return this.categoryType;
    }

    //setters
    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setBoolean(boolean isBoolean) {
        this.isBoolean = isBoolean;
    }

    public void setNumber(boolean isNumber) {
        this.isNumber = isNumber;
    }

    public void markAsSeen() {
        this.seen = true;
    }

    public void setSensitiveInfo(boolean sensitiveInfo) {
        this.sensitiveInfo = sensitiveInfo;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public String toString() {
        return "Key: " + this.key + ", Value: " + this.value + ". States: isBoolean: " +
                this.isBoolean + ", isNumber: " + this.isNumber + ". Seen: " + (this.seen ? "yes" : "no") + ", " +
                "Sensitive Info: " + (this.isSensitiveInfo() ? "yes" : "no") + (categoryType.isEmpty() ? "." : ", Category: " + categoryType);
    }
}
