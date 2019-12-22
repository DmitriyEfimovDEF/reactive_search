package ru.aisdev.android.reactive_search.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.reactive_search.R
import ru.aisdev.android.reactive_search.entity.WordItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_word.view.*
import ru.aisdev.android.reactive_search.SearchApplicationModule
import ru.aisdev.android.reactive_search.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private val adapter =
        Adapter()
    private val presenter = SearchApplicationModule.MAIN_COMPONENT.searchModel
    private var viewModelDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this@SearchActivity)

        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                presenter.onFilterTextChanged(p0?.toString() ?: "")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        subscribeToViewModels()
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModels()
    }

    override fun onStop() {
        super.onStop()
        unsubscribeFromViewModels()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    private fun subscribeToViewModels() {
        if (viewModelDisposable == null) {
            viewModelDisposable = presenter
                .observeViewModels()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { update(it) }
        }
    }

    private fun unsubscribeFromViewModels() {
        viewModelDisposable?.dispose()
    }

    private fun update(model: SearchViewModel) {
        pb.visibility(model.state is SearchViewModel.State.Loading)
        vError.visibility(model.state is SearchViewModel.State.Error)
        rv.visibility(model.state is SearchViewModel.State.Content)
        vEmpty.visibility(model.state is SearchViewModel.State.Empty)

        vError.text = if (model.state is SearchViewModel.State.Error) model.state.text else ""

        if (et.text.toString() != model.filter) {
            et.setText(model.filter)
        }
        adapter.swapData(model.items)
    }

    private fun View.visibility(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }

    class Adapter : RecyclerView.Adapter<WordItemViewHolder>() {

        private var items: List<WordItem> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_word, parent, false)
            return WordItemViewHolder(
                view
            )
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: WordItemViewHolder, position: Int) {
            holder.update(items[position])
        }

        fun swapData(items: List<WordItem>) {
            this.items = items
            notifyDataSetChanged()
        }
    }

    class WordItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val boldSpan = StyleSpan(Typeface.BOLD)
        fun update(item: WordItem) = with(itemView) {
            if (item.selection != null) {
                item_text.text = SpannableStringBuilder(item.text).apply {
                    setSpan(boldSpan, 0, item.selection.length, SPAN_INCLUSIVE_INCLUSIVE)
                }
            } else {
                item_text.text = item.text
            }
        }
    }
}
