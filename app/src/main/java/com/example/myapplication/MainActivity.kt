package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private val recyclerList = mutableListOf(1, 2, 3, 4)
    var currentScrollPosition = 0
    lateinit var gestureDetectorCompat: GestureDetectorCompat
    private var canStretch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val motionLayout = findViewById<MotionLayout>(R.id.root)
        val recyclerView = findViewById<StretchingCardRecyclerView>(R.id.recycler).apply {
            root = motionLayout
        }
        val scndRecycler = findViewById<RecyclerView>(R.id.scnd_recycler)
        val adapter = Adapter(recyclerList)
        val adapter2 = Adapter(mutableListOf(1, 5, 4, 24, 5, 2, 3, 24, 2, 52, 52, 5, 25, 25, 2, 5))
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                recyclerView.touch(e)
                return super.onInterceptTouchEvent(rv, e)
            }
        })
        scndRecycler.adapter = adapter2
        scndRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
                currentScrollPosition += dy
                canStretch =
                    (currentScrollPosition == 0 || currentScrollPosition % 100 == 0)
            }
        })
        gestureDetectorCompat =
            GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    if (canStretch) {
                        return recyclerView.stretchView(distanceY)
                    }
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }
            })
        view.setOnTouchListener { v, event ->
            gestureDetectorCompat.onTouchEvent(event)
            return@setOnTouchListener false
        }
        scndRecycler.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (!recyclerView.onFingerUp(e)) {
                    gestureDetectorCompat.onTouchEvent(e)
                }
                return super.onInterceptTouchEvent(rv, e)
            }
        })
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
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
    }

}

class Holder(view: View) : RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener {
            println("CLick")
        }
    }
}

class StretchingCardRecyclerView(context: Context, attributeSet: AttributeSet) :
    RecyclerView(context, attributeSet) {
    lateinit var root: MotionLayout

    private val gestureDetector = GestureDetectorCompat(context, GestureListener())
    private var inChangeHeigh = false

    private var enableMotionScene = true
    private val maxHeight =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics)

    private val maxOverHeight =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450f, resources.displayMetrics)
    private var force = 0f

    private var maxScrollForce = 1f

    fun touch(e: MotionEvent?) {
        when (e?.action) {
            MotionEvent.ACTION_UP -> {
                onFingerUp(e)
            }
        }
        gestureDetector.onTouchEvent(e)
    }

    fun onFingerUp(e: MotionEvent?): Boolean {
        when (e?.action) {
            MotionEvent.ACTION_UP -> {
                if (height > maxHeight) {
                    animatedSqueezeView()
                    return true
                }
            }
        }
        return false
    }

    fun stretchView(distanceY: Float): Boolean {
        if (!inChangeHeigh && distanceY < 0 && height >= maxHeight.toInt()) {
            inChangeHeigh = true
            toggleMotionScene(false)
            val currHeight = height
            calculateForce()
            layoutParams.height = (currHeight - (distanceY * force)).roundToInt()
            recycler.requestLayout()
            inChangeHeigh = false
            return true
        }
        return false
    }

    fun isOverStretch() = height > maxHeight

    fun squeezeView(distanceY: Float) {
        if (!inChangeHeigh && distanceY > 0) {
            if (height > maxHeight.toInt()) {
//                inChangeHeigh = true
//                toggleMotionScene(false)
//                val currHeight = height
//                layoutParams.height = (currHeight - distanceY).roundToInt()
//                recycler.requestLayout()
                toggleMotionScene(true)
            } else {
                toggleMotionScene(true)
            }
            inChangeHeigh = false
        }
    }

    private fun animatedSqueezeView() {
        val heightAnimator =
            ValueAnimator.ofInt(height, maxHeight.toInt()).setDuration(200)
        heightAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                toggleMotionScene(true)
            }
        })
        heightAnimator.addUpdateListener {
            layoutParams.height = it.animatedValue as Int
            recycler.requestLayout()
        }
        heightAnimator.start()
    }

    private fun toggleMotionScene(isEnable: Boolean) {
        root.apply {
            loadLayoutDescription(if (isEnable) R.xml.scenes else 0)
            enableMotionScene = isEnable
        }
    }

    private fun calculateForce() {
        val onePercentValue = maxOverHeight / 100
        force = maxScrollForce - (height / onePercentValue) / 100f
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (stretchView(distanceY)) {
                return true
            } else if (!inChangeHeigh) {
                inChangeHeigh = true
                if (!enableMotionScene) {
                    if (height > maxHeight.toInt()) {
                        val currHeight = height
                        layoutParams.height = currHeight - distanceY.toInt()
                        recycler.requestLayout()
                    } else {
                        toggleMotionScene(true)
                    }
                }
                val currentProgress = root.progress
                val newProgress = currentProgress + (distanceY / 100f)
                root.progress = newProgress
                inChangeHeigh = false
                return true
            }
            return false
        }
    }
}