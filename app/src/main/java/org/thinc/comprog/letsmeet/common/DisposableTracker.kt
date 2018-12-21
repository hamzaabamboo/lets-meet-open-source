package org.thinc.comprog.letsmeet.common

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface DisposableTracker {
    val compositeDisposable: CompositeDisposable

    fun Disposable.track() {
        compositeDisposable.add(this)
    }

    fun cleanUpDisposable() {
        compositeDisposable.clear()
    }
}