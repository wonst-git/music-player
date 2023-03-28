package `in`.wonst.domain.base

sealed class ApiResult<T> {
    data class Success<T>(val response: T) : ApiResult<T>()
    data class Failure<T>(val failure: String) : ApiResult<T>()
    data class Exception<T>(val exception: kotlin.Exception) : ApiResult<T>()
}