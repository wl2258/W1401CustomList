package kr.ac.kumoh.s20200607.w1401customlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20200607.w1401customlist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: SongViewModel
    private val songAdapter = SongAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[SongViewModel::class.java]

        binding.list.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = songAdapter
        }

        model.list.observe(this) {
            // 좀더 구체적인 이벤트를 사용하라고 warning 나와서 변경함
            //songAdapter.notifyDataSetChanged()
            //Log.i("size", "${model.list.value?.size ?: 0}")

            // Changed가 아니라 Inserted
            songAdapter.notifyItemRangeInserted(0,
                model.list.value?.size ?: 0)
        }

        model.requestSong()
    }

    inner class SongAdapter: RecyclerView.Adapter<SongAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), OnClickListener {
//            val txTitle: TextView = itemView.findViewById(android.R.id.text1)
//            val txSinger: TextView = itemView.findViewById(android.R.id.text2)
            val txTitle: TextView = itemView.findViewById(R.id.text1)
            val txSinger: TextView = itemView.findViewById(R.id.text2)

            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)

            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                //메모리 낭비하고 있음 (많이는 아니고)
                //공통된 함수 레퍼런스만 갖고 있으면 공용으로 사용할 수 있음
                //Toast.makeText(application, model.list.value?.get(adapterPosition)?.title, Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, SongActivity::class.java)
                val song = model.list.value?.get(adapterPosition)
                intent.putExtra(SongActivity.KEY_TITLE, song?.title) //오버로딩
                intent.putExtra(SongActivity.KEY_SINGER, song?.singer)
                intent.putExtra(SongActivity.KEY_IMAGE, model.getImageUrl(adapterPosition)) //수정!

                startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //val view = layoutInflater.inflate(android.R.layout.simple_list_item_2,
            val view = layoutInflater.inflate(R.layout.item_song,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txTitle.text = model.list.value?.get(position)?.title
            holder.txSinger.text = model.list.value?.get(position)?.singer
            //string인 경우 Elvis 연산자 필요없음 integer일 때만 필요

            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)

        }

        override fun getItemCount() = model.list.value?.size ?: 0
    }
}