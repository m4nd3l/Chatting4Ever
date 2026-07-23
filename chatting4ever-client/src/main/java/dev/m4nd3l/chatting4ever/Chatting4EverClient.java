package dev.m4nd3l.chatting4ever;

import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.payloads.account.LoginPayload;
import dev.m4nd3l.chatting4ever.api.response.TokenAndInfoResponse;
import dev.m4nd3l.chatting4ever.components.icons.EyeIcon;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.authentication.signup.SignupPage;
import dev.m4nd3l.chatting4ever.utils.AppInfo;
import dev.m4nd3l.chatting4ever.utils.Version;
import dev.m4nd3l.easysaves.EasySaves;
import dev.m4nd3l.easysaves.settings.EasySavesSettings;
import dev.m4nd3l.easysaves.settings.SavingLocations;
import dev.m4nd3l.easysaves.settings.StoringSystem;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Chatting4EverClient {

    public static final AppInfo Chatting4Ever = new AppInfo("Chatting4Ever", new Version(1, 0, 0));
    public static Chatting4EverWindow Window;

    public static void main(String[] args) throws IOException {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("demo.themes");
        FlatDarkPurpleIJTheme.setup();
        UIManager.put("PasswordField.revealIcon", new EyeIcon());
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));


        EasySavesSettings settings = EasySavesSettings.Builder.builder()
                .appName("Chatting4Ever")
                .configFileName("config.cfg")
                .location(SavingLocations.APPDATA)
                .storingSystem(StoringSystem.JSON_STRING)
                .build();
        EasySaves.init(settings);

        EventQueue.invokeLater(() -> {
            if (Boolean.parseBoolean(EasySaves.getSetting("logged-in"))) {
                try {
                    String username = EasySaves.getSecureSetting("username");
                    String email = EasySaves.getSecureSetting("email");
                    String password = EasySaves.getSecureSetting("password");

                    if ((username == null && email == null) || password == null) throw new Exception("Invalid credentials");
                    if ((username == null && email.isEmpty()) || (email == null && username.isEmpty()) || password.isEmpty()) throw new Exception("Invalid credentials");

                    String usernameOrEmail = username == null ? email : username;

                    TokenAndInfoResponse data = APIEndpoints.login.sendPostRequest(new LoginPayload(usernameOrEmail, password), TokenAndInfoResponse.class);
                    AccountData.setAccount(
                            new AccountData(
                                    data.getToken(),
                                    data.getUsername(),
                                    data.getDisplayedName(),
                                    data.getEmail(),
                                    data.getProfileImageURL(),
                                    data.getProfileDescription(),
                                    data.getProfileNote(),
                                    data.getCreatedAt(),
                                    data.isPublicEmail()));
                    Window = new Chatting4EverWindow(new MainPage());
                    Window.show();
                } catch (Exception _) {
                    EasySaves.addSetting("logged-in", String.valueOf(false));
                    Window = new Chatting4EverWindow(new SignupPage());
                    Window.show();
                    Window.error("An error occurred during auto-login");
                }
            } else {
                Window = new Chatting4EverWindow(new SignupPage());
                Window.show();
            }
        });
    }

    private boolean isNullOrEmpty(String... params) {
        if (params == null) return false;
        for (String param : params) if (isNullOrEmpty(param)) return true;
        return false;
    }
}