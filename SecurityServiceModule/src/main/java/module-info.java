module SecurityServiceModule {
    requires java.desktop;
    requires guava;
    requires java.prefs;
    requires miglayout;
    requires ImageServiceModule;
    requires com.google.gson;
    opens com.udacity.catpoint.data;
    opens com.udacity.catpoint.service;
}