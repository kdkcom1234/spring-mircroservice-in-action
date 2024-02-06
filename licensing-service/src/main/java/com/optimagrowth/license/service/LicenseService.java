package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LicenseService {
    @Autowired
    MessageSource messages;

    public License getLicense(String licenseId, String organizationId){
        License license = new License();
        license.setId(new Random().nextInt(1000));
        license.setLicenseId(licenseId);
        license.setOrganizationId(organizationId);
        license.setDescription("Software product");
        license.setProductName("Ostock");
        license.setLicenseType("full");
        return license;
    }

    public String createLicense(License license, String organizationId, Locale locale) {
        String responseMessage = null;
        if(license != null) {
            license.setOrganizationId(organizationId);
            responseMessage =  messages.getMessage("license.create.message", null, locale != null ? locale : Locale.US);
        }

        return responseMessage;
    }

    public String updateLicense(License license, String organizationId) {
        String responseMessage = null;
        if(license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = "This is the put and the object is $license";
        }

        return responseMessage;
    }

    public String deleteLicense(String organizationId, String licenseId) {
        String responseMessage = null;
        responseMessage = "Deleting license with id $licenseId for the organization $organizationId";
        return responseMessage;
    }
}