package dev.m4nd3l.chatting4ever;

import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.APIErrorException;
import dev.m4nd3l.chatting4ever.api.payloads.account.AutologinPayload;
import dev.m4nd3l.chatting4ever.api.payloads.account.LoginPayload;
import dev.m4nd3l.chatting4ever.api.response.auth.AccountAuthResponse;
import dev.m4nd3l.chatting4ever.components.icons.EyeIcon;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.authentication.login.LoginPage;
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
import java.util.UUID;

public class Chatting4EverClient {

    public static final AppInfo Chatting4Ever = new AppInfo("Chatting4Ever", new Version(1, 0, 0));
    public static Chatting4EverWindow Window;
    public static boolean DEBUG = true;

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
            if (!Boolean.parseBoolean(EasySaves.getSetting("logged-in"))) {
                Window = new Chatting4EverWindow(new LoginPage());
                Window.show();
                return;
            }

            try {
                String username = EasySaves.getSecureSetting("username");
                String email = EasySaves.getSecureSetting("email");
                String uuidString = EasySaves.getSecureSetting("uuid");

                if ((username == null && email == null) ||  uuidString == null) throw new Exception("Invalid credentials");
                if ((username == null && email.isEmpty()) || (email == null && username.isEmpty())  || uuidString.isEmpty()) throw new Exception("Invalid credentials");

                UUID uuid = UUID.fromString(uuidString);
                AccountAuthResponse data = APIEndpoints.autologin.sendPostRequest(new AutologinPayload(username, uuid), AccountAuthResponse.class);

                if (!data.isValidResponse()) {
                    data = APIEndpoints.autologin.sendPostRequest(new AutologinPayload(email, uuid), AccountAuthResponse.class);
                    if (!data.isValidResponse()) throw new APIErrorException(data.getServerErrorData(), data.getErrorData());
                }

                AccountData.setAccount(data);

                EasySaves.addSecureSetting("uuid", data.getUUID().toString());

                Window = new Chatting4EverWindow(new MainPage());
                Window.show();
            } catch (APIErrorException apiError) {
                deleteCredentials();
                Window = new Chatting4EverWindow(new SignupPage());
                Window.show();
                Window.error("An error occurred during auto-login:\n" + apiError.getErrorCause());
            } catch (Exception e) {
                deleteCredentials();
                Window = new Chatting4EverWindow(new SignupPage());
                Window.show();
                Window.error("An error occurred during auto-login:\n" + e.getClass().getSimpleName());
            }
        });
    }

    private static void deleteCredentials() {
        EasySaves.addSetting("logged-in", String.valueOf(false));
        EasySaves.removeSetting("username");
        EasySaves.removeSetting("email");
        EasySaves.removeSetting("uuid");
    }
}