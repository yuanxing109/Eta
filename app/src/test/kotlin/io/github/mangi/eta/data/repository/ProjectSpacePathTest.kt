package io.github.mangi.eta.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 私有项目的路径推导是纯函数，单独钉住：目录名一旦变化，
 * 已绑定会话与项目记忆都会指向不存在的路径。
 */
class ProjectSpacePathTest {

    @Test
    fun slugKeepsAsciiAndCollapsesSeparators() {
        assertEquals("eta-android", slugify("Eta Android"))
        assertEquals("eta-2-0", slugify("Eta__2.0"))
        assertEquals("project", slugify("你好"))
        assertEquals("project", slugify("  "))
    }

    @Test
    fun slugIsBoundedAndHasNoLeadingOrTrailingDash() {
        val long = slugify("-${"a".repeat(60)}-")
        assertEquals(32, long.length)
        assertEquals(false, long.startsWith("-"))
        assertEquals(false, long.endsWith("-"))
    }

    @Test
    fun privatePathsLiveUnderProjectRootAndWorkspaceSubdirectory() {
        val host = privateHostPath("/data/local/tmp/eta/", "eta-android", "3f9c1a2b-0000-1111")
        assertEquals("/data/local/tmp/eta/projects/eta-android-3f9c1a2b", host)
        assertEquals("/workspace/projects/eta-android", privateLinuxPath("eta-android"))
    }
}
