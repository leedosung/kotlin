/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package templates2

import templates.*


object Arrays : TemplateGroupBase() {
    val f_sort_range = fn("sort(fromIndex: Int = 0, toIndex: Int = size)") {
        platforms(Platform.JVM)
        include(Family.ArraysOfObjects, Family.ArraysOfPrimitives)
        exclude(PrimitiveType.Boolean)

        builder {
            doc { "Sorts a range in the array in-place." }
            returns("Unit")
            body { "java.util.Arrays.sort(this, fromIndex, toIndex)" }
        }
    }

    val f_copyOf = fn("copyOf()") {
        include(Family.InvariantArraysOfObjects)
        include(Family.ArraysOfPrimitives, PrimitiveType.defaultPrimitives)
        builderWith { primitive ->

            doc { "Returns new array which is a copy of the original array." }
            returns("SELF")
            on(Platform.JVM) {
                inlineOnly()
                body { "return java.util.Arrays.copyOf(this, size)" }
            }
            on(Platform.JS) {
                specialFor(Family.InvariantArraysOfObjects) { family = Family.ArraysOfObjects }
                when (primitive) {
                    null ->
                        body { "return this.asDynamic().slice()" }
                    PrimitiveType.Char, PrimitiveType.Boolean, PrimitiveType.Long ->
                        body { "return withType(\"${primitive}Array\", this.asDynamic().slice())" }
                    else -> {
                        inline(suppressWarning = true)
                        body { "return this.asDynamic().slice()" }
                    }
                }
            }

        }
    }

    val f_plusElementOperator = fn("plus(element: T)") {
        include(Family.InvariantArraysOfObjects, Family.ArraysOfPrimitives)

        builderWith { primitive ->
            doc { "Returns an array containing all elements of the original array and then the given [element]." }
            operator()
            returns("SELF")

            on(Platform.JVM) {
                body {
                    """
                        val index = size
                        val result = java.util.Arrays.copyOf(this, index + 1)
                        result[index] = element
                        return result
                        """
                }
            }

            on(Platform.JS) {
                inline(suppressWarning = true)
                specialFor(Family.InvariantArraysOfObjects) {
                    family = Family.ArraysOfObjects
                    returns("Array<T>")
                }

                body {
                    if (primitive == null)
                        "return this.asDynamic().concat(arrayOf(element))"
                    else
                        "return plus(${primitive.name.toLowerCase()}ArrayOf(element))"
                }
            }
        }
    }

}