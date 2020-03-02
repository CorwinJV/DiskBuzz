package com.corwinjv.diskbuzz

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

// inspired by http://giordanomaestro.blogspot.com/2013/01/running-java-applications-as-windows.html
// -cjv
object App {
    private var looper: Disposable? = null

    @JvmStatic
    @Throws(FileNotFoundException::class)
    fun main(args: Array<String>) {
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread(ShutdownHook()))

        // Init and keep alive
        onStart()
        keepAlive()
    }


    private fun onStart() {
        looper = Flowable.interval(0, 5, TimeUnit.MINUTES)
            .observeOn(Schedulers.io())
            .subscribe ({
                // touch disk
                val path = Path.of("D:\\")
                if(Files.exists(path)) {
                    Files.setLastModifiedTime(path, FileTime.from(Instant.now()))
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun dispose() {
        if(looper != null) {
            looper!!.dispose()
            looper = null
        }
    }

    private fun keepAlive() {
        while(true) {
            try {
                Thread.sleep(10000);
            } catch (e: InterruptedException) {
                println("Interrupted at " + Date())
            }
        }
    }

    class ShutdownHook : Runnable {
        override fun run() {
            onStop()
        }

        private fun onStop() {
            println(String.format("Ending at %s", Date()))

            dispose()

            System.out.flush()
            System.out.close()
        }
    }
}