package dev.kgbier.util.networklogger.view.list

import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.RecyclerView
import dev.kgbier.util.networklogger.R
import dev.kgbier.util.networklogger.view.widget.EventDetailsHeaderItemView
import dev.kgbier.util.networklogger.view.widget.EventDetailsSectionTitleItemView
import dev.kgbier.util.networklogger.view.widget.EventDetailsTextAreaItemView

internal class EventDetailsHeaderItemViewHolder(parent: ViewGroup) :
    EventDetailsListItemViewHolder(EventDetailsHeaderItemView(parent.context)) {

    private val rootView = itemView as EventDetailsHeaderItemView

    init {
        val paddingNormal = rootView.resources.getDimensionPixelSize(R.dimen.spacing_normal)
        rootView.setPadding(paddingNormal)
    }

    override fun bind(model: Any) {
        if (model !is EventDetailsHeaderItemView.ViewModel) return
        rootView.bind(model)
    }
}

internal class EventDetailsSectionTitleItemViewHolder(parent: ViewGroup) :
    EventDetailsListItemViewHolder(EventDetailsSectionTitleItemView(parent.context)) {
    private val rootView = itemView as EventDetailsSectionTitleItemView

    init {
        val paddingNormal = rootView.resources.getDimensionPixelSize(R.dimen.spacing_normal)
        val paddingSmall = rootView.resources.getDimensionPixelSize(R.dimen.spacing_small)
        rootView.updatePaddingRelative(
            start = paddingNormal,
            top = paddingNormal,
            end = paddingNormal,
            bottom = paddingSmall,
        )
    }

    override fun bind(model: Any) {
        if (model !is EventDetailsSectionTitleItemView.ViewModel) return
        rootView.bind(model)
    }
}

internal class EventDetailsTextAreaItemViewHolder(parent: ViewGroup) :
    EventDetailsListItemViewHolder(EventDetailsTextAreaItemView(parent.context)) {
    private val rootView = itemView as EventDetailsTextAreaItemView

    override fun bind(model: Any) {
        if (model !is EventDetailsTextAreaItemView.ViewModel) return
        rootView.bind(model)
    }
}

internal abstract class EventDetailsListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(model: Any)
}

internal class EventDetailsListAdapter(
    private val items: List<Any>,
) : RecyclerView.Adapter<EventDetailsListItemViewHolder>() {

    companion object {
        const val VIEW_TYPE_ERROR = 0
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_SECTION_TITLE = 2
        const val VIEW_TYPE_TEXT_AREA = 3
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EventDetailsListItemViewHolder = when (viewType) {
        VIEW_TYPE_HEADER -> EventDetailsHeaderItemViewHolder(parent)
        VIEW_TYPE_SECTION_TITLE -> EventDetailsSectionTitleItemViewHolder(parent)
        VIEW_TYPE_TEXT_AREA -> EventDetailsTextAreaItemViewHolder(parent)
        else -> error("Invalid view type: $viewType")
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is EventDetailsHeaderItemView.ViewModel -> VIEW_TYPE_HEADER
        is EventDetailsSectionTitleItemView.ViewModel -> VIEW_TYPE_SECTION_TITLE
        is EventDetailsTextAreaItemView.ViewModel -> VIEW_TYPE_TEXT_AREA
        else -> VIEW_TYPE_ERROR
    }

    override fun onBindViewHolder(holder: EventDetailsListItemViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}
