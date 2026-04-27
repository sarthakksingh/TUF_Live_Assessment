package com.sarthak.tufassessment.data.repo

import android.content.Context
import android.util.Log
import com.sarthak.tufassessment.data.model.AttachmentKind
import com.sarthak.tufassessment.data.model.ChatAttachment
import com.sarthak.tufassessment.data.model.ChatContact
import com.sarthak.tufassessment.data.model.ChatMessage
import com.sarthak.tufassessment.data.model.ChatStore
import com.sarthak.tufassessment.data.model.MessageStatus
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ChatRepository(context: Context) {
    private val storeFile = File(context.filesDir, STORE_FILE_NAME)

    fun loadStore(): ChatStore? {
        if (!storeFile.exists()) return null
        return runCatching {
            parseStore(JSONObject(storeFile.readText()))
        }.getOrElse {
            Log.w("ChatRepository", "Unable to read chat store", it)
            null
        }
    }

    fun saveStore(store: ChatStore) {
        storeFile.writeText(serializeStore(store).toString())
    }

    private fun serializeStore(store: ChatStore): JSONObject {
        val contactsArray = JSONArray()
        store.contacts.forEach { contact ->
            contactsArray.put(
                JSONObject()
                    .put("id", contact.id)
                    .put("name", contact.name)
                    .put("about", contact.about)
            )
        }

        val conversationsObject = JSONObject()
        store.conversations.forEach { (contactId, messages) ->
            val messagesArray = JSONArray()
            messages.forEach { message ->
                messagesArray.put(serializeMessage(message))
            }
            conversationsObject.put(contactId, messagesArray)
        }

        val archivedArray = JSONArray()
        store.archivedContactIds.forEach { contactId ->
            archivedArray.put(contactId)
        }

        return JSONObject()
            .put("contacts", contactsArray)
            .put("conversations", conversationsObject)
            .put("archivedContactIds", archivedArray)
    }

    private fun serializeMessage(message: ChatMessage): JSONObject {
        val json = JSONObject()
            .put("id", message.id)
            .put("senderId", message.senderId)
            .put("text", message.text)
            .put("timestampMillis", message.timestampMillis)
            .put("status", message.status.name)

        val attachment = message.attachment
        if (attachment != null) {
            json.put(
                "attachment",
                JSONObject()
                    .put("uri", attachment.uri)
                    .put("displayName", attachment.displayName)
                    .put("mimeType", attachment.mimeType)
                    .put("kind", attachment.kind.name)
                    .put("sizeBytes", attachment.sizeBytes)
            )
        }
        return json
    }

    private fun parseStore(json: JSONObject): ChatStore {
        val contacts = buildList {
            val contactsArray = json.optJSONArray("contacts") ?: JSONArray()
            for (index in 0 until contactsArray.length()) {
                val contactJson = contactsArray.getJSONObject(index)
                add(
                    ChatContact(
                        id = contactJson.getString("id"),
                        name = contactJson.getString("name"),
                        about = contactJson.optString("about", "")
                    )
                )
            }
        }

        val conversations = buildMap {
            val conversationsJson = json.optJSONObject("conversations") ?: JSONObject()
            val keys = conversationsJson.keys()
            while (keys.hasNext()) {
                val contactId = keys.next()
                val messagesArray = conversationsJson.getJSONArray(contactId)
                val messages = buildList {
                    for (index in 0 until messagesArray.length()) {
                        add(parseMessage(messagesArray.getJSONObject(index)))
                    }
                }
                put(contactId, messages.sortedBy { it.timestampMillis })
            }
        }

        val archivedContactIds = buildList {
            val archivedArray = json.optJSONArray("archivedContactIds") ?: JSONArray()
            for (index in 0 until archivedArray.length()) {
                add(archivedArray.getString(index))
            }
        }

        return ChatStore(
            contacts = contacts,
            conversations = conversations,
            archivedContactIds = archivedContactIds
        )
    }

    private fun parseMessage(json: JSONObject): ChatMessage {
        val attachment = json.optJSONObject("attachment")?.let { attachmentJson ->
            ChatAttachment(
                uri = attachmentJson.getString("uri"),
                displayName = attachmentJson.getString("displayName"),
                mimeType = attachmentJson.optString("mimeType", ""),
                kind = AttachmentKind.valueOf(attachmentJson.getString("kind")),
                sizeBytes = attachmentJson.optLong("sizeBytes", 0L)
            )
        }

        return ChatMessage(
            id = json.getString("id"),
            senderId = json.getString("senderId"),
            text = json.optString("text", ""),
            attachment = attachment,
            timestampMillis = json.getLong("timestampMillis"),
            status = MessageStatus.valueOf(json.getString("status"))
        )
    }

    companion object {
        private const val STORE_FILE_NAME = "chat_store.json"
    }
}