module SecurityServiceModule {
    requires java.desktop;
    requires guava;
    requires java.prefs;
    requires miglayout;
    requires com.google.gson;
    requires ImageServiceModule;
    opens com.udacity.catpoint.data to com.google.gson;
    //pens com.udacity.catpoint.service;
}