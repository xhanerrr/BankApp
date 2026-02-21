package com.example.bankapp.ui.register.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.bankapp.R // Asegúrate de que R esté disponible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfessionSelectionBottomSheet : BottomSheetDialogFragment() {

    interface ProfessionSelectionListener {
        fun onProfessionSelected(profession: String)
    }

    private var listener: ProfessionSelectionListener? = null
    private lateinit var professionListView: ListView

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ProfessionSelectionListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar ProfessionSelectionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_profession_selection, container, false)
        professionListView = view.findViewById(R.id.profession_list_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val professions = arguments?.getStringArrayList(ARG_PROFESSIONS) ?: emptyList()

        if (professions.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                professions
            )
            professionListView.adapter = adapter

            professionListView.setOnItemClickListener { _, _, position, _ ->
                val selectedProfession = professions[position]
                listener?.onProfessionSelected(selectedProfession)
                dismiss()
            }
        }
    }

    companion object {
        private const val ARG_PROFESSIONS = "professions"

        fun newInstance(professions: ArrayList<String>): ProfessionSelectionBottomSheet {
            val fragment = ProfessionSelectionBottomSheet()
            val args = Bundle().apply {
                putStringArrayList(ARG_PROFESSIONS, professions)
            }
            fragment.arguments = args
            return fragment
        }
    }
}