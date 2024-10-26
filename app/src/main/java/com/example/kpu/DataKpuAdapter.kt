import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kpu.DetailDataActivity
import com.example.kpu.R
import com.example.kpu.data
import com.example.kpu.databinding.DataBinding
import java.io.File

class DataKpuAdapter(
    private var dataList: List<data>,
    private val onClickItem: (data) -> Unit
) : RecyclerView.Adapter<DataKpuAdapter.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = DataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    inner class DataViewHolder(private val binding: DataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: data) {
            if (item.image != null) {
                Glide.with(itemView.context)
                    .load(File(item.image))
                    .centerCrop()
                    .into(binding.itemImage)
            } else {
                binding.itemImage.setImageResource(R.drawable.logo) // Use a placeholder if no image
            }
            binding.itemName.text = item.name
            binding.itemAlamat.text = item.nik
            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, DetailDataActivity::class.java)
                intent.putExtra("user_id", item.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    fun updateData(newData: List<data>) {
        dataList = newData
        notifyDataSetChanged()
    }

}
