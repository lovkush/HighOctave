package com.academy.octave;




import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.academy.octave.ui.home.HomeFragment


class HomeCardAdapter (private val title: ArrayList<String>,
                       private val description:ArrayList<String>,
                       private val listener: OnItemClickListener) : RecyclerView.Adapter<HomeCardAdapter.ViewHolder>() {



    var onItemClick: ((HomeFragment) -> Unit)? = null
    var contacts: ArrayList<String> = title


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.main_card, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: HomeCardAdapter.ViewHolder, position: Int) {
        holder.bindItems(title[position],description[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return title.size
    }

    //the class is hodling the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun bindItems(title: String,description: String) {
            val textViewName = itemView.findViewById(R.id.cardTitle) as TextView
            val textViewAddress  = itemView.findViewById(R.id.cardDescription) as TextView
            textViewName.text = title
            textViewAddress.text = description

        }
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}