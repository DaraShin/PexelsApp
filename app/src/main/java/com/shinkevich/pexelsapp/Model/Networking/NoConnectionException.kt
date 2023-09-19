package com.shinkevich.pexelsapp.Model.Networking

import java.io.IOException

class NoConnectionException : IOException() {
    override val message: String?
        get() = "No Internet connection"
}