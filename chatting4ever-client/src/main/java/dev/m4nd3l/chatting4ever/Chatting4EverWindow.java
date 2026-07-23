package dev.m4nd3l.chatting4ever;

import dev.m4nd3l.chatting4ever.pages.Page;

import javax.swing.*;

import java.awt.*;

import static dev.m4nd3l.chatting4ever.Chatting4EverClient.Chatting4Ever;

public class Chatting4EverWindow {
    private JFrame window;
    private Page page;

    public Chatting4EverWindow() { this(null); }
    public Chatting4EverWindow(Page page) {
        window = new JFrame(Chatting4Ever.getName());
        window.setMinimumSize(new Dimension(1000, 800));
        setContent(page);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO -> Add close adaptor to close the connection with the server
        window.setLocationRelativeTo(null);
    }

    public Chatting4EverWindow setContent(Page newPage) {
        if (newPage == null) return this;
        if (page != null) page.beforeChanging();
        newPage.beforeActivating();
        window.setContentPane(newPage.getPanel());
        window.revalidate();
        window.repaint();
        newPage.afterActivating();
        if (page != null) page.afterChanging();
        page = newPage;
        return this;
    }

    public Chatting4EverWindow show() { page.beforeShowing(); window.setVisible(true); page.afterShowing(); return this; }
    public Chatting4EverWindow hide() { page.beforeHiding(); window.setVisible(false); page.afterHiding(); return this; }
    public Chatting4EverWindow error(String error) { page.showError(error); return this; }
}