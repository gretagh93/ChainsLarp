package com.chainslarp.app.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chainslarp.app.R
import com.chainslarp.app.application.core.app
import com.chainslarp.app.application.chains.LoginFragment
import mini.rx.DefaultSubscriptionTracker
import mini.rx.SubscriptionTracker
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware


class MainActivity : AppCompatActivity(),
    SubscriptionTracker by DefaultSubscriptionTracker(),
    KodeinAware {

    override val kodein = Kodein {
        extend(app.kodein)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.mainContainer, LoginFragment(), LoginFragment.TAG)
                .commit()
        }
    }
}