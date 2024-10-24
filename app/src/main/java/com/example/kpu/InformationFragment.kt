package com.example.kpu

import DataKpuAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kpu.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var dataKpuAdapter: DataKpuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sample Data
//        val sampleData = listOf(
//            data(R.drawable.logo, "Name 1", "Alamat 1"),
//            data(R.drawable.logo, "Name 2", "Alamat 2"),
//            data(R.drawable.logo, "Name 3", "Alamat 3")
//        )

        // Setup Adapter
        dataKpuAdapter = DataKpuAdapter(databaseHelper.getAllUsers()) { selectedItem ->
            // Handle item click here if needed
        }

        // Set up RecyclerView
        binding.dataList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataKpuAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
