package com.example.urbanease.ui.animations
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
object AnimationDurations {
    const val FAST = 250
    const val NORMAL = 300
}
object AnimationEasings {
    val DEFAULT = EaseInOutCubic
}
object ScreenTransitions {
    fun slideInLeftTransition(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        )
    }
    fun slideOutLeftTransition(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        )
    }
    fun slideInRightTransition(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        )
    }
    fun slideOutRightTransition(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasings.DEFAULT
            )
        )
    }
    fun fadeInTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurations.FAST,
                easing = AnimationEasings.DEFAULT
            )
        )
    }
    fun fadeOutTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(
                durationMillis = AnimationDurations.FAST,
                easing = AnimationEasings.DEFAULT
            )
        )
    }
}
