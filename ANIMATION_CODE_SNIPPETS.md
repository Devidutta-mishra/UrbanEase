# Animation Code Snippets & Examples

## Common Use Cases

### 1. Animated List Items with Stagger

```kotlin
LazyColumn {
    items(myList.size) { index ->
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight / 4 },
                animationSpec = tween(
                    durationMillis = AnimationDurations.NORMAL,
                    delayMillis = index * 50
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = AnimationDurations.NORMAL,
                    delayMillis = index * 50
                )
            )
        ) {
            ListItemComposable(myList[index])
        }
    }
}
```

### 2. Smooth Screen Navigation

```kotlin
// In UrbanNavigation.kt
composable(
    route = UrbanScreens.YourScreen.name,
    enterTransition = { ScreenTransitions.slideInLeftTransition() },
    exitTransition = { ScreenTransitions.slideOutLeftTransition() }
) {
    YourScreen(navController)
}

// Navigation call
navController.navigate(UrbanScreens.YourScreen.name)
```

### 3. Animated Form Fields

```kotlin
@Composable
fun MyForm() {
    Column {
        AnimatedInputField(
            label = "Field 1",
            state = field1State,
            placeholder = "Enter value",
            index = 0
        )
        
        AnimatedInputField(
            label = "Field 2",
            state = field2State,
            placeholder = "Enter value",
            index = 1  // 50ms delay
        )
        
        AnimatedInputField(
            label = "Field 3",
            state = field3State,
            placeholder = "Enter value",
            index = 2  // 100ms delay
        )
    }
}
```

### 4. Button with Press Feedback

```kotlin
AnimatedButton(
    onClick = { 
        // Your action here
        myViewModel.submitForm()
    },
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = Color.Black
    ),
    shape = RoundedCornerShape(16.dp)
) {
    Text("Submit", fontSize = 16.sp, fontWeight = FontWeight.Bold)
}
```

### 5. Fade In/Out Sections

```kotlin
@Composable
fun MyScreen() {
    var isLoading by remember { mutableStateOf(true) }
    
    // Loading indicator
    AnimatedLoadingIndicator(
        isLoading = isLoading,
        color = Color.Black
    )
    
    // Content that fades in after loading
    AnimatedVisibility(
        visible = !isLoading,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurations.FAST
            )
        )
    ) {
        MyContent()
    }
}
```

### 6. Cascading Content Reveal

```kotlin
@Composable
fun PropertySummary() {
    Column {
        // Title fades in immediately
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            Text("Property Details", fontSize = 20.sp)
        }
        
        // Image section - slides + fades with 100ms delay
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { 100 },
                animationSpec = tween(300, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(300, delayMillis = 100))
        ) {
            PropertyImage()
        }
        
        // Details section - slides + fades with 200ms delay
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { 100 },
                animationSpec = tween(300, delayMillis = 200)
            ) + fadeIn(animationSpec = tween(300, delayMillis = 200))
        ) {
            PropertyDetails()
        }
    }
}
```

### 7. Animated Visibility Toggle

```kotlin
@Composable
fun ToggleableSection() {
    var isExpanded by remember { mutableStateOf(false) }
    
    Column {
        Button(onClick = { isExpanded = !isExpanded }) {
            Text("Toggle Details")
        }
        
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
        ) {
            DetailContent()
        }
    }
}
```

### 8. Staggered Multiple Button Animation

```kotlin
@Composable
fun ActionButtons() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            "Save" to { /* save action */ },
            "Publish" to { /* publish action */ },
            "Preview" to { /* preview action */ }
        ).forEachIndexed { index, (label, action) ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(300, delayMillis = index * 100)
                ) + fadeIn(animationSpec = tween(300, delayMillis = index * 100))
            ) {
                AnimatedButton(
                    onClick = action,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(label)
                }
            }
        }
    }
}
```

### 9. Smooth Loading State Transition

```kotlin
@Composable
fun DataLoadingScreen(
    isLoading: Boolean,
    data: List<Item>
) {
    Box {
        if (isLoading) {
            AnimatedLoadingIndicator(
                isLoading = true,
                color = Color.Black,
                modifier = Modifier.size(50.dp)
            )
        } else {
            LazyColumn {
                items(data.size) { index ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            animationSpec = tween(
                                300,
                                delayMillis = index * 100
                            )
                        )
                    ) {
                        ItemRow(data[index])
                    }
                }
            }
        }
    }
}
```

### 10. Custom Duration Animation

```kotlin
// For faster feedback
AnimatedVisibility(
    visible = true,
    enter = slideInVertically(
        initialOffsetY = { 50 },
        animationSpec = tween(
            durationMillis = 200,  // Faster: 200ms
            easing = EaseOutCubic
        )
    ) + fadeIn(animationSpec = tween(200))
) {
    MyContent()
}

// For slow, deliberate reveal
AnimatedVisibility(
    visible = true,
    enter = slideInVertically(
        initialOffsetY = { 100 },
        animationSpec = tween(
            durationMillis = 500,  // Slower: 500ms
            easing = EaseInOutCubic
        )
    ) + fadeIn(animationSpec = tween(500))
) {
    MyContent()
}
```

## Tips & Tricks

### Performance Tips

✅ **Use `remember` for state**
```kotlin
val fieldState = remember { mutableStateOf("") }
```

✅ **Stagger animations to distribute load**
```kotlin
delayMillis = index * 50  // Spreads animation load
```

✅ **Use `LazyColumn` for long lists**
```kotlin
LazyColumn {  // Only renders visible items
    items(1000) { index -> ItemRow(index) }
}
```

✅ **Combine animations (slide + fade)**
```kotlin
slideInVertically(...) + fadeIn(...)  // Both happen together
```

### Animation Easing Options

```kotlin
import androidx.compose.animation.core.*

// Available easings from Compose
EaseInOutCubic      // Current default (smooth, natural)
EaseInOut           // Linear smooth
EaseIn              // Starts slow, ends fast
EaseOut             // Starts fast, ends slow
EaseInCirc          // Circular easing
EaseOutElastic      // Bouncy effect
FastOutLinearIn     // Material design standard
LinearOutSlowIn     // Material design standard
```

### Timing Presets (from AnimationSpecs.kt)

```kotlin
AnimationDurations.FAST    // 250ms - Quick feedback
AnimationDurations.NORMAL  // 300ms - Standard transitions

// Use in your animations
tween(durationMillis = AnimationDurations.FAST)
tween(durationMillis = AnimationDurations.NORMAL)
```

## Integration Checklist

- [ ] Import animations in your screen
- [ ] Add navigation animations to new screens
- [ ] Use `AnimatedButton` for all action buttons
- [ ] Use `AnimatedInputField` for form fields
- [ ] Use `LazyColumn` for scrollable lists
- [ ] Add staggered animations to list items
- [ ] Test on low-end device (Snapdragon 665+)
- [ ] Verify no frame drops with profiler

## Common Issues & Solutions

### Issue: Animations don't show
**Solution**: Make sure you're using `AnimatedVisibility` or animation-aware composables
```kotlin
// ❌ Wrong - animation won't work
if (isVisible) MyContent()

// ✅ Correct
AnimatedVisibility(visible = isVisible) {
    MyContent()
}
```

### Issue: Lag during animation
**Solution**: Move heavy operations off main thread
```kotlin
// ❌ Wrong - blocks UI during animation
val result = heavyComputation()

// ✅ Correct - compute before animation
val result = remember { heavyComputation() }
```

### Issue: Buttons unresponsive
**Solution**: Ensure proper button sizing and no overlays
```kotlin
// ✅ Correct - proper touch target
AnimatedButton(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)  // Minimum 48dp for touch
) {
    Text("Button")
}
```

## Need Help?

1. Check `ANIMATION_IMPROVEMENTS.md` for detailed documentation
2. Check `QUICK_START_ANIMATIONS.md` for quick reference
3. Review Jetpack Compose docs: https://developer.android.com/jetpack/compose/animation
4. Use Android Studio Profiler to debug performance

Happy animating! 🎬

