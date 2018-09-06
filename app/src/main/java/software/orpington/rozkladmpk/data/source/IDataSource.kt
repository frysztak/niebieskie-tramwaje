package software.orpington.rozkladmpk.data.source

interface IDataSource {

    interface LoadDataCallback<T> {

        fun onDataLoaded(data: T)

        fun onDataNotAvailable()
    }
}
