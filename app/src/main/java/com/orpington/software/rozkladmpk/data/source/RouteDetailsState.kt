package com.orpington.software.rozkladmpk.data.source

import java.util.*

class RouteDetailsState : Observable() {
    private var _routeID: String = ""
    var routeID: String
        get() = _routeID
        set(value) {
            _routeID = value
            setChanged()
            notifyObservers()
        }

    private var _stopName: String = ""
    var stopName: String
        get() = _stopName
        set(value) {
            _stopName = value
            setChanged()
            notifyObservers()
        }

    private var _direction: String = ""
    var direction: String
        get() = _direction
        set(value) {
            _direction = value
            setChanged()
            notifyObservers("direction")
        }

    private var _directionIdx: Int = -1
    var directionIdx: Int
        get() = _directionIdx
        set(value) {
            _directionIdx = value
            setChanged()
            notifyObservers()
        }

    private var _tripID: String = ""
    var tripID: String
        get() = _tripID
        set(value) {
            _tripID = value
            setChanged()
            notifyObservers()
        }

    public fun reset() {
        routeID = ""
        stopName = ""
    }
}

