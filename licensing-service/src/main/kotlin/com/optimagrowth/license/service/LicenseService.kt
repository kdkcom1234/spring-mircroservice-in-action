package com.optimagrowth.license.service

import com.optimagrowth.license.model.License
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.*

@Service
class LicenseService(private val messages: MessageSource) {
    fun getLicense(licenseId : String, organizationId : String) : License{
        val license = License()
        license.id = Random().nextInt(1000)
        license.licenseId = licenseId
        license.organizationId = organizationId
        license.description = "Software product"
        license.productName = "Ostock"
        license.licenseType = "full"
        return  license
    }

    fun createLicense(license: License?, organizationId: String, locale: Locale) : String? {
        println(messages)

        var responseMessage : String? = null
        if(license != null) {
            license.organizationId = organizationId
            responseMessage =  messages.getMessage("license.create.message", null, locale)
        }

        return responseMessage
    }

    fun updateLicense(license: License?, organizationId: String) : String? {
        var responseMessage : String? = null
        if(license != null) {
            license.organizationId = organizationId
            responseMessage = "This is the put and the object is $license"
        }

        return responseMessage
    }

    fun deleteLicense(licenseId: String, organizationId: String) : String? {
        var responseMessage : String? = null
        responseMessage = "Deleting license with id $licenseId for the organization $organizationId"
        return responseMessage
    }
}