package org.thinc.comprog.letsmeet.feature.chat

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
import org.thinc.comprog.letsmeet.data.model.User

class MemberListAdapter(
    lifecycleOwner: LifecycleOwner,
    private val members: MutableLiveData<List<User>>
) : RecyclerView.Adapter<MemberListAdapter.MemberBindingViewHolder>() {

    class MemberBindingViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.setVariable(BR.viewModel, MemberListItemViewModel(user))
            binding.executePendingBindings()
        }

        fun cleanUp() {
        }
    }

    init {
        members.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberBindingViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.member_list_item, parent, false)
        return MemberBindingViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: MemberBindingViewHolder, position: Int) {
        holder.bind(members.value!![position])
    }

    override fun onViewRecycled(holder: MemberBindingViewHolder) {
        holder.cleanUp()
    }

    // Return the size of your data set (invoked by the layout manager)
    override fun getItemCount() = members.value!!.size
}