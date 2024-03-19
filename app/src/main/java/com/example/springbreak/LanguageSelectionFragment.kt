package com.example.springbreak

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class LanguageSelectionFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 100

        private var languageCodes = mapOf(
            "Spanish" to "es",
            "French" to "fr",
            "Chinese" to "zh-CN",
            "German" to "de"
        )

        private val languageToCitiesMap = mapOf(
            "es" to listOf("Madrid, Spain", "Barcelona, Spain", "Seville, Spain"),
            "fr" to listOf("Paris, France", "Lyon, France", "Marseille, France"),
            "zh-CN" to listOf("Beijing, China", "Shanghai, China", "Guangzhou, China"),
            "de" to listOf("Berlin, Germany", "Munich, Germany", "Hamburg, Germany")
        )
    }

    private var selectedLanguageCode: String = "en"
    private lateinit var sensorManager: SensorManager
    private var shakeDetector: ShakeDetector? = null

    override fun onResume() {
        super.onResume()
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector = ShakeDetector {
            playHelloAudio(selectedLanguageCode)
            openGoogleMapsForCity(selectedLanguageCode)
        }
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.let {
            sensorManager.unregisterListener(it)
        }
    }

    private lateinit var languagesAdapter: LanguagesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_language_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val languagesRecyclerView = view.findViewById<RecyclerView>(R.id.languagesRecyclerView)
        languagesRecyclerView.layoutManager = LinearLayoutManager(context)

        val languages = listOf("Spanish", "French", "Chinese", "German")
        languagesAdapter = LanguagesAdapter(languages) { language ->
            val languageCode = languageCodes[language] ?: Locale.getDefault().language
            promptSpeechInput(languageCode)
            selectedLanguageCode = languageCode
        }
        languagesRecyclerView.adapter = languagesAdapter
    }

    private fun promptSpeechInput(languageCode: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_now))
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(activity, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && null != data) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val editTextPhrase = view?.findViewById<EditText>(R.id.editTextPhrase)
            editTextPhrase?.setText(result?.get(0))
        }
    }

    private fun playHelloAudio(languageCode: String) {
        val resId = resources.getIdentifier("hello_$languageCode", "raw", context?.packageName)
        if (resId != 0) {
            val mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer.setOnCompletionListener { mp -> mp.release() }
            mediaPlayer.start()
        } else if (languageCode == "zh-CN") {
            val zhresId = resources.getIdentifier("hello_zhcn", "raw", context?.packageName)
            val zhmediaPlayer = MediaPlayer.create(context, zhresId)
            zhmediaPlayer.setOnCompletionListener { mp -> mp.release() }
            zhmediaPlayer.start()
        } else {
            Toast.makeText(context, R.string.pick, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGoogleMapsForCity(languageCode: String) {
        val cities = languageToCitiesMap[languageCode] ?: return
        val randomCity = cities.random()
        val IntentUri = Uri.parse("geo:0,0?q=$randomCity")
        val mapIntent = Intent(Intent.ACTION_VIEW, IntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(activity?.packageManager!!) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(context, R.string.install, Toast.LENGTH_SHORT).show()
        }
    }
}