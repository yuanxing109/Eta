package io.github.mangi.eta.agent.voice

import android.app.Application
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class SystemSpeechRecognizerTest {
    private fun service(
        packageName: String,
        name: String,
        permission: String? = null,
        exported: Boolean = true,
    ) = ResolveInfo().apply {
        serviceInfo = ServiceInfo().apply {
            this.packageName = packageName
            this.name = name
            this.permission = permission
            this.exported = exported
            this.enabled = true
            applicationInfo = ApplicationInfo().apply { enabled = true }
        }
    }

    @Test fun configuredRecognitionServiceCanOmitBindPermission() {
        val configured = ComponentName("com.google.android.tts", "GoogleTTSRecognitionService")
        assertEquals(
            configured,
            SystemSpeechRecognizer.selectExternalService(
                listOf(service(configured.packageName, configured.className)),
                "io.github.mangi.eta",
                configured,
            ),
        )
    }

    @Test fun unconfiguredServiceWithoutBindPermissionIsNotSelected() {
        assertNull(
            SystemSpeechRecognizer.selectExternalService(
                listOf(service("third.party", "Recognizer")),
                "io.github.mangi.eta",
                null,
            ),
        )
    }

    @Test fun unexportedAndSelfRecognitionServicesAreExcluded() {
        val configured = ComponentName("third.party", "Hidden")
        assertNull(
            SystemSpeechRecognizer.selectExternalService(
                listOf(
                    service("third.party", "Hidden", exported = false),
                    service("io.github.mangi.eta", "EtaRecognitionService", permission = "android.permission.BIND_SPEECH_RECOGNITION_SERVICE"),
                ),
                "io.github.mangi.eta",
                configured,
            ),
        )
    }

    // 小米识别服务用自带命名空间的签名级权限名，未配置 setting 时也必须能被选中。
    @Test fun vendorScopedBindPermissionIsSelectedWithoutConfiguredService() {
        val xiaomi = ComponentName(
            "com.xiaomi.mibrain.speech",
            "com.xiaomi.mibrain.speech.asr.AsrService",
        )
        assertEquals(
            xiaomi,
            SystemSpeechRecognizer.selectExternalService(
                listOf(
                    service(
                        xiaomi.packageName,
                        xiaomi.className,
                        permission = "com.xiaomi.mibrain.speech.permission.BIND_SPEECH_RECOGNITION_SERVICE",
                    ),
                ),
                "io.github.mangi.eta",
                null,
            ),
        )
    }

    @Test fun ownVendorScopedServiceIsStillExcluded() {
        assertNull(
            SystemSpeechRecognizer.selectExternalService(
                listOf(
                    service(
                        "io.github.mangi.eta",
                        "EtaRecognitionService",
                        permission = "io.github.mangi.eta.permission.BIND_SPEECH_RECOGNITION_SERVICE",
                    ),
                ),
                "io.github.mangi.eta",
                null,
            ),
        )
    }

    @Test fun permissionNameWithoutVendorScopeIsNotAccepted() {
        assertNull(
            SystemSpeechRecognizer.selectExternalService(
                listOf(
                    service("third.party", "Recognizer", permission = "third.party.BIND_SPEECH_RECOGNITION_SERVICE"),
                    service("third.party", "Suffix", permission = "third.party.permission.NOT_BIND_SPEECH_RECOGNITION_SERVICE"),
                ),
                "io.github.mangi.eta",
                null,
            ),
        )
    }

    @Test fun aospBindPermissionTakesPrecedenceOverVendorVariant() {
        val vendor = ComponentName("com.vendor.asr", "com.vendor.asr.AsrService")
        val aosp = ComponentName("com.google.android.tts", "GoogleTTSRecognitionService")
        assertEquals(
            aosp,
            SystemSpeechRecognizer.selectExternalService(
                listOf(
                    service(vendor.packageName, vendor.className, permission = "com.vendor.asr.permission.BIND_SPEECH_RECOGNITION_SERVICE"),
                    service(aosp.packageName, aosp.className, permission = "android.permission.BIND_SPEECH_RECOGNITION_SERVICE"),
                ),
                "io.github.mangi.eta",
                null,
            ),
        )
    }
}
