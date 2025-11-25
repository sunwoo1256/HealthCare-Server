package cerberus.HealthCare;

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

class ChatGPT(
    private val apikey: String,
){
    var client = OpenAI(apikey)

    suspend fun getCompletionMessage(openAI: OpenAI, prompt: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

        return completion.choices.firstOrNull()?.message?.content ?: "failed to get response."
    }

    // java-friendly wrapper
    fun getCompletionMessageBlocking(prompt: String): String =
        kotlinx.coroutines.runBlocking {
            getCompletionMessage(client, prompt)
        }
}