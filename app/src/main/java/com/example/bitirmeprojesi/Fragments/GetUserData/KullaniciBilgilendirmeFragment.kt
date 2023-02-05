package com.example.bitirmeprojesi.Fragments.GetUserData

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.FragmentGetPhoneNumberBinding
import com.example.bitirmeprojesi.databinding.FragmentKullaniciBilgilendirmeBinding
import com.example.bitirmeprojesi.databinding.GetuserdataWhenClosedDialogBinding

class KullaniciBilgilendirmeFragment : Fragment() {

    private var _binding: FragmentKullaniciBilgilendirmeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: AlertDialog
    private lateinit var getuserdataWhenClosedDialogBinding: GetuserdataWhenClosedDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentKullaniciBilgilendirmeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener{
            val action = KullaniciBilgilendirmeFragmentDirections.actionKullaniciBilgilendirmeFragmentToKullaniciAdiFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnClose.setOnClickListener {

            val alertDialog = AlertDialog.Builder(requireContext())
            getuserdataWhenClosedDialogBinding = GetuserdataWhenClosedDialogBinding.inflate(layoutInflater)
            alertDialog.setView(getuserdataWhenClosedDialogBinding.root)

            dialog = alertDialog.create()

            getuserdataWhenClosedDialogBinding.btnYes.setOnClickListener {
                requireActivity().finish()
            }
            getuserdataWhenClosedDialogBinding.btnNo.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }

}