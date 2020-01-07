package me.feuerwehr.notification.server.web.components.json

import com.fasterxml.jackson.annotation.JsonProperty

class MessageJSON(
    @JsonProperty("message")
    val message: String
)