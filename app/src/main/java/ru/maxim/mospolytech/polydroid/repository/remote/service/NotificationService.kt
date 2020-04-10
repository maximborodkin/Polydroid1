package ru.maxim.mospolytech.polydroid.repository.remote.service

import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient

interface NotificationService {

    companion object{
        operator fun invoke(): NotificationService =
            RetrofitClient.instance.create(NotificationService::class.java)
    }
}