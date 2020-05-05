package com.zendesk.tecna.sample;

import android.app.Application;
import android.content.Context;

import com.zendesk.logger.Logger;
import com.zendesk.util.StringUtils;

import zendesk.core.AnonymousIdentity;
import zendesk.core.Zendesk;
import zendesk.support.Support;

public class Global extends Application {

    public static String SUBDOMAIN_URL = "https://pocbs2help.zendesk.com";
    public static String APPLICATION_ID = "0c558d81a7654f34d1423aad966e59f5f8c81eeae9f80c5f";
    public static String OAUTH_CLIENT_ID = "mobile_sdk_client_095b5f28055d6475d71c";
    public static String ANONYMOUS_IDENTITY_NAME = "Tecna Sample";
    public static String ANONYMOUS_IDENTITY_EMAIL = "sample@tecnasistemas.com.br";
    private static boolean missingCredentials = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.setLoggable(true);

        if (StringUtils.isEmpty(SUBDOMAIN_URL)
                || StringUtils.isEmpty(APPLICATION_ID)
                || StringUtils.isEmpty(OAUTH_CLIENT_ID)) {
            missingCredentials = true;
            return;
        }

        /**
         * Initialize the SDK with your Zendesk subdomain, mobile SDK app ID, and client ID.
         *
         * Get these details from your Zendesk dashboard: Admin -> Channels -> MobileSDK.
         */
        setZendeskEnviroment(this, SUBDOMAIN_URL, APPLICATION_ID, OAUTH_CLIENT_ID);

        /**
         * Set an identity (authentication).
         *
         * Set either Anonymous or JWT identity, as below:
         */
        setZendeskAnonymousIdentity(ANONYMOUS_IDENTITY_NAME, ANONYMOUS_IDENTITY_EMAIL);
        // setZendeskJWTIdentity();

        Support.INSTANCE.init(Zendesk.INSTANCE);
    }

    public static boolean isMissingCredentials() {
        return missingCredentials;
    }

    public static void setZendeskEnviroment(Context context, String zendeskUrl, String applicationId, String oauthClientId) {
        SUBDOMAIN_URL = zendeskUrl;
        APPLICATION_ID = applicationId;
        OAUTH_CLIENT_ID = oauthClientId;

        Zendesk.INSTANCE.init(context, SUBDOMAIN_URL, APPLICATION_ID, OAUTH_CLIENT_ID);
    }

    public static void setZendeskAnonymousIdentity(String name, String email) {
        ANONYMOUS_IDENTITY_NAME = name;
        ANONYMOUS_IDENTITY_EMAIL = email;

        // Anonymous (All fields are optional)
        Zendesk.INSTANCE.setIdentity(
                new AnonymousIdentity.Builder()
                        .withNameIdentifier(ANONYMOUS_IDENTITY_NAME)
                        .withEmailIdentifier(ANONYMOUS_IDENTITY_EMAIL)
                        .build()
        );
    }

    public static void setZendeskJWTIdentity() {
        // JWT (Must be initialized with your JWT identifier)
        //        Zendesk.INSTANCE.setIdentity(new JwtIdentity("{JWT User Identifier}"));
    }
}
