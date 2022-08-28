package com.ayizor.vodiypolymer.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ayizor.afeme.utils.Logger
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.activity.MainActivity
import com.ayizor.vodiypolymer.databinding.FragmentCodeConfirmBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager
import com.ayizor.vodiypolymer.model.User
import com.ayizor.vodiypolymer.utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import java.util.*
import java.util.concurrent.TimeUnit


class CodeConfirmFragment : Fragment() {

    lateinit var binding: FragmentCodeConfirmBinding
    val TAG: String = CodeConfirmFragment::class.java.simpleName
    lateinit var user_phone_number: String
    lateinit var user_first_name: String
    lateinit var user_last_name: String
    lateinit var user_confirmation_code: String
    var user_device_type: String = "Android"
    private val args: CodeConfirmFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var reference: DatabaseReference
    lateinit var user: User
    lateinit var user_id: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodeConfirmBinding.inflate(inflater, container, false)

        inits()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun inits() {
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference;
        //get entered datas
        user_phone_number = args.number


        binding.tvTimer.text = startResendTimer(60).toString()
        sendVerificationCode(user_phone_number)



        binding.tvCodeConfirmInfo.text =
            getString(R.string.enter_the_confirmation_code_we_send_to) + " " + user_phone_number

        binding.tvResendCode.setOnClickListener {
            reSendVerificationCode(args.number)
        }
        binding.btnNext.setOnClickListener {


            if (binding.pinView.text?.isNotEmpty() == true) {
                if (binding.pinView.text!!.length == 6) {

                    user_confirmation_code = binding.pinView.text.toString()
                    verifyCode(user_confirmation_code)

                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.enter_the_code_sent_via_sms),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun verifyCode(code: String) {
        if (code.length == 6) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun reSendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setForceResendingToken(resendToken)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Logger.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Logger.e(TAG, "onVerificationFailed$e")

            if (e is FirebaseAuthInvalidCredentialsException) {
            } else if (e is FirebaseTooManyRequestsException) {
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Logger.d(TAG, "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Logger.d(TAG, "signInWithCredential:success")
                    if (args.authType.contentEquals("signin")) {
                        loginUser()
                    }
                    if (args.authType.contentEquals("signup")) {
                        registerUser()
                    }
                } else {
                    Logger.e(TAG, "signInWithCredential:failure" + task.exception)

                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerUser() {
        //get current device id
        val user_device_id: String = Utils.getDeviceID(requireContext())
        val user_device_token = Utils.loadFCMToken()
        val currentTime = Utils.getCurrentTime()
        val uuid: String = UUID.randomUUID().toString().replace("-", "");
        val user = User(
            uuid,
            args.firstName,
            args.lastName,
            args.companyName,
            args.number,
            user_device_id,
            user_device_token,
            null,
            null,
            currentTime,
            currentTime
        )
        reference.child("users").push().setValue(user)
        UserPrefManager(requireContext()).storeUser(user)
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()

    }

    private fun loginUser() {

                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("user_phone_number", args.number)
                startActivity(intent)
                activity?.finish()

    }

    private fun startResendTimer(seconds: Int) {
        binding.tvTimer.visibility = View.VISIBLE
        binding.tvResendCode.isEnabled = false
        object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var secondsString = (millisUntilFinished / 1000).toString()
                if (millisUntilFinished < 600) {
                    secondsString = "0$secondsString"
                    binding.tvResendCode.setOnClickListener {
                        Toast.makeText(requireContext(), "Daqiqa tugasin", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                binding.tvTimer.text = " 00:$secondsString"
            }

            override fun onFinish() {
                binding.tvResendCode.isEnabled = true
                binding.tvTimer.visibility = View.GONE
                binding.tvResendCode.setOnClickListener {
                    binding.tvTimer.visibility = View.VISIBLE
                    startResendTimer(60).toString()
                    reSendVerificationCode(args.number)
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        sendVerificationCode(args.number)
    }


}