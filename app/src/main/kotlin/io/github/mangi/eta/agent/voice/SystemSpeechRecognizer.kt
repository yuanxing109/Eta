package io.github.mangi.eta.agent.voice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.provider.Settings
import android.speech.RecognitionService
import android.speech.SpeechRecognizer

internal object SystemSpeechRecognizer {
    internal data class Source(val component: ComponentName?) {
        val label: String get() = component?.flattenToShortString() ?: "on_device"
    }

    internal data class Selection(val recognizer: SpeechRecognizer, val source: Source)

    fun create(context: Context): SpeechRecognizer? = select(context)?.recognizer

    fun select(context: Context): Selection? {
        val appContext = context.applicationContext
        resolveExternalService(appContext)?.let { component ->
            return Selection(SpeechRecognizer.createSpeechRecognizer(appContext, component), Source(component))
        }
        return if (SpeechRecognizer.isOnDeviceRecognitionAvailable(appContext)) {
            Selection(SpeechRecognizer.createOnDeviceSpeechRecognizer(appContext), Source(null))
        } else {
            null
        }
    }

    fun create(context: Context, source: Source): SpeechRecognizer =
        if (source.component == null) {
            SpeechRecognizer.createOnDeviceSpeechRecognizer(context.applicationContext)
        } else {
            SpeechRecognizer.createSpeechRecognizer(context.applicationContext, source.component)
        }

    internal fun resolveExternalService(context: Context): ComponentName? {
        val configured = Settings.Secure.getString(
            context.contentResolver,
            VOICE_RECOGNITION_SERVICE,
        )?.let(ComponentName::unflattenFromString)
        val services = context.packageManager.queryIntentServices(
            Intent(RecognitionService.SERVICE_INTERFACE),
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()),
        )
        return selectExternalService(services, context.packageName, configured)
    }

    internal fun selectExternalService(
        services: List<ResolveInfo>,
        ownPackage: String,
        configured: ComponentName?,
    ): ComponentName? = services
            .asSequence()
            .mapNotNull { it.serviceInfo }
            .filter {
                it.packageName != ownPackage && it.exported && it.enabled &&
                    it.applicationInfo?.enabled != false
            }
            .filter {
                val component = ComponentName(it.packageName, it.name)
                component == configured || declaresRecognitionBinding(it)
            }
            .sortedWith(
                compareByDescending<ServiceInfo> {
                    ComponentName(it.packageName, it.name) == configured
                }.thenByDescending {
                    it.permission == BIND_SPEECH_RECOGNITION_SERVICE
                }.thenByDescending {
                    (it.applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0
                },
            )
            .map { ComponentName(it.packageName, it.name) }
            .firstOrNull()

    /** 除 AOSP 权限名外，一并接受厂商按 {包名}.permission.BIND_SPEECH_RECOGNITION_SERVICE 约定声明的识别服务。 */
    private fun declaresRecognitionBinding(service: ServiceInfo): Boolean {
        val permission = service.permission ?: return false
        return permission == BIND_SPEECH_RECOGNITION_SERVICE ||
            permission.endsWith(BIND_SPEECH_RECOGNITION_PERMISSION_SUFFIX)
    }

    private const val BIND_SPEECH_RECOGNITION_SERVICE = "android.permission.BIND_SPEECH_RECOGNITION_SERVICE"
    private const val BIND_SPEECH_RECOGNITION_PERMISSION_SUFFIX = ".permission.BIND_SPEECH_RECOGNITION_SERVICE"
    private const val VOICE_RECOGNITION_SERVICE = "voice_recognition_service"
}
