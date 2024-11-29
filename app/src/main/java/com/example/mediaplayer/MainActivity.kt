package com.example.mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView


class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var seekBar: SeekBar
    private lateinit var songTitle: TextView
    private lateinit var timer: TextView
    private lateinit var songSpinner: Spinner
    private lateinit var handler: Handler

    private val songs = arrayOf("Jingle Bells", "Song 1", "Song 2", "Song 3", "Song 4", "Song 5")
    private val songResources = arrayOf(
        R.raw.jingle_bells,
        R.raw.song1,
        R.raw.song2,
        R.raw.song3,
        R.raw.song4,
        R.raw.song5
    )
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songTitle = findViewById(R.id.songTitle)
        timer = findViewById(R.id.timer)
        seekBar = findViewById(R.id.seekBar)
        songSpinner = findViewById(R.id.songSpinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, songs)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        songSpinner.adapter = adapter

        setupMediaPlayer()
        setupButtons()
        setupSeekBar()
        setupSpinner()
    }

    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, songResources[currentPosition])
        songTitle.text = songs[currentPosition]
        mediaPlayer?.setOnCompletionListener {
            playNextSong()
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            mediaPlayer?.start()
            updateSeekBar()
        }

        findViewById<Button>(R.id.btnPause).setOnClickListener {
            mediaPlayer?.pause()
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            mediaPlayer?.stop()
            resetMediaPlayer()
        }
    }

    private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupSpinner() {
        songSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (currentPosition != position) {
                    currentPosition = position
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    setupMediaPlayer() // Переинициализация MediaPlayer с новой песней
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun playNextSong() {
        currentPosition = (currentPosition + 1) % songs.size
        mediaPlayer?.stop()
        mediaPlayer?.release()
        setupMediaPlayer() // Запуск следующей песни
        mediaPlayer?.start() // Запускаем новую песню
        updateSeekBar()
    }

    private fun updateSeekBar() {
        seekBar.max = mediaPlayer?.duration ?: 0
        handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                mediaPlayer?.currentPosition?.let {
                    seekBar.progress = it
                    val minutes = (it / 1000) / 60
                    val seconds = (it / 1000) % 60
                    timer.text = String.format("%02d:%02d", minutes, seconds)
                }
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun resetMediaPlayer() {
        mediaPlayer?.release()
        currentPosition = 0
        mediaPlayer = MediaPlayer.create(this, songResources[currentPosition])
        songTitle.text = songs[currentPosition]
        seekBar.progress = 0
        timer.text = "00:00"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}