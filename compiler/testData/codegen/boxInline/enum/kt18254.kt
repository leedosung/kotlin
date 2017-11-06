// FILE: 1.kt
// WITH_RUNTIME
package test

inline fun stub() {}

enum class Z {
    OK
}

// FILE: 2.kt

import test.*

fun box(): String {
    return  { enumValueOf<Z>("OK").name  } ()
}
