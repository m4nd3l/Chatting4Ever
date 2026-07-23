package dev.m4nd3l.chatting4ever.utils;

public class AppInfo {
    private String name;
    private Version version;

    public AppInfo(String name, Version version) {
        this.name = name;
        this.version = version;
    }

    public String getName() { return name; }
    public Version getVersion() { return version; }

    public AppInfo setName(String name) { this.name = name; return this; }
    public AppInfo setVersion(Version version) { this.version = version; return this; }
}