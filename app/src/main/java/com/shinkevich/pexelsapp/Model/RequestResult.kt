package com.shinkevich.pexelsapp.Model

sealed class RequestResult<T> {
    class Success<T>(val data: T) : RequestResult<T>()
    class NoInternetConnectionFailure<T> : RequestResult<T>()
    class Failure<T>(val errorMessage: String) : RequestResult<T>()
}