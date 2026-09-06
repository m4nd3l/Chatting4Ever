package utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigData {
    @JsonProperty("username")  private String username;
    @JsonProperty("email")  private String email;
    @JsonProperty("password1")  private String password1;
    @JsonProperty("password2")  private String password2;

    public ConfigData() { }

    public ConfigData(String username, String email, String password1, String password2) {
        this.username = username;
        this.email = email;
        this.password1 = password1;
        this.password2 = password2;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword1() { return password1; }
    public String getPassword2() { return password2; }

    public ConfigData setUsername(String username) { this.username = username; return this; }
    public ConfigData setEmail(String email) { this.email = email; return this; }
    public ConfigData setPassword1(String password1) { this.password1 = password1; return this; }
    public ConfigData setPassword2(String password2) { this.password2 = password2; return this; }
}