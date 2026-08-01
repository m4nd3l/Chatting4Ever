package dev.m4nd3l.chatting4ever;

import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import dev.m4nd3l.chatting4ever.account.AccountData;
import dev.m4nd3l.chatting4ever.api.APIEndpoints;
import dev.m4nd3l.chatting4ever.api.APIErrorException;
import dev.m4nd3l.chatting4ever.api.payloads.account.LoginPayload;
import dev.m4nd3l.chatting4ever.api.response.TokenAndInfoResponse;
import dev.m4nd3l.chatting4ever.components.icons.EyeIcon;
import dev.m4nd3l.chatting4ever.pages.MainPage;
import dev.m4nd3l.chatting4ever.pages.authentication.signup.PersonalizeAccountPage;
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
            Window = new Chatting4EverWindow(new PersonalizeAccountPage());
            Window.show();
            //if (!Boolean.parseBoolean(EasySaves.getSetting("logged-in"))) {
            //    Window = new Chatting4EverWindow(new SignupPage());
            //    Window.show();
            //    return;
            //}
            //try {
            //    String username = EasySaves.getSecureSetting("username");
            //    String email = EasySaves.getSecureSetting("email");
            //    String password = EasySaves.getSecureSetting("password");
//
            //    if ((username == null && email == null) || password == null) throw new Exception("Invalid credentials");
            //    if ((username == null && email.isEmpty()) || (email == null && username.isEmpty()) || password.isEmpty()) throw new Exception("Invalid credentials");
//
            //    String usernameOrEmail = username == null ? email : username;
//
            //    TokenAndInfoResponse data = APIEndpoints.login.sendPostRequest(new LoginPayload(usernameOrEmail, password), TokenAndInfoResponse.class);
            //    if (!data.isValidResponse()) throw new APIErrorException(data.getServerErrorData(), data.getErrorData());
            //    AccountData.setAccount(data);
            //    Window = new Chatting4EverWindow(new MainPage());
            //    Window.show();
            //} catch (APIErrorException apiError) {
            //    EasySaves.addSetting("logged-in", String.valueOf(false));
            //    Window = new Chatting4EverWindow(new SignupPage());
            //    Window.show();
            //    Window.error("An error occurred during auto-login:\n" + apiError.getErrorCause());
            //} catch (Exception e) {
            //    EasySaves.addSetting("logged-in", String.valueOf(false));
            //    Window = new Chatting4EverWindow(new SignupPage());
            //    Window.show();
            //    Window.error("An error occurred during auto-login:\n" + e.getClass().getSimpleName());
            //}
        });
    }
}