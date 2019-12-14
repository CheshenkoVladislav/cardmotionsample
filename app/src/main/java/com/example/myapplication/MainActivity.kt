package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val recyclerList = mutableListOf(1, 2, 3, 4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val motionLayout = findViewById<MotionLayout>(R.id.root)
        val recyclerView = findViewById<MyRecyclerView>(R.id.recycler).apply {
            root = motionLayout
        }
        val scndRecycler = findViewById<RecyclerView>(R.id.scnd_recycler).also { scnd ->
            SpringAnimation(scnd, DynamicAnimation.TRANSLATION_Y, 0f)
        }
        val adapter = Adapter(recyclerList)
        val adapter2 = Adapter(mutableListOf(1, 5, 4, 24, 5, 2, 3, 24, 2, 52, 52, 5, 25, 25, 2, 5))
        recyclerView.adapter = adapter
        scndRecycler.adapter = adapter2
        val simpleCallback = SimpleItemTouchHelperCallback(adapter)
        val helper = ItemTouchHelper(simpleCallback)
        helper.attachToRecyclerView(recyclerView)

    }
}

class Adapter(val list: MutableList<Int>) : RecyclerView.Adapter<Holder>(), ItemTouchHelperAdapter {

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
    }

}

interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)
}

class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

}

class Holder(view: View) : RecyclerView.ViewHolder(view) {

}

class MyRecyclerView(context: Context, attributeSet: AttributeSet) :
    RecyclerView(context, attributeSet) {

    lateinit var root: MotionLayout
    val gestureDetector = GestureDetectorCompat(context, SimpleGestureListener())

    private var inChangeHeigh = false

    private val maxHeight =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics)
    private val minHeight =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics)

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(e)
        return super.onTouchEvent(e)
    }

    inner class SimpleGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            println("ON DOWN!!! ")
            return super.onDown(e)
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (!inChangeHeigh ) {
                println("CHANGE PARAMS = $height")
                inChangeHeigh = true
//                val saveHeight = height - 100
//                layoutParams = ConstraintLayout.LayoutParams(
//                    ConstraintLayout.LayoutParams.MATCH_PARENT,
//                    saveHeight
//                )
                val currentProgress = root.progress
                root.progress = currentProgress + distanceY / 100f
                inChangeHeigh = false
                return true
            } else if (!inChangeHeigh && distanceY < 0 && height < maxHeight) {
                inChangeHeigh = true
//                root.transitionToStart()
                inChangeHeigh = false
                return true
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
}