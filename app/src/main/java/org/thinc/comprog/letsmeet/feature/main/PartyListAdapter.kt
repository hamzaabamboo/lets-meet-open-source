package org.thinc.comprog.letsmeet.feature.main

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.party_list_card.view.*
import org.thinc.comprog.letsmeet.BR
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.model.Party
import org.thinc.comprog.letsmeet.data.model.User

class PartyListAdapter(
    lifecycleOwner: LifecycleOwner,
    private var partyList: MutableLiveData<List<Party>>,
    private val user: MutableLiveData<User>
) : RecyclerView.Adapter<PartyListAdapter.PartyBindingViewHolder>() {

    class PartyBindingViewHolder(val binding: ViewDataBinding, val user: MutableLiveData<User>) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(party: Party) {
            binding.setVariable(BR.viewModel, PartyListViewModel(party, user))
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
    ): PartyListAdapter.PartyBindingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.party_list_card, parent, false)
        return PartyBindingViewHolder(dataBinding, user)
    }

    override fun onBindViewHolder(holder: PartyBindingViewHolder, position: Int) {
        ViewCompat.setTransitionName(holder.binding.root.topic, partyList.value!![position].id)
        holder.bind(partyList.value!![position])
    }

    override fun onViewRecycled(holder: PartyBindingViewHolder) {
        holder.cleanUp()
    }

    // Return the size of your data set (invoked by the layout manager)
    override fun getItemCount() = partyList.value!!.size
}