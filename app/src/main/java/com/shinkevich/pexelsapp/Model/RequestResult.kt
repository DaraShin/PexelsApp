package com.shinkevich.pexelsapp.Model

sealed class RequestResult<T> {
    open class Success<T>(val data: T) : RequestResult<T>()
    class SuccessFromCache<T>(data: T) : Success<T>(data)
    class NoInternetConnectionFailure<T> : RequestResult<T>()
    class Failure<T>(val errorMessage: String) : RequestResult<T>()
}