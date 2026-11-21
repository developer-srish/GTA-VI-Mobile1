
package com.mario.gtavi

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mario.gtavi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemBars()
        playIntro()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun playIntro() {
        try {
            val videoFile = assets.openFd("GTAVI.mp4")

            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    videoFile.fileDescriptor,
                    videoFile.startOffset,
                    videoFile.length
                )

                setOnPreparedListener { player ->
                    player.start()
                }

                setOnCompletionListener {
                    releasePlayer()
                    showCrashPopup()
                }

                setOnErrorListener { _, _, _ ->
                    releasePlayer()
                    showCrashPopup()
                    true
                }

                prepareAsync()
            }

            videoFile.close()

            binding.videoView.setOnCompletionListener {
                // Completion is handled by MediaPlayer.
            }

        } catch (e: Exception) {
            releasePlayer()
            showCrashPopup()
        }
    }

    private fun showCrashPopup() {
        if (isFinishing || isDestroyed) return

        val message = """
            
            💀 AREY BHAI! TU PRANK HO GAYA!
            
            You thought you were entering GTA VI?
            
            SAPNA TOOT GAYA! 😂
            
            NO GAME. NO MISSION.
            ONLY KALESH.
            
            🔥 PRANK COMPLETE 🔥
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("GRAND THEFT INDIA\nMISSION FAILED!")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("PRANK COMPLETE 😂") { _, _ ->
                finishAffinity()
            }
            .show()
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }
}
