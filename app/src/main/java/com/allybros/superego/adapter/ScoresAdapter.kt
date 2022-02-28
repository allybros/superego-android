package com.allybros.superego.adapter

import com.allybros.superego.unit.Score
import android.widget.ArrayAdapter
import com.allybros.superego.R
import com.google.android.gms.ads.AdView
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import android.widget.FrameLayout
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import java.util.*

class ScoresAdapter(
    context: Context?,
    private val scores: ArrayList<Score>,
    private val isContainTitle: Boolean,
    shareListener: View.OnClickListener?
) : ArrayAdapter<Score?>(
    context!!, R.layout.scores_list_row, scores as List<Score?>
) {
    private var adResultBanner: AdView? = null
    private var shareListener: View.OnClickListener? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            if (position == scores.size - 1) {
                convertView = inflater.inflate(R.layout.list_ad_row, parent, false)
                adResultBanner = convertView.findViewById(R.id.resultBannerAdd)
                prepareBannerAd()
                return convertView
            } else {
                convertView = inflater.inflate(R.layout.scores_list_row, parent, false)
                if (isContainTitle && position == 0) {
                    val llTraitTitle: ConstraintLayout = convertView.findViewById(R.id.clTraitTitle)
                    val ivShareResults = convertView.findViewById<ImageView>(R.id.ivShareResults)
                    llTraitTitle.visibility = View.VISIBLE
                    ivShareResults.setOnClickListener { v: View? ->
                        shareListener!!.onClick(
                            ivShareResults
                        )
                    }
                    return convertView
                }
            }
        }
        val traitImage = convertView!!.findViewById<ImageView>(R.id.traitEmojiView)
        val traitNameView = convertView.findViewById<TextView>(R.id.traitNameView)
        val traitEmojiContainer = convertView.findViewById<FrameLayout>(R.id.imageViewContainer)
        val clTraitRow: ConstraintLayout = convertView.findViewById(R.id.clTraitRow)
        if (traitImage == null || traitNameView == null || traitEmojiContainer == null) return View(
            context
        )
        val s = getItem(position)
        if (s != null) {
            //Set name
            traitNameView.text = s.traitName
            //Load emoji
            val myUrl = Uri.parse(EMOJI_END_POINT + s.emojiName)
            GlideToVectorYou.justLoadImage(context as Activity, myUrl, traitImage)
        }

        //Set emoji container background
        val random = Random(System.currentTimeMillis())
        // Not too dark or bright. Keep in an interval.
        val r = random.nextInt(120) + 60
        val g = random.nextInt(120) + 60
        val b = random.nextInt(120) + 60
        val shape = traitEmojiContainer.background
        shape.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.SRC_ATOP)
        clTraitRow.visibility = View.VISIBLE
        return convertView
    }

    private fun prepareBannerAd() {
        // Initialize mobile ads
        MobileAds.initialize(context) { initializationStatus: InitializationStatus? ->
            Log.d(
                "MobileAds",
                "Initialized."
            )
        }
        val adTag = "ad_result_banner"
        // Load ad
        val adRequest = AdRequest.Builder().build()
        adResultBanner!!.loadAd(adRequest)
        // Set ad listener
        adResultBanner!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(adTag, "Result banner ad loaded.")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d(adTag, "Result banner ad couldn't be loaded")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(adTag, "Result banner ad opened.")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(adTag, "Result banner ad clicked.")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(adTag, "User left application")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(adTag, "User returned from ad.")
            }
        }
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    companion object {
        //CDN URL for emojis
        private const val EMOJI_END_POINT = "https://api.allybros.com/twemoji/?name="
    }

    init {
        if (shareListener != null) this.shareListener = shareListener
        scores.add(Score(-2016, 0f)) //It provide to adding ad row
        if (isContainTitle) scores.add(0, Score(-2016, 0f)) //It provide to adding title row
    }
}