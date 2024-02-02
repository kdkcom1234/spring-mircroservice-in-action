package com.optimagrowth.license.controller

import com.optimagrowth.license.model.License
import com.optimagrowth.license.service.LicenseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("v1/organization/{organizationId}/license")
class LicenseController(private val licenseService: LicenseService) {

    @GetMapping("/{licenseId}")
    fun getLicense(
        @PathVariable organizationId: String,
        @PathVariable licenseId: String) : ResponseEntity<License>  {
        val license = licenseService.getLicense(licenseId, organizationId)
        return ResponseEntity.ok(license)
    }

    @PutMapping
    fun updateLicense(
        @PathVariable organizationId: String,
        @RequestBody request : License) : ResponseEntity<String>  {
        return ResponseEntity.ok(licenseService.updateLicense(request, organizationId))
    }

    @PostMapping
    fun createLicense(
        @PathVariable organizationId: String,
        @RequestBody request : License,
        @RequestHeader(value = "Accept-Language", required = false) locale: Locale?) : ResponseEntity<String>  {
        println(locale)

        return ResponseEntity.ok(licenseService.createLicense(request, organizationId, locale))
    }

    @DeleteMapping("/{licenseId}")
    fun deleteLicense(
        @PathVariable organizationId: String,
        @PathVariable licenseId: String) : ResponseEntity<String>  {
         return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId))
    }
}