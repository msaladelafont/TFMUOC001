package android.app.tfmuoc001

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIMEOUT: Long = 600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen_layout)

        Handler().postDelayed({
            startActivity(Intent(this, MainPageActivity::class.java))
            finish()
        }, SPLASH_TIMEOUT)
    }
}