package chat.mural.network

import android.content.Context

enum class AIProvider(
    val displayName: String,
    val helperModel: String,
    val liveModel: String,
    val keyUrl: String,
    val usageUrl: String,
    val dataControlsUrl: String,
) {
    OPENAI("OpenAI", "gpt-6-luna", "gpt-live-1", "https://platform.openai.com/api-keys", "https://platform.openai.com/usage", "https://developers.openai.com/api/docs/guides/your-data"),
    GOOGLE_AI_STUDIO("Google AI Studio", "gemini-3.5-flash-lite", "gemini-3.8-live", "https://aistudio.google.com/app/apikey", "https://aistudio.google.com/app/usage", "https://ai.google.dev/gemini-api/docs/usage-policies"),
    NOUS_PORTAL("Nous Portal", "openai/gpt-6-luna", "", "https://portal.nousresearch.com/", "https://portal.nousresearch.com/", "https://portal.nousresearch.com/");

    /** Only providers with a realtime voice model can open live voice conversations. */
    val supportsVoice: Boolean get() = liveModel.isNotBlank()
}

object AIProviderSelection {
    private const val PREFERENCES = "mural_ai_provider"
    private const val LEGACY = "provider"
    private const val VOICE = "voice_provider"
    private const val HELPER = "helper_provider"

    /** Voice and reasoning providers are chosen independently; the previous single choice seeds both. */
    fun readVoice(context: Context): AIProvider {
        val selected = read(context, VOICE)
        return selected.takeIf { it.supportsVoice } ?: AIProvider.OPENAI
    }

    fun readHelper(context: Context): AIProvider = read(context, HELPER)

    private fun read(context: Context, key: String): AIProvider {
        val preferences = context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val raw = preferences.getString(key, null) ?: preferences.getString(LEGACY, null)
        return AIProvider.entries.firstOrNull { it.name == raw } ?: AIProvider.OPENAI
    }

    fun saveVoice(context: Context, provider: AIProvider) {
        context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putString(VOICE, provider.name).putString(LEGACY, provider.name).apply()
    }

    fun saveHelper(context: Context, provider: AIProvider) {
        context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putString(HELPER, provider.name).apply()
    }
}
