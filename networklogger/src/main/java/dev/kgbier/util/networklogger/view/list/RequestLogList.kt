package dev.kgbier.util.networklogger.view.list

import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import dev.kgbier.util.networklogger.R
import dev.kgbier.util.networklogger.view.widget.RequestLogItemView

class RequestLogListAdapter(
    private val events: List<RequestLogItemView.ViewModel>,
    private val onClickItem: (id: String) -> Unit,
) : RecyclerView.Adapter<RequestLogListItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RequestLogListItemViewHolder = RequestLogListItemViewHolder(parent)

    override fun onBindViewHolder(holder: RequestLogListItemViewHolder, position: Int) {
        val event = events[position]
        holder.rootView.bindEventModel(event)
        holder.rootView.setOnClickListener { onClickItem(event.id) }
    }

    override fun getItemCount(): Int = events.size
}

class RequestLogListItemViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(RequestLogItemView(parent.context)) {
    val rootView: RequestLogItemView = itemView as RequestLogItemView

    init {
        val paddingNormal = rootView.resources.getDimensionPixelSize(R.dimen.spacing_normal)
        rootView.setPadding(paddingNormal)
    }
}