package com.leong.marketplacelistingmanager;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("styles.css")
public class MarketplaceListingManagerApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceListingManagerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserOnStartup(){
        try {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", "http://localhost:8080").start();
        } catch (Exception ignored) {
        }
    }

}
