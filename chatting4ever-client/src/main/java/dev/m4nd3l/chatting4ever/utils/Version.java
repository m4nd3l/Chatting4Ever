package dev.m4nd3l.chatting4ever.utils;

import java.util.List;

public class Version {
    private int majorVersion, minorVersion, bugFixVersion;

    public Version(int majorVersion, int minorVersion, int bugFixVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.bugFixVersion = bugFixVersion;
    }

    public int getMajorVersion() { return majorVersion; }
    public int getMinorVersion() { return minorVersion; }
    public int getBugFixVersion() { return bugFixVersion; }

    public Version setMajorVersion(int majorVersion) { this.majorVersion = majorVersion; return this; }
    public Version setMinorVersion(int minorVersion) { this.minorVersion = minorVersion; return this; }
    public Version setBugFixVersion(int bugFixVersion) { this.bugFixVersion = bugFixVersion; return this; }

    @Override
    public String toString() { return String.format("%d.%d.%d", majorVersion, majorVersion, bugFixVersion); }
}