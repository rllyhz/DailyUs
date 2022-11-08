package id.rllyhz.dailyus.utils

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<out T>(data: T) : Resource<T>(data)
    class Error<out T>(message: String) : Resource<T>(null, message)
    class Loading<out T>(data: T? = null) : Resource<T>(data)
}
