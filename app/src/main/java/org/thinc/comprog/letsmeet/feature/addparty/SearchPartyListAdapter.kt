package org.thinc.comprog.letsmeet.feature.addparty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import org.thinc.comprog.letsmeet.BR
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.model.Party

class SearchPartyListAdapter(
    lifecycleOwner: LifecycleOwner,
    private var partyList: MutableLiveData<List<Party>>
) : RecyclerView.Adapter<SearchPartyListAdapter.PartyBindingViewHolder>() {

    class PartyBindingViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(party: Party) {
            binding.setVariable(BR.viewModel, SearchPartyListViewModel(party))
            binding.executePendingBindings()
        }

        fun cleanUp() {

        }
    }

    init {
        partyList.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchPartyListAdapter.PartyBindingViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.search_party_list_card, parent, false)
        return PartyBindingViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: PartyBindingViewHolder, position: Int) {
        holder.bind(partyList.value!![position])
    }

    override fun onViewRecycled(holder: PartyBindingViewHolder) {
        holder.cleanUp()
    }

    // Return the size of your data set (invoked by the layout manager)
    override fun getItemCount() = partyList.value!!.size
}