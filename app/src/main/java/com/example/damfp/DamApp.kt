package com.example.damfp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. `@HiltAndroidApp` enables dependency injection.
 * DI decision recorded in docs/adr/0001-di-hilt-vs-koin.md.
 */
@HiltAndroidApp
class DamApp : Application()
