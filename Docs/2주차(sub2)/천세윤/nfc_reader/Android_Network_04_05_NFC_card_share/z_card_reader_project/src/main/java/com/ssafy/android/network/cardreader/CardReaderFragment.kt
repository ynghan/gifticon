/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ssafy.android.network.cardreader

import android.app.Activity
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.android.common.logger.Log.i

/**
 * Generic UI for sample discovery.
 */
private const val TAG = "CardReaderFragment_싸피"
class CardReaderFragment : Fragment(), LoyaltyCardReader.AccountCallback {
    lateinit var mLoyaltyCardReader: LoyaltyCardReader
    private lateinit var mAccountField: TextView

    /**
     * Called when sample is created. Displays generic UI with welcome text.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.main_fragment, container, false)
        if (v != null) {
            mAccountField = v.findViewById<View>(R.id.card_account_field) as TextView
            mAccountField.text = "Waiting..."
            mLoyaltyCardReader = LoyaltyCardReader(this)

            // Disable Android Beam and register our card reader callback
            enableReaderMode()
        }
        return v
    }

    override fun onPause() {
        super.onPause()
        disableReaderMode()
    }

    override fun onResume() {
        super.onResume()
        enableReaderMode()
    }

    private fun enableReaderMode() {
        i(TAG, "Enabling reader mode")
        val activity: Activity? = activity
        val nfc = NfcAdapter.getDefaultAdapter(activity)
        nfc.enableReaderMode( activity, mLoyaltyCardReader, READER_FLAGS,null)
    }

    private fun disableReaderMode() {
        i(TAG, "Disabling reader mode")
        val activity: Activity? = activity
        val nfc = NfcAdapter.getDefaultAdapter(activity)
        nfc.disableReaderMode(activity)
    }

    override fun onAccountReceived(account: String?) {
        // This callback is run on a background thread, but updates to UI elements must be performed
        // on the UI thread.
        activity?.runOnUiThread { mAccountField.text = account }
    }

    companion object {

        // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
        // activity is interested in NFC-A devices (including other Android devices), and that the
        // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
        var READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
    }
}