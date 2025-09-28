package com.hrbabu.tracking.apiBase

class Appconstant {
    public companion object {

        public var isProd =true

        @JvmField
        var AUTH_API = if (isProd) {
            "https://hrapi.anmoluphaar.in"
        } else "https://hrapi.anmoluphaar.in"

    }
}


