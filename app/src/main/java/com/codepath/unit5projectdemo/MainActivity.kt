package com.codepath.unit5projectdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var characterImage = ""
    var characterName = ""
    var familyName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun getCharacterInfo() {
        val client = AsyncHttpClient()
        var choice = Random.nextInt(53)
        val characterJSON = "https://thronesapi.com/api/v2/Characters/" + choice

        client[characterJSON, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())

                characterImage = json.jsonObject.getString("imageUrl")
                characterName = json.jsonObject.getString("fullName")
                familyName = json.jsonObject.getString("family")

                Log.d("info", characterImage + ", " + characterName + ", " + familyName)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("error", errorResponse)
            }
        }]
    }

    private fun findCharacter(characterToFind: String) {
        val client = AsyncHttpClient()

        for (index in 0..52) {
            val characterJSON = "https://thronesapi.com/api/v2/Characters/" + index

            client[characterJSON, object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                    Log.d("search success", json.jsonObject.toString())

                    var resultsJSON = json.jsonObject
                    var current = resultsJSON.getString("firstName")
                    Log.d("current", current)

                    if (current.equals(characterToFind)) {
                        characterImage = resultsJSON.getString("imageUrl")
                        characterName = resultsJSON.getString("fullName")
                        familyName = resultsJSON.getString("family")
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    throwable: Throwable?
                ) {
                    Log.d("search error", errorResponse)
                }
            }]
        }
    }

    fun getNextCharacter(view: View) {
        getCharacterInfo()
        updateUI()
    }

    fun searchForCharacter(view: View) {
        val searchText = findViewById<EditText>(R.id.search)

        var searchValue = searchText.getText().toString()
        findCharacter(searchValue)
        updateUI()
    }

    private fun updateUI() {
        val imageView = findViewById<ImageView>(R.id.characterImage)
        val nameView = findViewById<TextView>(R.id.characterName)
        val familyView = findViewById<TextView>(R.id.familyName)

        nameView.setText(characterName)
        familyView.setText(familyName)

        Glide.with(this)
            .load(characterImage)
            .fitCenter()
            .into(imageView)

        characterName = ""
        familyName = ""
        characterImage = ""
    }
}