package com.optimagrowth.license.model

data class License (
    var id : Int = 0,
    var licenseId : String = "",
    var description : String = "",
    var organizationId : String = "",
    var productName : String = "",
    var licenseType : String = "",
)