package id.rllyhz.dailyus.vo

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<out T>(data: T) : Resource<T>(data)
    class Error<out T>(message: String) : Resource<T>(null, message)
    class Loading<out T>(data: T? = null) : Resource<T>(data)
    class Initial<out T> : Resource<T>()
}
